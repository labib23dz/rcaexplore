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

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;

import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.ObjectAttributeContext;
import org.rcaexplore.context.Entity;
import org.rcaexplore.context.ObjectObjectContext;
import org.rcaexplore.context.RelationalContextFamily;

public class GenerateRcft extends GenerateCode{

	private RelationalContextFamily rcf;
	
	public GenerateRcft(Writer buffer,RelationalContextFamily rcf){
		super(buffer);
		this.rcf=rcf;
		
	}
	
	private void generateFormalContext(ObjectAttributeContext c) throws IOException{
		//prettryPrint only with less than 100 columns, to limit file size
		boolean prettyPrint=c.getAttributeNb()<100;

		buffer.write("FormalContext "+c.getName()+"\n");
		if (!c.getDefaultAlgo().equals("")) {
			buffer.write("algo "+c.getDefaultAlgo());
			if (c.getDefaultAlgo().hasParameter())
				buffer.write(" "+c.getDefaultAlgoParameter());
			buffer.write("\n");
		}
		if (!c.getDescription().equals(""))
			append("description "+c.getDescription()+"\n");
		//find the longest string for an entity
		int longestEntityName=0;
		if (prettyPrint)
			for (Entity e: c.getEntities())
				if (e.getName().length()>longestEntityName)
					longestEntityName=e.getName().length();
		
		
		buffer.write("|"+(prettyPrint?nSpaces(longestEntityName):"")+"|");
		
		for(Attribute att:c.getAttributes())
			buffer.write(att.toString()+"|");
		buffer.write("\n");
		System.out.println(c.getName());
		System.out.println(c.getAttributeNb());
		for(Entity ent: c.getEntities() ) {
			buffer.write("|"+ent.getName()
					+(prettyPrint?nSpaces(longestEntityName-ent.getName().length()):"")
					+"|");
			for( Attribute attr: c.getAttributes() ) {
				System.out.println(attr);
				if ( c.hasPair(ent,attr))
					append("x"+(prettyPrint?nSpaces(attr.toString().length()-1):"")+"|");
				else 
					append((prettyPrint?nSpaces(attr.toString().length()):"")+"|");
				}
			newLine();
		}
		newLine();
		buffer.flush();		
		
		
		
	}
	
	private String nSpaces(int n){
		if (n<1) {
			return "";
		} else {
			char[] spaces=new char[n];
			Arrays.fill(spaces, ' ');
			return new String(spaces);
		}
	}
	
	private void generateRelationalContext(ObjectObjectContext rc) throws IOException{
		//prettryPrint only with less than 100 columns, to limit file size
		boolean prettyPrint=rc.getTargetEntitiesNb()<100;
		
		append("RelationalContext "+rc.getRelationName()+"\n");
		append("source "+rc.getSourceContext().getName()+"\n");
		append("target "+rc.getTargetContext().getName()+"\n");
		append("scaling "+rc.getOperator().getName()+" \n");
		if (!rc.getDescription().equals(""))
			append("description "+rc.getDescription()+"\n");
		//find the longest string for an entity
		int longestSrcEntityName=0;
		if (prettyPrint)
			for (Entity e: rc.getSourceEntities())
				if (e.getName().length()>longestSrcEntityName)
					longestSrcEntityName=e.getName().length();
				
		
		
		append("|"+(prettyPrint?nSpaces(longestSrcEntityName):"")+"|");
		Iterator<Entity> tgtEntityIt = rc.getTargetEntities().iterator();
		
		while( tgtEntityIt.hasNext() ) {
			Entity attr = tgtEntityIt.next();
			
			append(attr.toString() + "|"); 
		}
		newLine();
		
		for(Entity ent: rc.getSourceEntities() ) {
			append("|" + ent.getName() + (prettyPrint?nSpaces(longestSrcEntityName-ent.getName().length()):"") + "|");
			
			for( Entity attr: rc.getTargetEntities() )
				if ( rc.hasPair(ent,attr))
					append("x"+(prettyPrint?nSpaces(attr.getName().length()-1):"")+"|");
				else
					append((prettyPrint?nSpaces(attr.getName().length()):"")+"|");
			newLine();
		}
		newLine();
		buffer.flush();
		
		
	}
	
	@Override
	public void generateCode() throws IOException {
		for(ObjectAttributeContext fc:rcf.getOAContexts())
		{
			generateFormalContext(fc);
		}
		
		for (ObjectObjectContext rc:rcf.getOOContexts())
		{
			generateRelationalContext(rc);
		}
		buffer.flush();
	}

}
