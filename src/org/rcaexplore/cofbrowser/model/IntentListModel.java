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
import org.rcaexplore.context.Attribute;



public class IntentListModel extends AbstractListModel<String> implements CLFListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<Attribute> intent=null;
	public IntentListModel(COFNavigationModel clf) {
		clf.addCLFListener(this);
		
	}


	public void conceptCourantChanged(ConceptCourantChangedEvent event) {
		int ancienneTaille=getSize();
		intent=null;
		fireIntervalRemoved(this, 0, ancienneTaille);
		if (event.getNewConceptCourant()!=null)
		{
		intent =event.getNewConceptCourant().getIntent();
		fireIntervalAdded(this, 0, intent.size());}

	}


	public String getElementAt(int arg0) {
		Attribute a=intent.get(arg0);
		return a.toString();
	}


	public int getSize() {
		if (intent!=null)
			return intent.size();
		else
			return 0;
	}

	public void CLFChanged(COFChangedEvent event) {}


	@Override
	public void stepChanged() {}

}
