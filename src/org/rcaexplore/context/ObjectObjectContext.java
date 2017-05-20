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

package org.rcaexplore.context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.rcaexplore.scaling.ExistentialScaling;
import org.rcaexplore.scaling.ForAllExistentialScaling;
import org.rcaexplore.scaling.ScalingOperator;
import org.rcaexplore.scaling.AvailableScalingOperators;


public class ObjectObjectContext  {
	
	
	private String description;
	private ScalingOperator operator;
	private Map<Entity,Set<Entity>> relation;
	private String relationName;

	private Map<Entity,Set<Entity>> reverseRelation;
	private ObjectAttributeContext sourceContext;
	private ObjectAttributeContext targetContext;
	
	public ObjectObjectContext(){
		this.relation = new LinkedHashMap<Entity, Set<Entity>>();
		this.reverseRelation = new LinkedHashMap<Entity, Set<Entity>>();
		operator=new ExistentialScaling();
		description="";
		
	}
	public ObjectObjectContext(String relationName) {
		this();
		this.relationName=relationName;
	}

	public void addPair(Entity e,Entity a) {
		if ( !relation.containsKey(e) )
			relation.put(e,new HashSet<Entity>());
		
		if ( !reverseRelation.containsKey(a) )
			reverseRelation.put(a,new HashSet<Entity>());
		
		relation.get(e).add(a);
		reverseRelation.get(a).add(e);
	}
	
	@Override
	protected ObjectObjectContext clone() throws CloneNotSupportedException {
		return (ObjectObjectContext) super.clone();
	}
	public String getDescription() {
		return description;
	}
	
	public ScalingOperator getOperator() {
		return operator;
	}
	
	public int getPairNb() {
		int p = 0;
		for( Set<Entity> attrs: relation.values() ) p += attrs.size();
		return p;
	}
	public String getRelationName() {
		return relationName;
	}
	
	
	public ObjectAttributeContext getSourceContext() {
		return sourceContext;
	}
	public ArrayList<Entity> getSourceEntities() {
		return sourceContext.getEntities();
	}
	public Set<Entity> getSourceEntities(Entity e) {
		if ( reverseRelation.containsKey(e) )
			return reverseRelation.get(e);
		else
			return new HashSet<Entity>();
	}
	public ObjectAttributeContext getTargetContext() {
		return targetContext;
	}
	public ArrayList<Entity> getTargetEntities() {
		return targetContext.getEntities();
	}
	public Set<Entity> getTargetEntities(Entity e) {
		if ( relation.containsKey(e) )
			return relation.get(e);
		else
			return new HashSet<Entity>();
	}
	
	public int getTargetEntitiesNb() {
		return getTargetEntities().size();
	}
	
	public boolean hasPair(Entity e,Entity a) {
		return this.getTargetEntities(e).contains(a);
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setOperator(ScalingOperator operator) {
		this.operator = operator;
	}
	
	@SuppressWarnings("unchecked")
	public void setOperator(String operator) {
		if (operator.equals("")){
			System.err.println("warning: no operator name given, existential used by default");
			this.operator=new ExistentialScaling();
		}
		else if (operator.equals("com.googlecode.erca.framework.algo.scaling.Narrow")){
			this.operator=new ForAllExistentialScaling();
		} else if (operator.equals("com.googlecode.erca.framework.algo.scaling.Wide")||operator.equals("org.rcaexplore.scaling.ExistentialScaling")){
			this.operator=new ExistentialScaling();
		} else if (AvailableScalingOperators.getAvailableScaling().containsKey(operator)) {
			this.operator=AvailableScalingOperators.getAvailableScaling().get(operator);
		} else {
			Class<ScalingOperator> c;
			try {
				
				c=(Class<ScalingOperator>) Class.forName(operator);
				try {
					this.operator=c.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
					this.operator=new ExistentialScaling();
				} 
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	public void setSourceContext(ObjectAttributeContext sourceContext) {
		this.sourceContext = sourceContext;
	}
	public void setTargetContext(ObjectAttributeContext targetContext) {
		this.targetContext = targetContext;
	}
	
	
	
	
}
