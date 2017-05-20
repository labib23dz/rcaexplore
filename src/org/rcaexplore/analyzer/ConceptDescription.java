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
import java.util.Hashtable;

import org.rcaexplore.conceptorder.generic.GenericConcept;

/**a concept description is a set of formal or relational attribute. The description of a relational attribute also contains a concept description*/
public class ConceptDescription {
	private GenericConcept initialConcept;
	
	private int support;
	private ArrayList<FormalAttributeDescription> formalAttributes;
	private ArrayList<RelationalAttributeDescription> relationalAttributes;

	
	public ConceptDescription(GenericConcept c){
		setInitialConcept(c);
		setFormalAttributes(new ArrayList<FormalAttributeDescription>());
		setRelationalAttributes(new ArrayList<RelationalAttributeDescription>());
		setSupport(c.getExtent().size());
	}

	public GenericConcept getInitialConcept() {
		return initialConcept;
	}

	public void setInitialConcept(GenericConcept initialConcept) {
		this.initialConcept = initialConcept;
	}

	public ArrayList<FormalAttributeDescription> getFormalAttributes() {
		return formalAttributes;
	}

	public void setFormalAttributes(ArrayList<FormalAttributeDescription> formalAttributes) {
		this.formalAttributes = formalAttributes;
	}

	public ArrayList<RelationalAttributeDescription> getRelationalAttributes() {
		return relationalAttributes;
	}

	public void setRelationalAttributes(ArrayList<RelationalAttributeDescription> relationalAttributes) {
		this.relationalAttributes = relationalAttributes;
	}

	public void computeDescription(ConceptDescriptor c) {
		c.describeConcept(this);
		
	}

	public String stringDescription(Hashtable<String, Hashtable<String, String>> idInterpretation) {
		String result="";
		for (FormalAttributeDescription formalAtt : formalAttributes)
		{
			result+= formalAtt.stringDescription(idInterpretation)+" ";
		}
		
		for (RelationalAttributeDescription relationalAtt : relationalAttributes)
		{
			result+=relationalAtt.stringDescription(idInterpretation)+" ";
		}
		if (!result.equals(""))
		return result.substring(0, result.length()-1);
		else
			return result;
	}

	public int getSupport() {
		return support;
	}

	public void setSupport(int support) {
		this.support = support;
	}
	
	
}
