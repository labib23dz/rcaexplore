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
import java.util.Iterator;

import org.rcaexplore.context.Entity;
import org.rcaexplore.context.ObjectObjectContext;



public class GenerateLatexFromOOContext extends GenerateCode {

	private ObjectObjectContext context;
	
	private boolean rotateAttributes;
	
	public GenerateLatexFromOOContext(FileWriter buffer,ObjectObjectContext context) {
		this(buffer,context,true);
	}
	
	public GenerateLatexFromOOContext(FileWriter buffer,ObjectObjectContext context,boolean rotateAttributes) {
		super(buffer);
		this.rotateAttributes = rotateAttributes;
		this.context = context;
	}

	
	
	@Override
	public void generateCode() throws IOException {
		append("\\begin{tabular}");
		
		append("{|l|");
		for(int i = 0;i < context.getTargetEntitiesNb(); i++)
			append("c|");
		append("}");
		
		newLine();
		
		appendLine("\\hline");
		append("\\texttt{"+GenerateLatexFromOAContext.texifyString(context.getRelationName())+"}");
		if (context.getTargetEntitiesNb()>0)
			append(" & ");
		else
			append("\\\\");

		Iterator<Entity> tgtEntityIt = context.getTargetEntities().iterator();
		
		while( tgtEntityIt.hasNext() ) {
			Entity attr = tgtEntityIt.next();
			
			String desc = "\\textbf{" + GenerateLatexFromOAContext.texifyString(attr.toString()) + "}"; 
			
			if ( rotateAttributes )
				desc = "\\begin{sideways}" + desc + "\\end{sideways}";
			
			if ( tgtEntityIt.hasNext() )
				append(desc + " & ");
			else
				append(desc + "\\\\");
		}
		
		newLine();
		
		for(Entity ent: context.getSourceEntities() ) {
			appendLine("\\hline");
			append("\\textbf{" + GenerateLatexFromOAContext.texifyString(ent.getName()) + "}");
			
			for( Entity attr: context.getTargetEntities() )
				if ( context.hasPair(ent,attr))
					append("& $\\times$ ");
				else
					append(" & ");
			
			append("\\\\");
			newLine();
		}
		appendLine("\\hline");
		appendLine("\\end{tabular}");
		buffer.flush();
	}

}
