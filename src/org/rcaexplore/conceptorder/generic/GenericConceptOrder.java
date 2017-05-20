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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.rcaexplore.algo.Algorithm;
import org.rcaexplore.conceptorder.structure.IConceptOrder;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.Entity;


//TODO check if this class and generic classes in general are really used now
public class GenericConceptOrder implements IConceptOrder<GenericConcept>{
	int algoParameter;
	private ArrayList<Attribute> attributes;
	ArrayList<GenericConcept> concepts;
	private ArrayList<String> consideredRelations; 
	private Algorithm constructionAlgorithm;
	private ArrayList<Entity> entities;
	private String name;
	private HashMap<String, ArrayList<String>> scalingOperators;

	
	
	public GenericConceptOrder() {
		super();
		concepts=new ArrayList<GenericConcept>();
		name="";
		consideredRelations = new ArrayList<String>();
		scalingOperators = new HashMap<String, ArrayList<String>>();
		constructionAlgorithm=Algorithm.getDefaultAlgo();
	}

	@Override
	public void addRelation(String relation, String operator) {
		if (!consideredRelations.contains(relation)){
			consideredRelations.add(relation);
		}
		if (!getScalingOperators(relation).contains(operator)){
			getScalingOperators(relation).add(operator);
		}
	}

	@Override
	public int getAttributeNB() {

		return getAttributes().size();
	}

	@Override
	public ArrayList<Attribute> getAttributes() {
		if (attributes==null)
			attributes=new ArrayList<Attribute>();
		
		return attributes;
	}

	@Override
	public int getConceptNb() {
		
		return getConcepts().size();
	}

	public ArrayList<GenericConcept> getConcepts() {
		return concepts;
	}

	@Override
	public Algorithm getConstructionAlgorithm() {
		return constructionAlgorithm;
	}

	@Override
	public ArrayList<Entity> getEntities() {
		if (entities==null)
			entities=new ArrayList<Entity>();
		return entities;
	}

	@Override
	public int getEntityNb() {
		return getEntities().size();
	}

	public String getName() {
		return name;
	}

	@Override
	public ArrayList<String> getRelations() {
		if (consideredRelations == null){
			consideredRelations=new ArrayList<String>();
		}
		return consideredRelations;
	}

	@Override
	public ArrayList<String> getScalingOperators(String relation) {
		if (!scalingOperators.containsKey(relation)){
			scalingOperators.put(relation, new ArrayList<String>());
		}
		return scalingOperators.get(relation);
	}

	@Override
	public void setConstructionAlgorithm(Algorithm algo) {
		constructionAlgorithm=algo;
		
	}

	@Override
	public void setConstructionAlgorithm(Algorithm algo, int parameter) {
		constructionAlgorithm=algo;
		algoParameter=parameter;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int size() {
		return concepts.size();
	}

	@Override
	public Iterator<GenericConcept> iterator() {
		return concepts.iterator();
	}
}
