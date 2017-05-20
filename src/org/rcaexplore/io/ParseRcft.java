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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.rcaexplore.algo.Algorithm;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.BinaryAttribute;
import org.rcaexplore.context.ObjectAttributeContext;
import org.rcaexplore.context.Entity;
import org.rcaexplore.context.ObjectObjectContext;
import org.rcaexplore.context.RelationalContextFamily;


/**
 * A parser for the RCFT relational context family description format.
 * @author Jean-Rémy Falleri
 *
 */
public class ParseRcft {
	
	
	private class Pair{
		public Entity e;
		public Attribute a;
		
		public Pair(Entity e, Attribute a){
			this.e=e;
			this.a=a;
		}
		
	}
	
	private RelationalContextFamily rcf;



	private Reader reader;
	private int lineNumber;

	public ParseRcft(String path) {
		
		try {
			reader =new FileReader(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public ParseRcft(Reader reader) {
		this.reader= reader;
	}

	public RelationalContextFamily getRcf() {
		return this.rcf;
	}
	
	public static RelationalContextFamily fromFile(String path) throws IOException, ParserException {
		ParseRcft p = new ParseRcft(path);
		p.parse();
		return p.getRcf();
	}

	public void parse() throws IOException, ParserException {
		long debug_time0=System.currentTimeMillis()/1000;
		lineNumber=0;
		this.rcf = new RelationalContextFamily();
		BufferedReader input = new BufferedReader(reader);


		String line = input.readLine();
		lineNumber++;
		while( line != null ) {
			String tline = line.trim();
			if ( tline.startsWith("FormalContext") )
				parseOAContext(input,tline);
			else if ( tline.startsWith("RelationalContext") )
				parseRelationalContext(input,tline);
			else if (!tline.startsWith("#")&&!tline.equals(""))
				throw new ParserException("Unknown context type.",lineNumber,0);
			
			line = input.readLine();
			lineNumber++;
		}
		long debug_timen=System.currentTimeMillis()/1000;
		System.out.println("Parsing RCFT: done.("+(debug_timen-debug_time0)+"s )");
	}

	private void parseOAContext(BufferedReader input,String desc) throws IOException, ParserException {

		int oAContextLine=lineNumber;
		String oacName = desc.split("\\ ")[1]; 
		
		ObjectAttributeContext oac = new ObjectAttributeContext(oacName);
		
		System.out.println("Parsing formal context " + oacName);
		
		
		
		input.mark(0);
		String line = input.readLine();
		String trimmedLine = line.trim();
		
		lineNumber++;
		//parse parameters
		
		while (!trimmedLine.startsWith("|"))
		{
		
			if (trimmedLine.split("\\ ").length==0)
				throw new ParserException("lines after a formal context declaration should declare parameters",lineNumber,0);
			String parameterName=trimmedLine.split("\\ ")[0];
			if (parameterName.equals("algo"))
			{
				if (trimmedLine.split("\\ ").length>3)
					throw new ParserException("Too many values for algo parameter",lineNumber,0);
				String algoString=trimmedLine.split("\\ ")[1];
				Algorithm algo=Algorithm.getAlgo(algoString);//TODO send a warning if null
				if (null ==algo)
					algo=Algorithm.getDefaultAlgo();
				
				
				if (algo.hasParameter())
					if(trimmedLine.split("\\ ").length==3) {
						try {
							oac.setDefaultAlgo(algo, Integer.parseInt(trimmedLine.split("\\ ")[2]));
						}
						catch (NumberFormatException e){
							throw new ParserException("Incorrect parameter format",lineNumber,0);
						}
					} else {
						oac.setDefaultAlgo(algo,algo.getDefaultParameterValue());
					}
				else
					oac.setDefaultAlgo(algo);
						
				/*else
					if (line.trim().split("\\ ").length>2)
						throw new ParserException("Too many values for algo parameter",lineNumber,0);*/ //TODO send a warning if a parameter is not expected
			}
			else if (parameterName.equals("description"))
				oac.setDescription(trimmedLine.substring(trimmedLine.split("\\ ")[0].length()).trim());
			else
				throw new ParserException("\""+parameterName+"\" is not a valid formal context parameter.",lineNumber,0);
			line = input.readLine();
			trimmedLine = line.trim();
			lineNumber++;
		}
		
		
		int currentRow = 0;
		Map<Integer, Attribute> attrs = new HashMap<Integer, Attribute>();

		ArrayList<Pair> pairs=new ArrayList<ParseRcft.Pair>();
		while ( line != null ) {

			String tline = line.trim();
			if ( tline.startsWith("FormalContext") )
				break;
			else if ( tline.startsWith("RelationalContext") )
				break;
			else if ( tline.equals("") )
				break;
			
			String[] tokens = tline.split("\\|");
			int len = tokens.length;
			if ( currentRow == 0 ) {

				for(int i = 2 ; i < len ; i++ ) {
					String attrDesc = tokens[i].trim();
					Attribute attr = new BinaryAttribute(attrDesc);
					attrs.put(i,attr);
					oac.addAttribute(attr);
					
					System.out.println("Attribute " + attr.toString() + " created. Row: " + i);
				}
			}
			else {
				String name = tokens[1].trim();
				Entity ent = new Entity(name);
				
				System.out.println("Entity " + name + " created. Line: " + currentRow);
				
				
				oac.addEntity(ent);
				for(int i = 2 ; i < len ; i++ ) {
					String cell = tokens[i].trim().toLowerCase();
					if ( "x".equals(cell) ) {
						Attribute attr = attrs.get(i);
						pairs.add(new Pair(ent,attr));
						
						
						System.out.println("Pair between " + ent.getName() + " and " + attr.toString() + " created. Line: " + currentRow);
					}
				}
			}

			currentRow++;

			input.mark(0);

			line = input.readLine();
			lineNumber++;
		}
		for (Pair p: pairs)
			oac.addPair(p.e, p.a);
		try {
			rcf.addFormalContext(oac);
		} catch (Exception e) {
			throw new ParserException(e.getMessage(),oAContextLine,0);

		}

		if ( line != null )
			input.reset();

	}

	private void parseRelationalContext(BufferedReader input,String desc) throws IOException, ParserException {
		
		String rcName = desc.split("\\ ")[1];
		ObjectObjectContext rc = new ObjectObjectContext(rcName);
		
		System.out.println("Parsing relational context " + rcName);
		
		int relationLineNumber=lineNumber;
		input.mark(0);
		String line = input.readLine();
		String trimmedLine = line.trim();
		lineNumber++;
		
		//parse parameters
		String source=null;
		String target=null;
		String sclOp=null;
		System.out.println("first line");
		while (!line.startsWith("|"))
		{
		
			String parameterName=trimmedLine.split("\\ ")[0];
			if (parameterName.equals("description")) {
				rc.setDescription(trimmedLine.substring(trimmedLine.split("\\ ")[0].length()).trim());
			} else if (trimmedLine.split("\\ ").length!=2)
				throw new ParserException("invalid number of arguments.",lineNumber,0);
			else if (parameterName.equals("source")) {
				source=trimmedLine.split("\\ ")[1];
			} else if (parameterName.equals("target")) {
				target=trimmedLine.split("\\ ")[1];
			} else if (parameterName.equals("scaling")) {
				sclOp=trimmedLine.split("\\ ")[1];
			} else 
				throw new ParserException("\""+parameterName+"\" is not a valid formal context paramater.",lineNumber,0);
			
			line = input.readLine();
			trimmedLine = line.trim();
			lineNumber++;
		}
		
		if (source==null)
			throw new ParserException("Missing source of relation "+rcName,relationLineNumber,0);
		if (target==null)
			throw new ParserException("Missing source of relation "+rcName,relationLineNumber,0);
		
		ObjectAttributeContext sourceFc = rcf.getOAContext(source);
		if (sourceFc==null)
			throw new IOException("source context \""+source+"\" of relational context \""+rcName+"\" cannot be found (line "+(lineNumber-2)+")");
		ObjectAttributeContext targetFc = rcf.getOAContext(target);
		if (targetFc==null)
			throw new IOException("target context \""+target+"\" of relational context \""+rcName+"\" cannot be found (line "+(lineNumber-1)+")");
		
		rc.setSourceContext(sourceFc);
		rc.setTargetContext(targetFc);
		if (sclOp!=null)
			rc.setOperator(sclOp);
		
		
		
		
		int currentRow = 0;
		Map<Integer, Entity> tgtEnts = new HashMap<Integer, Entity>();

		System.out.println("remaining lines");
		long debug_time1=System.currentTimeMillis()/1000;
		Hashtable<String, Entity> srcEntities=new Hashtable<String, Entity>();
		for (Entity e : sourceFc.getEntities()){
			srcEntities.put(e.getName(),e);
		}
		
		
		while ( line != null ) {
			String tline = line.trim();
			if ( tline.startsWith("RelationalContext") )
				break;
			else if ( tline.startsWith("FormalContext") )
				break;
			else if ( tline.equals("") )
				break;

			String[] tokens = tline.split("\\|");
			int len = tokens.length;
			if ( currentRow == 0 ) {

				for(int i = 2 ; i < len ; i++ ) {
					String name = tokens[i].trim();
					Entity ent = targetFc.getEntity(name);
					if (ent==null){
						System.out.println(name);
						throw new ParserException("error relation "+rcName+".",lineNumber,0);
					}
					tgtEnts.put(i,ent);
				}
				
			}
			else {
				String name = tokens[1].trim();
				Entity srcEnt = srcEntities.get(name);

				for(int i = 2 ; i < len ; i++ ) {
					String cell = tokens[i].trim().toLowerCase();
					if ( "x".equals(cell) ) {
						Entity tgtEnt = tgtEnts.get(i);
						
						rc.addPair(srcEnt, tgtEnt);
						
					}
				}
			}

			currentRow++;

			input.mark(0);

			line = input.readLine();
			lineNumber++;
		}
		long debug_time2=System.currentTimeMillis()/1000;
		System.out.println("end of relational context ("+(debug_time2-debug_time1)+" s)");
		try {
			rcf.addRelationalContext(rc);
		} catch (Exception e) {
			throw new ParserException(e.getMessage(),relationLineNumber,0);
		}

		if ( line != null )
			input.reset();

	}

}
