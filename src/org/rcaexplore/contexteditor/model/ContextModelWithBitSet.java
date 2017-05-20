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
import java.util.BitSet;

import javax.swing.table.AbstractTableModel;

import org.rcaexplore.contexteditor.controller.ContextChangeEvent;
import org.rcaexplore.contexteditor.controller.ContextChangeListener;


public abstract class ContextModelWithBitSet extends AbstractTableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**row name increment*/
	private int createdrows=0;
	
	/**column name increment*/
	private int createdColumns=0;
	
	/** content of the table*/
	private ArrayList<RowBitSet> table;
	/** columns names*/
	private ArrayList<String> columnsNames;
	
	/** context name*/
	private String name;
	
	/**list of listener for this context*/
	private ArrayList<ContextChangeListener> ccls;

	/**description of the context*/
	private String description;

	/**Internal class to represent each row as a name and
	 * a boolean array*/
	private class RowBitSet{
		String rowName;
		BitSet row;
		int rowSize;
		public RowBitSet(String rowName, int rowSize){
			this.rowName=rowName;
			row=new BitSet(rowSize);
			this.rowSize=rowSize;
		}
		
		public Object getValueAt(int columnIndex) {
			if (columnIndex==0)
				return rowName;
			else
				return row.get(columnIndex-1);
		}
		public void setValueAt(Object aValue, int columnIndex) {
			if (columnIndex==0&&aValue instanceof String)
				rowName=(String) aValue;
			else if(columnIndex!=0&&aValue instanceof Boolean)
				row.set(columnIndex-1, (boolean) aValue);
			
		}
		public void removeColumn(int colIndex) {
			int rowIndex=colIndex-1;
			BitSet beforeIndex= row.get(0,rowSize-1);
			beforeIndex.set(rowIndex,rowSize,false);
			BitSet afterIndex=row.get(1,rowSize);
			afterIndex.set(0,rowIndex,false);
			beforeIndex.or(afterIndex);
			row=beforeIndex;
			rowSize--;
		}

		public void addColumn() {
			row.set(row.size(),false);
			rowSize++;
		}
	}
	
	
	public ContextModelWithBitSet(int row, int column) {
		table=new ArrayList<ContextModelWithBitSet.RowBitSet>();
		columnsNames=new ArrayList<String>();
		ccls=new ArrayList<ContextChangeListener>();
		description="";
		for (int i=0;i<column;i++)
			addColumn();
		for (int j=0;j<row;j++)
			addRow();
	}

	public ContextModelWithBitSet(String name) {
		this(0,0);
		setName(name);
		addColumn("");
		addRow();
	}
	
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name=name;
		
	}
	
	//--TABLE READING


	/** number of columns*/
	@Override
	public int getColumnCount() {
		
		return columnsNames.size();
	}
	
	/**number of rows*/
	@Override
	public int getRowCount() {
		return table.size();
	}
	
	/** value from the cell at the given position*/
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		return table.get(rowIndex).getValueAt(columnIndex);
	}
	
	/** is cell editable ? names are not editable, but boolean values are*/
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		return columnIndex!=0;
	}
	
	/** return column class: String for the first column (row name) and boolean for others*/
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex==0)
			return String.class;
		else
			return Boolean.class;
	}
	
	/** column name at given index*/
	@Override
	public String getColumnName(int column) {
		
		return columnsNames.get(column);
	}

	//--TABLE MODIFICATION
	
	//-REMOVAL
	/** remove the column with the given name (column names are supposed to be unique)*/
	public synchronized void removeColumn(String s) {
		int colIndex=columnsNames.indexOf(s);
		if (colIndex>=0){
			for (RowBitSet row: table)
				row.removeColumn(colIndex);
			columnsNames.remove(colIndex);
			fireTableStructureChanged();
		}
	}

	/**remove the row with the given name*/
	public synchronized void removeRow(String s) {
		int rowIndex=0;
		for (RowBitSet row: table ){
			if (row.rowName.equals(s)) {
				table.remove(row);
				break;
			} else
				rowIndex++;
		}
		fireTableRowsDeleted(rowIndex,rowIndex);
		fireContextChangeEvent(new ContextChangeEvent(this,ContextChangeEvent.ROW_DELETED,s));
	}
	
	//-CHANGE
	/** rename the column with the given name*/
	public synchronized void renameCol(String old, String title) {
		int colIndex=columnsNames.indexOf(old);
		if (colIndex!=-1) {
			columnsNames.remove(colIndex);
			columnsNames.add(colIndex, title);
			fireTableStructureChanged();
			fireContextChangeEvent(new ContextChangeEvent(this,ContextChangeEvent.COL_TITLE_CHANGED,old,title));
		}
	}
	
	/** rename the row with the given name*/
	public synchronized void renameRow(String oldName, String newName) {
		int rowIndex=0;
		for (RowBitSet row: table ){
			if (row.rowName.equals(oldName)){
				row.rowName=newName;
				break;
			}
			else 
				rowIndex++;
		}
		fireTableCellUpdated(rowIndex, 0);
		fireContextChangeEvent(new ContextChangeEvent(this,ContextChangeEvent.ROW_TITLE_CHANGED,oldName,newName));
	}
	
	/** set given value at given position*/
		@Override
	public synchronized void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		
		table.get(rowIndex).setValueAt(aValue, columnIndex);
		fireTableCellUpdated(rowIndex, columnIndex);
	}
		
	//-ADDITIONS
		
	public synchronized void addRow() {
		addRow("Object_"+createdrows++);
	}
	
	
	public synchronized void addRow(String rowName) {
		table.add(new RowBitSet(rowName, columnsNames.size())); 
		fireTableRowsInserted(getRowCount()-1, getRowCount()-1);
		
	}

	public synchronized void addColumn()	{
		addColumn("Attribute_"+createdColumns++);
	}
		
		
		
		
	public synchronized void addColumn(String string) {
			for (RowBitSet row: table)
				row.addColumn();
			columnsNames.add(string);
			fireTableStructureChanged();
		}

	
	//--ContextChangeListener
	
	public void addContextChangeListener(ContextChangeListener ccl) {
		ccls.add(ccl);
	}
	
	
	/** send the givent context event to the listeners*/
	public void fireContextChangeEvent(ContextChangeEvent e) {	
		for (ContextChangeListener ccl: ccls)
			ccl.contextChanged(e);
	}
	
	public abstract void saveRCF(Writer fw) throws IOException ;

}
