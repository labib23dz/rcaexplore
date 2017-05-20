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

import java.util.ArrayList;
import java.util.Hashtable;

import org.rcaexplore.algo.Algorithm;
import org.rcaexplore.conceptorder.generic.GenericConcept;
import org.rcaexplore.conceptorder.generic.GenericConceptOrder;
import org.rcaexplore.conceptorder.generic.GenericRelationalAttribute;
import org.rcaexplore.conceptorder.structure.ConceptOrderFamily;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.BinaryAttribute;
import org.rcaexplore.context.Entity;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
//TODO javadoc: what kind of CLF can be parsed by this
/**
 * Object of this class will parse xml concept order family history 
 * serialized by GenerateXMLFromConceptOrderFamilyHistory
 * */
public class ParseXMLCOFHistory extends DefaultHandler{

	private ConceptOrderFamily<GenericConceptOrder> currentCof;
	private GenericConceptOrder currentConceptOrder;
	private GenericConcept currentConcept;
	private GenericRelationalAttribute currentRelAttribute;
	private Hashtable<String , Entity> entities;
	private Hashtable<String, Attribute> attributes;
	private Hashtable<String, GenericConcept> concepts;
	private Hashtable<String, GenericConcept> previousConcepts;
	private String relAttributeId;
	private ArrayList<ConceptOrderFamily<GenericConceptOrder>> cofHistory;
	
	
	private boolean isName;
	private boolean isConceptID;
	private boolean isObjectRef;
	private boolean isObject;
	private boolean isAttribute;
	private boolean isAttributeRef;
	private boolean isRelAttributeRef;
	private boolean isRelAttribute;
	private boolean isConceptRef;
	private String currentRelationName;
	private boolean isScaling;
	
	private class FakeConcept extends GenericConcept{
		FakeConcept(String id){
			setName(id);
		}
	}
	
	public ParseXMLCOFHistory(ArrayList<ConceptOrderFamily<GenericConceptOrder>> cofHistory2) {
		super();
		this.cofHistory=cofHistory2;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (isName){
			currentConceptOrder.setName(new String(ch, start, length));
			System.out.println(new String(ch, start,length));
			isName=false;
		}else if (isConceptID){
			currentConcept.setName(new String(ch, start, length));
			concepts.put(new String(ch, start, length), currentConcept);
			isConceptID=false;
		}else if (isObjectRef){
			currentConcept.getExtent().add(entities.get(new String(ch,start,length)));
			isObjectRef=false;
		}else if (isAttributeRef){
			currentConcept.getIntent().add(attributes.get(new String(ch,start,length)));
			isAttributeRef=false;
		}else if (isRelAttributeRef){
			relAttributeId+="("+new String(ch,start,length)+")";
			currentConcept.getIntent().add(attributes.get(relAttributeId));
			isRelAttributeRef=false;
		}else if (isRelAttribute){
			currentRelAttribute.setConcept(previousConcepts.get(new String(ch,start, length)));
		
			attributes.put(currentRelAttribute.toString(),currentRelAttribute);
			isRelAttribute=false;
		}else if (isObject){
			Entity ent=new Entity(new String(ch,start,length));
			entities.put(new String(ch,start,length), ent);
			currentConceptOrder.getEntities().add(ent);
			isObject=false;
		}else if (isAttribute){
			Attribute att=new BinaryAttribute(new String(ch,start,length));
			attributes.put(new String(ch,start,length),att);
			currentConceptOrder.getAttributes().add(att);
			isAttribute=false;
		}else if (isConceptRef){
			currentConcept.getParents().add(new FakeConcept(new String(ch,start,length)));
			isConceptRef=false;
		}else if (isScaling){
			currentConceptOrder.addRelation(currentRelationName, new String(ch,start,length));
			isScaling=false;
		}
		
	}

	@Override
	public void endDocument() throws SAXException {
		
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		 if (localName.equals("Step")){
				fixparents();
		 }
		
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		
	}

	

	@Override
	public void skippedEntity(String name) throws SAXException {
		
	}

	@Override
	public void startDocument() throws SAXException {
		
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if (localName.equals("RCAExplore_Document")){
			//cofHistory=new ArrayList<ConceptOrderFamily>();
		}else if (localName.equals("Step")){
			currentCof=new ConceptOrderFamily<>();
			cofHistory.add(currentCof);
			System.out.println(cofHistory.size());
			previousConcepts=concepts;
			concepts=new Hashtable<String, GenericConcept>();
		}else if (localName.equals("Lattice")){
			currentConceptOrder=new GenericConceptOrder();
			currentCof.getConceptOrders().add(currentConceptOrder);
			entities=new Hashtable<String, Entity>();
			attributes=new Hashtable<String, Attribute>();
		}else if (localName.equals("Object")){
			isObject=true;
		}else if (localName.equals("Attribute")){
			isAttribute=true;
		}else if (localName.equals("Name")){
			isName=true;
		}else if (localName.equals("Concept")){
			currentConcept=new GenericConcept();
			currentConceptOrder.getConcepts().add(currentConcept);
			currentConcept.setConceptOrder(currentConceptOrder);
		}else if (localName.equals("ID")){
			isConceptID=true;
		}else if (localName.equals("Object_Ref")){
			isObjectRef=true;
		}else if (localName.equals("Attribute_Ref")){
			isAttributeRef=true;
		}else if (localName.equals("RelationalAttribute")){
			currentRelAttribute=new GenericRelationalAttribute();
			currentRelAttribute.setRelation(atts.getValue("relation"));
			currentRelAttribute.setScaling(atts.getValue("scaling"));
			isRelAttribute=true;
		}else if (localName.equals("RelationalAttribute_Ref")){
			relAttributeId=atts.getValue("scaling")+" "+atts.getValue("relation");
			isRelAttributeRef=true;
		}else if (localName.equals("Concept_Ref")){
			isConceptRef=true;
		} else if (localName.equals("Config")){
			currentConceptOrder.setConstructionAlgorithm(Algorithm.getAlgo(atts.getValue("algo")));
		} else if (localName.equals("Relation")){
			currentRelationName=atts.getValue("name");
		} else if (localName.equals("Scaling")){
			isScaling=true;
		}
		
	}

	private void fixparents() {
		for (GenericConceptOrder co : currentCof.getConceptOrders()){
			for(GenericConcept c : co.getConcepts()){
				ArrayList<GenericConcept> toRemove=new ArrayList<GenericConcept>();
				for (GenericConcept parent : c.getParents()){
					if (parent instanceof FakeConcept)
						toRemove.add(parent);
				}
				c.removeParents(toRemove);
				for (GenericConcept removedParent : toRemove){
					c.addParent(concepts.get(removedParent.getName()));
				}
				
			}
		}

		
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		
	}

	
	
}
