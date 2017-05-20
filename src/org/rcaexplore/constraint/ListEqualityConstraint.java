package org.rcaexplore.constraint;
	
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

/**Class singleton to retrieve the relationships 
 * of the "equality" constraint and save them in a HashMap */

public class ListEqualityConstraint{    
		
	private static HashMap<String, ArrayList<String>> lstConstraint = new HashMap<String, ArrayList<String>>(); ;
	
	private ListEqualityConstraint(){};	
	
	/** Single pre-initialized instance  */
	private static ListEqualityConstraint instance = new ListEqualityConstraint();
	
	
	/**The only static method to recover the single instance*/
    public static ListEqualityConstraint getInstance() {
         return ListEqualityConstraint.instance;
    }
	    
	public HashMap<String, ArrayList<String>> getLstConstraint(){
        return lstConstraint;
    }
	
	/**    
	   * Looks for a list that is mapped to the given key. If there is not one then a new one is created    
	   * mapped and has the value added to it.    
	   *     
	   * @param key    
	   * @param value    
	   * @return true if the list has already been created, false if a new list is created.    
	   */    
	  public boolean putOne(String key, String value) {    
	    if (lstConstraint.containsKey(key)) {    
	    	lstConstraint.get(key).add(value);    
	      return true;    
	    } else {    
	    	ArrayList<String> values = new ArrayList<>();    
	      values.add(value);    
	      lstConstraint.put(key, values);    
	      return false;    
	    }    
	  }
	  
	  public boolean checkFrequence()
	  {
		    ArrayList<String> lstRelation = new ArrayList<String>();
			for(ArrayList<String> value : lstConstraint.values())
			{	
				lstRelation.addAll(value);
									
			}
			int frequence=0; 
			String relation="";
			for (String relationName : lstRelation)
			{
				frequence = Collections.frequency(lstRelation, relationName);				
				if (frequence>1) 
				{
					relation = relationName; 
					break;
				}
										
			}
			if (frequence<=1)
			{
				//System.out.println(lstRelation);
				return true;
			}
				
			else 
			{
				lstConstraint.clear();
				ShowDialog showDialog = new ShowDialog("The relation " + relation + " appears " +  frequence + " times in your constraint list.", "Error",0);
				showDialog.showMessageDialog();
				return false;
			}
	  }
	  
	  
	  public  String toString()
	  {
		  String s="";
		  for(Entry<String, ArrayList<String>> entry : lstConstraint.entrySet()) 
		  {
			    String cle = entry.getKey();
			    ArrayList<String> valeur = entry.getValue();
			    s+=  cle + " : " + valeur + "\n";
		  }	
		 return s;		  
	  }
	  
	}    

