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
package org.rcaexplore.conceptorder.structure;

import java.util.List;
import java.util.Set;

import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.Entity;

public interface IConcept {

	public String getName();
	public int getId();
	public void setName(String name);
	public List<Entity> getExtent();
	public List<Attribute> getIntent();
	public List<Entity> getSimplifiedExtent();
	public List<Attribute> getSimplifiedIntent();
	public Set<? extends IConcept> getParents();
	public Set<? extends IConcept> getAllParents();
	public Set<? extends IConcept> getChildren();
	public Set<? extends IConcept> getAllChildren();
}
