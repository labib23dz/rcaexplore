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
 *  - Xavier Dolques
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
package org.rcaexplore.io;

import java.io.FileWriter;
import java.io.IOException;

import org.rcaexplore.conceptorder.optimized.OptimizedConceptOrder;
import org.rcaexplore.conceptorder.structure.IConcept;
import org.rcaexplore.conceptorder.structure.IConceptOrder;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.Entity;


/**
 * Generates the dot code corresponding to a concept lattice.
 * @author Jean-R�my Falleri
 */
public class GenerateDot extends GenerateCode {
	
	private IConceptOrder<?> conceptOrder;
	
	private String newConceptColor = "lightblue";
	
	private String fusionConceptColor = "orange";
	
	private String normalConceptColor = "yellow";
	
	private String conceptNamePrefix = "C";
	
	private boolean displayConceptNumber = true;

	private boolean useSimplifiedIntent = true;

	private boolean useSimplifiedExtent = true;
	
	private boolean displayIntent = true;
	
	private boolean displayExtent = true;

	private boolean useColor = false;
	
	
	public GenerateDot(FileWriter buffer,OptimizedConceptOrder lattice) {
		super(buffer);
		this.conceptOrder = lattice;
	}
	public GenerateDot(FileWriter buffer,OptimizedConceptOrder lattice, boolean fullIntentExtent) {
		this(buffer,lattice);
		useSimplifiedIntent=!fullIntentExtent;
		useSimplifiedExtent=!fullIntentExtent;
	}
	public String getNewConceptColor() {
		return newConceptColor;
	}

	public void setNewConceptColor(String newConceptColor) {
		this.newConceptColor = newConceptColor;
	}

	public String getFusionConceptColor() {
		return fusionConceptColor;
	}

	public void setFusionConceptColor(String fusionConceptColor) {
		this.fusionConceptColor = fusionConceptColor;
	}

	public String getNormalConceptColor() {
		return normalConceptColor;
	}

	public void setNormalConceptColor(String normalConceptColor) {
		this.normalConceptColor = normalConceptColor;
	}

	public String getConceptNamePrefix() {
		return conceptNamePrefix;
	}

	public void setConceptNamePrefix(String conceptNamePrefix) {
		this.conceptNamePrefix = conceptNamePrefix;
	}

	public boolean isDisplayConceptNumber() {
		return displayConceptNumber;
	}

	public void setDisplayConceptNumber(boolean displayConceptNumber) {
		this.displayConceptNumber = displayConceptNumber;
	}

	public boolean isUseSimplifiedIntent() {
		return useSimplifiedIntent;
	}

	public void setUseSimplifiedIntent(boolean useSimplifiedIntent) {
		this.useSimplifiedIntent = useSimplifiedIntent;
	}

	public boolean isUseSimplifiedExtent() {
		return useSimplifiedExtent;
	}

	public void setUseSimplifiedExtent(boolean useSimplifiedExtent) {
		this.useSimplifiedExtent = useSimplifiedExtent;
	}

	public boolean isDisplayIntent() {
		return displayIntent;
	}

	public void setDisplayIntent(boolean displayIntent) {
		this.displayIntent = displayIntent;
	}

	public boolean isDisplayExtent() {
		return displayExtent;
	}

	public void setDisplayExtent(boolean displayExtent) {
		this.displayExtent = displayExtent;
	}

	public boolean isUseColor() {
		return useColor;
	}

	public void setUseColor(boolean useColor) {
		this.useColor = useColor;
	}

	
	
	/**
	 * Generates the dot code corresponding to the concept lattice.
	 */
	public void generateCode() throws IOException{
		appendHeader();
		generateDot();
		appendFooter();
		buffer.flush();
	}
	
	private void generateDot() throws IOException{
		for(IConcept c: conceptOrder.getConcepts() ) {
			append(c.getId()+ " ");
			append("[shape=none");
			
			if ( useColor ) {
				append(",style=filled");
				/*if ( c.isNewEntity() )
					append(",fillcolor=" + newConceptColor );
				else if ( c.isEntityFusion() )
					append(",fillcolor=" + fusionConceptColor );
				else*/
					append(",fillcolor=" + normalConceptColor );
			}
						
			append(",label=<<table border=\"0\" cellborder=\"1\" cellspacing=\"0\" port=\"p\">");
			
			if ( displayConceptNumber )
				append("<tr><td>"+c.getName()+"</td></tr>");
			
			if ( displayIntent ) {
				append("<tr><td>");
				if ( useSimplifiedIntent ) {
					for( Attribute attr: c.getSimplifiedIntent() )
						append( attr.toString() + "<br/>" );
					if (c.getSimplifiedIntent().isEmpty())
						append("<br/>");
				} else {
					for( Attribute attr: c.getIntent() )
						append( attr.toString() + "<br/>" );
					if (c.getIntent().isEmpty())
						append("<br/>");
				}
				append("</td></tr>");
			}

			if ( displayExtent ) {
				
				append("<tr><td>");
				if ( useSimplifiedExtent ) {
					for(Entity ent: c.getSimplifiedExtent() ) 
						append(ent.getName() + "<br/>");
					if (c.getSimplifiedExtent().isEmpty())
						append("<br/>");
				} else {
					for(Entity ent: c.getExtent())
						append(ent.getName() + "<br/>");
					if (c.getExtent().isEmpty())
						append("<br/>");
				}
				append("</td></tr>");
			}

			append("</table>>];\n");
		}

		for( IConcept c: conceptOrder.getConcepts() ) {
			int idC=c.getId();
			for( IConcept parent: c.getChildren() )
				appendLine("\t" + parent.getId() + ":p -> " + idC + ":p");
		}
	}


	private void appendHeader() throws IOException{
		appendLine("digraph G { ");
		appendLine("\trankdir=BT;");
		appendLine("\tmargin=0;");
		appendLine("\tnode [margin=\"0.03,0.03\",fontname=\"DejaVu Sans\"];");
		appendLine("\tranksep=0.3;");
		appendLine("\tnodesep=0.2;");
		appendLine("//graph[label=\""+"name:"+conceptOrder.getName()+",concept number:"+conceptOrder.getConceptNb() +",object number:"+conceptOrder.getEntityNb() +",attribute number:"+conceptOrder.getAttributeNB() +"\"");
	}

	private void appendFooter() throws IOException{
		append("}");
	}

}
