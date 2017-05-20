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
package org.rcaexplore.analyzer;

import java.util.ArrayList;

import org.rcaexplore.conceptorder.generic.GenericRelationalAttribute;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.BinaryAttribute;

public class FullIntentDescriptor implements ConceptDescriptor {

	
	public FullIntentDescriptor() {}

	@Override
	public void describeConcept(ConceptDescription conceptDescription) {
		ArrayList<Attribute> att=conceptDescription.getInitialConcept().getIntent();
		
		for (Attribute a : att)
		{
			if (a instanceof GenericRelationalAttribute)
				conceptDescription.getRelationalAttributes().add(new RelationalAttributeDescription(((GenericRelationalAttribute) a), this));
			else if (a instanceof BinaryAttribute)
			{
				String value=((BinaryAttribute) a).getValue();
				
				conceptDescription.getFormalAttributes().add(new FormalAttributeDescription(value,conceptDescription.getInitialConcept().getConceptOrder().getName()));
				
			}
		}
		
		
		
		

	}

	
	

}
