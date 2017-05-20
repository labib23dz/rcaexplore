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

public class ImplicationRule {

	private AttributeDescription premisse;
	private ArrayList<AttributeDescription> conclusion;
	private GenericConcept c;

	public AttributeDescription getPremisse() {
		return premisse;
	}
	public void setPremisse(AttributeDescription premisse) {
		this.premisse = premisse;
	}
	public ArrayList<AttributeDescription> getConclusion() {
		if (conclusion==null)
			conclusion=new ArrayList<AttributeDescription>();
		return conclusion;
	}
	public void setConclusion(ArrayList<AttributeDescription> conclusion) {
		this.conclusion = conclusion;
	}
	
	
	public GenericConcept getC() {
		return c;
	}
	public void setC(GenericConcept c) {
		this.c = c;
	}
	public int getSupport() {
		return c.getExtent().size();
	}
	
	public String stringDescription(Hashtable<String, Hashtable<String, String>> idInterpretation){
		String conclusionString="";
		for (AttributeDescription attDesc : getConclusion()){
			conclusionString+="\t"+attDesc.stringDescription(idInterpretation);
				if (attDesc instanceof RelationalAttributeDescription)
					conclusionString+= "("+SimplifiedSpecificCloseImplicationComputation.higherConcept(getC().getConceptOrder(), ((RelationalAttributeDescription)attDesc).getRelationalAttribute()).getExtent().size()+")";
			conclusionString+="\n";
		}
		return "\n"+
				getPremisse().stringDescription(idInterpretation)+"\n"+
				"->\n"+conclusionString+
				"support: "+getSupport()+"\n\n";
				
	}
}
