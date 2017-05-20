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
package org.rcaexplore.cofbrowser.model;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

import org.rcaexplore.cofbrowser.CLFListener;
import org.rcaexplore.cofbrowser.COFChangedEvent;
import org.rcaexplore.cofbrowser.ConceptCourantChangedEvent;
import org.rcaexplore.conceptorder.generic.GenericConcept;



public class ParentsListModel extends AbstractListModel<String> implements CLFListener {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<GenericConcept> parents=null;
	public ParentsListModel(COFNavigationModel clf) {
		clf.addCLFListener(this);
		
	}


	public void conceptCourantChanged(ConceptCourantChangedEvent event) {
		int ancienneTaille=getSize();
		parents=null;
		fireIntervalRemoved(this, 0, ancienneTaille);
		if (event.getNewConceptCourant()!=null)
		{
		parents =new ArrayList<GenericConcept>();
		parents.addAll(event.getNewConceptCourant().getAllParents());
		fireIntervalAdded(this, 0, parents.size());}

	}


	public String getElementAt(int arg0) {
		return parents.get(arg0).getName();
	}


	public int getSize() {
		if (parents!=null)
			return parents.size();
		else
			return 0;
	}


	public void CLFChanged(COFChangedEvent event) {}


	@Override
	public void stepChanged() {}

}
