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
package org.rcaexplore.io;

/**
 * @author xdolques
 *
 */
public class ParserException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String msg;
	private int line;
	private int offset;

	public ParserException(String msg, int line, int offset){
		this.msg=msg;
		this.line=line;
		this.offset=offset;
	}

	@Override
	public String getMessage() {
		
		return msg+" Error at line: "+line+", char: "+offset;
	}
	
	
	
	
	
}
