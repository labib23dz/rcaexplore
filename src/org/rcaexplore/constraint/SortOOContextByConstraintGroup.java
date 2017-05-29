package org.rcaexplore.constraint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.rcaexplore.context.ObjectObjectContext;

public class SortOOContextByConstraintGroup {
	
	private static ArrayList<ObjectObjectContext> objectObjectContextsSorted = new ArrayList<>();

	
	public SortOOContextByConstraintGroup(HashMap<String, ArrayList<ObjectObjectContext>> mapKeyListOOContext, ArrayList<ObjectObjectContext> selectedOOContexts){
		
		objectObjectContextsSorted = sortObjectObjectContexts(mapKeyListOOContext, selectedOOContexts);
	}


	public static ArrayList<ObjectObjectContext> getObjectObjectContextsSorted() {
		return objectObjectContextsSorted;
	}
	
	private ArrayList<ObjectObjectContext> sortObjectObjectContexts(HashMap<String, ArrayList<ObjectObjectContext>> mapKeyListOOContext, ArrayList<ObjectObjectContext> selectedOOContexts)
	{
		if (mapKeyListOOContext.isEmpty()) return selectedOOContexts;
		
		ArrayList<ObjectObjectContext> objectObjectContextsSorted = new ArrayList<>();
		
		for(Map.Entry<String, ArrayList<ObjectObjectContext>> entry : mapKeyListOOContext.entrySet()) 
		{				
			for(ObjectObjectContext ooContext : selectedOOContexts)
			{
				if (entry.getValue().contains(ooContext))
					objectObjectContextsSorted.add(ooContext);
			}						
		}
		return objectObjectContextsSorted;
	}	
	
}
