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

package org.rcaexplore.conceptorder.structure;

import java.util.ArrayList;


/**
 * A concept order family is a family of concept hierarchies (lattices but not limited to it) that are possibly interrelated
 * 
 * */
public class ConceptOrderFamily<CO extends IConceptOrder<?>> {
	protected ArrayList<CO> conceptOrders;
	private int stepNb;
	public ConceptOrderFamily() {
		super();
		conceptOrders=new ArrayList<CO>();
	}

	public boolean addConceptOrder(CO e) {
		return conceptOrders.add(e);
	}

	public ArrayList<CO> getConceptOrders() {
		return conceptOrders;
	}
	/**
	 * This information is valuable for the classical RCA as the concept generation is monotonous
	 * */
	public int totalConceptNb(){
		int result=0;
		for (CO co: conceptOrders){
			result+=co.size();
		}
		return result;
		
	}
	public int getStepNb() {
		return stepNb;
	}

	public void setStepNb(int stepNb) {
		this.stepNb = stepNb;
	}
	
	public CO getConceptOrder(String string) {
		for (CO c : conceptOrders)
			if (c.getName().equals(string))
				return c;
		return null;
	}
	
	
}
