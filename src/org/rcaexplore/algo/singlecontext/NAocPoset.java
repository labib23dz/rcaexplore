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

package org.rcaexplore.algo.singlecontext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.rcaexplore.conceptorder.optimized.CompareOptimizedConcepts;
import org.rcaexplore.conceptorder.optimized.OptimizedConcept;
import org.rcaexplore.conceptorder.optimized.OptimizedConceptOrder;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.ObjectAttributeContext;

import cern.colt.bitvector.BitVector;

/**
 * */

public class NAocPoset extends SingleContextAlgorithm<OptimizedConceptOrder> {

	/**This enumeration parameterise the aocposet algorithm to either return AOCposet
	 * or OCposet or ACposet */
	public enum Poset {
		acposet(true,false),
		ocposet(false,true),
		aocposet(true,true);
		
		/** Define if the poset has all the attribute concepts*/
		private final boolean hasAllAConcepts;
		/** Define it the poset has all the object concepts*/
		private final boolean hasAllOConcepts;
		
		private Poset(boolean hasAllAConcepts , boolean hasAllOConcept) {
			this.hasAllAConcepts = hasAllAConcepts;
			this.hasAllOConcepts = hasAllOConcept;
		}
		
		public boolean hasAllAConcepts() {
			return hasAllAConcepts;
		}
		public boolean hasAllOConcepts() {
			return hasAllOConcepts;
		}
	}
	
	
	
	public NAocPoset(ObjectAttributeContext context) {
		this(context,Poset.aocposet);
		
	}
	
	public NAocPoset(ObjectAttributeContext context, Poset posetKind) {
		super(context);
		this.posetKind=posetKind;
	}
	
	
	private final Poset posetKind;
	private OptimizedConceptOrder aocPoset;
	private ArrayList<OptimizedConcept> concepts;

	@Override
	public OptimizedConceptOrder getConceptOrder() {

		return aocPoset;
	}
	@Override
	public void compute() {
		//tops=new ArrayList<OptimizedConcept>();
		//bottoms=new ArrayList<OptimizedConcept>();
		aocPoset=new OptimizedConceptOrder(context);
		
		
		concepts=new ArrayList<OptimizedConcept>();
		if (posetKind.hasAllOConcepts()) {
			OptimizedConcept top =new OptimizedConcept(aocPoset);
			top.getBitExtent().not();
			concepts.add(top);
		}
		for (Attribute a : context.getAttributes()) 
			aares(a, aocPoset);
		aocPoset.getConcepts().addAll(concepts);
	}
	
	
	private void aares(Attribute a, OptimizedConceptOrder aocPoset) {
		HashSet<OptimizedConcept> subConceptsOfA=new HashSet<OptimizedConcept>();
		HashSet<OptimizedConcept> nonIntroducingConcepts=new HashSet<OptimizedConcept>();
		HashSet<OptimizedConcept> doNotCheck=new HashSet<OptimizedConcept>();
		
		/* properEntitiesOfA aims at being the list of proper Entities of the attribute a
		 * it starts with all the entities having a as attributes
		 * */
		BitVector properEntitiesOfA=context.getEntities(a).copy();
		
				
		boolean isCADefined=false;
		OptimizedConcept ca=null;
		/* conceptList is a linear extension of the already created concepts from more specific to more general*/
		ArrayList<OptimizedConcept> conceptList=new ArrayList<OptimizedConcept>();
		Collections.sort(concepts, new CompareOptimizedConcepts());
		conceptList.addAll(concepts);
		for (OptimizedConcept c : conceptList) {
			
			if (!nonIntroducingConcepts.contains(c) && 
					!doNotCheck.contains(c) && 
					(isIntersect(c.getBitExtent(), context.getEntities(a)) || 
							(c.getBitExtent().cardinality()==0 || context.getEntities(a).cardinality()==0))) {
				
				BitVector ec= intersection(c.getBitSimplifiedExtent(), context.getEntities(a));
				BitVector rc= substract(c.getBitExtent(), context.getEntities(a));
				BitVector ra= substract(context.getEntities(a), c.getBitExtent());
				BitVector cc= intersection(c.getBitExtent(),context.getEntities(a) );
				
				if (rc.cardinality()==0 && rc.equals(ra)){//case 1 extent of c is g(a)
					//c.getBitIntent().set(context.getAttributes().indexOf(a));
					c.getSimplifiedIntent().add(a);
					Collections.sort(conceptList, new CompareOptimizedConcepts());
					return;
				} else if (rc.cardinality()==0 ) {//case 2 c subconcept of g(a)
					subConceptsOfA.add(c);
					//c.getBitIntent().set(context.getAttributes().indexOf(a));
					properEntitiesOfA.andNot(c.getBitSimplifiedExtent());
					
				} else if (ra.cardinality()==0 ) {//case 3 c superconcept of g(a) 
					if (!isCADefined){
						ca=new OptimizedConcept(aocPoset);
						concepts.add(ca);
						isCADefined=true;
						//ca.getBitIntent().set(context.getAttributes().indexOf(a));
						ca.getSimplifiedIntent().add(a);
						ca.getBitExtent().or(context.getEntities(a));
						BitVector extsC = c.getBitSimplifiedExtent().copy();
						if (ec.cardinality()!=0){
							extsC.andNot(context.getEntities(a));
							if ( (c.getSimplifiedIntent().size()==0 || !posetKind.hasAllAConcepts())		
									&& (extsC.cardinality()==0 || !posetKind.hasAllOConcepts()))
								nonIntroducingConcepts.add(c);
						}
						for (OptimizedConcept maxSub: max(subConceptsOfA)){
							ca.addChild(maxSub);
							maxSub.addParent(ca);
							maxSub.removeParent(c);
						}
							
					}
					ca.addParent(c);
					//ca.getBitIntent().or(c.getBitIntent());
					c.addChild(ca);
					for ( OptimizedConcept child: ca.getChildren())
						c.removeChild(child);

					doNotCheck.addAll(c.getAllParents());
					
				} else if (ec.cardinality()!=0 && posetKind.hasAllOConcepts()) {//case 4 c and g(a) not comparable
					OptimizedConcept c2=new OptimizedConcept(aocPoset);
					concepts.add(c2);
					BitVector extsC2 = ec.copy();
					BitVector extsC = c.getBitSimplifiedExtent().copy();
					c2.getBitExtent().or(cc);
					//TODO
					//c2.getBitIntent().or(c.getBitIntent());
					//c2.getBitIntent().set(context.getAttributes().indexOf(a));
					c2.addParent(c);
					HashSet<OptimizedConcept> interC_SCA=new HashSet<OptimizedConcept>();
					for(OptimizedConcept child : c.getAllChildren())
						if (subConceptsOfA.contains(child))
							interC_SCA.add(child);
					for (OptimizedConcept c3 : max(interC_SCA))
					{
						c3.addParent(c2);
						c3.removeParent(c);
					}
					c.addChild(c2);
					subConceptsOfA.add(c2);
					for (OptimizedConcept c2Child :c2.getChildren())
						c.removeChild(c2Child);
					extsC.andNot(extsC2);
					if ( (c.getSimplifiedIntent().size()==0 || !posetKind.hasAllAConcepts())
							&& extsC.cardinality()==0)
						nonIntroducingConcepts.add(c);
					properEntitiesOfA.andNot(c2.getBitSimplifiedExtent());
				}
			}
		}
		
		if ( !isCADefined && 
				(posetKind.hasAllAConcepts() || properEntitiesOfA.cardinality()!=0)//if oc poset then the concept must have proper entities
				){
			ca=new OptimizedConcept(aocPoset);
			
			//ca.getBitIntent().set(context.getAttributes().indexOf(a));
			ca.getSimplifiedIntent().add(a);
			ca.getBitExtent().or(context.getEntities(a));
			for (OptimizedConcept maxSub: max(subConceptsOfA)){
				ca.addChild(maxSub);
				maxSub.addParent(ca);
			}
			concepts.add(ca);
		} else if (isCADefined && !posetKind.hasAllAConcepts() && properEntitiesOfA.cardinality()==0) {
			nonIntroducingConcepts.add(ca);
		}
				
		
		for (OptimizedConcept toRemove : nonIntroducingConcepts)
		{
			if (!toRemove.getSimplifiedIntent().isEmpty()){
				for (OptimizedConcept child: toRemove.getChildren())
					child.getSimplifiedIntent().addAll(toRemove.getSimplifiedIntent());
			}
			
			
			ArrayList<OptimizedConcept> parents=new ArrayList<OptimizedConcept>();
			ArrayList<OptimizedConcept> children=new ArrayList<OptimizedConcept>();
			parents.addAll(toRemove.getParents());
			children.addAll(toRemove.getChildren());
			for (OptimizedConcept p : parents)
				toRemove.removeParent(p);
			for (OptimizedConcept c: children)
				toRemove.removeChild(c);
			for (OptimizedConcept p : parents)
				for (OptimizedConcept c : children)
				{
					if (!p.hasChild(c))
						p.addChild(c);
				}
			
			concepts.remove(toRemove);
		}
		
	}
	
	private HashSet<OptimizedConcept> max(HashSet<OptimizedConcept> concepts) {
		HashSet<OptimizedConcept> max =new HashSet<OptimizedConcept>();
		HashSet<OptimizedConcept> notMax=new HashSet<OptimizedConcept>();
		for (OptimizedConcept c2: concepts)
			for (OptimizedConcept parent : c2.getAllParents())
				if (parent!= c2 && concepts.contains(parent))
				{
					notMax.add(c2);
					break;
				}
		max.addAll(concepts);
		max.removeAll(notMax);
		return max;
	}
	
	
	
	static BitVector substract(BitVector x, BitVector y )
	{
		BitVector copyOfX=x.copy();
		copyOfX.andNot(y);
		return copyOfX;
	}
	
	static BitVector intersection(BitVector x, BitVector y )
	{
		BitVector copyOfX=x.copy();
		copyOfX.and(y);
		return copyOfX;
	}
	
	static boolean isIntersect( BitVector x, BitVector y)
	{
		
		return intersection(x, y).cardinality()!=0;
	}
	
	
	

}
