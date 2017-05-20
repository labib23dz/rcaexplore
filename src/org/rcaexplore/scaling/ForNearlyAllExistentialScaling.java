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
public class ForNearlyAllExistentialScaling extends ScalingOperator {

	private int x;
	
	
	@Override
	public boolean scale(Entity e, ArrayList<Entity> c, ObjectObjectContext rc) {
		
		int maxLinks=rc.getTargetEntities(e).size();
		int threshold=(x*maxLinks)/100;
		
		int miss=0;
		for (Entity e2 : rc.getTargetEntities(e))
		{
			if (!c.contains(e2)) {
				miss++;
				if (maxLinks-miss<=threshold)
					return false;
			}
		}
		return !rc.getTargetEntities(e).isEmpty() && !c.isEmpty();
	}

	@Override
	public String getName() {

		return "for"+x+"percent";
	}
	@Override
	public boolean hasParameter(){
		return true;
	}

	@Override
	public void setParameter(int param) {
		x=param;
	}
	
	

}
