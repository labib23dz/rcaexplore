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
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.rcaexplore.context.*;
import org.rcaexplore.contexteditor.controller.ContextChangeEvent;
import org.rcaexplore.contexteditor.controller.ContextChangeListener;


public class EditorRelationalContextModel extends ContextModelWithBitSet implements TableModelListener, ContextChangeListener {

	private static final long serialVersionUID = 1L;
	private String scalingOperator;
	private EditorFormalContextModel source;
	private EditorFormalContextModel target;
	
	public EditorRelationalContextModel(String name,EditorFormalContextModel source, final EditorFormalContextModel target) {
		super(0,0);
		setName(name);
		scalingOperator="exist";
		addColumn("");
		this.setSource(source);
		
		for (int i=0; i<target.getRowCount();i++)
			addColumn((String) target.getValueAt(i, 0));
		getSource().addContextChangeListener(EditorRelationalContextModel.this);
		setTarget(target);
		for (int i=0; i<source.getRowCount();i++)
			addRow((String)source.getValueAt(i, 0));
			
		target.addContextChangeListener(this);
		
		
	}

	public void contextChanged(final ContextChangeEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				switch ( e.getAction() )
				{
				case ContextChangeEvent.ROW_ADDED : 
					System.out.println("row added");
					System.out.println(e.getSource());
					System.out.println(getTarget());
					System.out.println(getSource().getRowCount()+"  "+(getColumnCount()-1));
					if (e.getSource().equals(getSource())&&getSource().getRowCount()!=getRowCount())
						addRow((String)getSource().getValueAt(getSource().getRowCount()-1, 0));
					if (e.getSource().equals(getTarget())&&getTarget().getRowCount()!=getColumnCount()-1)
						addColumn((String)getTarget().getValueAt(getTarget().getRowCount()-1, 0));
					break;
				case ContextChangeEvent.ROW_DELETED :
					if (e.getSource().equals(getSource())&&getSource().getRowCount()!=getRowCount())
						removeRow(e.getId());
					if (e.getSource().equals(getTarget())&&getTarget().getRowCount()!=getColumnCount()-1)
						removeColumn(e.getId());
					break;
				case ContextChangeEvent.ROW_TITLE_CHANGED :
					if (e.getSource().equals(getSource()))
						renameRow(e.getId(), e.getNewName());
					if (e.getSource().equals(getTarget()))
						renameCol(e.getId(), e.getNewName());
				}	
				
				
			}
		});
		
		

	}

	public ObjectObjectContext genRelationalContext(RelationalContextFamily rcf) {
			ObjectObjectContext rc = new ObjectObjectContext(getName());
			ObjectAttributeContext source=rcf.getOAContext(this.getSource().getName());
			ObjectAttributeContext target=rcf.getOAContext(this.getTarget().getName());
			ArrayList<Entity>sourceEntities=source.getEntities();
			ArrayList<Entity>targetEntities=target.getEntities();
			rc.setSourceContext(source);
			rc.setTargetContext(target);
			rc.setOperator(scalingOperator);
			rc.setDescription(getDescription());
			
			
			for (int i=0;i<getRowCount();i++)
				for (int j =1;j<getColumnCount();j++)
					if ((Boolean)getValueAt(i, j))
						rc.addPair(sourceEntities.get(i),targetEntities.get(j-1));

			return rc;
		}

public String getScalingOperator() {
	return scalingOperator;
}
	
public void saveRCF(Writer fw) throws IOException {
		fw.write("RelationalContext "+getName()+"\n");
		fw.write("source "+getSource().getName()+"\n");
		fw.write("target "+getTarget().getName()+"\n");
		fw.write("scaling org.rcaexplore.scaling.ExistentialScaling \n");
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

public void setScalingOperator(String scalingOperator) {
	this.scalingOperator = scalingOperator;
	fireContextChangeEvent(new ContextChangeEvent(this,ContextChangeEvent.SCALING_OP_CHANGED,getName(),scalingOperator));

}

public void tableChanged(TableModelEvent arg0) {
	System.out.println(arg0);
	System.out.println(arg0.getType());
	System.out.println(arg0.getColumn());
	System.out.println(arg0.getFirstRow());
	System.out.println(arg0.getLastRow());
}

public EditorFormalContextModel getTarget() {
	return target;
}

public void setTarget(EditorFormalContextModel target) {
	this.target = target;
}

public EditorFormalContextModel getSource() {
	return source;
}

public void setSource(EditorFormalContextModel source) {
	this.source = source;
}

	
}
