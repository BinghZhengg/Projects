package cs1501_p2;


import java.util.*;
import java.util.Map.Entry;
import java.io.Serializable;
import java.io.File;
import java.io.FileNotFoundException;

public class UserHistory implements Dict, java.io.Serializable
{
    private DLBNode root;
    //private final char endChar = '^';
    private int size;
    private String currentSearch;
    private Map<String, Integer> frequencyMap;



    public UserHistory()
    {
        
        currentSearch="";
        frequencyMap = new HashMap<>();
        size =0;
    }
    public UserHistory(String historyFile) 
	{
		this();
	  	   try {	
	  	        Scanner myReader = new Scanner(new File(historyFile));
	  	        while (myReader.hasNextLine()) 
                {
	  	            String word = myReader.nextLine();
	  	            if(word.isEmpty()) break;
	  	            String[] keyValuePair = word.split(";");
	  	            frequencyMap.put(keyValuePair[0], Integer.parseInt(keyValuePair[1]));
	  	        }
	  	        myReader.close();
	  	    } catch (FileNotFoundException e) {
	  		      System.out.println("An error occurred when reading history state file:" + historyFile);
	  		      e.printStackTrace();
	  	    }

	}
    public void add(String key)
    {
        
        Integer oldValue = frequencyMap.get(key);
		if(oldValue == null) {
			frequencyMap.put(key, 1);
			size++;
		}
		else 
		{
			frequencyMap.replace(key, oldValue, oldValue+1);
		}
        
        // adding node by char of word

    }
    public boolean contains(String key)
    {
        return frequencyMap.containsKey(key);
        //traverse search by string key, must check have termination charater

    }
    public boolean containsPrefix(String pre)
    {
        for(String key : frequencyMap.keySet())
        {
            if(key.startsWith(pre)) return true;
        }

        return false;
        //traverse search by string prefix

    }
    public int searchByChar(char next)
    {
        currentSearch+=next;
        boolean isContained = this.contains(currentSearch);
		boolean isPrefix = this.containsPrefix(currentSearch);
        
	
		if(isPrefix && (!isContained))
		{
			return 0;
		}
		else if((!isPrefix) && isContained)
		{
			return 1;
		}
		else if(isPrefix && isContained)
		{
			return 2;
		}
		else 
        {
		    return -1;
		}	
		
        //int value indicating result for current by-character search:
	 			//-1: not a valid word or prefix
	 			// 0: valid prefix, but not a valid word
	 			// 1: valid word, but not a valid prefix to any other words
	 			// 2: both valid word and a valid prefix to other words

    }
    public void resetByChar()
    {
        currentSearch = "";

    }
    @Override
	public String toString() 
    {
		StringBuffer sb = new StringBuffer();
		for(Map.Entry<String,Integer> mapElement : this.frequencyMap.entrySet()) 
		{
            String key = (String)mapElement.getKey();
            Integer value = (Integer)mapElement.getValue();
            sb.append(key);
            sb.append(";");
            sb.append(value.intValue());
            sb.append("\n");
		}
		return sb.toString();
	}
    public ArrayList<String> suggest()
    {

        HashMap<String, Integer> wordsStartWithPrefix =  new HashMap<String, Integer>();
		for(Map.Entry<String,Integer> mapElement : this.frequencyMap.entrySet()) 
        {
            String key = (String)mapElement.getKey();
            if(key.startsWith(currentSearch))
            {
                Integer value = mapElement.getValue();
            	wordsStartWithPrefix.put(key, value);
            }
		}
		
		Comparator<Entry<String, Integer>> valueComparator = new Comparator<Entry<String,Integer>>() 
		{ 
			@Override 
			public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) 
			{ 
				Integer v1 = e1.getValue(); Integer v2 = e2.getValue(); return v2.compareTo(v1); 
			}  
		};
		
		Set<Entry<String, Integer>> entries = wordsStartWithPrefix.entrySet();
		// Sort method needs a List, so let's first convert Set to List in Java 
		List<Entry<String, Integer>> listOfEntries = new ArrayList<Entry<String, Integer>>(entries); 
		// sorting HashMap by values using comparator 
		Collections.sort(listOfEntries, valueComparator); 

		ArrayList<String> firstFiveEntries = new ArrayList<String>();
		for(int index = 0; (index < 5)&&index<listOfEntries.size(); index++)
		{
			firstFiveEntries.add(listOfEntries.get(index).getKey()); 
		}
		return firstFiveEntries;
        
    
        //if containsPrefix() traverse through everything under and options

    }
    public ArrayList<String> traverse()
    {
        ArrayList<String> allEntries = new ArrayList<String>();
		for(Map.Entry<String,Integer> mapElement : this.frequencyMap.entrySet()) 
		{
			allEntries.add(mapElement.getKey()); 
		}
		return allEntries;
     
        //logic issue how to traverse

    }
    
    public int count()
    {
        //after add, increment count variable
        return size;
    }

}