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
 *  
 */

package org.rcaexplore.io;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import org.rcaexplore.algo.multicontext.ExploMultiFCA;
import org.rcaexplore.conceptorder.structure.ConceptOrderFamily;
import org.rcaexplore.conceptorder.structure.IConcept;
import org.rcaexplore.conceptorder.structure.IConceptOrder;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.Entity;
import org.rcaexplore.context.RelationalAttribute;

public class GenerateXMLFromConceptOrderFamilyHistory extends GenerateCode {
	
	private ArrayList<ConceptOrderFamily<? extends IConceptOrder<? extends IConcept>>> cofHistory;

	public GenerateXMLFromConceptOrderFamilyHistory(Writer buffer,ExploMultiFCA emfca){
		super(buffer);
		cofHistory=new ArrayList<>();
		cofHistory.addAll(emfca.getTransitionalConceptPosetFamilies());
	}
	
	public void generateCode() throws IOException {
		appendHeader();
		int i=0;
		for (ConceptOrderFamily<? extends IConceptOrder<? extends IConcept>> cof : cofHistory){
				appendLineWithTab("<Step nb=\""+i+"\">");
				generateXML(cof);
				appendLineWithTab("</Step >");
			i++;
		}
		appendFooter();
		buffer.flush();
		
	}
	
	private void generateXML(ConceptOrderFamily<? extends IConceptOrder<? extends IConcept>> cof) throws IOException {
		
		for (IConceptOrder<? extends IConcept> conceptOrder : cof.getConceptOrders()){
			append("\t\t<Lattice ");
			append("numberObj=\""+conceptOrder.getEntityNb()+"\" ");
			append("numberAtt=\""+conceptOrder.getAttributeNB()+"\" ");
			append("numberCpt=\""+conceptOrder.getConceptNb()+"\" >\n");
			appendLineWithTabs("<Config algo=\""+conceptOrder.getConstructionAlgorithm()+"\">",3);
			for (String relation : conceptOrder.getRelations()){
				appendLineWithTabs("<Relation name=\""+relation+"\">",4);
				for (String scaling : conceptOrder.getScalingOperators(relation)){
					appendLineWithTabs("<Scaling>"+scaling+"</Scaling>", 5);
				}
				appendLineWithTabs("</Relation>",4);
			}
			
			appendLineWithTabs("</Config>", 3);
			appendLineWithTabs("<Name>"+conceptOrder.getName()+"</Name>",3);
			
			for (Entity e : conceptOrder.getEntities())
			{
				appendLineWithTabs("<Object>"+e.getName()+"</Object>",3);
			}
			for (Attribute a  : conceptOrder.getAttributes())
			{
				if (a instanceof RelationalAttribute) {
					RelationalAttribute ra =(RelationalAttribute) a;
					append("\t\t\t<RelationalAttribute relation=\""+ra.getRelation().getRelationName()+"\" ");
					append("scaling=\""+ra.getScaling()+"\"");
					appendLine(">"+ra.getConcept().getName()+"</RelationalAttribute>");
				}
				else
				appendLineWithTabs("<Attribute>"+a.toString()+"</Attribute>",3);
			}
			
			for(IConcept c: conceptOrder.getConcepts() ) {
				appendLineWithTabs("<Concept>",3);
				appendLineWithTabs("<ID>"+c.getName()+"</ID>",4);
							
				appendLineWithTabs("<Extent>", 4);
				for(Entity ent: c.getExtent())
					appendLineWithTabs("<Object_Ref>"+ent.getName()+"</Object_Ref>", 5);
				appendLineWithTabs("</Extent>", 4);
				appendLineWithTabs("<Intent>", 4);
				for( Attribute attr: c.getIntent() ){
					if (attr instanceof RelationalAttribute) {
						RelationalAttribute ra =(RelationalAttribute) attr;
						append("\t\t\t\t\t<RelationalAttribute_Ref relation=\""+ra.getRelation().getRelationName()+"\" ");
						append("scaling=\""+ra.getScaling()+"\"");
						appendLine(">"+ra.getConcept().getName()+"</RelationalAttribute_Ref>");
					}
					else
						appendLineWithTabs("<Attribute_Ref>"+attr.toString()+"</Attribute_Ref>", 5);

				}
				appendLineWithTabs("</Intent>", 4);
				appendLineWithTabs("<UpperCovers>", 4);
				for (IConcept parent : c.getParents())
					appendLineWithTabs("<Concept_Ref>"+parent.getName()+"</Concept_Ref>", 5);
				appendLineWithTabs("</UpperCovers>", 4);	
				appendLineWithTabs("</Concept>",3);

			}
			appendLineWithTabs("</Lattice>", 2);
		}

		
	}


	private void appendHeader() throws IOException {
		appendLine("<RCAExplore_Document>");
	}

	private void appendFooter() throws IOException {
		appendLine("</RCAExplore_Document>");
		
	}

}
