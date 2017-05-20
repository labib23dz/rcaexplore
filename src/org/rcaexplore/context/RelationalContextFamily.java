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
/**
 * A Relational Context Family is a couple (formalContexts, relationalContexts) where formalContexts is a set of Object to Object relation context and
 * relationalContexts is a set of Objet to attribute relation context. 
 * 
 * */
public class RelationalContextFamily {

	protected ArrayList<ObjectObjectContext> relationalContexts;
	protected ArrayList<ObjectAttributeContext> formalContexts;

	
	public RelationalContextFamily() {
		super();
		this.relationalContexts = new ArrayList<ObjectObjectContext>();
		this.formalContexts = new ArrayList<ObjectAttributeContext>();
	}
	
	public ArrayList<ObjectObjectContext> getOOContexts() {
		return relationalContexts;
	}
	
	public void addRelationalContext(ObjectObjectContext rc) throws Exception {
		
		if (null!=getRelationalContext(rc.getRelationName()))
			throw new Exception("Object-Object context "+rc.getRelationName()+" already exists");
		
		this.relationalContexts.add(rc);
	}
	
	public void addFormalContext(ObjectAttributeContext fc) throws Exception {
		if (null!=getOAContext(fc.getName()))
			throw new Exception("Object-Attribute context "+fc.getName()+" already exists");
		this.formalContexts.add(fc);
	}
	
	public ArrayList<ObjectAttributeContext> getOAContexts() {
		return formalContexts;
	}
	
	public ObjectAttributeContext getOAContext(String name) {
		for (ObjectAttributeContext c: formalContexts){
			if (c.getName().equals(name))
				return c;
		}
		return null;
	}
	public ObjectObjectContext getRelationalContext(String name) {
		for (ObjectObjectContext c: relationalContexts){
			if (c.getRelationName().equals(name))
				return c;
		}
		return null;
	}
}
