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
package org.rcaexplore.conceptorder.generic;

import org.rcaexplore.context.Attribute;

public class GenericRelationalAttribute extends Attribute {

	private String scaling;
	private String relation;
	private GenericConcept concept;
	
	public String getConceptOrderName(){
		return getConcept().getConceptOrder().getName();
	}
	
	
	public String getScaling() {
		return scaling;
	}

	public void setScaling(String scaling) {
		this.scaling = scaling;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public GenericConcept getConcept() {
		return concept;
	}

	public void setConcept(GenericConcept concept) {
		this.concept = concept;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return scaling+" "+relation+"("+concept+")";
		
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof GenericRelationalAttribute 
				&& ((GenericRelationalAttribute) o).relation.equals(relation) 
				&& ((GenericRelationalAttribute) o).concept==concept
				&& ((GenericRelationalAttribute) o).scaling.equals(scaling)
				;
		
	}

}
