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
package org.rcaexplore.cofbrowser.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.rcaexplore.cofbrowser.CLFListener;
import org.rcaexplore.cofbrowser.COFChange;
import org.rcaexplore.cofbrowser.COFChangedEvent;
import org.rcaexplore.conceptorder.generic.GenericConcept;
import org.rcaexplore.conceptorder.generic.GenericConceptOrder;
import org.rcaexplore.conceptorder.generic.GenericRelationalAttribute;
import org.rcaexplore.conceptorder.structure.ConceptOrderFamily;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.io.ParseXMLCOFHistory;

public class COFNavigationModel {
	

	private GenericConcept currentConcept;
	private EventListenerList listeners;
	private String clfPath;
	private ArrayList<ConceptOrderFamily<GenericConceptOrder>> cofHistory;
	private int currentStep=0;
	private GenericConceptOrder currentConceptOrder;

	/**Constructor*/
	public COFNavigationModel(String fileURI){
		super();
		clfPath=fileURI;
		loadCOF(clfPath);
		listeners=new EventListenerList();
		currentStep=0;
		currentConceptOrder=cofHistory.get(currentStep).getConceptOrders().get(0);
		currentConcept=currentConceptOrder.getConcepts().get(0);
		fireCOFChange(COFChange.currentStepChanged);
		
	}
	/** The current concept on wich we are focusing*/
	public GenericConcept getCurrentConcept() {
		return currentConcept;
	}

	/**set the current concept using name*/
	public void setCurrentConcept(String currentConceptName) {
		currentConcept =null;
		//for (ConceptOrder lattice : cofHistory.get(currentStep).getConceptOrders()) {
			for (GenericConcept c : currentConceptOrder.getConcepts()) {
				if (c.getName().equals(currentConceptName)) {
					currentConcept=c;
					break;
				}
			}
		//}
		fireCOFChange(COFChange.currentConceptChanged);
	}
	
	public void setCurrentStep(int s){
		if (s<cofHistory.size() && s>=0){
			currentStep=s;
			currentConceptOrder= cofHistory.get(s).getConceptOrder(currentConceptOrder.getName());
			if (currentConcept!=null)
				currentConcept=getConceptInCurrentConceptOrder(currentConcept.getName());
			fireCOFChange(COFChange.currentStepChanged);
		}
	}
	
	public int getCurrentStep(){
		return currentStep;
	}
	
	/** find a concept in current COF (i.e. at the current step) with the name passed as parameter*/
	public GenericConcept getConceptInCurrentConceptOrder(String conceptName) {
		//for(ConceptOrder cl:getCurrentConceptOrderFamily().getConceptOrders()) {
			for(GenericConcept c : currentConceptOrder.getConcepts()) {
				if (c.getName().equals(conceptName)) {
					return c;
				}
			}
		//}
		return null;
	}
	public String getConceptCourantName() {
		if (null==currentConcept)
			return "null";
		else 
			return currentConcept.getName();
	}
	
	
	/** custom simplified Intent will only keep the most specific concepts of the simplified intent*/
	public ArrayList<Attribute> getCustomSimplifiedIntent()
	{
		if (null==currentConcept)
			return null;
		ArrayList<Attribute> intent=currentConcept.getIntent();
		
		ArrayList<Attribute> tmp=new ArrayList<Attribute>();
		boolean isInSimplifiedIntent;
		for (int i=0;i<intent.size();i++)
		{
			isInSimplifiedIntent=true;
			
			GenericRelationalAttribute a1=null;
			if(intent.get(i) instanceof GenericRelationalAttribute) 
				a1=(GenericRelationalAttribute)intent.get(i);
			else
				continue;
			for (int j=0;j<intent.size();j++)
			{
				
				GenericRelationalAttribute a2=null;
				if (intent.get(j) instanceof GenericRelationalAttribute)
					a2=(GenericRelationalAttribute)intent.get(j);
				else 
					continue;
				GenericConcept c1=a1.getConcept();
				
				
				if (i!=j
						&& a1.getRelation().equals(a2.getRelation())
						&& a1.getScaling().equals(a2.getScaling()))
				{
					GenericConcept c2=a2.getConcept();
					
					if (c2.getAllParents().contains(c1))
						{
							System.out.println(c1+">"+c2);
							System.out.println(i+" "+j);
						
							isInSimplifiedIntent=false;
							break;
						}
				}
			}
			
			if (isInSimplifiedIntent)
				tmp.add(a1);
		}
		return tmp;
		
		
	}
	/** return a list of all the concepts from the current COF*/
	public Object[] getAllConceptsInCurrentConceptOrder() {
		ArrayList<String> allConcepts=new ArrayList<String>();
		//for(ConceptOrder cl:getCurrentConceptOrderFamily().getConceptOrders()) {
			for(GenericConcept c : currentConceptOrder.getConcepts()) {
				allConcepts.add(c.getName());
			}
		//}
		Collections.sort(allConcepts);
		return allConcepts.toArray();
	}
	
	/** return a list of all the concepts from the current COF*/
	public GenericConcept[] getAllConceptsInCurrentConceptOrderOverThreshold(int threshold) {
		ArrayList<GenericConcept> allConcepts=new ArrayList<GenericConcept>();
		//for(ConceptOrder cl:getCurrentConceptOrderFamily().getConceptOrders()) {
			for(GenericConcept c : currentConceptOrder.getConcepts()) {
				if (c.getExtent().size()>=threshold)
					allConcepts.add(c);
			}
		//}
		return allConcepts.toArray(new GenericConcept[0]);
		
	}
	
	/** The set of the concept order families*/
	public ArrayList<ConceptOrderFamily<GenericConceptOrder>> getCofHistory() {
		return cofHistory;
	}
	public ConceptOrderFamily<GenericConceptOrder> getCurrentConceptOrderFamily() {
		return cofHistory.get(currentStep);
	}

	/**return the current concept order*/
	public GenericConceptOrder getCurrentConceptOrder() {
		return currentConceptOrder;
	}
	public void addCLFListener(CLFListener listener){
		listeners.add(CLFListener.class, listener);
	}
	public void removeCLFListener(CLFListener l){
		 listeners.remove(CLFListener.class, l);
	}
	/** load a COF*/
	public void loadCOF(String path){
		String uri = path;

	    cofHistory = new ArrayList<>();

	    try {
	    	SAXParserFactory factory = SAXParserFactory.newInstance();
	        factory.setValidating(true);
	        factory.setNamespaceAware(true);
	        SAXParser parser = factory.newSAXParser();
	        parser.parse(uri, new ParseXMLCOFHistory(cofHistory));  
	    	
	    } catch (Throwable t) {
	            t.printStackTrace();
	    }
	    
	    currentStep=cofHistory.size()-1;
		
	}
	
	/** save the model, NYI*/
	public void saveModel(){
		/*try {
			ErcaIO.saveErcaObject(clf,clfPath+".edited.clf");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		System.out.println("saving file is not possible yet");
	}
	
	/**load a model ???*/
	public void loadModel(){
		loadCOF(clfPath+".edited.clf");
		
	}
	
	/** generate a visualization of the concept orders*/
	public void generatePicture() 
	{
		System.out.println("not yet implemented !!");
		/*ClfToDot dot;
		dot = new ClfToDot(clf);
		dot.generateCode();
		try {
			dot.toFile(clfPath+"edited.dot");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	/**fire an event to tell the current concept has changed*/
	/*public void fireCurrentConceptChanged(){
		CLFListener[] listenerList = (CLFListener[])listeners.getListeners(CLFListener.class);
		
		for(CLFListener listener : listenerList){
			listener.conceptCourantChanged(new ConceptCourantChangedEvent(this, getCurrentConcept()));
		}
	}*/
	/** Fire a new event telling that the COF has changed*/
	/*public void fireCLFChanged()
	{
		CLFListener[] listenerList = (CLFListener[])listeners.getListeners(CLFListener.class);
		for(CLFListener listener : listenerList){
			listener.CLFChanged(new COFChangedEvent(this, getCurrentConceptOrderFamily()));
		}
	}*/
	
	public void fireCOFChange(COFChange change){
		CLFListener[] listenerList = (CLFListener[])listeners.getListeners(CLFListener.class);
		for(CLFListener listener : listenerList){
			listener.CLFChanged(new COFChangedEvent(change));
		}
	}
	
	/** check if the name name is already a name used for a concept in current COF*/
	private boolean nameExists(String name)
	{
		for (GenericConceptOrder lattice : getCurrentConceptOrderFamily().getConceptOrders())
		{
			for (GenericConcept c: lattice.getConcepts())
			{
				if (c.getName().equals(name))
					return true;
			}
		}
		return false;
	}
	
	/**rename the concepts named oldname by newName in currentCOF*/
	public void renameAll(String oldName, String newName) {
		if (! nameExists(newName))
		{
			for (ConceptOrderFamily<GenericConceptOrder> cof : cofHistory){
				for (GenericConceptOrder lattice : cof.getConceptOrders()) {
					for (GenericConcept c : lattice.getConcepts()) {
						if (c.getName().equals(oldName)) {
							c.setName(newName);
						}
					}
				}
			}
			fireCOFChange(COFChange.currentConceptChanged);
		}
	}

	/**modify the singletons names from the concept order named as string
	 * if a concept contains only one object, the name of the concept is 
	 * becoming the name of the object.
	 * */
	public void modifySingletonsInCO(String string) {
		GenericConceptOrder lattice = getCurrentConceptOrderFamily().getConceptOrder(string);
		for (GenericConcept c : lattice.getConcepts()) {
			if (c.getExtent().size()==1) {
				renameAll(c.getName(),c.getExtent().get(0).getName());
			}
		}
	}
	
	
	
	/** return an array of concept names from a list of concepts*/
	public static Object[] nameArray(List<GenericConcept> l) {
		ArrayList<String> nameList=new ArrayList<String>();
		for (GenericConcept c : l) {
			nameList.add(c.getName());
		}
		return nameList.toArray();
	}

	/**  check the existence of a concept named as the string passed as parameter in current COF*/
	public boolean containsInCurrentCOF(String text) {
		for(GenericConceptOrder cl:getCurrentConceptOrderFamily().getConceptOrders()) {
			for(GenericConcept c : cl.getConcepts()) {
				if (c.getName().equals(text)) {
					return true;
				}
			}
		}
		return false;
	}
	public void setCurrentConceptOrder(String string) {
		
		currentConceptOrder= cofHistory.get(currentStep).getConceptOrder(string);
		
		if (currentConcept!=null)
			currentConcept=getConceptInCurrentConceptOrder(currentConcept.getName());

		fireCOFChange(COFChange.currentConceptOrderChanged);
		
	}
	
	
}