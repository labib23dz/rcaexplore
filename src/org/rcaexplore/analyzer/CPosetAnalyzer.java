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
package org.rcaexplore.analyzer;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.rcaexplore.conceptorder.generic.GenericConcept;
import org.rcaexplore.conceptorder.generic.GenericConceptOrder;
import org.rcaexplore.conceptorder.generic.GenericRelationalAttribute;
import org.rcaexplore.conceptorder.structure.ConceptOrderFamily;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.BinaryAttribute;
import org.rcaexplore.io.ParseXMLCOFHistory;

/**This Class is the main class of the analyzing tool for concept poset family
 * it will consider at first only relational concepts that have no loop in their definition
 * */
public class CPosetAnalyzer {

	/*public static void main(String[] args) {
		CPosetAnalyzer analyzer=new CPosetAnalyzer();
		analyzer.loadCPosetFamilyHistory("data/rlq-20130430/result.xml");
		
		//list of concept poset families
		ArrayList<GenericConceptOrderFamily> coflist = analyzer.getCofHistory();
		
		//last cof
		
		GenericConceptOrderFamily cof=coflist.get(coflist.size()-1);
		
		//take one conceptOrder
		
		GenericConceptOrder co=cof.getConceptOrders().get(2);
		
		//take one concept
		GenericConcept c= co.getConcepts().get(5);
		
		analyzer.fullyDescribeConcept(c);
		ConceptDescription cd =new ConceptDescription(c);
		cd.computeDescription(new FullIntentDescriptor());
		System.out.println();
		System.out.println(cd.stringDescription());
	}*/
	
	/**indicate which step should be analyzed, default value -1 is the last one*/
	private int analyzedStep=-1;

	private ArrayList<ConceptOrderFamily<GenericConceptOrder>> cofHistory;

	/**indicate which context should be analyzed.*/
	private String mainContext;

	public CPosetAnalyzer(){
		
	}
	
	public void fullyDescribeConcept(GenericConcept c){
		for (Attribute a : c.getIntent()){
			if (a instanceof BinaryAttribute)
				System.out.print(a);
			else if (a instanceof GenericRelationalAttribute){
				GenericRelationalAttribute rat=(GenericRelationalAttribute) a;
				System.out.print(rat.getScaling()+" "+rat.getRelation()+"[ ");
				fullyDescribeConcept(rat.getConcept());
				System.out.print("]");
			}
			System.out.print(" ");
		}
	}
	
	public int getAnalyzedStep() {
		return analyzedStep;
	}
	
	public ArrayList<ConceptOrderFamily<GenericConceptOrder>> getCofHistory() {
		return cofHistory;
	}

	public String getMainContext() {
		return mainContext;
	}

	public void loadCPosetFamilyHistory(String path){
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
	}

	public void setAnalyzedStep(int analyzedStep) {
		this.analyzedStep = analyzedStep;
	}

	public void setCofHistory(ArrayList<ConceptOrderFamily<GenericConceptOrder>> cofHistory) {
		this.cofHistory = cofHistory;
	}
	
	public void setCPosetFamilyHistory(ArrayList<ConceptOrderFamily<GenericConceptOrder>> cofHistory)
	{
		this.cofHistory=cofHistory;
	}
	
	public void setMainContext(String mainContext) {
		this.mainContext = mainContext;
	}
}
