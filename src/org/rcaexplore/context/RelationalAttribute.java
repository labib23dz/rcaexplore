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

package org.rcaexplore.context;

import org.rcaexplore.conceptorder.optimized.OptimizedConcept;

public final class RelationalAttribute extends Attribute {

	private final int hash;
	private final ObjectObjectContext relation;
	private final String scaling;
	private final OptimizedConcept concept;

	public ObjectObjectContext getRelation() {
		return relation;
	}
	
	public String getScaling() {
		return scaling;
	}

	public OptimizedConcept getConcept() {
		return concept;
	}

	public RelationalAttribute(OptimizedConcept c, ObjectObjectContext rc){
		this(c,rc,"");
		
	}
	
	public RelationalAttribute(OptimizedConcept c, ObjectObjectContext rc, String scaling){
		this.concept=c;
		this.relation=rc;
		this.hash=relation.hashCode()+concept.hashCode();
		this.scaling=scaling;
	}
	
	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public String toString() {
		
		return scaling+" "+relation.getRelationName()+"("+concept.getName()+")";
	}

	@Override
	public boolean equals(Object o) {
		
		return o instanceof RelationalAttribute 
				&& ((RelationalAttribute) o).relation==relation 
				&& ((RelationalAttribute) o).concept==concept
				&& ((RelationalAttribute) o).scaling.equals(scaling)
				;
	}

}
