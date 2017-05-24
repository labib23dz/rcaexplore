package org.rcaexplore.constraint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.rcaexplore.context.ObjectObjectContext;

public class BuildMap {
	
	private static HashMap<String, ArrayList<ObjectObjectContext>> mapKeyListOOContext;

	
	public BuildMap(ArrayList<ObjectObjectContext> OOContexts){
		mapKeyListOOContext = new HashMap<String, ArrayList<ObjectObjectContext>>();
		constructHashMap(OOContexts);
	};
	
	public static HashMap<String, ArrayList<ObjectObjectContext>> getMapKeyListOOContext() {
		return mapKeyListOOContext;
	}
	
	
	private boolean putOne(String key, ObjectObjectContext value) {    
	    if (mapKeyListOOContext.containsKey(key)) {    
	    	mapKeyListOOContext.get(key).add(value);    
	      return true;    
	    } else {    
	      ArrayList<ObjectObjectContext> values = new ArrayList<>();    
	      values.add(value);    
	      mapKeyListOOContext.put(key, values);    
	      return false;    
	    }    
	  }
	
	
	private  HashMap<String, ArrayList<ObjectObjectContext>> constructHashMap(ArrayList<ObjectObjectContext> OOContexts)
	{
		HashMap<String, ArrayList<String>> lstConstraint = ListEqualityConstraint.getInstance().getLstConstraint();
		for (ObjectObjectContext ooContext:OOContexts)
		{
			for(Map.Entry<String, ArrayList<String>> entry : lstConstraint.entrySet()) 
			{				
				if (entry.getValue().contains(ooContext.getRelationName()))
				{					
					putOne(entry.getKey(), ooContext);
				}
			}
		}
		return mapKeyListOOContext;
	}	

}
