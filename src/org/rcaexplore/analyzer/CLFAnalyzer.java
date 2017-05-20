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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.rcaexplore.conceptorder.generic.GenericConcept;
import org.rcaexplore.conceptorder.generic.GenericConceptOrder;
import org.rcaexplore.conceptorder.generic.GenericRelationalAttribute;
import org.rcaexplore.conceptorder.structure.ConceptOrderFamily;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.BinaryAttribute;
import org.rcaexplore.io.ParseXMLCOFHistory;

public class CLFAnalyzer {
	private ArrayList<ConceptOrderFamily<GenericConceptOrder>> cofHistory;
	
	private ArrayList<ResultingRule> rulesTraitImpact=new ArrayList<ResultingRule>();;
	private ArrayList<ResultingRule> rulesPCImpact= new ArrayList<ResultingRule>();
	

	private int statImplicationPC=0;
	private int statImplicationTrait=0;

	
	public int getStatImplicationPC() {
		return statImplicationPC;
	}
	public int getStatImplicationTrait() {
		return statImplicationTrait;
	}



	
	
	public void loadCLF(String path){
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
	public void makeImplicationRulesTraitImpact( String traitToConsider){

		//last cof
		ConceptOrderFamily<GenericConceptOrder> cof=cofHistory.get(cofHistory.size()-1);

		//stations poset
		GenericConceptOrder stations = cof.getConceptOrder("stations");


		for (GenericConcept c : stations.getConcepts()){
			if (!c.getExtent().isEmpty())
				for (Attribute stationAtt : c.getSimplifiedIntent()){
					if (stationAtt instanceof GenericRelationalAttribute && ((GenericRelationalAttribute) stationAtt).getConcept().getConceptOrder().getName().equals("taxons") )
					{
						GenericConcept taxonConcept = ((GenericRelationalAttribute) stationAtt).getConcept();
						if (! taxonConcept.getExtent().isEmpty())
							for (Attribute taxonAtt : taxonConcept.getSimplifiedIntent())
							{
								if (taxonAtt instanceof GenericRelationalAttribute)
								{
									GenericConcept traitConcept= ((GenericRelationalAttribute) taxonAtt).getConcept();
									if (!traitConcept.getExtent().isEmpty())
										for (Attribute traitAtt : traitConcept.getIntent()){
											if (traitAtt instanceof BinaryAttribute && ((BinaryAttribute)traitAtt).getValue().equals(traitToConsider)){

												//we have the right concepts here for the premise 
												boolean first=true;
												ResultingRule currentRule=new ResultingRule();

												for (Attribute conclusionAtt : c.getIntent()){
													if (conclusionAtt instanceof GenericRelationalAttribute && ((GenericRelationalAttribute) conclusionAtt).getConcept().getConceptOrder().getName().equals("PCChar") )
													{
														GenericConcept pcConcept=((GenericRelationalAttribute)conclusionAtt).getConcept();
														if (!pcConcept.getExtent().isEmpty())
															
															
															for (Attribute pcAtt : pcConcept.getIntent())
															{
																if (pcAtt instanceof BinaryAttribute){
																	
																	
																	//fw.append("stationConcept : "+c+" taxonConcept : "+taxonConcept+" traitConcept : "+traitConcept+ "pcConcept : "+((GenericRelationalAttribute) conclusionAtt).getConcept()+"\n");
																	if (first){
																		statImplicationTrait++;
																		currentRule.rule="exists level ["
																				+((GenericRelationalAttribute)stationAtt).getRelation()
																				+"] taxon with trait ["
																				+((BinaryAttribute)traitAtt).getValue()
																				+"] at level ["+((GenericRelationalAttribute)taxonAtt).getRelation()
																				+"] implies"
																				+" PC char ";
																		
																		first=false;
																	}
																	else
																		currentRule.rule+="\n\t and ";
																	
																	currentRule.rule+="["+((BinaryAttribute)pcAtt).getValue()
																		+"] at level ["
																		+((GenericRelationalAttribute)conclusionAtt).getRelation()
																		+"]";
																}
															}
															
													}
												}
												if(!first){
													
													currentRule.support=c.getExtent().size();
													currentRule.stations=stations.getEntityNb();
													rulesTraitImpact.add(currentRule);
												}
													
													//fw.append(" (support "
													//	+c.getExtent().size()+"/"+stations.getEntityNB()
													//	+")\n");
											}
										}
								}
							}
					}
				}
		}
		
		

	}

	public void makeImplicationRulesPCImpact( String pcToConsider){

		//StringBuffer fw=new StringBuffer();
		
		//last cof
		ConceptOrderFamily<GenericConceptOrder> cof=cofHistory.get(cofHistory.size()-1);
		//stations poset
		GenericConceptOrder stations = cof.getConceptOrder("stations");
		for (GenericConcept c : stations.getConcepts()){

			// first check if the trait to consider is there
			if (!c.getExtent().isEmpty()&& c.getExtent().size()>(stations.getEntityNb()*0/100))
				for (Attribute stationAtt : c.getSimplifiedIntent()){
					if (stationAtt instanceof GenericRelationalAttribute 
							&& ((GenericRelationalAttribute) stationAtt).getConceptOrderName().equals("PCChar") ) {
						GenericConcept pcCharConcept = ((GenericRelationalAttribute) stationAtt).getConcept();
						if (! pcCharConcept.getExtent().isEmpty())
							for (Attribute pcAtt : pcCharConcept.getSimplifiedIntent())
							{
								if (pcAtt instanceof BinaryAttribute 
										&& ((BinaryAttribute)pcAtt).getValue().equals(pcToConsider))
								{
									//we have the right concepts here for the premise 

									for (Attribute conclusionAtt : c.getIntent()){
										if (conclusionAtt instanceof GenericRelationalAttribute 
												&& ((GenericRelationalAttribute) conclusionAtt).getConceptOrderName().equals("taxons") )
										{
											GenericConcept taxonConcept=((GenericRelationalAttribute)conclusionAtt).getConcept();
											if (!taxonConcept.getExtent().isEmpty()){
												statImplicationPC++;
												
												ResultingRule currentRule=new ResultingRule();
												//fw.append("stationConcept : "+c+" taxonConcept : "+taxonConcept+" traitConcept : "+traitConcept+ "pcConcept : "+((GenericRelationalAttribute) conclusionAtt).getConcept()+"\n");
												currentRule.rule="exists level ["
														+((GenericRelationalAttribute)stationAtt).getRelation()
														+"] PC Char ["
														+((BinaryAttribute)pcAtt).getValue()
														//+"] at level ["+((GenericRelationalAttribute)pcAtt).getRelation()
														+"] implies exists taxons at level ["
														+((GenericRelationalAttribute) conclusionAtt).getRelation()
														+"] with traits ";
												boolean first=true;
												for (Attribute taxonsAtt : taxonConcept.getIntent())
												{
													if (taxonsAtt instanceof GenericRelationalAttribute 
															&& ((GenericRelationalAttribute) taxonsAtt).getConceptOrderName().equals("traits") ) {											

														GenericConcept traitConcept=((GenericRelationalAttribute)taxonsAtt).getConcept();
														if(!traitConcept.getExtent().isEmpty()) {

															if (first) 
																first=false;
															else
																currentRule.rule+="\n\tand ";
															
															for (Attribute traitAtt: traitConcept.getIntent()) {
																currentRule.rule+="["+((BinaryAttribute)traitAtt).getValue()
																		+"] at level ["
																		+((GenericRelationalAttribute)taxonsAtt).getRelation()+"] ";

															}
															
														}
													}
												}
												
												currentRule.support=c.getExtent().size();
												currentRule.stations=stations.getEntityNb();
												rulesPCImpact.add(currentRule);
											}
										}
									}
								}
							}
					}
				}
		}
		
		//return fw.toString();

	}


	public String makeAssociationRulesTraitImpact( String traitToConsider){
		StringBuffer fw=new StringBuffer();
		//last cof
		ConceptOrderFamily<GenericConceptOrder> cof=cofHistory.get(cofHistory.size()-1);
		//stations poset
		GenericConceptOrder stations = cof.getConceptOrder("stations");


		for (GenericConcept c : stations.getConcepts()){

			// first check if the trait to consider is there
			if (!c.getExtent().isEmpty() && c.getExtent().size()>(stations.getEntityNb()*80/100))
				for (Attribute stationAtt : c.getSimplifiedIntent()){
					if (stationAtt instanceof GenericRelationalAttribute && ((GenericRelationalAttribute) stationAtt).getConcept().getConceptOrder().getName().equals("taxons") )
					{
						GenericConcept taxonConcept = ((GenericRelationalAttribute) stationAtt).getConcept();
						if (! taxonConcept.getExtent().isEmpty())
							for (Attribute taxonAtt : taxonConcept.getSimplifiedIntent())
							{
								if (taxonAtt instanceof GenericRelationalAttribute)
								{
									GenericConcept traitConcept= ((GenericRelationalAttribute) taxonAtt).getConcept();
									if (!traitConcept.getExtent().isEmpty())
										for (Attribute traitAtt : traitConcept.getIntent()){
											if (traitAtt instanceof BinaryAttribute && ((BinaryAttribute)traitAtt).getValue().equals(traitToConsider)){

												//we have the right concepts here for the premise 

												for(GenericConcept child : c.getChildren()){
													float confidence=new Integer(child.getExtent().size()).floatValue()/new Integer(c.getExtent().size()).floatValue();
													if (confidence>0.8)
														for (Attribute conclusionAtt : child.getIntent()){
															if (conclusionAtt instanceof GenericRelationalAttribute && ((GenericRelationalAttribute) conclusionAtt).getConcept().getConceptOrder().getName().equals("PCChar") )
															{
																GenericConcept pcConcept=((GenericRelationalAttribute)conclusionAtt).getConcept();
																if (!pcConcept.getExtent().isEmpty())
																	for (Attribute pcAtt : pcConcept.getIntent())
																	{
																		if (pcAtt instanceof BinaryAttribute){

																			//fw.append("stationConcept : "+c+" taxonConcept : "+taxonConcept+" traitConcept : "+traitConcept+ "pcConcept : "+((GenericRelationalAttribute) conclusionAtt).getConcept()+"\n");
																			fw.append("exists level ["
																					+((GenericRelationalAttribute)stationAtt).getRelation()
																					+"] taxon with trait ["
																					+((BinaryAttribute)traitAtt).getValue()
																					+"] at level ["+((GenericRelationalAttribute)taxonAtt).getRelation()
																					+"] implies"
																					+" PC char ["
																					+((BinaryAttribute)pcAtt).getValue()
																					+"] at level ["
																					+((GenericRelationalAttribute)conclusionAtt).getRelation()
																					+"] (support "
																					+c.getExtent().size()+"/"+stations.getEntityNb()
																					+", confidence" 
																					+confidence
																					+")\n");
																		}
																	}
															}
														}
												}
											}
										}
								}
							}
					}
				}
		}
		return fw.toString();
	}

	public void makeAllImplicationRulesPCImpact(String file) throws IOException{
		FileWriter fw=new FileWriter(file);
		

		//last cof
		ConceptOrderFamily<GenericConceptOrder> cof=cofHistory.get(cofHistory.size()-1);
		//traits poset
		GenericConceptOrder co = cof.getConceptOrder("PCChar");
		for (Attribute att : co.getAttributes())
		{
			if (att instanceof BinaryAttribute){
				makeImplicationRulesPCImpact(((BinaryAttribute) att).getValue());
			}
		}
		
		Collections.sort(rulesPCImpact,Collections.reverseOrder());
		for (ResultingRule r : rulesPCImpact)
		{
			fw.append(r.rule+" (support "+r.support+"/"+r.stations+")\n");
		}
		fw.close();
		
	}
	
	public void makeAllImplicationRulesTraitImpact(String file) throws IOException{
		FileWriter fw=new FileWriter(file);
		
		//last cof
		ConceptOrderFamily<GenericConceptOrder> cof=cofHistory.get(cofHistory.size()-1);
		//traits poset
		GenericConceptOrder co = cof.getConceptOrder("traits");
		for (Attribute att : co.getAttributes())
		{
			if (att instanceof BinaryAttribute){
				makeImplicationRulesTraitImpact(((BinaryAttribute) att).getValue());
			}
		}

		
		Collections.sort(rulesTraitImpact,Collections.reverseOrder());
		for (ResultingRule r : rulesTraitImpact)
		{
			fw.append(r.rule+" (support "+r.support+"/"+r.stations+")\n");
		}
		
		
		fw.close();
		
	}
	



	public static void main(String[] args) {
		CLFAnalyzer analyzer=new CLFAnalyzer();

		analyzer.loadCLF("data/rlq-20130430/result.xml");
		//analyzer.analyze();
		try {
			analyzer.makeAllImplicationRulesPCImpact("analyze/pcrules.txt");
			analyzer.makeAllImplicationRulesTraitImpact("analyze/traitrules.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("PC rules number: "+analyzer.getStatImplicationPC());
		System.out.println("Traits rules number: "+analyzer.getStatImplicationTrait());
	}

}
