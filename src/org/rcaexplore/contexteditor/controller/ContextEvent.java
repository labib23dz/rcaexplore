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
package org.rcaexplore.contexteditor.controller;

import java.util.EventObject;

import org.rcaexplore.contexteditor.model.ContextModelWithBitSet;

public class ContextEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private String action;
	
	private String contextName;
	
	private ContextModelWithBitSet context;
	
	public String getAction() {
		return action;
	}
	
	public String getContextName() {
		return contextName;
	}
	
	public ContextModelWithBitSet getContext() {
		return context;
	}
	
	public ContextEvent(Object source, String action,String contextName, ContextModelWithBitSet cm)
	{
		super(source);
		this.action = action;
		this.context = cm;
		this.contextName = contextName;
	}
	
	public ContextEvent(Object source, String action)
	{
		this(source, action,null,null);
	}

}
