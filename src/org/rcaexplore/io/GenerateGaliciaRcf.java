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

package org.rcaexplore.io;

import java.io.FileWriter;
import java.io.IOException;

import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.ObjectAttributeContext;
import org.rcaexplore.context.Entity;
import org.rcaexplore.context.ObjectObjectContext;
import org.rcaexplore.context.RelationalContextFamily;
/**
 * Class for serializing a relational context family in Galicia readable format 
 * 
 * */
public class GenerateGaliciaRcf extends GenerateCode{

	private RelationalContextFamily rcf;
	
	public GenerateGaliciaRcf(FileWriter buffer,RelationalContextFamily rcf){
		super(buffer);
		this.rcf=rcf;
		
	}
	
	private void generateFormalContext(ObjectAttributeContext c) throws IOException{
		append("[Binary Relation]\n");
		append(c.getName()+"\n");
		for (Entity e: c.getEntities()){
			append( e.getName()+" | ");
		}
		append("\n");
		for (Attribute a : c.getAttributes()){
			append(a.toString()+" | ");
		}
		append("\n");
		for (Entity e: c.getEntities()){
			for (Attribute a : c.getAttributes()){
				if (c.hasPair(e, a))
					append("1 ");
				else
					append("0 ");
			}
			append("\n");
		}
		
		
		
	}
	
	private void generateRelationalContext(ObjectObjectContext rc) throws IOException{
		append("[Inter Object Binary Relation]\n");
		append(rc.getRelationName()+"\n");
		append(rc.getSourceContext().getName()+"\n");
		append(rc.getTargetContext().getName()+"\n");
		for (Entity e: rc.getSourceEntities()){
			append(e.getName()+" | ");
		}
		append("\n");
		for (Entity a : rc.getTargetEntities()){
			append(a.toString()+" | ");
		}
		append("\n");
		for (Entity e: rc.getSourceEntities()){
			for (Entity a : rc.getTargetEntities()){
				if (rc.hasPair(e, a))
					append("1 ");
				else
					append("0 ");
			}
			append("\n");
		}
		
		
	}
	
	private void appendHeader() throws IOException{
		append("[Relational Context]\n");
		append("DefaultName\n");
	}
	
	private void appendFooter() throws IOException{
		append("[END Relational Context]");
	}
	
	
	
	@Override
	public void generateCode() throws IOException {
		appendHeader();
		for (ObjectAttributeContext c : rcf.getOAContexts()){
			generateFormalContext(c);
		}
		for (ObjectObjectContext rc : rcf.getOOContexts()){
			generateRelationalContext(rc);
		}
		appendFooter();
		buffer.flush();
	}

}
