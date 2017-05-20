/*
 * Copyright (c) 2014, ENGEES. All rights reserved.
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.rcaexplore.conceptorder.structure.IConcept;
import org.rcaexplore.conceptorder.structure.IConceptOrder;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.BinaryAttribute;
import org.rcaexplore.context.Entity;
import org.rcaexplore.context.RelationalAttribute;

public class GenerateGraphConceptsDot extends GenerateCode{
	
	private IConceptOrder<?> co;
	private String path;

	
	
	public GenerateGraphConceptsDot(FileWriter buffer,IConceptOrder<?> lattice, String path)
	{
		super(buffer);
		this.path=path;
		co=lattice;
	}
	
	
	
	public void createDirs(){
		File f= new File(path+"/data");
		f.mkdirs();
		
	}
	
	public void rule2Dot(){
		try {
			createDirs();
			rule2MakeFile();
			createRulesLattice(co);
			for (IConcept r: co.getConcepts())
			{
				createRule(r);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void createRulesLattice(IConceptOrder<?> lat) throws IOException{
		
		
		
		
		buffer.append("digraph G {\n");
		buffer.append("rankdir=BT;\n");
		buffer.append("node [margin=\"0.2,0.2\",fontname=\"DejaVuSans\"];\n");
		buffer.append("ranksep=0;nodesep=0;\n");
		
		for (IConcept r:lat.getConcepts()){
			buffer.append(normalise(r.getName())+" [shape=box,image=\"data/"+normalise(r.getName())+".png\",label=\"\"];\n");
		}
		
		for (IConcept r : lat.getConcepts())
			for (IConcept p : r.getParents())
				buffer.append(normalise(r.getName())+"->"+normalise(p.getName())+"\n");
		
		buffer.append("}");
		
	}
	
	private class Edge{
		
		
		
		public Edge(IConcept source, RelationalAttribute target) {
			super();
			this.source = source;
			this.target = target;
		}
		IConcept source;
		RelationalAttribute target;
	}
	
	private static void appendNode(IConcept c, StringBuffer sb){
		sb.append(normalise(c.getName()) +" [");
		sb.append("shape=record,label=\"{"+normalise(c.getName())+"|");
		for (Attribute a : c.getIntent())
			if(a instanceof BinaryAttribute)
				sb.append(((BinaryAttribute) a).getValue()+"\\n");
		sb.append("|");
		for (Entity e : c.getExtent())
			sb.append(e.getName()+"\\n");
		sb.append("}\"];\n");
	}
	
	
	public void createRule(IConcept c) throws IOException{
		
		FileWriter fw=new FileWriter(path+"/data/"+normalise(c.getName())+".dot");

		fw.append("digraph G {\n");
		fw.append("rankdir=TB;\n");
		fw.append("node [margin=\"0,0\",fontname=\"DejaVuSans\"];\n");
		fw.append("edge [fontname=\"DejaVuSans\"];\n");
		fw.append("ranksep=0;nodesep=0;\n");
		fw.append("label=\""+normalise(c.getName())+"\";\n");
		
		HashSet<String> pastConcepts=new HashSet<String>();
		ArrayList<Edge> toGenerate=new ArrayList<GenerateGraphConceptsDot.Edge>();
		
		StringBuffer nodes=new StringBuffer();
		StringBuffer arcs=new StringBuffer();
		
		pastConcepts.add(c.getName());
		appendNode(c, nodes);
		for (Attribute a : c.getIntent())
			if (a instanceof RelationalAttribute)
				toGenerate.add(new Edge(c,(RelationalAttribute) a));
		
		while (!toGenerate.isEmpty())
		{
			Edge e=toGenerate.remove(0);
			
			if (e.target instanceof RelationalAttribute &&!pastConcepts.contains(e.target.getConcept().getName())){
				//add node for concept
				pastConcepts.add(e.target.getConcept().getName());
				appendNode(e.target.getConcept(), nodes);
				
				for (Attribute a : e.target.getConcept().getIntent())
					if (a instanceof RelationalAttribute)
						toGenerate.add(new Edge(e.target.getConcept(),(RelationalAttribute) a));
				
			}
			arcs.append(normalise(e.source.getName())+"->"+normalise(e.target.getConcept().getName())+" [label=\""+e.target.getScaling()+"\\n"+e.target.getRelation().getRelationName()+"\"];\n");
			
			
		}
		
		
		fw.append(nodes.toString());
		fw.append(arcs.toString());
		fw.append("}");
		fw.close();
	}
	
	public void rule2MakeFile() throws IOException{
		
		FileWriter fw=new FileWriter(path+"/Makefile");
		fw.append("SRC= $(wildcard data/*.dot)\n"+
					"OBJ= $(SRC:.dot=.png)\n\n"+
					"%.png: %.dot\n"+
					"\tdot -Tpng  $< -o $@\n\n"+
					"main.svg: main.dot $(OBJ)\n"+
					"\tdot -Tsvg main.dot -o main.svg");
		fw.close();
	}
	
	
	
	
	
	private static String normalise(String s){
		char[] tmp=s.toCharArray();
		String ret="";
		for (char c: tmp)
		{
			switch (c)
			{
			case '/' : break;
			case '@' : break;
			case '.' : c='_';
			default : ret=ret+c;
			}
			
		}
		return ret;
	}



	@Override
	public void generateCode() throws IOException {
		rule2Dot();
		buffer.flush();
	}
	public static void main(String[] args) {
		
	}
	
}
