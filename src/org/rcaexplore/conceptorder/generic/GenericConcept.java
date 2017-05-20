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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.rcaexplore.conceptorder.structure.IConcept;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.Entity;

public class GenericConcept implements IConcept {
	protected static int NB=0;
	private HashSet<GenericConcept> children;
	private GenericConceptOrder conceptOrder;
	
	private ArrayList<Entity> extent;
	private ArrayList<Attribute> intent;
	private String name;
	private int id;
	
	private HashSet<GenericConcept> parents;


	private ArrayList<Entity> simplifiedExtent;


	private ArrayList<Attribute> simplifiedIntent;


	public GenericConcept() {
		super();
		name="";
		intent=new ArrayList<Attribute>();
		extent=new ArrayList<Entity>();
		parents=new HashSet<GenericConcept>();
		children=new HashSet<GenericConcept>();
		id=NB;
		NB++;
	}



	public void addChild(GenericConcept c){
		children.add(c);
		c.parents.add(this);
	}
	
	
	
	public void addChildren(Collection<GenericConcept> c){
		children.addAll(c);
		for (GenericConcept child : c)
			child.parents.add(this);
	}


	public void addParent(GenericConcept c){
		parents.add(c);
		c.children.add(this);
	}
	public void addParents(Collection<GenericConcept> c){
		parents.addAll(c);
		for (GenericConcept p : c)
			p.children.add(this);
	}
	
	public HashSet<GenericConcept> getAllChildren(){
		HashSet<GenericConcept> result=new HashSet<GenericConcept>();
		result.addAll(children);
		for (GenericConcept c : children){
			result.addAll(c.getAllChildren());
		}
		return result;
	}
	
	public HashSet<GenericConcept> getAllParents(){
		HashSet<GenericConcept> result=new HashSet<GenericConcept>();
		result.addAll(parents);
		for (GenericConcept p : parents){
			result.addAll(p.getAllParents());
		}
		return result;
	}
	public Set<GenericConcept> getChildren() {
		return children;
	}
	public GenericConceptOrder getConceptOrder() {
		return conceptOrder;
	}
	
	public ArrayList<Entity> getExtent() {
		return extent;
	}
	
	public ArrayList<Attribute> getIntent() {
		return intent;
	}
	
	public String getName() {
		return name;
	}
	
	public Set<GenericConcept> getParents() {
		return parents;
	}
	
	public ArrayList<Entity> getSimplifiedExtent() {
		if (simplifiedExtent == null){
			simplifiedExtent =new ArrayList<Entity>();
			for (Entity e : extent){
				boolean simplified=true;
				for (GenericConcept c : children){
					if (c.getExtent().contains(e)){
						simplified=false;
						break;
					}
				}
				if (simplified)
					simplifiedExtent.add(e);
				
			}
		}
		return simplifiedExtent;
	}
	
	public ArrayList<Attribute> getSimplifiedIntent() {
		if (simplifiedIntent == null){
			simplifiedIntent =new ArrayList<Attribute>();
			for (Attribute a : intent){
				boolean simplified=true;
				for (GenericConcept p : parents){
					if (p.getIntent().contains(a)){
						simplified=false;
						break;
					}
				}
				if (simplified)
					simplifiedIntent.add(a);
				
			}
		}
			return simplifiedIntent;
	}
	
	public void removeChild(GenericConcept c ){
		children.remove(c);
		c.parents.remove(this);
	}
	
	public void removeChildren(Collection<GenericConcept> c){
		children.removeAll(c);
		for (GenericConcept child : c)
			child.parents.remove(this);
	}
	
	public void removeParent(GenericConcept c){
		parents.remove(c);
		c.children.remove(this);
	}
	
	
	public void removeParents(Collection<GenericConcept> c){
		parents.removeAll(c);
		for (GenericConcept p :  c)
			p.children.remove(this);
	}
	
	public void setConceptOrder(GenericConceptOrder conceptOrder) {
		this.conceptOrder = conceptOrder;
	}



	public void setName(String id) {
		this.name = id;
	}


	public int getId(){
		return id;
	}

	public String toString(){
		return name;
		
	}
	
}
