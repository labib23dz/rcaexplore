/*
 * Copyright (c) 2013, ENGEES. All rights reserved.
 * This file is part of RCAExplore.
 * 
 *  RCAExplore is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  RCAExplore is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with RCAExplore.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  Authors : 
 *  - Xavier Dolques
 */

package org.rcaexplore;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.rcaexplore.ExplorationPath.LoadedStep;
import org.rcaexplore.algo.Algorithm;
import org.rcaexplore.context.ObjectAttributeContext;
import org.rcaexplore.context.ObjectObjectContext;
import org.rcaexplore.context.RelationalContextFamily;
import org.rcaexplore.scaling.AvailableScalingOperators;
import org.rcaexplore.scaling.ScalingOperator;

public class StepConfiguration {

	private Hashtable<ObjectAttributeContext, Algorithm> algo;
	private Hashtable<ObjectAttributeContext, Integer> algoParameter;
	private ArrayList<ObjectAttributeContext> oaContexts;
	private ArrayList<ObjectAttributeContext> previouslySelectedOAContexts;

	private boolean halt = false;
	//private StepConfiguration previousStep;
	private int stepNumber;
	private RelationalContextFamily initialRCF;
	private ArrayList<ObjectObjectContext> ooContexts;
	private Hashtable<ObjectObjectContext, ArrayList<ScalingOperator>> scaling;

	public StepConfiguration() {
		oaContexts = new ArrayList<>();
		ooContexts = new ArrayList<>();
		previouslySelectedOAContexts= new ArrayList<>();
		
		scaling = new Hashtable<>();
		algo = new Hashtable<>();
		algoParameter = new Hashtable<>();
	}

	public StepConfiguration(LoadedStep s, RelationalContextFamily rcf) {
		this();
		this.initialRCF = rcf;
		initStepConfiguration(s,null);
	}

	public StepConfiguration(RelationalContextFamily rcf) {
		this(null, rcf);

	}

	public StepConfiguration(StepConfiguration previousStep) {
		this(null, previousStep);
	}

	public StepConfiguration(LoadedStep s, StepConfiguration previousStep) {
		this();
		//this.previousStep = previousStep;
		this.initialRCF = previousStep.initialRCF;

		initStepConfiguration(s, previousStep);

	}

	@SuppressWarnings("unchecked")
	public void initStepConfiguration(LoadedStep loadedStep, StepConfiguration previousStep) {
		//set step number
		if (previousStep!=null)
			stepNumber=previousStep.stepNumber+1;
		else
			stepNumber=0;
		//set previouslySelectedOAContext
		if (previousStep!=null)
			previouslySelectedOAContexts.addAll(previousStep.getSelectedOAContexts());
		// select formal contexts
		if (loadedStep == null) {
			
			if (previousStep == null)
				oaContexts.addAll(initialRCF.getOAContexts());
			else
				oaContexts.addAll(previousStep.oaContexts);

		} else { //loaded step exists

			for (ObjectAttributeContext c : initialRCF.getOAContexts())
				if (loadedStep.containsOAContext(c.getName()))
					oaContexts.add(c);

		}
		// select algos for formal contexts
		for (ObjectAttributeContext oac : oaContexts)
			if (loadedStep != null) {
					//&& Algorithm.getAlgo(
					//		s.oacontexts.get(fc.getName()).getName()) != null )//TODO recheck Algorithm always exist if not null
				algo.put(oac, loadedStep.oacontextsAlgorithm.get(oac.getName()));
				if (loadedStep.oacontextsAlgoParameter.containsKey(oac.getName()))
					algoParameter.put(oac, loadedStep.oacontextsAlgoParameter.get(oac.getName()));
			} else if (previousStep != null) {
				algo.put(oac, previousStep.algo.get(oac));
				if (previousStep.algoParameter.containsKey(oac))
					algoParameter.put(oac, previousStep.algoParameter.get(oac));
			//else if (Algorithm.getAlgo(
			//		oac.getDefaultAlgo().getName()) != null)
			//	algo.put(oac, oac.getDefaultAlgo());//TODO recheck
			} else {
				algo.put(oac, oac.getDefaultAlgo());
				if (oac.getDefaultAlgo().hasParameter()) {
					algoParameter.put(oac, oac.getDefaultAlgoParameter());
				}
			}

		// deal with relational contexts, only after the first step
		if (previousStep != null) {
			// select relational contexts
			if (loadedStep == null) {
				
				// first stepconfiguration after the initialisation, all the
				// allowed oo contexts must be enabled
				if (stepNumber == 1) {
					for (ObjectObjectContext rc : initialRCF.getOOContexts()) {
						if (previousStep.oaContexts.contains(rc
								.getTargetContext())
								&& oaContexts.contains(rc.getSourceContext())) {
							ooContexts.add(rc);
						}
					}
				} else {
					
					for (ObjectObjectContext rc : previousStep.ooContexts) {
						if (previousStep.oaContexts.contains(rc
								.getTargetContext())) {
							ooContexts.add(rc);
						}
					}
				}
				
			} else {
				for (ObjectObjectContext c : initialRCF.getOOContexts())
					if (loadedStep.containsOOContext(c.getRelationName()))
						ooContexts.add(c);
			}

			// select scaling operators TODO check if every test is useful
			if (loadedStep == null) {
				if (stepNumber == 1) {
					for (ObjectObjectContext rc : ooContexts) {
						ArrayList<ScalingOperator> addScaling = new ArrayList<>();
						if (AvailableScalingOperators.getAvailableScaling()
								.containsKey(rc.getOperator().getName()))
							addScaling.add(rc.getOperator());
						else
							addScaling.add(AvailableScalingOperators.defaultScalingOperator());
						scaling.put(rc, addScaling);
					}
				} else
					for (ObjectObjectContext rc : ooContexts) {
						scaling.put(rc,
								(ArrayList<ScalingOperator>) previousStep.scaling
										.get(rc).clone());
					}
			} else {
				for (ObjectObjectContext rc : ooContexts) {
					scaling.put(rc, loadedStep.oocontextsScalings.get(rc.getRelationName()));
				}
			}
		}
	}

	/**
	 * Adds an Object-Attribute context to the selection, assign it its default algorithm or fca
	 * if it does not exist
	 */
	public void addOAContext(ObjectAttributeContext c) {
		oaContexts.add(c);
		algo.put(c, c.getDefaultAlgo());
		if (c.getDefaultAlgo().hasParameter())
			algoParameter.put(c, c.getDefaultAlgoParameter());
	}

	/**
	 * Add a object-object context to the selection and assign to it a scaling operator. The
	 * default one defined in the rcf or existential otherwise
	 */
	public void addOOContext(ObjectObjectContext rc) {
		ArrayList<ScalingOperator> addScaling = new ArrayList<>();
		if (AvailableScalingOperators.getAvailableScaling().containsKey(
				rc.getOperator().getName()))
			addScaling.add(rc.getOperator());
		else
			addScaling.add(AvailableScalingOperators.defaultScalingOperator());
		scaling.put(rc, addScaling);
		ooContexts.add(rc);
	}

	/**Add a scaling operator to an object-object context*/
	public void addScalingOperator(ObjectObjectContext chosenRC, ScalingOperator op) {
		getScalingOperators(chosenRC).add(op);

	}

	/**Set the construction algorithm for an object-attribute context */ //TODO check case of no parameter etc.
	public void chooseConstructionAlgo(ObjectAttributeContext c, Algorithm constructionAlgo) {
		algo.put(c, constructionAlgo);
	}

	public void chooseConstructionAlgo(ObjectAttributeContext c, Algorithm constructionAlgo, int algoParameterValue) {
		this.algo.put(c, constructionAlgo);
		this.algoParameter.put(c, algoParameterValue);
	}
	
	/**Display the current configuration in the standard output*/
	public void displayCurrentConfig() {
		System.out.println("Current configuration: ");
		System.out.println("\t object-attribute contexts: ");
		for (ObjectAttributeContext c : oaContexts) {
			System.out.println("\t\t" + c.getName() + " (" + algo.get(c) + ")");
		}
		System.out.println("\t object-object contexts: ");
		for (ObjectObjectContext rc : ooContexts) {
			System.out.println("\t\t" + rc.getRelationName() + " ( "
					+ rc.getSourceContext().getName() + "->"
					+ rc.getTargetContext().getName() + " , " + scaling.get(rc)
					+ " ) ");
		}
	}
	
	/**Returns the algorithm applied on the given object-attribute context*/
	public Algorithm getAlgo(ObjectAttributeContext bc) {
		return algo.get(bc);
	}
	
	/** Returns the selected oa contexts during the previous step */
	public ArrayList<ObjectAttributeContext> getPreviouslySelectedOAContexts() {
		return previouslySelectedOAContexts;
	}

	/**Returns the previous step configuration, if it exists, or null otherwise*/
	/*public StepConfiguration getPreviousStep() {
		return previousStep;
	}*/
	/**Returns the scaling operators associated to object-object contexts*/
	public ArrayList<ScalingOperator> getScalingOperators(ObjectObjectContext rc) {
		if (!scaling.containsKey(rc))
			scaling.put(rc, new ArrayList<ScalingOperator>());
		return scaling.get(rc);
	}

	/**Returns the selected object-attributes contexts*/
	public ArrayList<ObjectAttributeContext> getSelectedOAContexts() {
		return oaContexts;
	}
	
	/**Returns the selected object-object contexts*/
	public ArrayList<ObjectObjectContext> getSelectedOOContexts() {
		return ooContexts;
	}

	/**Returns the value of the stopping state*/
	public boolean halt() {
		return halt;
	}

	/**Remove an Object-attribute context from the selection*/
	public void removeOAContext(ObjectAttributeContext c) {
		oaContexts.remove(c);
		algo.remove(c);

		for (Iterator<ObjectObjectContext> it = ooContexts.iterator(); it
				.hasNext();) {
			ObjectObjectContext rc = it.next();
			if (rc.getSourceContext() == c) {
				scaling.remove(rc);
				it.remove();
			}
		}
	}

	/**Remove an Object-object context from the selection*/
	public void removeOOContext(ObjectObjectContext toRemove) {
		ooContexts.remove(toRemove);
		scaling.remove(toRemove);
	}

	/**Remove a scaling operator to an object-object context*/
	public void removeScalingOperator(ObjectObjectContext chosenOOC,
			String operator) {
		scaling.get(chosenOOC).remove(AvailableScalingOperators.getAvailableScaling().get(operator));
	}

	/**Returns the current step number*/
	public int stepNumber() {
		return stepNumber;
	}

	/**
	 * Returns the value of the algo parameter for a given object attribute context
	 * */
	public Integer getAlgoParameter(ObjectAttributeContext oac) {
		if (algoParameter.containsKey(oac))
			return algoParameter.get(oac);
		else
			return null;
	}

}
