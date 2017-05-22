package org.rcaexplore.constraint;

import java.util.*;


import org.rcaexplore.algo.multicontext.ExploMultiFCA;
import org.rcaexplore.context.ObjectObjectContext;
import org.rcaexplore.scaling.ScalingOperator;


public class CheckEqualityOperators {

	private HashMap<String, ArrayList<ObjectObjectContext>> constraintOOContext;
	private String msgError = "";
	
	public CheckEqualityOperators(){
		constraintOOContext = new HashMap<String, ArrayList<ObjectObjectContext>>();
	};
		
	
	public String getMsgError() {
		return msgError;
	}

	public void setMsgError(String msgError) {
		this.msgError += msgError;
	}


	private boolean putOne(String key, ObjectObjectContext value) {    
	    if (constraintOOContext.containsKey(key)) {    
	    	constraintOOContext.get(key).add(value);    
	      return true;    
	    } else {    
	      ArrayList<ObjectObjectContext> values = new ArrayList<>();    
	      values.add(value);    
	      constraintOOContext.put(key, values);    
	      return false;    
	    }    
	  }
	  	  
	
	public  HashMap<String, ArrayList<ObjectObjectContext>> constructHashMap(ArrayList<ObjectObjectContext> OOContexts)
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
		return constraintOOContext;
	}
			
	public ArrayList<ObjectObjectContext>  getListObjectObjectContexts (HashMap<String, ArrayList<ObjectObjectContext>> constraintOOContext, ObjectObjectContext c){
		
		ArrayList<ObjectObjectContext> listObjectObjectContexts = new ArrayList<>();
		for (ArrayList<ObjectObjectContext> listOOContext : constraintOOContext.values())
		{
			if (listOOContext.contains(c))
			{
				listObjectObjectContexts =  listOOContext;
			}
		}
		return listObjectObjectContexts;
	}
	
	public  boolean  changeScalingOperator(ExploMultiFCA model, ObjectObjectContext c)
	{
		boolean changed = false;
        HashMap<String, ArrayList<ObjectObjectContext>> constraintOOContext  =  constructHashMap(model.getCurrentConfig().getSelectedOOContexts());
        ArrayList<ObjectObjectContext> listObjectObjectContexts = getListObjectObjectContexts(constraintOOContext, c) ;
        for (ObjectObjectContext ooContext : listObjectObjectContexts)
        {		            	
        	ArrayList<String> scalingToRemove = new ArrayList<String>();
        	changed = true;
        	if (!ooContext.equals(c))
        	{
        		for (ScalingOperator scaling : model.getCurrentConfig().getScalingOperators(ooContext))
        		{		            			
        			scalingToRemove.add(scaling.getName());
        			
        		}
        		for (String s : scalingToRemove)
        		{
        			model.getCurrentConfig().removeScalingOperator(ooContext, s);
        			System.out.println(s + " removed from " + ooContext.getRelationName());
        		}
        		for (ScalingOperator scaling : model.getCurrentConfig().getScalingOperators(c))
        		{
        			model.getCurrentConfig().addScalingOperator(ooContext, scaling);
        			System.out.println(scaling.getName() + " add to " + ooContext.getRelationName());
        		}
        	}
        }		            		           
		return changed ; 
	}
	
	public boolean checkEqualityOperatorsOOContexts (HashMap<String, ArrayList<ObjectObjectContext>> constraintOOContext, ExploMultiFCA model )
	{
		boolean equal= true;

		for (ArrayList<ObjectObjectContext> listOOContext : constraintOOContext.values()) 
        {	
			ListeDistinctScalingOperators listeDistinctScalingOperators = new ListeDistinctScalingOperators();
			ArrayList<ArrayList<ScalingOperator>> lstDistinctScalinOp;
			lstDistinctScalinOp = listeDistinctScalingOperators.listDistinctScalingOperator(listOOContext, model);
			if (lstDistinctScalinOp.size()>1)
			{
				equal = false;
				setMsgError(listeDistinctScalingOperators.getRelationNames()+ "must have same operators \n");
			}			
		}
		return equal;
	}

	
	/*
	public void checkChangeScalingOperator(HashMap<String, ArrayList<ObjectObjectContext>> constraintOOContext, ObjectObjectContext c, ExploMultiFCA model){
		
		String msg = "Operators : \n";
		
		ArrayList<ScalingOperator> scalingOperator = model.getCurrentConfig().getScalingOperators(c);
		
		for (ScalingOperator scaling : scalingOperator)
		{
			msg += "\t" + scaling.getName() + ", "; 
		}
		
		msg +="\n will be applied to the relations : \n";
		
		for (ArrayList<ObjectObjectContext> listOOContext : constraintOOContext.values())
		{
			if (listOOContext.contains(c))
			{
				for (ObjectObjectContext ooContext : listOOContext)
				{
					if (!(ooContext.equals(c)));
					{
						msg+="\t" + ooContext.getRelationName()+", ";
					}
				}
			}
		}
		msg +="\nWould you like to continue ?";
		JOptionPane jop = new JOptionPane();
		int option = jop.showConfirmDialog(null, msg,
											"Information", 
											JOptionPane.YES_NO_OPTION, 
											JOptionPane.QUESTION_MESSAGE);
		
	}*/
	
	
}
