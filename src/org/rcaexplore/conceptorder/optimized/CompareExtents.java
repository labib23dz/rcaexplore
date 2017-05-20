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

import cern.colt.bitvector.BitVector;


/**
 * Concept comparator that sorts concepts starting from smallest extent 
 * */
public class CompareExtents implements Comparator<BitVector>{

		

		@Override
		public int compare(BitVector c1, BitVector c2) {
		
			int c1Cardinality = c1.cardinality();
			int c2Cardinality = c2.cardinality();
			if (c1Cardinality!=c2Cardinality)
				return ((Integer)c1Cardinality).compareTo(c2Cardinality);
			else
			{
				int b;
				for (int i =0;i<c1.size();i++){
					b=((Boolean)c1.get(i)).compareTo(c2.get(i));
					if (b!=0)
						return b;
				}
				
				return 0;
				
			}
			
		}
	}

