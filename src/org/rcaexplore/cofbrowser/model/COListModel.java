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

import javax.swing.ComboBoxModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.rcaexplore.conceptorder.generic.GenericConceptOrder;



public class COListModel implements ComboBoxModel<String>{
	
	private COFNavigationModel model;
	private int selected;
	private EventListenerList listeners;
	
	
	public COListModel(COFNavigationModel model)
	{
		listeners=new EventListenerList();
		this.model=model;
		selected=0;
	}

	public Object getSelectedItem() {
		if (model.getCurrentConceptOrderFamily()!=null)
			return model.getCurrentConceptOrderFamily().getConceptOrders().get(selected).getName();
		return null;
	}


	public void setSelectedItem(Object arg0) {
		for (int i=0; i<model.getCurrentConceptOrderFamily().getConceptOrders().size();i++)
		{
			GenericConceptOrder cl=model.getCurrentConceptOrderFamily().getConceptOrders().get(i);
			if	(cl.getName().equals(arg0))
				selected=i;
		}

	}


	public void addListDataListener(ListDataListener arg0) {
		
		listeners.add(ListDataListener.class, arg0);
	}


	public String getElementAt(int arg0) {
		if (model.getCurrentConceptOrderFamily()!=null)
			return model.getCurrentConceptOrderFamily().getConceptOrders().get(arg0).getName();
		return null;

	}


	public int getSize() {
		if (model.getCurrentConceptOrderFamily()!=null)
			return model.getCurrentConceptOrderFamily().getConceptOrders().size();
		return 0;
	}


	public void removeListDataListener(ListDataListener arg0) {
		
		listeners.remove(ListDataListener.class, arg0);
	}
	
	public void fireDataChanged(){
		ListDataListener[] listenerList = (ListDataListener[])listeners.getListeners(ListDataListener.class);
		
		for(ListDataListener listener : listenerList){
			listener.contentsChanged(new ListDataEvent (this,ListDataEvent.CONTENTS_CHANGED,0,getSize()));
		}
	}

	

	

}
