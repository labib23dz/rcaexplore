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


public class StepsListModel implements ComboBoxModel<Integer> {
	
	
	COFNavigationModel model;
	
	int selected;
	EventListenerList listeners;
	
	
	public StepsListModel(COFNavigationModel model)
	{
		this.model=model;
		listeners=new EventListenerList();
		selected=model.getCofHistory().lastIndexOf(model.getCurrentConceptOrderFamily());
	}

	public Object getSelectedItem() {
		if (model!=null)
			return selected;
		return null;
	}


	public void setSelectedItem(Object arg0) {
		if (arg0 instanceof Integer)
		{
			selected=(Integer) arg0;
		}

	}


	public void addListDataListener(ListDataListener arg0) {
		
		listeners.add(ListDataListener.class, arg0);
	}


	public Integer getElementAt(int arg0) {
		
		return (Integer)arg0;

	}


	public int getSize() {
		if (model!=null)
			return model.getCofHistory().size();
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
