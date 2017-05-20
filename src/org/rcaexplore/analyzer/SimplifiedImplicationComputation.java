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
import java.util.Collections;

import org.rcaexplore.conceptorder.generic.GenericConcept;
import org.rcaexplore.conceptorder.generic.GenericRelationalAttribute;
import org.rcaexplore.conceptorder.structure.IConceptOrder;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.BinaryAttribute;

public class SimplifiedImplicationComputation extends ImplicationComputation{
	public SimplifiedImplicationComputation(String string, IConceptOrder<GenericConcept> conceptOrder) {
		
		super(string, conceptOrder);
		
	}
public void computeImplications() {
		
		setRules(new ArrayList<ImplicationRule>());
		for (GenericConcept c : getcPoset().getConcepts())
		{
			if (c.getExtent().size()!=0)
				for (Attribute att : c.getSimplifiedIntent())
				{
					if (att instanceof GenericRelationalAttribute)
					{
						if (((GenericRelationalAttribute)att).getConcept().getConceptOrder().getName().equals(getPremisse()))
						{
							ImplicationRule ir=new ImplicationRule();
							ir.setC(c);
							
							ir.setPremisse(new RelationalAttributeDescription((GenericRelationalAttribute) att, new FullIntentDescriptor()));
							for (Attribute att2 : c.getIntent()) 
							{
								if (att2!=att)
								{
									if (att2 instanceof GenericRelationalAttribute && ! ((GenericRelationalAttribute)att2).getConceptOrderName().equals(getPremisse()))
										ir.getConclusion().add(new RelationalAttributeDescription((GenericRelationalAttribute) att2,new FullIntentDescriptor()));
									else if (att2 instanceof BinaryAttribute)
										ir.getConclusion().add(new FormalAttributeDescription(((BinaryAttribute) att2).getValue(), c.getConceptOrder().getName()));
								}
							}
							if (!ir.getConclusion().isEmpty())
								getRules().add(ir);
						}
					}
				}
		}
		Collections.sort(getRules(), new ImplicationRuleSupportComparator());
	}
}