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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.rcaexplore.conceptorder.generic.GenericConcept;
import org.rcaexplore.conceptorder.generic.GenericConceptOrder;
import org.rcaexplore.conceptorder.structure.ConceptOrderFamily;
import org.rcaexplore.conceptorder.structure.IConceptOrder;

public class ImplicationList {
	
	
	
	private IConceptOrder<GenericConcept> cPoset;
	private String premisse;
	public String getPremisse() {
		return premisse;
	}
	public ImplicationList(IConceptOrder<GenericConcept> co){
		this.setcPoset(co);
	}
	public IConceptOrder<GenericConcept> getcPoset() {
		return cPoset;
	}
	public void setcPoset(IConceptOrder<GenericConcept> cPoset) {
		this.cPoset = cPoset;
	}
	
	public static void main(String[] args) {
		Hashtable<String, String> tableTaxons=new Hashtable<String, String>();
		loadIdMaps("rcft/rlq-maps/taxon-maps.csv", tableTaxons);
		Hashtable<String, String> tableTraits=new Hashtable<String, String>();
		loadIdMaps("rcft/rlq-maps/traits-maps.csv", tableTraits);
		Hashtable<String, Hashtable<String, String>> idInterpretation=new Hashtable<String, Hashtable<String,String>>();
		idInterpretation.put("taxons", tableTaxons);
		idInterpretation.put("traits", tableTraits);
		
		String folder="results/test-trophie";
		//String folder="results/data-jst/forall";
		//String folder="results/data-jst/forNall-Q345";
		CPosetAnalyzer analyzer=new CPosetAnalyzer();
		analyzer.loadCPosetFamilyHistory(folder+"/result.xml");
		
		//list of concept poset families
		ArrayList<ConceptOrderFamily<GenericConceptOrder>> coflist = analyzer.getCofHistory();
		
		//last cof
		
		ConceptOrderFamily<GenericConceptOrder> cof=coflist.get(coflist.size()-1);
		
		
		
		//take one conceptOrder
		GenericConceptOrder co = null;
		
		for (GenericConceptOrder cotmp: cof.getConceptOrders())
		{
			if (cotmp.getName().equals("stations"))
				co=cotmp;
		}
		
		
		//take one concept
		//GenericConcept c= co.getConcepts().get(5);
		
		/*analyzer.fullyDescribeConcept(c);
		ConceptDescription cd =new ConceptDescription(c);
		cd.computeDescription(new FullIntentDescriptor());
		System.out.println();
		System.out.println(cd.stringDescription());*/
		
		computeImplications(idInterpretation, folder, co, "taxons", "/taxon.txt");
		computeImplications(idInterpretation, folder, co, "PCChar", "/pcChar.txt");
		
		
		
		
	}
	public static void computeImplications(
			Hashtable<String, Hashtable<String, String>> idInterpretation,
			String folder, GenericConceptOrder co, 
			String premisse, 
			String fileName) {
		ImplicationList il_taxons=new ImplicationList(co);
		
		il_taxons.setPremisse(premisse);
		//ImplicationComputation implicationComputation = new FullImplicationComputation(il_taxons.getPremisse(), il_taxons.getcPoset());
		ImplicationComputation implicationComputation = new SimplifiedSpecificCloseImplicationComputation(il_taxons.getPremisse(), il_taxons.getcPoset());
		implicationComputation.computeImplications();
		
		
		System.out.println(implicationComputation.getRules().size());
		try {

			FileWriter fw =new FileWriter(folder+fileName);

			fw.append("number of concepts: "+il_taxons.getcPoset().getConceptNb()+"\n");
			fw.append("number of rules: "+implicationComputation.getRules().size()+"\n");
			for (ImplicationRule ir: implicationComputation.getRules())
			{
				fw.append(ir.stringDescription(idInterpretation));
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void setPremisse(String string) {
		this.premisse=string;
		
	}
	public static void loadIdMaps(String string, Hashtable<String, String> hashtable) {
		
		String separator="|";
		FileReader readerObject = null;
		try {
			readerObject = new FileReader(string);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		BufferedReader inputObject =new BufferedReader(readerObject);
		
		String lineObject = null;
		try {
			lineObject = inputObject.readLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		while(lineObject!=null)
		{
			String[] tokensObject=lineObject.split("\\"+separator);
			hashtable.put(tokensObject[0].trim(),tokensObject[1].trim());
			try {
				lineObject=inputObject.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			inputObject.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	
	

}
