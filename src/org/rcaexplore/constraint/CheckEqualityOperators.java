package org.rcaexplore.constraint;

import java.util.*;

import javax.swing.JList;
import javax.swing.JOptionPane;

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
	  	  
	
	public  HashMap<String, ArrayList<ObjectObjectContext>> constructHashMap(HashMap<String, ArrayList<String>> lstConstraint, ArrayList<ObjectObjectContext> OOContexts)
	{
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

}
