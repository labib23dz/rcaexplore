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
package org.rcaexplore.cofbrowser.view;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.rcaexplore.conceptorder.generic.GenericConcept;

public class ConceptCellRenderer extends DefaultListCellRenderer{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
		//Get the selected index. (The index param isn't
		//always valid, so just use the value.)
		System.out.println(value.getClass());
		
		if (isSelected) {
		setBackground(list.getSelectionBackground());
		setForeground(list.getSelectionForeground());
		} else {
		setBackground(list.getBackground());
		setForeground(list.getForeground());
		}
		
		if (value instanceof GenericConcept){
			setText(value.toString()+" ("+((GenericConcept)value).getExtent().size()+")");
		} else {
			setText(value.toString());
		}
		
		return this;
	}
	

}
