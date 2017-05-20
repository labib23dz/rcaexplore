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

import java.io.IOException;
import java.io.Writer;

import org.rcaexplore.algo.Algorithm;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.BinaryAttribute;
import org.rcaexplore.context.ObjectAttributeContext;
import org.rcaexplore.context.Entity;
import org.rcaexplore.contexteditor.controller.ContextChangeEvent;

public class EditorFormalContextModel extends ContextModelWithBitSet {

	private static final long serialVersionUID = 1L;
	
	private Algorithm algoName;
	private int algoParameter;
	
	public EditorFormalContextModel(String name) {
		super(0,0);
		setName(name);
		addColumn("");
		addRow();
		algoName=Algorithm.getDefaultAlgo();
		algoParameter=Algorithm.getDefaultAlgo().getDefaultParameterValue();
	}


	@Override
	public void addColumn() {
		super.addColumn();
		fireContextChangeEvent(new ContextChangeEvent(this,ContextChangeEvent.COL_ADDED));
		
	}


	@Override
	public void addColumn(String arg0) {
		super.addColumn(arg0);
		fireContextChangeEvent(new ContextChangeEvent(this,ContextChangeEvent.COL_ADDED));
		
	}

	@Override
	public void addRow() {
		super.addRow();
		fireContextChangeEvent(new ContextChangeEvent(this,ContextChangeEvent.ROW_ADDED));
	}
	
	
	public ObjectAttributeContext genFormalContext(){
		ObjectAttributeContext ctx=new ObjectAttributeContext(getName());
		Attribute[] attrs=new Attribute[getColumnCount()-1];
		
		for(int i=0;i<getRowCount();i++) {
			Entity currentEntity=new Entity(getValueAt(i, 0)+"");
			ctx.addEntity(currentEntity);
		}
		
		for (int i=1;i<getColumnCount();i++){
			
				Attribute att=new BinaryAttribute(getColumnName(i)+"");
				ctx.addAttribute(att);
				attrs[i-1]=att;
		}
		for(int i =0;i<getRowCount();i++ ) {
			Entity currentEntity =ctx.getEntities().get(i);
			for (int j=1;j<getColumnCount();j++) {
				if ((Boolean)getValueAt(i, j))
					ctx.addPair(currentEntity, attrs[j-1]);

			}
		}
		if (getAlgo().hasParameter())
			ctx.setDefaultAlgo(getAlgo(), algoParameter);
		else
			ctx.setDefaultAlgo(getAlgo());
		ctx.setDescription(getDescription());
		
		return ctx;
			
	}

	public Algorithm getAlgo() {
		return algoName;
		
	}

	@Override
	public void removeColumn(String s) {
		super.removeColumn(s);
		fireContextChangeEvent(new ContextChangeEvent(this,ContextChangeEvent.COL_DELETED,s));
	}

	@Override
	public void removeRow(String s) {
		super.removeRow(s);
		fireContextChangeEvent(new ContextChangeEvent(this,ContextChangeEvent.ROW_DELETED,s));
	}
	
	@Override
	public void renameCol(String old, String title) {
		super.renameCol(old, title);
		fireContextChangeEvent(new ContextChangeEvent(this,ContextChangeEvent.COL_TITLE_CHANGED,old,title));
	}
	
	@Override
	public void renameRow(String oldName, String newName) {
		super.renameRow(oldName, newName);
		fireContextChangeEvent(new ContextChangeEvent(this,ContextChangeEvent.ROW_TITLE_CHANGED,oldName,newName));
	}
	
	@Override
	public void saveRCF(Writer fw) throws IOException {
		fw.write("FormalContext "+getName()+"\n");
		fw.write("|\t\t|");
		
		for (int i = 1;i<getColumnCount();i++)
			fw.write(getColumnName(i)+"\t|");
		fw.write("\n");
		
		for (int j=0;j<getRowCount();j++) {
			fw.write("|"+getValueAt(j, 0)+"\t|");
			for (int i=1;i<getColumnCount();i++) {
				if ((Boolean)getValueAt(j, i))
					fw.write("x\t\t|");
				else
					fw.write("\t\t|");
			}
			fw.write("\n");
		}
		
		fw.write("\n");
	}
	

	public void setAlgo(Algorithm algo){
		if (this.algoName!=algo){
		this.algoName=algo;
		fireContextChangeEvent(new ContextChangeEvent(this,ContextChangeEvent.ALGO_CHANGED,getName(),algoName.getName()));
		}
	}


	public Object getAlgoParameter() {
		return algoParameter;
	}


	public void setAlgoParameter(int parameterValue) {
		algoParameter=parameterValue;
		fireContextChangeEvent(new ContextChangeEvent(this,ContextChangeEvent.ALGO_PARAM_CHANGED,getName(),algoName.getName(),parameterValue+""));
	}

}