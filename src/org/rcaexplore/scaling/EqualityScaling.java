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
package org.rcaexplore.scaling;

import java.util.ArrayList;

import org.rcaexplore.context.Entity;
import org.rcaexplore.context.ObjectObjectContext;
public class EqualityScaling extends ScalingOperator {

	@Override
	public boolean scale(Entity e, ArrayList<Entity> c, ObjectObjectContext rc) {
		
		if (c.size()!=0 && c.size()==rc.getTargetEntities(e).size())
		{
				for (Entity e2 : c)
			{
				if (!rc.hasPair(e, e2)) {
					return false;
				}
			}
			return true;
		}
		else
			return false;
	}

	@Override
	public String getName() {
		return "equality";
	}
	
	

}
