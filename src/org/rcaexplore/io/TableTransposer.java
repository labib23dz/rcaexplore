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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author xdolques
 *
 */
public class TableTransposer {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
			FileReader reader=new FileReader(new File("icfca/hifiresult/step2-2.list"));
			//FileReader reader=new FileReader(new File("icfca/hifiresult/rlq_rca2.csv"));
			BufferedReader input = new BufferedReader(reader);


			String line = input.readLine();
			ArrayList<StringBuffer> extents=new ArrayList<StringBuffer>();
			while( line != null ) {
				for (int i=0;i<line.length();i++)
				{
					if (extents.size()<i+1)
						extents.add(new StringBuffer());
					extents.get(i).append(line.charAt(i));
				}
				
				line = input.readLine();
			}
			
			input.close();
			
			Collections.sort(extents, new CompareStringBuffer());
			FileWriter output=new FileWriter("icfca/hifiresult/step2-2.transposed");
			//FileWriter output=new FileWriter("icfca/hifiresult/rlq_rca2.transposed");
			
			for (StringBuffer sb : extents)
			{
				output.write(sb.toString());
				output.write("\n");
			}
			output.close();
			
	}

}
