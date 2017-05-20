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

package org.rcaexplore.context;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.rcaexplore.algo.Algorithm;


import cern.colt.bitvector.BitVector;


/**
 * Formal context or object-attribute context.
 * with RCA the number of Attributes can be variable while the number of entities remains
 * constant during the whole process.
 * That's why the data structure has been defined to first add the objects and then the attributes
 * When starting to add the attributes the number of objects should not change.
 * 
 * */
//TODO the inability to change the number of objects, even at high cost, is a problem
public class ObjectAttributeContext implements Cloneable{
	
	/**list of the attributes of the context. Order is important as it is the order used by the bitvector for intent of the concepts*/
	private ArrayList<Attribute> attributes;
	
	
	/**Default algorithm that should be applied on this context to generate concepts, can be overriden at runtime by user*/
	private Algorithm defaultAlgo;
	
	private int defaultAlgoParameter;
	
	/**Description of the context*/
	private String description;
	
	/**list of the entities of the context. Order is important as it is the order used by the bitvector for extents of the concepts*/
	private ArrayList<Entity> entities;
	
	/**name of the context*/
	private String name;
	
	/**define the relation of the context, from attributes to the entities*/
	private LinkedHashMap<Attribute,BitVector> reverseRelation;
	
	public ObjectAttributeContext() {
		this.attributes = new ArrayList<Attribute>();
		this.entities = new ArrayList<Entity>();
		this.reverseRelation = new LinkedHashMap<Attribute, BitVector>();
		defaultAlgo=Algorithm.getDefaultAlgo();
		description="";
	}

	public ObjectAttributeContext(String name){
		this();
		this.name=name;
	}

	public void addAttribute(Attribute a) {
		this.attributes.add(a);
	}

	public void addAttributeAndExtent(Attribute a, BitVector extent){
		this.attributes.add(a);
		reverseRelation.put(a, extent);
	}

	public void addEntity(Entity e) {
		this.entities.add(e);
	}
	
	public void addPair(Entity e,Attribute a) {
				
		if ( !reverseRelation.containsKey(a) )
			reverseRelation.put(a,new BitVector(entities.size()));
		reverseRelation.get(a).set(entities.indexOf(e));
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public ObjectAttributeContext clone() throws CloneNotSupportedException {
		ObjectAttributeContext result=(ObjectAttributeContext) super.clone();
		result.name=this.name;
		result.attributes=(ArrayList<Attribute>) attributes.clone();
		result.entities= (ArrayList<Entity>) entities.clone();
		
		result.reverseRelation = new LinkedHashMap<Attribute, BitVector>();
		for (Attribute att : reverseRelation.keySet())
		{
			result.reverseRelation.put(att, reverseRelation.get(att).copy());
		}
		
		return result;
	}
	
	public int getAttributeNb() {
		return attributes.size();
	}
	
	public ArrayList<Attribute> getAttributes() {
		return attributes;
	}
	
	public BitVector getAttributes(Entity e) {
		int entityIndex = attributes.indexOf(e);
		
		BitVector result= new BitVector(attributes.size());
		
		
		int i=0;
		for (BitVector attVector : reverseRelation.values()){
			result.put(i, attVector.get(entityIndex));
			i++;
		}
		return result;
		
	}
	
	/**
	 * @return the defaultAlgo, is never null
	 */
	public Algorithm getDefaultAlgo() {
		return defaultAlgo;
	}
	
	public String getDescription() {
		return description;
	}
	
	
	public ArrayList<Entity> getEntities() {
		return entities;
	}
	
	public BitVector getEntities(Attribute a) {
		if ( reverseRelation.containsKey(a) )
			return reverseRelation.get(a);
		else
			return new BitVector(getEntityNb());
	}
	
	public Entity getEntity(String name2) {
		for (Entity e : entities){
			if (e.getName().equals(name2))
				return e;
		}
		return null;
	}
	
	public int getEntityNb() {
		return entities.size();
	}
	
	public String getName(){
		return name;
	}
	
	public int getPairNb() {
		int p = 0;
		for( BitVector ent: reverseRelation.values() ) p += ent.cardinality();
		return p;
	}
	
	public LinkedHashMap<Attribute, BitVector> getReverseRelation() {
		return reverseRelation;
	}
	
	public boolean hasPair(Entity e,Attribute a) {
		return this.getEntities(a).get(entities.indexOf(e));
	}
	/**
	 * @param defaultAlgo the defaultAlgo to set
	 */
	public void setDefaultAlgo(Algorithm defaultAlgo) {
		this.defaultAlgo = defaultAlgo;
		if (defaultAlgo.hasParameter())
			defaultAlgoParameter = defaultAlgo.getDefaultParameterValue();
		else
			defaultAlgoParameter = -1;
	}

	public void setDefaultAlgo(Algorithm defaultAlgo, int parameter){
		this.defaultAlgo = defaultAlgo;
		this.defaultAlgoParameter = parameter;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return this.getClass().getSimpleName() + " with " + getEntityNb() +
		" entities, " + getAttributeNb() + " attributes and " +
		getPairNb() + " pairs in the binary relation.";
	}
	
	
	public int getDefaultAlgoParameter(){
			return defaultAlgoParameter;
	}
	
}
