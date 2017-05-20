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
 *  - Xavier Dolques : copy of AddExtent class to add iceberg support
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


package org.rcaexplore.algo.singlecontext;

import java.util.ArrayList;
import java.util.Iterator;

import org.rcaexplore.conceptorder.optimized.OptimizedConcept;
import org.rcaexplore.conceptorder.optimized.OptimizedConceptLattice;
import org.rcaexplore.conceptorder.optimized.OptimizedConceptOrder;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.ObjectAttributeContext;

import cern.colt.bitvector.BitVector;

/** algorithm for building lattices. This algorithm is incremental on the attributes i.e. the lattice is computed by adding the attributes along with the corresponding extent
 * one by one.*/
public class IcebergAddExtent extends SingleContextAlgorithm<OptimizedConceptOrder> {
	
	private OptimizedConceptLattice lattice;
	
	private int icebergThreshold;
	
	private OptimizedConcept bottom;
	
	public IcebergAddExtent(ObjectAttributeContext context,int percentage) {
		super(context);
		icebergThreshold=context.getEntityNb()*percentage/100;
	}
	
	
	@Override
	public OptimizedConceptOrder getConceptOrder() {
		return lattice;
	}
	
	/** compute the lattice*/
	@Override
	public void compute() {
		System.out.println("computing concepts ("+context.getName()+")");
		//lattice initialization, create a link to the original context
		lattice = new OptimizedConceptLattice(context);
		
		//create a first concept, the top of the lattice and add all the objects into it
		OptimizedConcept top = new OptimizedConcept(lattice);
		top.getBitExtent().replaceFromToWith(0, entityNb/*context.getEntityNb()*/-1, true);
		lattice.getConcepts().add(top);
		lattice.setTop(top);
		
		//for each attribute, try to add it to the top  
		int i=0;
		long time1=System.currentTimeMillis()/1000;
		for(Attribute attribute: context.getAttributes()) {
			i++;
			
			System.out.println(i + "/"+context.getAttributeNb());
			OptimizedConcept concept = addExtent(context.getEntities(attribute),top,lattice);
			concept.getSimplifiedIntent().add(attribute);
			/*for(OptimizedConcept child: concept.getAllChildren() )
				child.getBitIntent().set(i-1);*/
		}
		long time2=System.currentTimeMillis()/1000;
		System.out.println("concepts computed: ("+(time2-time1)+"s )");
		System.out.println("generate lattice");
		
		
	}
	
	
	/** try to add a new attribute to a concept and all its subconcepts. By calling this method to the top it will apply the process to the whole lattice*/
	private OptimizedConcept addExtent(BitVector extent,OptimizedConcept generatorParam, OptimizedConceptLattice lattice) {		

		int extentCardinality=extent.cardinality();
		if (extentCardinality<icebergThreshold){
			extent=new BitVector(entityNb);
		}
		OptimizedConcept generator;
		
		if (extentCardinality<icebergThreshold&&bottom!=null) {
			generator=bottom;
		} else {
			generator = getSmallestContainingConcept(extent, generatorParam);
		}
		
		if ( extent.equals(generator.getBitExtent()) )
			return generator;
		
		/** otherwise*/
		ArrayList<OptimizedConcept> newChildren=new ArrayList<OptimizedConcept>();
		BitVector candidateIntersectionWithThis=new BitVector(entityNb);
		BitVector candidateIntersectionWithChild=new BitVector(entityNb);
		/**each child of the smallest concept containing the extent is considered as a candidate*/
		for( OptimizedConcept candidate: generator.getChildren() ) {
			
			/** we calculate the intersection between extent and the candidate's extent*/
			intersection(candidateIntersectionWithThis, candidate.getBitExtent(), extent);
			//if the intersection is not the extent create a concept with the intersection as extent
			if ( ! candidateIntersectionWithThis.equals(extent) ) {//needed test ? don't think so, to be tested…
				candidate = addExtent(candidateIntersectionWithThis, candidate,lattice);
			}
			boolean addChild = true;
			OptimizedConcept child;
			//check if the children already found do not conflict with the new candidate (is it a double, is the new candidate a super concept, a sub concept)
			for(Iterator<OptimizedConcept> it=newChildren.iterator(); it.hasNext();) {
				child=it.next();
				intersection(candidateIntersectionWithChild,candidate.getBitExtent(), child.getBitExtent());
				//if the existing children is more general than the one to be added, then don't add a new one
				if ( candidateIntersectionWithChild.equals(candidate.getBitExtent()) ) {
					addChild = false;
					break;
				}
				//else if the candidate is more general than the one already added, remove the already added and keep the new one
				else if ( candidateIntersectionWithChild.equals(child.getBitExtent()) )
					it.remove();
			}
			if ( addChild )
				newChildren.add(candidate);
		}

		OptimizedConcept newConcept = new OptimizedConcept(lattice);
		
		newConcept.getBitExtent().or(extent);
		
		lattice.getConcepts().add(newConcept);
		if (lattice.getConceptNb()%1000==0)
			System.out.println("             concepts : "+lattice.getConceptNb());

		for ( OptimizedConcept child: newChildren ) {
			generator.removeChild(child);
			newConcept.addChild(child);
		}

		generator.addChild(newConcept);
		
		if (newConcept.getChildren().isEmpty())
			bottom=newConcept;
	
		return newConcept;
	}

	/**compute the intersection between 2 sets of bit and put the result in the "container" parameter*/
	public final static void intersection(BitVector container, BitVector a, BitVector b){
		container.clear();
		container.or(a);
		container.and(b);
	}

	/**
	 * get a concept whose extent contains the extent passed in parameter but whose children don't
	 * */
	private OptimizedConcept getSmallestContainingConcept(BitVector extent, OptimizedConcept generator) {
		boolean isMaximal = true;
		BitVector inter=new BitVector(entityNb);
		while ( isMaximal ) {
			isMaximal = false;
			for ( OptimizedConcept child: generator.getChildren()) {
				intersection(inter, child.getBitExtent(), extent);
				if ( inter.equals(extent) ) {
					generator = child;
					isMaximal = true;
					break;
				}
			}
		}
		return generator;
	}

}
