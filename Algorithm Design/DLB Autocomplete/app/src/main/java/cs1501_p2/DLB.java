package cs1501_p2;


import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class DLB implements Dict
{
    private DLBNode root;
    private final char SENTINEL='^';
    private int size;
    private ArrayList<String> allwords;
    private ArrayList<String> suggestedWords;
    private String currentSearch;

    public DLB()
    {
        root = null;
		currentSearch = "";
		allwords = new ArrayList<String>();
		suggestedWords = new ArrayList<String>();
		size = 0;
       
        

    }
    public DLB(String dictionaryFile)
	{
		this();
		try {	
	        Scanner myReader = new Scanner(new File(dictionaryFile));
	        while (myReader.hasNextLine()) {
	            this.add(myReader.nextLine());
	        }
	        myReader.close();
	    } catch (FileNotFoundException e) {
		      //System.out.println("An error occurred while reading the dictionary file: " + dictionaryFile);
		      e.printStackTrace();
	    }
		
	}
    private void addRemainedLettersToTheEnd(DLBNode top, String key, int startIndex)
	{
		DLBNode curr = top;
		for(int i = startIndex; i < key.length(); i = i + 1)
		{
		    curr.setDown(new DLBNode(key.charAt(i)));
		    curr = curr.getDown();
		}
		curr.setDown(new DLBNode(this.SENTINEL));
		size ++;
		//System.out.println("Created a new word in the DLB: "+key);
	}


    public void add(String key)
    {
    if(root == null)
		{
			// makes a new node for the root
			root = new DLBNode(key.charAt(0));						
			addRemainedLettersToTheEnd(root,key,1);
			return;
		}
		
		// the DLB isn't empty
		else 
        {
			DLBNode curr = root;
			int index = 0;
			
			// if inserted word is a single letter word
			if(key.length()==1)
			{
				if(curr.getLet() == key.charAt(0))
				{
					DLBNode downNode = curr.getDown();
					if(downNode == null)
					{
						//System.err.println("Error of inserting letter"+key.charAt(index)+"with index = " + index + " of " + key);
						//System.err.println("End of word missing at the end of existing DLB node chain");
						return;
					}
					else if (downNode.getLet() != this.SENTINEL)
					{
						DLBNode sentinelNode = new DLBNode(this.SENTINEL);
						sentinelNode.setRight(curr.getDown());						
						curr.setDown(sentinelNode);
						size ++;						
						//System.out.println("Inserted a new word in the DLB: "+key);
						return;
					}
					else
					{
						//System.err.println("Word: "+key+" already exist in the DLB!");
						return;						
					}
					
				}
				// loop through the rest of right nodes and try to find a match
				else
				{
					// while loop #1, deal with the char at index of key is NOT equal to the letter of the node
					while(curr.getLet() != key.charAt(0))
					{
						DLBNode rightNode = curr.getRight();
						// if no more right node, we need to create a new chain here with remaining letters
						if(rightNode == null)
						{  
							rightNode = new DLBNode(key.charAt(index));
							addRemainedLettersToTheEnd(rightNode,key,index+1);
							curr.setRight(rightNode);
							return;
						}
						// search the next right chain
						else 
                        {
							curr = rightNode;
							continue; // search next node
						}

					} // end of while loop #1
										
				}

			}
			
			// more than 1 letter in the word
			else 
			{
			
				// this while loop contains two sub while loops, while loop #1 and while loop #2
				// each time when a new letter is read from the key, it is compared to the curr node
				// if they are not equal go to while loop #1, if they are equal go to while loop #2
				while(index < key.length()-1)
				{			
					
					// while loop #1, deal with the char at index of key is NOT equal to the letter of the node
					while(curr.getLet() != key.charAt(index))
					{
						DLBNode rightNode = curr.getRight();
						// if no more right node, we need to create a new chain here with remaining letters
						if(rightNode == null)
						{  
							rightNode = new DLBNode(key.charAt(index));
							curr.setRight(rightNode);
							addRemainedLettersToTheEnd(rightNode,key,index+1);
							return;
						}
						// search the next right chain
						else 
                        {
							curr = rightNode;
							break; // break the while loop # 1, enter the big while loop
						}

					} // end of while loop #1

					
					// while loop #2, deal with the char at index of key is EQAUL to the letter of the node
					while(curr.getLet() == key.charAt(index))
					{
						DLBNode downNode = curr.getDown();
						if(downNode == null)
						{
							//System.err.println("Error of inserting letter"+key.charAt(index)+"with index = " + index + " of " + key);
							//System.err.println("End of word missing at the end of existing DLB node chain");
							return;
						}
						else 
                        {
							curr = downNode;
							index ++;
							break; // break the while loop #2, enter the big while loop
						}
						
					} // # end of while loop #2
				} // end of big while loop
				if(key.length()-1 == index )
				{					
					if(curr.getLet() != key.charAt(index))						
					{
						DLBNode nextNode;
						do {
							nextNode=curr.getRight();
							if(nextNode==null)
							{
								nextNode = new DLBNode(key.charAt(index));						
								nextNode.setDown(new DLBNode(this.SENTINEL));
								curr.setRight(nextNode);
								size ++;
								//System.out.println("Inserted a new word in the DLB: "+key);
								return;								
							}
							
							if(nextNode.getLet() != key.charAt(index)) 
							{
								curr=nextNode;
								continue;
							}
							else {
								if (nextNode.getLet() != this.SENTINEL)
								{
									DLBNode sentinelNode = new DLBNode(this.SENTINEL);
									sentinelNode.setRight(nextNode.getDown());						
									nextNode.setDown(sentinelNode);
									size ++;
									System.out.println("Inserted a new word in the DLB: "+key);
									return;
								}
								else
								{
									System.err.println("Word: "+key+" already exist in the DLB!");
									return;						
								}								
							}
						    
						}while(curr != null);
					}
					
					DLBNode downNode = curr.getDown();
					if(downNode == null)
					{
						System.err.println("Error of inserting letter"+key.charAt(index)+"with index = " + index + " of " + key);
						System.err.println("End of word missing at the end of existing DLB node chain");
						return;					
					}
					else 
					{
						if (downNode.getLet() != this.SENTINEL)
						{
							DLBNode sentinelNode = new DLBNode(this.SENTINEL);
							sentinelNode.setRight(curr.getDown());						
							curr.setDown(sentinelNode);
							size ++;
							System.out.println("Inserted a new word in the DLB: "+key);
							return;
						}
						else
						{
							do 
							{ 
								DLBNode nextNode = curr.getRight();
								if(nextNode==null)
								{
									nextNode = new DLBNode(key.charAt(index));						
									nextNode.setDown(new DLBNode(this.SENTINEL));
									curr.setRight(nextNode);
									size ++;
									System.out.println("Inserted a new word in the DLB: "+key);
									return;								
								}
								
								if(nextNode.getLet() != key.charAt(index)) 
								{
									curr=nextNode;
									continue;
								}
								else {
									if (nextNode.getLet() != this.SENTINEL)
									{
										DLBNode sentinelNode = new DLBNode(this.SENTINEL);
										sentinelNode.setRight(nextNode.getDown());						
										nextNode.setDown(sentinelNode);
										size ++;
										System.out.println("Inserted a new word in the DLB: "+key);
										return;
									}
									else
									{
										System.err.println("Word: "+key+" already exist in the DLB!");
										return;						
									}								
								}
																
							}while(curr!=null);
					//		System.err.println("Word: "+key+" already exist in the DLB!");
						//	return;						
						}
					}
					
				}
			
			}
			
		} // end of else if DLB not empty
		
	}

    public boolean contains(String key)
    {
        int index = 0;
		DLBNode curr = root;
		
		do
		{
			if(curr == null)
			{
				return false;
			}
			else
			{
				if(key.charAt(index) == curr.getLet())
				{
					if(key.length()-1 == index)
					{
						// we found the string, time to break and check if a sentinel is after it.
						if(curr.getDown().getLet() == this.SENTINEL)
						{
							return true;
						}
						else
						{
							return false;
						}
					}
					/* we still have more letters from the word to search for downward */	
					else 
					{
						if(curr.getDown().getLet() == this.SENTINEL)
						{
							curr = curr.getDown().getRight();
						}
						else 
                        {
							curr = curr.getDown();
						}
						index ++;
						continue;
					}
				}
				// not equal, go right to keep searching
				else 
				{
					curr = curr.getRight();
					continue;
				}
				
			}
		}while(index < key.length());
		return false;
	}
    public boolean containsPrefix(String pre)
    {
        int index = 0;
        DLBNode curr = root;

        do
		{
			if(curr == null)
			{
				return false;
			}
			else
			{
				if(pre.charAt(index) == curr.getLet())
				{
					if(pre.length()-1 == index)
					{
						// we found the string, time to break and check if a sentinel is after it.
						if(curr.getDown().getLet() == this.SENTINEL)
						{
							if(curr.getDown().getRight() != null)
							{
								return true;
							}
							else
							{
								return false;
							}
						}
						else
						{
							return true;
						}
					}
					/* we still have more letters from the word to search for downward */	
					else 
					{
						if(curr.getDown().getLet() == this.SENTINEL)
						{
							if(curr.getDown().getRight() != null)
							{
								curr=curr.getDown().getRight();
							}
						}
						else 
                        {
							curr = curr.getDown();
						}
						index ++;
						continue;
					}
				}
				// not equal, go right to keep searching
				else 
				{
					curr = curr.getRight();
					continue;
				}
				
			}
		}while(index < pre.length());

		return false;
	}
    public int searchByChar(char next)
    {
        currentSearch += next;
        boolean isContained = this.contains(currentSearch);
		boolean isPrefix = this.containsPrefix(currentSearch);
        

        DLBNode curr = root;
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
        //going back to the root
        currentSearch = "";
        suggestedWords.clear();
        

    }
    private DLBNode getCurrentSearchStartNode()
	{
	int index = 0;
	DLBNode curr = root;

	do
	{
		if(currentSearch == null || currentSearch == "")
		{
			break;
		}
		else
		{
			if(currentSearch.charAt(index) == curr.getLet())
			{
				if(currentSearch.length()-1 == index)
				{
						break;
				}
				/* we still have more letters from the word to search for downward */	
				else 
				{
					if(curr.getDown().getLet() == this.SENTINEL)
					{
						if(curr.getDown().getRight() != null)
						{
							curr=curr.getDown().getRight();
						}
					}
					else 
                    {
						curr = curr.getDown();
					}
					index ++;
					continue;
				}
			}
			// not equal, go right to keep searching
			else 
			{
				curr = curr.getRight();
				continue;
			}
			
		}
	}while(curr!=null && (index < currentSearch.length()));
	return curr;
}
    public ArrayList<String> suggest() //???
    {
        suggestedWords.clear();
		DLBNode curr = getCurrentSearchStartNode();
		recursiveSuggest(curr,currentSearch);
		return suggestedWords;


        //if containsPrefix() traverse through everything under and options

    }
    public void recursiveSuggest(DLBNode top, String prefix)
    {
        if(top == null) return;
		
		DLBNode curr = top.getDown();
				
		do 
        {
			if(curr.getLet() == this.SENTINEL) 
            {
				suggestedWords.add(prefix);
			}
			else 
            {
				recursiveSuggest(curr, prefix+curr.getLet());
			}
			curr = curr.getRight();
		}while((curr!= null)&&(suggestedWords.size()<5));

    }
 
    public ArrayList<String> traverse()
    {
        DLBNode curr = root;
        do
        {
            recursiveTraverse(curr, String.valueOf(curr.getLet()));
            curr=curr.getRight();
            
        }while(curr!=null);
        
        
        
        return allwords;

        //logic issue how to traverse

    }
    public void recursiveTraverse(DLBNode top, String prefix)
    {
        if (top==null) return;
        DLBNode current = top.getDown();

        do
        {
            if(current.getLet()==this.SENTINEL)
            {
                allwords.add(prefix);
            }
            else
            {
                recursiveTraverse(current, prefix+current.getLet());
            }
            current = current.getRight();

        }while(current!=null);
            
        
    }
   

    public int count()
    {

        return size;

    }


   

}