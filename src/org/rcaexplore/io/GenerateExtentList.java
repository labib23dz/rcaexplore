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
 *  
 */
package org.rcaexplore.io;

import java.io.FileWriter;
import java.io.IOException;

import org.rcaexplore.conceptorder.optimized.OptimizedConcept;
import org.rcaexplore.conceptorder.optimized.OptimizedConceptOrder;


public class GenerateExtentList extends GenerateCode {
	
	private OptimizedConceptOrder conceptOrder;
	

	public GenerateExtentList(FileWriter buffer,OptimizedConceptOrder lattice) {
		super(buffer);
		this.conceptOrder = lattice;
	}
	
	
	public void generateCode() throws IOException{
		
		StringBuffer[] lists=new StringBuffer[conceptOrder.getEntityNb()];
		for (int i=0;i<conceptOrder.getEntityNb();i++)
			lists[i]=new StringBuffer();
		for(OptimizedConcept c: conceptOrder.getConcepts() ) {
			for (int i =0; i<c.getBitExtent().size();i++)
				if (c.getBitExtent().get(i))
					lists[i].append("1");
				else
					lists[i].append("0");
		}
				
		for (int i=0;i<conceptOrder.getEntityNb();i++){
			append(lists[i].toString());
			appendLine("");
		}
		buffer.flush();
	}


	
}
