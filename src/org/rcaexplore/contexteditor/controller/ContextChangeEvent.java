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
//TODO sanitize this mess
public class ContextChangeEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	public final static int ROW_DELETED=0;

	public final static int ROW_ADDED=1;

	public final static int ROW_TITLE_CHANGED=2;

	public final static int COL_DELETED=3;

	public final static int COL_ADDED=4;

	public final static int COL_TITLE_CHANGED=5;
	
	public final static int ALGO_CHANGED=6;
	
	public final static int DESCRIPTION_CHANGED=7;
	
	public final static int SCALING_OP_CHANGED=8;

	public final static int ALGO_PARAM_CHANGED = 9;

	private int action=-1;
	
	private String id="";
	
	private String newName="";

	private String newParam="";
	
	public int getAction() {
		return action;
	}
	
	public String getId() {
		return id;
	}
	
	public String getNewName() {
		return newName;
	}
	
	public ContextChangeEvent(Object source, int action) {
		super(source);
		this.action = action;
	}
	
	public ContextChangeEvent(Object source, int action, String id) {
		super(source);
		this.action = action;
		this.id = id;
	}
	
	public ContextChangeEvent(Object source, int action, String id, String newName) {
		super(source);
		this.action = action;
		this.id = id;
		this.newName = newName;
	}

	public ContextChangeEvent(Object source, int action, String id, String newName, String newParam) {
		super(source);
		this.action = action;
		this.id = id;
		this.newName = newName;
		this.newParam=newParam;
	}

	public String getNewParam() {
		return newParam;
	}
}
