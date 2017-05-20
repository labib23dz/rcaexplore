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
import java.util.HashMap;
import java.util.Map;

import org.rcaexplore.conceptorder.structure.ConceptOrderFamily;
import org.rcaexplore.conceptorder.structure.IConcept;
import org.rcaexplore.conceptorder.structure.IConceptOrder;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.Entity;


public class GenerateSVGDot extends GenerateCode {
	
	private ConceptOrderFamily<?> conceptOrderFamily;
	
		
	private boolean displayConceptNumber = true;

	private boolean useSimplifiedIntent = true;

	private boolean useSimplifiedExtent = true;
	
	private boolean displayIntent = true;
	
	private boolean displayExtent = true;

	private boolean useColor = false;
	
	private Map<IConcept,Integer> idMap;

	public GenerateSVGDot(FileWriter buffer,ConceptOrderFamily<?> cof) {
		super(buffer);
		this.conceptOrderFamily = cof;
		this.idMap = new HashMap<>();
		initIdMap();
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

	
	
	public void initIdMap() {
		int id = 1;
		for (IConceptOrder<?> conceptOrder:this.conceptOrderFamily.getConceptOrders())
			for( IConcept c: conceptOrder ) {
				idMap.put(c,id);
				id++;
			}
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
		for (IConceptOrder<?> conceptOrder:this.conceptOrderFamily.getConceptOrders()) {
			appendLine("subgraph "+conceptOrder.getName()+" { ");
			appendLine("label=\""+conceptOrder.getName()+"\";");
			for(IConcept c: conceptOrder.getConcepts() ) {
				
				append(idMap.get(c) + " ");
				append("[shape=record");
				append(",label=\"{");
				
				if ( displayConceptNumber )
					append(c.getName()+"|");
				
				if ( displayIntent ) {
					if ( useSimplifiedIntent )
						for( Attribute attr: c.getSimplifiedIntent() )
							append( attr.toString() + "\\n" );
					else
						for( Attribute attr: c.getIntent() )
							append( attr.toString() + "\\n" );
				}
	
				if ( displayExtent ) {
					append("|");
					if ( useSimplifiedExtent ) 
						for(Entity ent: c.getSimplifiedExtent() ) 
							append(ent.getName() + "\\n");
					else
						for(Entity ent: c.getExtent())
							append(ent.getName() + "\\n");
				}
				append("}\"");
				append("];\n");
			}
			for( IConcept c: conceptOrder.getConcepts() ){
				int idC=idMap.get(c);

				for( IConcept parent: c.getChildren() )
					appendLine("\t" + idMap.get(parent) + " -> " + idC );
			}
			appendLine("}");
		}
	}


	private void appendHeader() throws IOException{
		appendLine("digraph G { ");
		appendLine("\trankdir=BT;");
	}

	private void appendFooter() throws IOException{
		append("}");
	}

}
