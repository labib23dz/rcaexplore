package org.rcaexplore.constraint;

import java.util.ArrayList;

import org.rcaexplore.algo.multicontext.ExploMultiFCA;
import org.rcaexplore.context.ObjectObjectContext;
import org.rcaexplore.scaling.ScalingOperator;

/**
  This class returns the list of operators used by the set of 
  relational contexts that are related by an equality constraint.
 **/
public class ListeDistinctScalingOperators {
	
	private String relationNames = "Relations : ";
	private ArrayList<ArrayList<ScalingOperator>> listDistinctScalinOperator= new ArrayList<ArrayList<ScalingOperator>>() ;	

	public ListeDistinctScalingOperators() {
		
	}
	
	public String getRelationNames() {
		return relationNames;
	}

	public void setRelationNames(String relationName) {
		this.relationNames += relationName+", ";
	}

	
	public ArrayList<ArrayList<ScalingOperator>> listDistinctScalingOperator(ArrayList<ObjectObjectContext> listOOContext, ExploMultiFCA model)
	{
		for (ObjectObjectContext ooContext : listOOContext)
		{			
			setRelationNames(ooContext.getRelationName());
			
			if (!(this.listDistinctScalinOperator.contains(model.getCurrentConfig().getScalingOperators(ooContext))))
			{
				this.listDistinctScalinOperator.add(model.getCurrentConfig().getScalingOperators(ooContext));
			}				
		}		
		return this.listDistinctScalinOperator;
	}
	
}
