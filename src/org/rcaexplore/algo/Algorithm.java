/*
 * Copyright (c) 2014, ENGEES. All rights reserved.
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


package org.rcaexplore.algo;

public enum Algorithm {
	FCA("fca", false),
	ARES("ares",false),
	ACPOSET("acposet",false),
	OCPOSET("ocposet",false),
	ICEBERG("iceberg",50);
	
	
	/** name of the algorithm*/
	private final String name;
	/** if the algorithm needs a parameter (e.g. iceberg)*/
	private final boolean parameter;
	/** parameter default value */
	private final int defaultValue;
	
	
	private Algorithm(String name, boolean parameter) {
		this.name=name;
		this.parameter=parameter;
		this.defaultValue=0;
	}

	private Algorithm(String name, int defaultValue)
	{
		this.name=name;
		this.parameter=true;
		this.defaultValue=defaultValue;
	}
	

	public String getName() {
		return name;
	}

	public String toString() {
		return getName();
	}
	
	public boolean hasParameter() {
		return parameter;
	}
	
	public int getDefaultParameterValue(){
		return defaultValue;
	}
	
	/**return an algorithm from its name, null if it does not exist*/
	public static Algorithm getAlgo(String name){
		for (Algorithm algo: values())
			if (algo.getName().equals(name))
				return algo;
		return null;
	}
	
	public static Algorithm getDefaultAlgo(){
		return FCA;
	}
}
