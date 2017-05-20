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
 *  - Jean-Rémy Falleri
 *  - Xavier Dolques
 *  
 *  this file contains code covered by the following terms:
 *  Copyright 2009 Jean-Rémy Falleri
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.rcaexplore.conceptorder.optimized;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.rcaexplore.algo.Algorithm;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.ObjectAttributeContext;
import org.rcaexplore.context.Entity;

import cern.colt.bitvector.BitVector;

public class OptimizedConceptOrder implements  org.rcaexplore.conceptorder.structure.IConceptOrder<OptimizedConcept> {

	private int algoParameter;
	protected ArrayList<OptimizedConcept> concepts;
	private ArrayList<String> consideredRelations;
	private Algorithm constructionAlgorithm;
	protected ObjectAttributeContext context;
	private HashMap<String, ArrayList<String>> scalingOperators;
	
	public OptimizedConceptOrder() {
		this.concepts = new ArrayList<OptimizedConcept>();
		consideredRelations = new ArrayList<String>();
		scalingOperators = new HashMap<String, ArrayList<String>>();
		constructionAlgorithm=Algorithm.getDefaultAlgo();
		
	}
	
	public OptimizedConceptOrder(ObjectAttributeContext context) {
		this();
		this.concepts = new ArrayList<OptimizedConcept>();
		this.context=context;
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


	
	public int entityFusionNb() {
		int i = 0;
		for ( OptimizedConcept c: this ) if ( c.isEntityFusion() ) i++;
		return i;
	}
	
	public int entityNb() {
		int i = 0;
		for ( OptimizedConcept c: this ) if ( c.isEntity() ) i++;
		return i;
	}
	
	
	public int getAlgoParameter() {
		return algoParameter;
	}

	@Override
	public int getAttributeNB() {
		
		return getContext().getAttributeNb();
		
	}
	
	public ArrayList<Attribute> getAttributes(){
		return getContext().getAttributes();
	}
	
	public int getConceptNb() {
		return getConcepts().size();
	}
	
	public ArrayList<OptimizedConcept> getConcepts() {
		return concepts;
	}

	@Override
	public Algorithm getConstructionAlgorithm() {
		return constructionAlgorithm;
	}
	
	public ObjectAttributeContext getContext(){
		return context;
	}
	
	
	public ArrayList<Entity> getEntities() {
		return getContext().getEntities();
	}

	@Override
	public int getEntityNb() {
		return getContext().getEntityNb();
	}

	public String getName(){
		return getContext().getName();
	}
	public ArrayList<OptimizedConcept> getParents(OptimizedConcept c){
		ArrayList<OptimizedConcept> result=new ArrayList<OptimizedConcept>();
		for (OptimizedConcept current : concepts)
		{
			if (current.getChildren().contains(c))
				result.add(current);
		}
		return result;
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
	public Set<Attribute> getSimplifiedIntent(OptimizedConcept self) {
		Set<Attribute> simplifiedIntent = new HashSet<Attribute>();
		
		ArrayList<OptimizedConcept> parents = getParents(self);
		for( Attribute c: self.getIntent() ) {
			boolean leafAttribute = true;
			for(OptimizedConcept parent: parents )
				if( parent.getIntent().contains(c))
					leafAttribute = false;
			if ( leafAttribute )
				simplifiedIntent.add(c);
		}
		return simplifiedIntent;
	}
	@Override
	public Iterator<OptimizedConcept> iterator() {
		return concepts.iterator();
	}

	public int newEntityNb() {
		int i = 0;
		for ( OptimizedConcept c: this ) if ( c.isNewEntity() ) i++;
		return i;
	}

	/** rename concepts according to the extents already generated*/
	public void renameConcepts(TreeMap<BitVector,Integer> extents){
		
		@SuppressWarnings("unchecked")
		TreeMap<BitVector,Integer> sortedPreviousExtents=(TreeMap<BitVector, Integer>) extents.clone();
		
		@SuppressWarnings("unchecked")
		ArrayList<OptimizedConcept> sortedConcepts=(ArrayList<OptimizedConcept>) concepts.clone();
		
		Collections.sort(sortedConcepts, new CompareOptimizedConcepts2());
		
		Iterator<Entry<BitVector, Integer>> previousExtentsIterator = sortedPreviousExtents.entrySet().iterator();
		Iterator<OptimizedConcept> conceptIterator = sortedConcepts.iterator();
		OptimizedConcept c=null;
		Entry<BitVector,Integer> e=null;
		CompareExtents compExtent=new CompareExtents();
		
		while(conceptIterator.hasNext() || previousExtentsIterator.hasNext()) {
			if (c==null && conceptIterator.hasNext())
				c=conceptIterator.next();
			if (e==null && previousExtentsIterator.hasNext())
				e=previousExtentsIterator.next();
			
			if (c==null) {
				break;
			} else if (e==null || compExtent.compare(c.getBitExtent(), e.getKey())<0 ) {
				extents.put((BitVector) c.getBitExtent().clone(), extents.size());
				c.setName("Concept_"+getContext().getName()+"_"+(extents.size()-1));
				if (conceptIterator.hasNext())
					c=null;
				else
					break;
			} else if (compExtent.compare(c.getBitExtent(), e.getKey())>0 ) {
				if (previousExtentsIterator.hasNext())
					e=null;
				else {
					extents.put((BitVector) c.getBitExtent().clone(), extents.size());
					c.setName("Concept_"+getContext().getName()+"_"+(extents.size()-1));
				 	c=null;
				}
			} else if (compExtent.compare(c.getBitExtent(), e.getKey())==0 ) {
				c.setName("Concept_"+getContext().getName()+"_"+e.getValue());
				if (conceptIterator.hasNext())
					c=null;
				else 
					break;
				if (previousExtentsIterator.hasNext())
					e=null;
			}
		} 
	
	}

	public void setAlgoParameter(int algoParameter) {
		this.algoParameter = algoParameter;
	}

	@Override
	public void setConstructionAlgorithm(Algorithm algo) {
		constructionAlgorithm=algo;
		
	}

	@Override
	public void setConstructionAlgorithm(Algorithm algo, int parameter) {
		constructionAlgorithm=algo;
		setAlgoParameter(parameter);
		
	}

	public int size() {
		return this.concepts.size();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " with " 
			+ size() + " concepts. " + entityNb() + " entity concepts, " 
			+ entityFusionNb() + " entity fusion concepts and "
			+ newEntityNb() + " new entity concepts.";
	}
	
}
