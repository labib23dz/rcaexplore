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
package org.rcaexplore.explo.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

import org.rcaexplore.algo.Algorithm;
import org.rcaexplore.algo.multicontext.ExploMultiFCA;
import org.rcaexplore.conceptorder.optimized.OptimizedConceptOrder;
import org.rcaexplore.context.ObjectAttributeContext;
import org.rcaexplore.context.ObjectObjectContext;
import org.rcaexplore.context.RelationalContextFamily;
import org.rcaexplore.event.UserEvent;
import org.rcaexplore.event.UserListener;
import org.rcaexplore.explo.view.RCAExploreView;
import org.rcaexplore.io.GenerateDot;
import org.rcaexplore.io.GenerateHTMLFromOAContext;
import org.rcaexplore.io.GenerateLatexFromOAContext;
import org.rcaexplore.io.GenerateSVGDot;
import org.rcaexplore.io.ParserException;
import org.rcaexplore.io.ParseRcft;
import org.rcaexplore.launch.OutputChooser;
import org.rcaexplore.scaling.AvailableScalingOperators;

public class ExplorationController extends Thread implements UserListener{
	
	/** parse a RCF from a reader. Method needed by constructor*/
	private static RelationalContextFamily parseRCF(Reader input){
		ParseRcft parser=new ParseRcft(input);
		try {
			parser.parse();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (ParserException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return parser.getRcf();
	}
	
	private ExploMultiFCA exploMFca;
	private String outputFolder;
	
	
	private RCAExploreView view;
	
	public ExplorationController(Reader input, String outputFolder, RCAExploreView view){
		init(input, outputFolder, view);	
	}
	
	public ExplorationController(RelationalContextFamily rcf, String outputFolder, RCAExploreView view){
		init(rcf,outputFolder,view);
	}
	
	public ExplorationController(String inputFile, String outputFolder, RCAExploreView view) throws IOException{
		if (inputFile.endsWith(".rcft"))	
			init(new FileReader(inputFile) 	, outputFolder, view);
		else if (inputFile.endsWith(".rcfgz"))
			init(new BufferedReader(
					new InputStreamReader(
					new GZIPInputStream(new FileInputStream(inputFile))))
					, outputFolder, view);
	}

	private void init(Reader input, String outputFolder, RCAExploreView view) {
		init(parseRCF(input), outputFolder, view);
	}
	
	private void init(RelationalContextFamily rcf, String outputFolder, RCAExploreView view){
		exploMFca=new ExploMultiFCA(rcf);
		this.outputFolder=outputFolder;
		this.view=view;
	}
	
	@Override
	public void notifiedUserAction(UserEvent e) {
		
		switch(e.getAction()){
		case SET_AUTO_MODE: 
			exploMFca.setAutomatic(true); 
			break;
		case SET_MANUAL_MODE: 
			exploMFca.setAutomatic(false); 
			break;
		case ADD_OA_CONTEXT: 
			exploMFca.getCurrentConfig().addOAContext((ObjectAttributeContext) e.getParameters()[0]); 
			break;
		case REMOVE_OA_CONTEXT : 
			exploMFca.getCurrentConfig().removeOAContext((ObjectAttributeContext)e.getParameters()[0]); 
			break;
		case SET_OA_CONSTRUCTION_ALGO : 
			exploMFca.getCurrentConfig().chooseConstructionAlgo((ObjectAttributeContext)e.getParameters()[0],(Algorithm)e.getParameters()[1]); 
			break;
		case SET_OA_CONSTRUCTION_ALGO_WITH_PARAM:
			exploMFca.getCurrentConfig().chooseConstructionAlgo((ObjectAttributeContext)e.getParameters()[0],(Algorithm)e.getParameters()[1],(Integer)e.getParameters()[2]); 
			break;
		case ADD_SCALING_OPERATOR: 
			exploMFca.getCurrentConfig().addScalingOperator((ObjectObjectContext)e.getParameters()[0],AvailableScalingOperators.getAvailableScaling().get((String)e.getParameters()[1]));
			break;
		case ADD_OO_CONTEXT :  
			System.out.println("receive event add oo");
			exploMFca.getCurrentConfig().addOOContext((ObjectObjectContext)e.getParameters()[0]);
			break;
		case REMOVE_OO_CONTEXT : 
			System.out.println("receive event remove oo");
			exploMFca.getCurrentConfig().removeOOContext((ObjectObjectContext)e.getParameters()[0]);
			break;
		case REMOVE_SCALING_OPERATOR : 
			exploMFca.getCurrentConfig().removeScalingOperator((ObjectObjectContext)e.getParameters()[0],(String)e.getParameters()[1]);
			break;
		case STOP_PROCESS : 
			exploMFca.setEnd(true);
			break;
		}
		
	}

	/**
	 * This is what is run in interactive mode
	 * needs to implement rendez-vous with the gui
	 * */
	public void run() {
		view.setModel(this.exploMFca);
		view.addUserListener(this);

		boolean firstStep=true;
		//for firstStep, the configuration must be done manually
		exploMFca.setAutomatic(false);
		
		int step=0;
		long time=0;
		while (!exploMFca.isEnd()){
			final Object lock2=new Object();
			if (!exploMFca.isAutomatic())
			{
				view.config(lock2);
				synchronized (lock2) {
					try {
						lock2.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (!exploMFca.isEnd())
					exploMFca.addTrace();
			}
			//mode automatic or manual must be chosen after the execution of the first step, before that, it works the same
			if (firstStep) {
				final Object lock=new Object();
				view.chooseMode(lock);
				synchronized (lock) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("mode chosen");
				firstStep=false;
			}
			
			if (!exploMFca.isEnd())
			{
				long time1=System.currentTimeMillis();
				exploMFca.computeStep();
				saveExtendedContexts(step);
				saveConceptOrders(step);
				long time2=System.currentTimeMillis();
				time+=time2-time1;
				System.out.println("Step duration : "+(time2-time1)/1000+"s - time since beginning : "+time/1000+"s");
				if (exploMFca.stopCondition()){
					if (exploMFca.isAutomatic()){
						exploMFca.setEnd(true);
					}
					else
					{
						final Object lock3=new Object();
						view.askStop(lock3);
						synchronized (lock3) {
							try {
								lock3.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
				if (exploMFca.isAutomatic()&& step%10==0&& step!=0 )
				{
					final Object lock4=new Object();
					view.askStopAfter10Steps(lock4,step);
					synchronized (lock4) {
						try {
							lock4.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
				step++;
			}
		}
		exploMFca.saveTrace(outputFolder);
		if (OutputChooser.XML)
			exploMFca.saveXML(outputFolder);
		saveScriptBuilder();
		
		
		view.notifyEnd(outputFolder);
	}
	
	private void saveConceptOrders(int step){
		int i=0;
		FileWriter fw0 = null;
		try {
			
			fw0= new FileWriter(outputFolder+"/step"+step+".dot");
			GenerateSVGDot genSVGdot=new GenerateSVGDot(fw0,exploMFca.getConceptOrderFamily());
			genSVGdot.generateCode();
			fw0.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (OptimizedConceptOrder cPoset : exploMFca.getConceptOrderFamily().getConceptOrders()){
			FileWriter fw = null;
			try {
				
				fw= new FileWriter(outputFolder+"/step"+step+"-"+i+".dot");
				GenerateDot gendot=new GenerateDot(fw,cPoset,OutputChooser.FULL_INTENT_EXTENT);

				//The following code may be used to generate another kind of graphic representation
				//the main lattice is represented as a graph where every relational concept is itself a graph
	    /*				
				File f= new File(outputFolder+"/step"+step+"-"+i);
				f.mkdirs();
				
				fw= new FileWriter(outputFolder+"/step"+step+"-"+i+"/main.dot");
				GenerateGraphConceptsDot gendot=new GenerateGraphConceptsDot(fw, cPoset, outputFolder+"/step"+step+"-"+i);
	    */			
				gendot.generateCode();
				fw.close();
				
				/*FileWriter fw2=new FileWriter(outputFolder+"/step"+step+"-"+i+".list");
				GenerateExtentList genList=new GenerateExtentList(fw2, cPoset);
				genList.generateCode();
				fw2.close();*/
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
				i++;
		}
		
		
	}
	private void saveExtendedContexts(int step){
		int i=0;
		for (ObjectAttributeContext ctx:exploMFca.getTransitionalContextFamily().getContexts()){
			
			if (OutputChooser.TEX_CONTEXTS){
				FileWriter fw =null;
				try {
					fw= new FileWriter(outputFolder+"/step"+step+"-"+i+".tex");
					GenerateLatexFromOAContext genTex=new GenerateLatexFromOAContext(fw,ctx);
					genTex.generateCode();
					fw.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (OutputChooser.HTML_CONTEXTS){
				FileWriter fw =null;
				try {
					fw= new FileWriter(outputFolder+"/step"+step+"-"+i+".html");
					GenerateHTMLFromOAContext genHtml=new GenerateHTMLFromOAContext(fw,ctx);
					genHtml.generateCode();
					fw.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
			
			i++;

		}
	}
	
	private void saveScriptBuilder(){
		int posets =exploMFca.getRCF().getOAContexts().size();
		int steps= exploMFca.getTransitionalConceptPosetFamilies().size();
		FileWriter fw = null;
		try {
			fw = new FileWriter(outputFolder+"/latticebuilder.sh");
			fw.append("#!/bin/bash\n\n");
			fw.append("for i in {0.."+(steps-1)+"};\n");
			fw.append("do\n");
			if (OutputChooser.SVG_CONCEPT_FAMILY_POSET) {
				fw.append("    dot -Tsvg step$i.dot -o step$i.svg;\n");
				fw.append("    sed -i'' 's/(\\(Concept_.*\\))/(\\<a xlink:href=\\\"#\\1\\\"\\>\\1\\<\\/a\\>)/' step$i.svg;\n");
				fw.append("    sed -i'' 's/>\\(Concept_.*_[0-9]*\\)<\\/text>/ id=\"\\1\">\\1<\\/text>/' step$i.svg;\n");
			}
			fw.append("    for j in {0.."+(posets-1)+"};\n");
			fw.append("    do\n");
			fw.append("	   dot -Tpdf step$i-$j.dot -o step$i-$j.pdf;\n");
			fw.append("    done\n");
			fw.append("done");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

	
}
