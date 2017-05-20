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
package org.rcaexplore.cofbrowser.view;
import org.rcaexplore.conceptorder.generic.GenericConcept;
import org.rcaexplore.conceptorder.generic.GenericRelationalAttribute;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.BinaryAttribute;

public class ConceptAnalyzer {

	public static String analyzeRelationalAttribute(GenericRelationalAttribute relAtt, int currentDepth, int depthMax){
		String result="";
		for (int i=0;i<currentDepth;i++)
			result+="\t";
		result+=relAtt.getScaling()+" "+relAtt.getRelation()+" [\n";
		result+=analyzeConcept(relAtt.getConcept(), currentDepth+1,depthMax);
		result+="\n";
		for (int i=0;i<currentDepth;i++)
			result+="\t";
		result+="]";
		return result;
	}

	private static String analyzeConcept(GenericConcept concept, int currentDepth,
			int depthMax) {
		String result="";
		if (currentDepth>=depthMax){
			for (int i=0;i<currentDepth;i++)
				result+="\t";
			result+=concept.getName();
		}
		else {
			for (int i=0;i<currentDepth;i++)
				result+="\t";
			result+=concept.getName()+":\n";
			for (Attribute att : concept.getIntent()){
				if (att instanceof GenericRelationalAttribute) {
					result+=analyzeRelationalAttribute((GenericRelationalAttribute) att, currentDepth, depthMax)+"\n";
				}else if (att instanceof BinaryAttribute){
					for (int i=0;i<currentDepth;i++)
						result+="\t";
					result+= ((BinaryAttribute) att).getValue();
				}
			}
		}
		return result;
	}

}
