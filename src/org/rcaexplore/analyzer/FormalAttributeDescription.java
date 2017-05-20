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
package org.rcaexplore.analyzer;

import java.util.Hashtable;

public class FormalAttributeDescription extends AttributeDescription {
	private String attributeName; 
	public FormalAttributeDescription(String attributeName, String context){
		super(context);
		this.setAttributeName(attributeName);
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	@Override
	public String stringDescription(Hashtable<String, Hashtable<String, String>> idInterpretation) {
		
		if (idInterpretation.containsKey(getContext()))
		{
			if (idInterpretation.get(getContext()).containsKey(getAttributeName()))
				return idInterpretation.get(getContext()).get(getAttributeName());
		}
			
		return getAttributeName();
	}

	
}
