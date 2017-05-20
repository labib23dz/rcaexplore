
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

package org.rcaexplore;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.rcaexplore.algo.Algorithm;
import org.rcaexplore.scaling.AvailableScalingOperators;
import org.rcaexplore.scaling.ScalingOperator;

/**This class represent a predefined exploration path that permits to reproduce 
 * an RCAExplore execution*/
public class ExplorationPath {
	private ArrayList<LoadedStep> path;
	public ArrayList<LoadedStep> getPath() {
		return path;
	}
	public void setPath(ArrayList<LoadedStep> path) {
		this.path = path;
	}
	
	private FileReader reader;
	
	public class LoadedStep{
		
		
		int number;
		Hashtable<String,Algorithm> oacontextsAlgorithm=new Hashtable<>();
		Hashtable<String,Integer> oacontextsAlgoParameter=new Hashtable<>();
		Hashtable<String, ArrayList<ScalingOperator>> oocontextsScalings=new Hashtable<>();
		
		public boolean containsOAContext(String contextName){
			return oacontextsAlgorithm.containsKey(contextName);
		}

		public boolean containsOOContext(String relationName) {
			return oocontextsScalings.containsKey(relationName);
		}
		
		
	}
	
	public ExplorationPath(String filePath){
		path=new ArrayList<>();
		
		try {
			reader =new FileReader(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parse the path file. The file format is CSV.
	 * */
	public void parse() throws IOException {
		final String separator=",";
		BufferedReader input =new BufferedReader(reader);
		
		String line=input.readLine();
		
		while(line!=null){
			
			if(line.equals(""))
			{
				line=input.readLine();
			}
			else
			{
				LoadedStep s =new LoadedStep();
				path.add(s);
				
				String[] tokens = line.split("\\"+separator);
				s.number=new Integer(tokens[0].trim());
				input.readLine();
				line=input.readLine();
				while(!"OOContexts".equals(line))
				{
					tokens = line.split("\\"+separator);
					String contextName=tokens[0];
					String algo=tokens[1];
					s.oacontextsAlgorithm.put(contextName,Algorithm.getAlgo(algo));
					if (Algorithm.getAlgo(algo).hasParameter())
					{
						if (tokens.length>2)
							s.oacontextsAlgoParameter.put(contextName, Integer.parseInt(tokens[2]));
						//TODO else error
					}
					
					line=input.readLine();
				}
				line=input.readLine();
				while(line!=null&&!"".equals(line))
				{
					tokens = line.split("\\"+separator);
					String contextName=tokens[0];
					
					ArrayList<ScalingOperator> scalings=new ArrayList<>();
					for (int i=1;i<tokens.length;i++){
						if (AvailableScalingOperators.getAvailableScaling().containsKey(tokens[i]))
							scalings.add(AvailableScalingOperators.getAvailableScaling().get(tokens[i]));
						
					}
					if (scalings.isEmpty())
						scalings.add(AvailableScalingOperators.defaultScalingOperator());
					s.oocontextsScalings.put(contextName,scalings);
					line=input.readLine();
				}
				
			}
		}
		
		
		
	}

}
