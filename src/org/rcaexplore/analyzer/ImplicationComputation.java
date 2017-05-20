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
package org.rcaexplore.analyzer;
import java.util.ArrayList;

import org.rcaexplore.conceptorder.generic.GenericConcept;
import org.rcaexplore.conceptorder.structure.IConceptOrder;

public abstract class ImplicationComputation {

	public ImplicationComputation(String string, IConceptOrder<GenericConcept> conceptOrder) {
		
		this.premisse=string;
		this.cPoset=conceptOrder;
		
	}
	
	private ArrayList<ImplicationRule> rules;
	private String premisse;
	private IConceptOrder<GenericConcept> cPoset;

	public ArrayList<ImplicationRule> getRules() {
		return rules;
	}

	public void setRules(ArrayList<ImplicationRule> rules) {
		this.rules = rules;
	}
	public String getPremisse() {
		return premisse;
	}

	public void setPremisse(String premisse) {
		this.premisse = premisse;
	}

	public IConceptOrder<GenericConcept> getcPoset() {
		return cPoset;
	}

	public void setcPoset(IConceptOrder<GenericConcept> cPoset) {
		this.cPoset = cPoset;
	}

	public abstract void computeImplications();

}
