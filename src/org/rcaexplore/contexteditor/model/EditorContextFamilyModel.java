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
package org.rcaexplore.contexteditor.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.rcaexplore.algo.Algorithm;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.Entity;
import org.rcaexplore.context.ObjectAttributeContext;
import org.rcaexplore.context.ObjectObjectContext;
import org.rcaexplore.context.RelationalContextFamily;
import org.rcaexplore.contexteditor.controller.ContextEvent;
import org.rcaexplore.contexteditor.controller.ContextListener;
import org.rcaexplore.io.ParseCSVContext;
import org.rcaexplore.io.GenerateGaliciaRcf;
import org.rcaexplore.io.GenerateLatexFromOAContext;
import org.rcaexplore.io.GenerateLatexFromOOContext;
import org.rcaexplore.io.GenerateRcft;
import org.rcaexplore.io.ParserException;
import org.rcaexplore.io.ParseRcft;



public class EditorContextFamilyModel {

	private HashMap<String,ContextModelWithBitSet> contextModels;

	private File currentFile;

	private ArrayList<String> formalContexts;
	
	private ArrayList<ContextListener> listeners;

	public EditorContextFamilyModel()
	{
		contextModels=new HashMap<String, ContextModelWithBitSet>();
		listeners=new ArrayList<ContextListener>();
		formalContexts=new ArrayList<String>();
	}

	public void addColumn(final String selectedContext) {
		
				contextModels.get(selectedContext).addColumn();
			
		
	}

	private void addColumn(final String contextName, final String attrDesc) {
	contextModels.get(contextName).addColumn(attrDesc);
			
	
	
	}

	public void addContextListener(ContextListener cl)
	{
		listeners.add(cl);
	}

	public void addFormalContext(final String name)
	{
		
				if (name==null)
					return;
				ContextModelWithBitSet cm=new EditorFormalContextModel(name);
				contextModels.put(name,cm);
				formalContexts.add(name);
				fireNewContextEvent(name,cm);
				
		
	}

	public void addOppositeRelationnalContext(String name, String originalContext)
	{
		EditorRelationalContextModel original=(EditorRelationalContextModel) contextModels.get(originalContext);
		ContextModelWithBitSet cm=new EditorRelationalContextModel(name,original.getTarget(), original.getSource());
		
		for (int row=0; row<original.getRowCount();row++)
			for (int column=1; column<original.getColumnCount();column++)
				cm.setValueAt(original.getValueAt(row, column), column-1, row+1);
		
		contextModels.put(name,cm);
		fireNewContextEvent(name,cm);
	}

	public void addRelationnalContext(final String name, final String source, final String target)
	{
		
				ContextModelWithBitSet cm=new EditorRelationalContextModel(name,(EditorFormalContextModel)contextModels.get(source), (EditorFormalContextModel)contextModels.get(target));
				contextModels.put(name,cm);
				fireNewContextEvent(name,cm);

				
		
	}

	public void addRow(String selectedContext) {

		contextModels.get(selectedContext).addRow();

	}
	
	private void addRow(String contextName, String name) {
		contextModels.get(contextName).addRow(name);
	}

	public void changeDescription(String selectedContext, String description) {
		contextModels.get(selectedContext).setDescription(description);
	}
	public boolean existsContext(String name)
	{
		return (contextModels.containsKey(name));
	}

	public void exportAsGalicia(File file) throws IOException {
		RelationalContextFamily rcf=genRCF();
		FileWriter fw= new FileWriter(file);
		
		GenerateGaliciaRcf rcfgen= new GenerateGaliciaRcf(fw, rcf);
		rcfgen.generateCode();
		fw.close();
		
		
	}

	public void exportAsTex(File file) throws IOException  {
		
		RelationalContextFamily rcf=genRCF();
		FileWriter fw;
		fw = new FileWriter(file);
	
		for (ObjectAttributeContext ctx : rcf.getOAContexts()) {
			GenerateLatexFromOAContext rcfgen=new GenerateLatexFromOAContext(fw,ctx);
			rcfgen.generateCode();
		}
		for (ObjectObjectContext ctx : rcf.getOOContexts()) {
			GenerateLatexFromOOContext rcfgen=new GenerateLatexFromOOContext(fw,ctx);
			rcfgen.generateCode();
		}
		fw.close();
		
		
	}

	public int familySize()
	{
		return contextModels.size();
	}

	private void fireNewContextEvent(String name, ContextModelWithBitSet cm) {
		ContextEvent event=new ContextEvent(this,"new Context",name, cm);
		for (ContextListener cl:listeners)
			cl.contextChanged(event);

	}

	private void fireRemoveContextEvent(String name, ContextModelWithBitSet toRemove) {
		ContextEvent event=new ContextEvent(this,"remove Context",name, toRemove);
		for (ContextListener cl : listeners)
			cl.contextChanged(event);
		
	}

	private void fireRenameContextEvent(String name, ContextModelWithBitSet cm){
		ContextEvent event=new ContextEvent(this,"new Context name",name, cm);
		for (ContextListener cl:listeners)
			cl.contextChanged(event);
	}

	private void fireFileChangedContextEvent(){
		ContextEvent event=new ContextEvent(this,"file Changed");
		for (ContextListener cl:listeners)
			cl.contextChanged(event);
		
	}
		
	public RelationalContextFamily genRCF()
	{
		RelationalContextFamily rcf= new RelationalContextFamily();
		
		for(String fc: formalContexts)
		{
			ContextModelWithBitSet editorCtx=contextModels.get(fc);

			try {
				rcf.addFormalContext(((EditorFormalContextModel) editorCtx).genFormalContext());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (String rc : getRelationalContexts())
		{
			EditorRelationalContextModel editorRC=(EditorRelationalContextModel) contextModels.get(rc);
			try {
				rcf.addRelationalContext(editorRC.genRelationalContext(rcf));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return rcf;
	}

	public void genRCFT2(Writer fw) throws IOException
	{
		RelationalContextFamily rcf = genRCF();
		GenerateRcft gen=new GenerateRcft(fw, rcf);
		gen.generateCode();
	}

	public Object[] getAttributes(String selectedContext)
	{
		ContextModelWithBitSet context=contextModels.get(selectedContext);
		String[] ret=new String[context.getColumnCount()-1];
		for (int i=1;i<context.getColumnCount();i++)
			ret[i-1]=context.getColumnName(i);
		return ret;
	}

	public ContextModelWithBitSet getContextModel(String name){
		return contextModels.get(name);
		
	}

	public Set<String> getContexts() {
		return contextModels.keySet();
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public ArrayList<String> getFormalContexts()
	{
		return formalContexts;
	}

	public Object[] getObjects(String selectedContext) {
		ContextModelWithBitSet context=contextModels.get(selectedContext);
		String[] ret=new String[context.getRowCount()];
		for (int i=0;i<context.getRowCount();i++)
			ret[i]=(String) context.getValueAt(i, 0);
		return ret;
		
	}

	public ArrayList<String> getRelationalContexts()
	{
		ArrayList<String> result = new ArrayList<String>();
		for (ContextModelWithBitSet cm : contextModels.values())
			if (cm instanceof EditorRelationalContextModel)
				result.add(cm.getName());
		return result;
	}

	public void loadRCF(File f) throws IOException {
		setCurrentFile(f);
		String fileExtension=(f.getName().substring(f.getName().lastIndexOf(".")+1));
				
		System.out.println("file extension : "+fileExtension);
		if ("rcft".equals(fileExtension)||"rcfgz".equals(fileExtension))
		{
			ParseRcft rcftParser=null;
			if ("rcft".equals(fileExtension))
				rcftParser=new ParseRcft(new FileReader(f));
			else
				rcftParser=new ParseRcft(
						new BufferedReader(
								new InputStreamReader(
										new GZIPInputStream(new FileInputStream(f)))));		
			try {
				rcftParser.parse();//TODO close ?
			} catch (ParserException e) {
				e.printStackTrace();
			}
			
			final RelationalContextFamily rcf=rcftParser.getRcf();
			
			//remove all existing contexts before adding new ones
			removeAll();
			for (ObjectAttributeContext  c: rcf.getOAContexts())
			{
				importOAContext(c);
				
			}
			for (ObjectObjectContext  c: rcf.getOOContexts())
			{
				addRelationnalContext(c.getRelationName(), c.getSourceContext().getName(), c.getTargetContext().getName());
				for (int row=0;row<c.getSourceEntities().size();row++) {
					for (int col=0;col<c.getTargetEntitiesNb();col++) {
						if (c.hasPair(c.getSourceEntities().get(row), c.getTargetEntities().get(col)))
							contextModels.get(c.getRelationName()).setValueAt(true, row, col+1);
					}
				}
				((EditorRelationalContextModel)contextModels.get(c.getRelationName())).setScalingOperator(c.getOperator().getName());
				contextModels.get(c.getRelationName()).setDescription(c.getDescription());
			}
			
		}
		fireFileChangedContextEvent();
	}

	/** Import an OA context in the context editor format*/
	private void importOAContext(ObjectAttributeContext c) {
		addFormalContext(c.getName());
		renameAlgo(c.getName(), c.getDefaultAlgo().getName(), c.getDefaultAlgoParameter()+"");
		for (Attribute a : c.getAttributes())
			addColumn(c.getName(), a.toString());
		boolean first=true;
		for (Entity e : c.getEntities())
			if (first ){
				renameRow(c.getName(), "Object_0", e.getName());
				first=false;
			} else
				addRow(c.getName(),e.getName());
		
		for (int row=0;row<c.getEntityNb();row++) {
			for (int col=0;col<c.getAttributeNb();col++) {
				if (c.hasPair(c.getEntities().get(row), c.getAttributes().get(col)))
					contextModels.get(c.getName()).setValueAt(true, row, col+1);
			}
		}
		((EditorFormalContextModel)contextModels.get(c.getName())).setAlgo(c.getDefaultAlgo());
		contextModels.get(c.getName()).setDescription(c.getDescription());
	}

	private void removeAll() {
		
		while(!getFormalContexts().isEmpty()){
			removeFormalContext(getFormalContexts().get(0));
		}
		while(!getRelationalContexts().isEmpty()){
			removeRelationalContext(getRelationalContexts().get(0));
		}
		
	}

	public void removeColumn(String selectedContext, String col)
	{
		
		contextModels.get(selectedContext).removeColumn(col);
	}
	
	/**
	 * remove the formal Context named name and all the relational contexts that have
	 * this formal context as source or target
	 * */
	public void removeFormalContext(String name) {
		if (name==null)
			return;
		ContextModelWithBitSet toRemove=contextModels.get(name);
		ArrayList<String>relationalContextsToRemove=new ArrayList<String>();
		for (String contextName : contextModels.keySet()){
			ContextModelWithBitSet cm=contextModels.get(contextName);
			if (cm instanceof EditorRelationalContextModel)
			{
				EditorRelationalContextModel rc=(EditorRelationalContextModel) cm;
				if (rc.getSource() == toRemove || rc.getTarget() == toRemove){
					relationalContextsToRemove.add(rc.getName());
				}
			}
		}
		for (String relationName : relationalContextsToRemove)
			removeRelationalContext(relationName);
		formalContexts.remove(name);
		contextModels.remove(name);
		
		fireRemoveContextEvent(name,toRemove);
		
	}
	
	public void removeRelationalContext(String name) {
		if (name==null)
			return;
		ContextModelWithBitSet toRemove=contextModels.get(name);
		
		contextModels.remove(name);
		fireRemoveContextEvent(name,toRemove);
		
	}
	
	public void removeRow(String selectedContext, String s) {
		contextModels.get(selectedContext).removeRow(s);
	}

	public void renameAlgo(String selectedContext, String algoName) {
		((EditorFormalContextModel)contextModels.get(selectedContext)).setAlgo(Algorithm.getAlgo(algoName));
	}

	public void renameAlgo(String selectedContext, String algoName, String algoParameter) {
		((EditorFormalContextModel)contextModels.get(selectedContext)).setAlgo(Algorithm.getAlgo(algoName));
		((EditorFormalContextModel)contextModels.get(selectedContext)).setAlgoParameter(Integer.parseInt(algoParameter));
		
	}

	public void renameCol(String selectedContext,String oldName, String newName) {
		contextModels.get(selectedContext).renameCol(oldName,newName);
	}

	public void renameContext(String oldName, String newName) {
		contextModels.get(oldName).setName(newName);
		contextModels.put(newName,contextModels.remove(oldName));
		if (formalContexts.contains(oldName)) {
			formalContexts.remove(oldName);
			formalContexts.add(newName);
		}
		fireRenameContextEvent(oldName, contextModels.get(newName));
	}

	public void renameRow(String selectedContext,String oldName, String newName) {
		contextModels.get(selectedContext).renameRow(oldName,newName);
	}

	public void renameScaling(String selectedContext, String scalingOperator) {
		((EditorRelationalContextModel)contextModels.get(selectedContext)).setScalingOperator(scalingOperator);
		
		
	}

	public void save() {
		saveAsRCF(currentFile);
	}

	/**save the rcf from the editor in RCAExplore file format*/
	public void saveAsRCF(File f) {
		setCurrentFile(f);
		Writer w=null;
		try {
			if (f.getPath().endsWith("rcfgz"))
				w=new BufferedWriter(new OutputStreamWriter(
						new GZIPOutputStream(new FileOutputStream(f))));
			else 
				w=new FileWriter(f);
			genRCFT2(w);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		fireFileChangedContextEvent();
	}

	public void setCurrentFile(File currentFile) {
		this.currentFile = currentFile;
	}

	public void importOAContextCSV(File file) {
		try {
			
			FileReader reader=new FileReader(file);
			ParseCSVContext parser=new ParseCSVContext(reader);
			System.out.println("mark 1");
			parser.parse();
			System.out.println("mark 2");
			importOAContext(parser.getContext());
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	
}
