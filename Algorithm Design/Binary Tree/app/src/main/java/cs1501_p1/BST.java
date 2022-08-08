package cs1501_p1;

public class BST <T extends Comparable<T>> implements BST_Inter<T>
{
     BTNode<T> root;

     public BST(){}

     public void put(T key)
     {
        BTNode<T> curr;
        BTNode<T> newnode = new BTNode<T>(key);
		if(root==null)
		{
			root = newnode;
            root.setLeft(null);
            root.setRight(null);
		}

		else
		{
			curr = root;
			while(curr != null)
			{
				if(curr.getKey().compareTo(key)>0)
				{
					if(curr.getLeft()==null)
                    {
                        curr.setLeft(newnode);
                         break;
                    }
                    

					else curr = curr.getLeft();
					

				}
				else if(curr.getKey().compareTo(key)<0)
				{
					if(curr.getRight()==null)
					{
						curr.setRight(newnode);
						break;

					}
					else
					{
						curr=curr.getRight();
					}
					
				}
				else
				{
					System.out.println("Key already present");
					break;
					
				}	
			}
		}
		

     }
    
     public boolean contains(T key)
     {
         BTNode<T> curr = root;
         boolean found = false;
         while (curr != null)
         {
             if (curr.getKey().compareTo(key)>0) curr = curr.getLeft();
             else if (curr.getKey().compareTo(key)<0) curr = curr.getRight();
             else return true;
         }
         return found;
     }

     public void delete(T key)
     {
         BTNode<T> curr = root;
         BTNode<T> fordelete = null;

         if(root==null)
         {
            System.out.println("No node to be deleted");
         }
         else if (root.getKey().compareTo(key)==0)
         {
        	 T minKey = minValue(root.getRight());
             BTNode<T> replacementNode = new BTNode<T>(minKey);
    		 delete(minKey);
           	 replacementNode.setLeft(root.getLeft());
             replacementNode.setRight(root.getRight());
             root = replacementNode; 
         }
         else
         {
             while(curr != null)
             {
                 if(curr.getKey().compareTo(key)>0)
                 {
                     if(curr.getLeft().getKey().compareTo(key)==0)
                     {
                        fordelete = curr.getLeft();
                        break;
                     }
                     else if (curr.getRight().getKey().compareTo(key)==0)
                     {
                         fordelete = curr.getRight();
                         break;
                     }
                     else curr = curr.getLeft();
                    

                 }
                 else if (curr.getKey().compareTo(key)<0)
                 {
                     if(curr.getRight().getKey().compareTo(key)==0)
                     {
                         fordelete = curr.getRight();
                         break;
                     }
                     else if (curr.getLeft().getKey().compareTo(key)>0)
                     {
                         fordelete = curr.getLeft();
                         break;
                     }
                     else curr = curr.getRight();
                 }
                 else break;
             }
             
             if(fordelete == null)
             {
            	System.out.println("Key to be deleted:" + key.toString()+" not found");;
             }
             else 
             {
              	deletecases(curr, fordelete, key);            	 
             }

         }
     }

     public void deletecases(BTNode<T> curr, BTNode<T> fordelete, T key)
     {
     
  
     // if fordelete is a leaf
     if(fordelete.getLeft()== null && fordelete.getRight()==null)
     {
         if(curr.getLeft()==fordelete)
         {
             curr.setLeft(null);
         }
         else if (curr.getRight()== fordelete)
         {
             curr.setRight(null);
         }
     }
     // if fordelete only has right node
     else if(fordelete.getRight()!= null && fordelete.getLeft()==null)
     {
         if(curr.getLeft()==fordelete)
         {
             curr.setLeft(fordelete.getRight());
         }
         else if (curr.getRight()== fordelete)
         {
             curr.setRight(fordelete.getRight());
         }

         //curr.setRight(fordelete.getRight());
         //fordelete.setRight(null);
     }
     // if fordelete only has left node
     else if(fordelete.getLeft()!= null && fordelete.getRight()==null)
     {
         if(curr.getLeft()==fordelete)
         {
             curr.setLeft(fordelete.getLeft());
         }
         else if (curr.getRight()== fordelete)
         {
             curr.setRight(fordelete.getLeft());
         }

         //curr.setLeft(fordelete.getLeft());
         //fordelete.setLeft(null);
     }
     // if fordelete has both left and right nodes
     else 
     {
         T minKey = minValue(fordelete.getRight());
         BTNode<T> replacementNode = new BTNode<T>(minKey);
         delete(minKey);    		 
         if(curr.getLeft() == fordelete)
         {
             curr.setLeft(replacementNode);
         }
         else if(curr.getRight()==fordelete)
         {
             curr.setRight(replacementNode);
         }
                      
            replacementNode.setLeft(fordelete.getLeft());
         replacementNode.setRight(fordelete.getRight());
          
     }
 }

 private T minValue(BTNode<T> root)
 {
     T minv = root.getKey();
     while (root.getLeft() != null)
     {
         minv = root.getLeft().getKey();
         root = root.getLeft();
     }
     return minv;
 }



     public int height()
     {
         return counter(root,0);
     }

     public int counter(BTNode<T> curr, int level)
     {
         if (curr == null)
	            return level;
	        level++;
	 
	        return Math.max(counter(curr.getLeft(), level),
	                        counter(curr.getRight(), level));
 	    }	 

     public boolean isBalanced()
     {
        int lefth = counter(root.getLeft(),0);
        int righth = counter(root.getRight(),0);

        if (Math.abs(lefth-righth)<=1) return true;
        else return false;

        
     }

     public String serialize()
     {
        return recursiveserial(root, "");  
     }

     public String recursiveserial(BTNode<T> curr, String traverse)
     {
         String suffix = "";
 
         if(curr == root)
         {
             traverse += "R(" + root.getKey() + ")";
             if(root.getLeft() == null && root.getRight() == null); // if root is also a leaf
             else if(root.getLeft() == null)
                 traverse += ",X(NULL)";
             else if(root.getRight() == null)
                 suffix = ",X(NULL)";
         }
         
         else if(curr.getLeft() != null && curr.getRight() != null)
         {
             traverse += "I(" + curr.getKey() + ")";
         }
 
         else if(curr.getLeft() == null && curr.getRight() != null)
         {
             traverse += "I(" + curr.getKey() + "),X(NULL)";
         }
         else if(curr.getLeft() != null && curr.getRight() == null)
         {	
             
             traverse += "I(" + curr.getKey() + ")";
         }
         else if(curr.getLeft() == null && curr.getRight() == null)
         {
 
             traverse += "L(" + curr.getKey() + ")";
          
         }
         if(curr.getLeft() != null)
             traverse = recursiveserial(curr.getLeft(), traverse + ",");
         if(curr.getRight() != null)
              traverse = recursiveserial(curr.getRight(), traverse + ",");
          
         return traverse + suffix;
     }

     public String inOrderTraversal()
     {
         String temp= recursiveIOT(root,"");
         String correctedString = temp.substring(0, temp.length()-1);
         return correctedString;
     }

     public String recursiveIOT(BTNode<T> curr, String traverse)
     {
         if(curr.getLeft()!=null) traverse = recursiveIOT(curr.getLeft(), traverse);
         traverse += curr.getKey()+":";
         if(curr.getRight()!= null) traverse = recursiveIOT(curr.getRight(), traverse);
         return traverse;
         
     }

      public BST_Inter<T> reverse()
     {
        root = recursiveCopy(root);
        return this;
     }

     public BTNode<T> recursiveCopy(BTNode<T> curr) 
     {
        BTNode<T> left = null;
        BTNode<T> right = null;
        if (curr.getLeft() != null) {
            left = recursiveCopy(curr.getLeft());
        }
        if (curr.getRight() != null) {
            right = recursiveCopy(curr.getRight());
        }
        BTNode<T> newNode = new BTNode<T>(curr.getKey());
        newNode.setLeft(right);
        newNode.setRight(left);
        return newNode;
    }
}
