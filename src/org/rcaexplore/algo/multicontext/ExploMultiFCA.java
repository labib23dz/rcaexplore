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

package org.rcaexplore.algo.multicontext;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TreeMap;

import org.rcaexplore.ExplorationPath;
import org.rcaexplore.StepConfiguration;
import org.rcaexplore.TransitionalContextFamily;
import org.rcaexplore.algo.Algorithm;
import org.rcaexplore.algo.singlecontext.AddExtent;
import org.rcaexplore.algo.singlecontext.IcebergAddExtent;
import org.rcaexplore.algo.singlecontext.NAocPoset;
import org.rcaexplore.algo.singlecontext.NAocPoset.Poset;
import org.rcaexplore.algo.singlecontext.SingleContextAlgorithm;
import org.rcaexplore.conceptorder.optimized.CompareExtents;
import org.rcaexplore.conceptorder.optimized.OptimizedConcept;
import org.rcaexplore.conceptorder.optimized.OptimizedConceptOrder;
import org.rcaexplore.conceptorder.structure.ConceptOrderFamily;
import org.rcaexplore.context.Entity;
import org.rcaexplore.context.ObjectAttributeContext;
import org.rcaexplore.context.ObjectObjectContext;
import org.rcaexplore.context.RelationalAttribute;
import org.rcaexplore.context.RelationalContextFamily;
import org.rcaexplore.io.GenerateDot;
import org.rcaexplore.io.GenerateXMLFromConceptOrderFamilyHistory;
import org.rcaexplore.launch.OutputChooser;
import org.rcaexplore.scaling.ScalingOperator;

import cern.colt.bitvector.BitVector;


/**
 * Implementation of an interactive version of RCA. In this version the user is involved at each step to make decisions
 * */
public class ExploMultiFCA {
	
	/** the final concept order family that is taken as output of the process*/
	private ConceptOrderFamily<OptimizedConceptOrder> cof;
	/**the transitional contexts are the extended contexts created at each step of the process*/
	private ArrayList<TransitionalContextFamily> transitionalContexts;
	/**the transitional concept orders are the poset of concepts created at each step of the process. 
	 * They do not contain the last one unless a new step is processed*/
	private ArrayList<ConceptOrderFamily<OptimizedConceptOrder>> transitionalConceptPosetFamilies;
	
	/**list the conceptExtent created. This is needed to assign a unique identifier to concepts through the whole process*/
	private Hashtable<String,TreeMap<BitVector,Integer>> conceptExtentsList;
	
	/**choose between automatic or manual mode*/
	private boolean automatic=true;
	
	/**indicates if the current step is the initial step*/
	private boolean init=true;
	
	/**indicates if the process is ending*/
	private boolean end=false;
	
	/**current configuration of the process*/
	private StepConfiguration currentConfig;
	
	/**exploration path, if given*/
	private final ExplorationPath explopath;
	
	/**map a context to an algo name*/
	private Hashtable<ObjectAttributeContext, Algorithm> algo;
	/**map a context to a parameter value for an algo*/
	private Hashtable<ObjectAttributeContext, Integer> algoParam;
	
	/**current relational context family*/
	protected RelationalContextFamily relationalContextFamily;
	
	/**trace of the current exploration*/
	private StringBuffer trace;
	
	
	public ExploMultiFCA(ExplorationPath exploPath,RelationalContextFamily rcf) {
		trace=new StringBuffer();
		this.explopath=exploPath;
		this.relationalContextFamily=rcf;
		transitionalContexts=new ArrayList<>();
		transitionalConceptPosetFamilies=new ArrayList<>();
		
		conceptExtentsList=new Hashtable<>();
		for (ObjectAttributeContext c : rcf.getOAContexts()){
			conceptExtentsList.put(c.getName(), new TreeMap<BitVector,Integer>(new CompareExtents()));
		}
		
		if (exploPath!=null)
			currentConfig=new StepConfiguration( exploPath.getPath().get(0),relationalContextFamily);
		else
			currentConfig=new StepConfiguration( relationalContextFamily);
	}
	
	public ExploMultiFCA(RelationalContextFamily rcf){
		this(null, rcf);
	}
	
	public StepConfiguration getCurrentConfig() {
		return currentConfig;
	}


	public boolean isInit() {
		return init;
	}

	public void setInit(boolean init) {
		this.init = init;
	}

	public boolean isAutomatic() {
		return automatic;
	}

	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
	}

	public ArrayList<ConceptOrderFamily<OptimizedConceptOrder>> getTransitionalConceptPosetFamilies() {
		return transitionalConceptPosetFamilies;
	}

	public void addTrace(){
		trace.append("\n"+getCurrentConfig().stepNumber()+"\n");
		trace.append("OAContexts\n");
		for (ObjectAttributeContext c : getCurrentConfig().getSelectedOAContexts())
		{
			Algorithm algo=getCurrentConfig().getAlgo(c);
			trace.append(c.getName()
					+","
					+algo
					+(algo.hasParameter()?","+getCurrentConfig().getAlgoParameter(c):"")
					+"\n");
		}
		trace.append("OOContexts\n");
		for (ObjectObjectContext rc : getCurrentConfig().getSelectedOOContexts())
		{
			for (ScalingOperator scaling : getCurrentConfig().getScalingOperators(rc))
				trace.append(rc.getRelationName()+","+scaling.getName()+"\n");
			
		}
		trace.append("\n");
	}
	
	

	public ConceptOrderFamily<OptimizedConceptOrder> getConceptOrderFamily() {
		return cof;
	}
	
	public TransitionalContextFamily getTransitionalContextFamily(){
		return transitionalContexts.get(transitionalContexts.size()-1);
	}
	
	/**generate the concept posets for current step and create the next step configuration (load it in case of automatic process)*/
	public void computeStep(){
		if (init)
		{
			initialization();
			generateConceptOrders();
		}
		else
		{
			extendContexts();
			generateConceptOrders();
		}
		init=false;
		if (explopath!=null&&explopath.getPath().size()>transitionalConceptPosetFamilies.size())
			currentConfig=new StepConfiguration(explopath.getPath().get(transitionalConceptPosetFamilies.size()),currentConfig);
		else
			currentConfig=new StepConfiguration(currentConfig);
		
	}

	public boolean stopCondition(){
		if (explopath!=null)
			return explopath.getPath().size()==transitionalConceptPosetFamilies.size();
		else if (transitionalConceptPosetFamilies.size()>1)
			return transitionalConceptPosetFamilies.get(transitionalConceptPosetFamilies.size()-1).totalConceptNb()==transitionalConceptPosetFamilies.get(transitionalConceptPosetFamilies.size()-2).totalConceptNb();
		else
			return false;
	}


	/**extend formal context with scaled relational contexts*/
	public void extendContexts(){
		long time_before_extending=System.currentTimeMillis()/1000;
		System.out.println("extending contexts");
		initialization();
		TransitionalContextFamily tcf =transitionalContexts.get(transitionalContexts.size()-1);
		ConceptOrderFamily<OptimizedConceptOrder> previouscof=transitionalConceptPosetFamilies.get(transitionalConceptPosetFamilies.size()-1);
		for (ObjectAttributeContext ctx: tcf.getContexts()){
			for (ObjectObjectContext rc : currentConfig.getSelectedOOContexts())
			{
				if (rc.getSourceContext().getName().equals(ctx.getName()))
					for (OptimizedConceptOrder lattice  : previouscof.getConceptOrders())
						if (lattice.getContext().getName().equals(rc.getTargetContext().getName()))
							for (OptimizedConcept aConcept:lattice.getConcepts()){
								ArrayList<Entity> aConceptExtent=aConcept.getExtent();
								
								for (ScalingOperator op : currentConfig.getScalingOperators(rc)){
								
									RelationalAttribute ra = new RelationalAttribute(aConcept, rc, op.getName());
									BitVector extent =new BitVector(ctx.getEntityNb());
									int i=0;
									for (Entity e: ctx.getEntities())
									{
										if (op.scale(e, aConceptExtent,rc))
											extent.set(i);
										i++;
									}
									ctx.addAttributeAndExtent(ra, extent);
								}
							}
			}
		}
		for (ObjectAttributeContext ctx: tcf.getContexts()){
			System.out.println("ctx "+ctx.getName()+" : "+ctx.getEntityNb()+" entities, "+ctx.getAttributeNb()+" attributes");
		}
		long time_after_extending=System.currentTimeMillis()/1000;
		System.out.println("contexts extended ("+(time_after_extending-time_before_extending)+"s )");
	}
	
	public void generateConceptOrders() {
		TransitionalContextFamily tcf =transitionalContexts.get(transitionalContexts.size()-1);
		cof=new ConceptOrderFamily<>();
		cof.setStepNb(transitionalContexts.size()-1);
		for (ObjectAttributeContext c: tcf.getContexts()) {
			System.out.println("computing ctx "+c.getName()+".");
			
			SingleContextAlgorithm<OptimizedConceptOrder> constructionAlgorithm;

			
			switch (algo.get(c)){
			case ARES : constructionAlgorithm=new NAocPoset(c); break;
			case ACPOSET : constructionAlgorithm=new NAocPoset(c,Poset.acposet); break;
			case OCPOSET : constructionAlgorithm=new NAocPoset(c,Poset.ocposet); break;
			case FCA : constructionAlgorithm=new AddExtent(c); break;
			case ICEBERG : constructionAlgorithm=new IcebergAddExtent(c,algoParam.get(c)); break;
			default : constructionAlgorithm=new AddExtent(c);break;
			}
			
			constructionAlgorithm.compute();
			long time_before_renaming=System.currentTimeMillis()/1000;
			System.out.println("Rename concepts");
			constructionAlgorithm.getConceptOrder().renameConcepts(conceptExtentsList.get(c.getName()));
			long time_after_renaming=System.currentTimeMillis()/1000;
			System.out.println("concepts renamed ("+(time_after_renaming-time_before_renaming)+"s )");
			constructionAlgorithm.getConceptOrder().setConstructionAlgorithm(algo.get(c));
			for (ObjectObjectContext relation : getCurrentConfig().getSelectedOOContexts()){
				if (relation.getSourceContext().getName().equals(c.getName())){
					for (ScalingOperator s : getCurrentConfig().getScalingOperators(relation))
						constructionAlgorithm.getConceptOrder().addRelation(relation.getRelationName(), s.getName());
					
				}
			}
			
			cof.addConceptOrder(constructionAlgorithm.getConceptOrder());
			System.out.println("ctx "+c.getName()+" computed.");
		}
		transitionalConceptPosetFamilies.add(cof);
	}
	
	/**Initialization of variables before the first iteration*/
	public void initialization(){
		algo=new Hashtable<>();
		algoParam=new Hashtable<>();
		TransitionalContextFamily tcf=new TransitionalContextFamily(transitionalContexts.size());
		for (ObjectAttributeContext oac : currentConfig.getSelectedOAContexts())
		{
			try {
				ObjectAttributeContext addedContext=(ObjectAttributeContext) oac.clone();
				algo.put(addedContext,currentConfig.getAlgo(oac));
				if (currentConfig.getAlgo(oac).hasParameter())
					algoParam.put(addedContext,currentConfig.getAlgoParameter(oac));
				tcf.addContext(addedContext);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		transitionalContexts.add(tcf);
	}

	

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}
	
	/**variant of automatic process without config changes between steps. Defines a maxstep in case of non convergence*/
	public void launchAutomaticRCA(int maxStep, String outputFolder){
		launchAutomaticRCA(maxStep,null,outputFolder);
	}
	/**variant of automatic process with config changes between steps following an exploratory path*/
	public void launchAutomaticRCA(ExplorationPath exploPath,
			String outputFolder) {
		launchAutomaticRCA(-1,exploPath, outputFolder);
		
		
	}
	
	/**this method is to be used if an interactive process is not required*/
	private void launchAutomaticRCA(int maxStep, ExplorationPath exploPath, String outputFolder){
		setAutomatic(true);
		
		int step=0;
		addTrace();
		while (!isEnd()){
			
			addTrace();
			computeStep();
			int i=0;
			for (OptimizedConceptOrder cPoset : getConceptOrderFamily().getConceptOrders()){
				FileWriter fw = null;
				try {
					
					fw= new FileWriter(outputFolder+"/step"+step+"-"+i+".dot");
					GenerateDot gendot=new GenerateDot(fw,cPoset, OutputChooser.FULL_INTENT_EXTENT);
					gendot.generateCode();
					fw.close();
					
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
					
					i++;
			}
			
			//TODO saveExtendedContexts(step);
			if (stopCondition()||step==maxStep)
				setEnd(true);
			step++;
			
		}
		saveTrace(outputFolder);
		saveXML(outputFolder);
	}
	/**save a trace of successives configurations used*/
	public void saveTrace(String outputFolder){
		try {
			
			FileWriter fw= new FileWriter(outputFolder+"/trace.csv");
			fw.write(trace.toString());
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**save concept posets in xml format*/
	public void saveXML(String outputFolder){
		long time1=System.currentTimeMillis();
		FileWriter fw = null;
		try {
			fw = new FileWriter(outputFolder+"/result.xml");
			GenerateXMLFromConceptOrderFamilyHistory genXML=new GenerateXMLFromConceptOrderFamilyHistory(fw,this);
			genXML.generateCode();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long time2=System.currentTimeMillis();
		
		System.out.println("saved as XML ("+(time2-time1)/1000+"s)");
		
	}

	public RelationalContextFamily getRCF() {
		return relationalContextFamily;
	}



	
}
