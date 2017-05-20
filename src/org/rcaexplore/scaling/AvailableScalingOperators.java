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

import java.util.Hashtable;


public final class AvailableScalingOperators {
	/** map of the available scaling operators*/
	private final static Hashtable<String, ScalingOperator> availableScaling=loadAvailableScaling();
	
	public static ScalingOperator defaultScalingOperator(){
		return availableScaling.get("exist");
	}
	
	private static Hashtable<String, ScalingOperator> loadAvailableScaling() {
		Hashtable<String, ScalingOperator> result=new Hashtable<String, ScalingOperator>();
		result.put("exist", new ExistentialScaling());
		result.put("forall", new ForAllExistentialScaling());
		
		//result.put("contains", new ContainsScaling());
		result.put("containsExist", new ContainsExistScaling());
		
		ScalingOperator for30percent=new ForNearlyAllExistentialScaling();
		for30percent.setParameter(30);
		result.put("for30percent", for30percent);
		ScalingOperator for60percent=new ForNearlyAllExistentialScaling();
		for60percent.setParameter(60);
		result.put("for60percent", for60percent);
		
		/*ScalingOperator for75percent=new ForNearlyAllExistentialScaling();
		for75percent.setParameter(75);
		result.put("for75percent", for75percent);
		*/
		
		ScalingOperator contains30percent=new ContainsExistNScaling();
		contains30percent.setParameter(30);
		result.put("contains30percent", contains30percent);
		
		ScalingOperator contains60percent=new ContainsExistNScaling();
		contains60percent.setParameter(60);
		result.put("contains60percent", contains60percent);
		
		/*ScalingOperator contains75percent=new ContainsExistNScaling();
		contains75percent.setParameter(75);
		result.put("contains75percent", contains75percent);
		*/
		return result;	
	}
	
	
	/*private static Hashtable<String, ScalingOperator> loadAvailableScaling() {
		Hashtable<String, ScalingOperator> result=new Hashtable<String, ScalingOperator>();
		Properties properties = new Properties();
		try {
		    
			
			properties.load(AvailableScalingOperators.class.getResourceAsStream("/scaling.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadAvailableScalingFromProperties(properties,result);
		
		if (new File("scaling.properties").exists()) {
			Properties properties2 = new Properties();
			try {
			    properties2.load(new FileInputStream("scaling.properties"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			loadAvailableScalingFromProperties(properties2,result);
		}
		return result;
	}*/
/*
	@SuppressWarnings("unchecked")
	private static void loadAvailableScalingFromProperties(Properties properties, Hashtable<String, ScalingOperator> result) {
		for (Object p : properties.keySet()){
			String propertyName=(String) p;
			Class<ScalingOperator> c;
			try {
				c=(Class<ScalingOperator>) Class.forName(properties.getProperty(propertyName));
				try {
					result.put(propertyName, c.newInstance());
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
*/
	public static Hashtable<String, ScalingOperator> getAvailableScaling() {
		return availableScaling;
	}
	
	
	
	
}