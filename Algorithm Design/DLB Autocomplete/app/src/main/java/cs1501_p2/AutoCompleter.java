package cs1501_p2;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;


public class  AutoCompleter implements AutoComplete_Inter 
{

    private String currentSearch;	
    
	private DLB dictionary;
	private UserHistory userHistory;
    
	public AutoCompleter(String dict, String hist)
	{
		this.dictionary = new DLB(dict);
		try {
            FileInputStream fileIn = new FileInputStream(hist);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            this.userHistory = (UserHistory) in.readObject();
            this.userHistory.resetByChar(); //make sure previous cached currentSearch is cleared.
            in.close();
            fileIn.close();
         } catch (IOException e) {
               System.err.println("Error in reading the user history file: " + hist);
            e.printStackTrace();
            return;
         } catch (ClassNotFoundException ce) {
            System.err.println("UserHistory class not found");
            ce.printStackTrace();
            return;
         }
	}
	
	public AutoCompleter(String dict)
	{
		this.dictionary = new DLB(dict);
		this.userHistory = new UserHistory();
	}
	
	public ArrayList<String> nextChar(char next)
	{ 
		this.dictionary.searchByChar(next);
		this.userHistory.searchByChar(next);	
		this.currentSearch += next;
		ArrayList<String> fiveSuggestions = new ArrayList<String>(5);
		ArrayList<String> historySuggestions = userHistory.suggest(); 
		fiveSuggestions.addAll(historySuggestions);		
		ArrayList<String> fiveSuggestionsFromDict = dictionary.suggest();
		for(int i = 0; (fiveSuggestions.size()<5)&&(i<fiveSuggestionsFromDict.size());i++ )
		{
			String word = fiveSuggestionsFromDict.get(i);
			if(!fiveSuggestions.contains(word))
			{
				fiveSuggestions.add(word);
			}
		}
		return  fiveSuggestions;
	}


	public void finishWord(String cur) 
	{
        //currentSearch=cur;
		this.userHistory.add(cur);
		this.userHistory.resetByChar();
		this.dictionary.resetByChar();
		this.currentSearch = "";
	}
	
	public void saveUserHistory(String fname) 
	{
		try {
	        FileOutputStream fileOut =  new FileOutputStream(fname);
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(this.userHistory);
	        out.close();
	        fileOut.close();
	        System.out.printf("Serialized data is saved in " +fname);
     } catch (IOException e) {
			System.err.println("Error in saving the user history file: " + fname);
			System.err.println("History state may not be up to date!");
			e.printStackTrace();
     }
	}

}
