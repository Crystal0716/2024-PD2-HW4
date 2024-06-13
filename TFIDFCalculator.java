import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileWriter;
import java.util.Hashtable;

public class TFIDFCalculator
{
    public static void main(String[] args)
    {
        Trie trie = new Trie(); 
        ArrayList<String> searchName = new ArrayList<String>();
        ArrayList<String> searchNumber = new ArrayList<String>();
        Hashtable<Integer, Integer> wordsCount = new Hashtable<>();
        int totalTextCount = 0 ;
        try
        {
            String line ;
            int lineCount = 0 ;
            int textCount = 0 ;
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            while((line = br.readLine())!= null)
            {
                String[] parts = line.split("\\s+");
                lineCount = Integer.parseInt(parts[0]);
                textCount = (lineCount-1)/5 ;
                line = line.trim().toLowerCase().replaceAll("[^a-zA-Z]", " ");
                String[] parts2 = line.split("\\s+");
                if (!wordsCount.containsKey(textCount)) 
                { 
                    wordsCount.put(textCount, -5);
                }
                for(int i = 0 ; i < parts2.length ; i++)
                {
                    trie.insert(parts2[i], Integer.toString(textCount));
                    wordsCount.put(textCount, wordsCount.get(textCount) + 1);
                }
                if((lineCount % 5 )== 0)
                {
                    totalTextCount ++ ;
                }
                
            }
           
            br.close();
        }
        
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            String line ;
            BufferedReader br = new BufferedReader(new FileReader(args[1]));
            int lineCount = 0;
            while((line = br.readLine())!= null)
            {
                if (lineCount == 0)
                {
                    String[] parts = line.split("\\s+");
                    for(int i = 0 ; i < parts.length ; i++ )
                    {
                        searchName.add(parts[i]);
                    }
                }
                else if(lineCount == 1 )
                {
                    String[] parts = line.split("\\s+");
                    for(int i = 0 ; i < parts.length ; i++ )
                    {
                        searchNumber.add(parts[i]);
                    }
                }
                lineCount++;
            }
            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        double findTf = 0.00;
        double findIdf = 0.00;
        double tf_idf = 0.00 ;
        try
        {
            FileWriter fr = new FileWriter("output.txt");
            for (int i = 0; i < searchName.size(); i++) 
            {
                boolean isFound = trie.search(searchName.get(i), searchNumber.get(i));
                if (isFound) 
                {
                    int place = Integer.parseInt(searchNumber.get(i)) ;
                    Integer wordCountValue = wordsCount.get(place);
                    if (wordCountValue != null) 
                    {
                        findTf = (double) trie.tf(searchName.get(i),searchNumber.get(i)) / wordCountValue;
                        findIdf = trie.idf(searchName.get(i),totalTextCount);
                        tf_idf = findTf*findIdf ;
                        fr.write(String.format("%.5f", tf_idf) + " ");
                    } 
                    else
                    {

                    }
                } 
                else 
                {
                    fr.write("0.00000 ");
                }
            }
            fr.close();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}


class TrieNode 
{
    TrieNode[] children = new TrieNode[26];
    boolean isEndOfWord = false;
    Hashtable<String, Integer> additionalInfo = new Hashtable<>();
    int keyWordsCount ;
}

class Trie 
{
    TrieNode root = new TrieNode();

    public void insert(String word, String searchNumber) 
    {
        TrieNode node = root;
        for (char c : word.toCharArray()) 
        {
            if (node.children[c - 'a'] == null) 
            {
                node.children[c - 'a'] = new TrieNode();
            }
            node = node.children[c - 'a'];
        }
        if (!node.additionalInfo.containsKey(searchNumber)) 
        {
            node.keyWordsCount++;
        }
        node.isEndOfWord = true;
        int count = node.additionalInfo.getOrDefault(searchNumber, 0);
        node.additionalInfo.put(searchNumber, count + 1);     
    }
    

    public boolean search(String word, String searchNumber) 
    {
        TrieNode node = root;
        for (char c : word.toCharArray())
        {
            node = node.children[c - 'a'];
            if (node == null) 
            {
                return false;
            }
        }
        return node.additionalInfo.containsKey(searchNumber);
    }

    public int tf(String searchName, String searchNumber) 
    {
        TrieNode node = root;
        for (char c : searchName.toCharArray()) 
        {
            if (node == null) 
            {
                return 0;
            }
            node = node.children[c - 'a'];
        }
        if (node == null || !node.additionalInfo.containsKey(searchNumber)) 
        {
            return 0;
        }
        Integer terms = node.additionalInfo.get(searchNumber);
        return terms != null ? terms : 0;
    }

    public double idf(String searchName , int totalTextCount) 
    {
        TrieNode node = root;
        for (char c : searchName.toCharArray()) 
        {
            if (node == null) 
            {
                return 0;
            }
            node = node.children[c - 'a'];
        }
        //I finished
        Integer terms = node.keyWordsCount;
        double idfTerm = Math.log((double) totalTextCount/terms);
        return idfTerm ;
    }
}