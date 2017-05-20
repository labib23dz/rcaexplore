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
package org.rcaexplore.conceptorder.optimized;
import java.util.Comparator;


/**
 * Concept comparator that sorts concepts starting from smallest extent 
 * */
public class CompareOptimizedConcepts implements Comparator<OptimizedConcept>{

		

		@Override
		public int compare(OptimizedConcept c1, OptimizedConcept c2) {
			if (c1.getBitExtent().cardinality()!=c2.getBitExtent().cardinality())
				return ((Integer)c1.getBitExtent().cardinality()).compareTo(c2.getBitExtent().cardinality());
			else
				return c1.getName().compareTo(c2.getName());
			
		}
	}

