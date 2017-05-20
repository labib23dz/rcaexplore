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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.rcaexplore.conceptorder.structure.IConcept;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.Entity;

import cern.colt.bitvector.BitVector;

public class OptimizedConcept implements IConcept{
	protected static int NB=0;
	
	public static OptimizedConcept pickOne(Set<OptimizedConcept> concepts) {
		if ( concepts.isEmpty() )
			return null;
		
		Iterator<OptimizedConcept> cIt = concepts.iterator();
		OptimizedConcept tmp = cIt.next();
		cIt.remove();
		return tmp;
	}

	private Set<OptimizedConcept> children;
	
	//private final ObjectAttributeContext context;
	private OptimizedConceptOrder conceptOrder;

	private BitVector extent;

	private int hash=0;
	
	private int id;

	private String name;
	
	private Set<OptimizedConcept> parents;

	private LinkedList<Attribute> simplifiedIntent;
	
	private Set<OptimizedConcept> simplifiedParents;

	public OptimizedConcept(OptimizedConceptOrder conceptOrder) {
		this.conceptOrder=conceptOrder;
		this.parents = new HashSet<OptimizedConcept>(3);
		this.children = new HashSet<OptimizedConcept>(3);
		this.simplifiedIntent=new LinkedList<>();
		this.extent = new BitVector(conceptOrder.getEntityNb());
		this.name="Concept "+NB;
		id=NB;
		NB++;
	}

	public void addChild(OptimizedConcept c) {
		this.children.add(c);
		c.parents.add(this);
	}
	
	
	public void addParent(OptimizedConcept c) {
		this.parents.add(c);
		c.children.add(this);
	}


	public boolean equals(Object o) {
		if ( ! (o instanceof OptimizedConcept) )
			return false;
		else {
			OptimizedConcept c = (OptimizedConcept) o;
			boolean extEq = this.extent.equals(c.extent);
			boolean intEq = this.simplifiedIntent.equals(c.simplifiedIntent);
			return extEq && intEq;
		}
	}

	
		
	/**returns all the children of the current concept. This relation is reflexive so the current concept is included in its children*/
	public Set<OptimizedConcept> getAllChildren() {
		Set<OptimizedConcept> result = new HashSet<OptimizedConcept>();
		Set<OptimizedConcept> currentTemp;
		Set<OptimizedConcept> nextTemp =new HashSet<OptimizedConcept>();
		nextTemp.add(this);
		while ( nextTemp.size() > 0 ) {
			currentTemp = nextTemp;
			nextTemp=new HashSet<OptimizedConcept>();
			for (OptimizedConcept concept : currentTemp)
			{
				if ( !result.contains(concept)) {
					result.add(concept);
					nextTemp.addAll(concept.getChildren());
					}
			}
		}
		return result;
	}

	/**returns all the parents of the current concept. This relation is reflexive so the current concept is included in its parents*/
	public Set<OptimizedConcept> getAllParents() {
		Set<OptimizedConcept> result = new HashSet<OptimizedConcept>();
		Set<OptimizedConcept> temp = new HashSet<OptimizedConcept>();
		temp.add(this);
		while ( temp.size() > 0 ) {
			OptimizedConcept concept = pickOne(temp);
			if ( !result.contains(concept)) {
				result.add(concept);
				temp.addAll(concept.getParents());
			}
		}
		return result;
	}

	public final BitVector getBitExtent() {
		return extent;
	}
	
	public BitVector getBitSimplifiedExtent() {
		BitVector simplifiedExtent = new BitVector(conceptOrder.getEntityNb());
		simplifiedExtent.or(getBitExtent());
		for(OptimizedConcept child: this.children )
			simplifiedExtent.andNot(child.getBitExtent());
		return simplifiedExtent;
	}
	
	public Set<OptimizedConcept> getChildren() {
		return children;
	}
	
	
	
	public ArrayList<Entity> getExtent(){
		ArrayList<Entity> result=new ArrayList<Entity>();
		int entityNb=conceptOrder.getEntityNb();
		for(int i=0; i<entityNb; i++) 
		 { 
			if (extent.get(i))
			  result.add(conceptOrder.getEntities().get(i));
		 } 
		return result;
		
	}

	public int getId(){
		return id;
	}
	
	public ArrayList<Attribute> getIntent(){
		/*ArrayList<Attribute> result=new ArrayList<Attribute>();
		for (OptimizedConcept c: getAllParents()){
			result.addAll(c.simplifiedIntent);
		}
		
		return result;
			*/
		Set<OptimizedConcept> allSimplifiedParents = new HashSet<OptimizedConcept>();
		Set<OptimizedConcept> temp = new HashSet<OptimizedConcept>();
		Set<Attribute> resultSet= new HashSet<>();
		temp.add(this);
		while ( temp.size() > 0 ) {
			OptimizedConcept concept = pickOne(temp);
			if ( !allSimplifiedParents.contains(concept)) {
				allSimplifiedParents.add(concept);
				resultSet.addAll(concept.getSimplifiedIntent());
				temp.addAll(concept.getSimplifiedParents());
			}
		}
		ArrayList<Attribute> result=new ArrayList<Attribute>(resultSet);
		return result;
		
	}
	
	public String getName() {
		return name;
	}
	public Set<OptimizedConcept> getParents() {
		return parents;
	}
	
	public ArrayList<Entity> getSimplifiedExtent(){
		BitVector bitSimplifiedExtent=getBitSimplifiedExtent();
		ArrayList<Entity> result=new ArrayList<Entity>();
		
		for(int i=0; i<conceptOrder.getEntityNb(); i++) 
		 { 
			if (bitSimplifiedExtent.get(i))
			  result.add(conceptOrder.getEntities().get(i));
		 } 
		return result;
	}
	
	public LinkedList<Attribute> getSimplifiedIntent() {
		return simplifiedIntent;
		
	}
	
	public Set<OptimizedConcept> getSimplifiedParents(){
		if (simplifiedParents==null) {
			simplifiedParents=new HashSet<OptimizedConcept>();
			for(OptimizedConcept c:getParents())
			{
				if (!c.getSimplifiedIntent().isEmpty())
					simplifiedParents.add(c);
				else
					simplifiedParents.addAll(c.getSimplifiedParents());
			}
		}
		return simplifiedParents;
		
		
	}
	
	
	public boolean hasChild(OptimizedConcept c) {
		for (OptimizedConcept child : getChildren())
			if (child.isGreaterThan(c))
				return true;
		return false;
	}

	public int hashCode() {
		if (hash==0)
			hash=this.simplifiedIntent.hashCode() + this.extent.hashCode();
		return hash;
	}

	public boolean hasParent(OptimizedConcept c) {
		for (OptimizedConcept parent : getParents())
			if (parent.isSmallerThan(c))
				return true;
		return false;
	}

	public boolean isEntity() {
		return getSimplifiedExtent().size() == 1;
	}
	
	public boolean isEntityFusion() {
		return getSimplifiedExtent().size() > 1;
	}
	
	public boolean isGreaterThan(OptimizedConcept c) {
		BitVector inter= this.extent.copy();
		inter.and(c.extent);
		return inter.equals(c.extent);
		
		}
	
	public boolean isNewEntity() {
		return getSimplifiedExtent().size() < 1;
	}
	
	public boolean isSmallerThan(OptimizedConcept c) {
		
		BitVector inter=this.extent.copy();
		inter.and(c.extent);
		return inter.equals(this.extent);
	}
	
		
	public void removeChild(OptimizedConcept c) {
		this.children.remove(c);
		c.parents.remove(this);
	}
	
	public void removeParent(OptimizedConcept c) {
		this.parents.remove(c);
		c.children.remove(this);
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString(){
		String result;
		result=this.getName()+"\n";
		result+=this.getBitExtent()+"\n";
		for (Attribute a : this.getIntent())
		{
			result+=a.toString()+"\n";
		}
		result+="===\n";
		return result;
	}

}
