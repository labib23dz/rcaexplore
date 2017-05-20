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

import org.rcaexplore.algo.Algorithm;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.Entity;

public interface IConceptOrder<C extends IConcept> extends Iterable<C>{

	public int getEntityNb();
	public int getAttributeNB();
	public int getConceptNb();
	public String getName();
	public ArrayList<Entity> getEntities();
	public ArrayList<Attribute> getAttributes();
	public ArrayList<C> getConcepts();
	public Algorithm getConstructionAlgorithm();
	public void setConstructionAlgorithm(Algorithm algo);
	public void setConstructionAlgorithm(Algorithm algo, int parameter);
	public ArrayList<String> getRelations();
	public ArrayList<String> getScalingOperators(String relations);
	public void addRelation(String relation, String operator);
	public int size();
}
