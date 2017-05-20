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

import java.util.Hashtable;

import org.rcaexplore.conceptorder.generic.GenericConcept;
import org.rcaexplore.conceptorder.generic.GenericRelationalAttribute;

public class RelationalAttributeDescription extends AttributeDescription {

	
	private String relationName;
	private String quantifier;
	private GenericConcept concept;
	private ConceptDescription conceptDescription;
	private GenericRelationalAttribute relationalAttribute;

	public ConceptDescription getConceptDescription() {
		return conceptDescription;
	}

	public void setConceptDescription(ConceptDescription conceptDescription) {
		this.conceptDescription = conceptDescription;
	}

	public RelationalAttributeDescription(GenericRelationalAttribute rat, ConceptDescriptor conceptDescriptor){
		this(rat.getRelation(),rat.getScaling(),rat.getConcept(),conceptDescriptor);
		setRelationalAttribute(rat);
	}
	
	private RelationalAttributeDescription(String relationName, String  quantifier, GenericConcept concept, ConceptDescriptor conceptDescriptor)
	{
		super(concept.getConceptOrder().getName());
		this.setRelationName(relationName);
		this.setQuantifier(quantifier);
		this.setConcept(concept);
		this.conceptDescription=new ConceptDescription(concept);
		conceptDescription.computeDescription(conceptDescriptor);
	}

	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public String getQuantifier() {
		return quantifier;
	}

	public void setQuantifier(String quantifier) {
		this.quantifier = quantifier;
	}

	public GenericConcept getConcept() {
		return concept;
	}

	public void setConcept(GenericConcept concept) {
		this.concept = concept;
	}

	@Override
	public String stringDescription(Hashtable<String, Hashtable<String, String>> idInterpretation) {
		return this.getQuantifier()+"."+this.getRelationName()+"("+this.getConceptDescription().stringDescription(idInterpretation)+")";
	}

	public GenericRelationalAttribute getRelationalAttribute() {
		return relationalAttribute;
	}

	public void setRelationalAttribute(GenericRelationalAttribute relationalAttribute) {
		this.relationalAttribute = relationalAttribute;
	}
}
