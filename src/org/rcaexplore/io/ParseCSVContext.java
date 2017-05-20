
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.BinaryAttribute;
import org.rcaexplore.context.ObjectAttributeContext;
import org.rcaexplore.context.Entity;

/**
 * Objects of this class will parse contexts stored as csv files, using "," as default separator, and 
 * and generate the corresponding object attribute context.
 * */
public class ParseCSVContext {
	
	private ObjectAttributeContext context;
	private String separator;
	
	private Reader reader;
	
	public ParseCSVContext(String path) {
		this(path, ",");
	}
	
	public ParseCSVContext(Reader reader){
		this(reader, ",");
	}

	public ParseCSVContext(Reader reader, String separator) {
		this.reader=reader;
		this.separator=separator;
	}
	public ParseCSVContext(String path, String separator) {
		
		try {
			reader =new FileReader(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.separator=separator;
	}

	public void parse() throws IOException {
		BufferedReader input =new BufferedReader(reader);
		
		String line=input.readLine();
		// first line contains the context name in the first cell and 
		//the list of attributes in the remaining of the row
		String[] tokens = line.split("\\"+separator);
		this.context=new ObjectAttributeContext(tokens[0].trim());
		
		Attribute[] attrs = new Attribute[tokens.length-1];
		for (int i=1;i<tokens.length;i++)
		{
			Attribute att= new BinaryAttribute(tokens[i].trim());
			context.addAttribute(att);
			attrs[i-1]=att;
		}
		
		//then for each line, first cell contains an object, the remaining 
		//is the relation with attributes
		line=input.readLine();
		while(line!=null)
		{
			tokens=line.split("\\"+separator);
			Entity currentEntity=new Entity(tokens[0].trim());
			this.context.addEntity(currentEntity);
			
			for(int i =1;i<tokens.length; i++){
				String val=tokens[i].trim();
				if (val.equals("1")||val.toLowerCase().equals("x"))
					context.addPair(currentEntity, attrs[i-1]);
				
			}
			line=input.readLine();
		}
		
		
	}
	
	public ObjectAttributeContext getContext(){
		return context;
	}
	
	
}
