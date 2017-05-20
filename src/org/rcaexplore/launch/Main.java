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
package org.rcaexplore.launch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.rcaexplore.ExplorationPath;
import org.rcaexplore.algo.multicontext.ExploMultiFCA;
import org.rcaexplore.cofbrowser.controller.COFController;
import org.rcaexplore.cofbrowser.model.COFNavigationModel;
import org.rcaexplore.contexteditor.controller.Controller;
import org.rcaexplore.contexteditor.model.EditorContextFamilyModel;
import org.rcaexplore.contexteditor.view.EditorFrame;
import org.rcaexplore.explo.controller.ExplorationController;
import org.rcaexplore.explo.view.CommandLineMenus;
import org.rcaexplore.explo.view.GraphicalMenus;
import org.rcaexplore.explo.view.RCAExploreView;
import org.rcaexplore.io.ParserException;
import org.rcaexplore.io.ParseRcft;
import org.rcaexplore.visu.VisuFrame;

/**
 * Main class where one can launch the different modules of the program
 * */
public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		
		//launch RCF editor
		if (args.length==0 ||args[0].equals("editor")) {
			
			EditorContextFamilyModel cfm=new EditorContextFamilyModel();
			final EditorFrame ef=new EditorFrame(cfm);
			@SuppressWarnings("unused")
			Controller c=new Controller(cfm,ef);
			if (args.length==2)
				try {
				
					//TODO should be done on the controller
					cfm.loadRCF(new File(args[1]));
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
			
				}
			
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						ef.setVisible(true);
					}
				});
			
		} 
		else if (args[0].equals("explogui")||args[0].equals("explocli")){
			
				RCAExploreView view;
				String rcftFile="";
				String outputFolder="";
				//launch rca exploration with graphical interface
				if (args[0].equals("explogui")){
					if (args.length==1){
						JFileChooser jfc=new JFileChooser(".");
						
						FileNameExtensionFilter filter = new FileNameExtensionFilter("RCFT file", "rcft", "rcfgz");
						jfc.addChoosableFileFilter(filter);
						jfc.setFileFilter(filter);
						if ( JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(new JFrame()) ) {
							rcftFile=jfc.getSelectedFile().getAbsolutePath();
							
						}
						jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						if ( JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(new JFrame()) ) {
							outputFolder=jfc.getSelectedFile().getAbsolutePath();
							
						}
					
					}
					else{
						rcftFile=args[1];
						outputFolder=args[2];
					}
					OutputChooser.graphicalChooser();
					view =new GraphicalMenus();
				}
				//launch RCA exploration with command line interface (kept for historical purpose but not really maintained, may be buggy)
				else if (args[0].equals("explocli")){
					rcftFile=args[1];
					outputFolder=args[2];
					view =new CommandLineMenus(System.in, System.out);
				}
				else
					return;
				
					
				ExplorationController exploController;
				try {
					exploController = new ExplorationController(rcftFile,outputFolder, view);
					exploController.start();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
		} 
		else if (args[0].equals("auto")) {
			String inputFile = null;
			String outputFolder= null;
			ExplorationPath exploPath=null;
			
			for (int i =1; i< args.length;i++)
			{
				if (args[i].equals("--output-svg"))
				{
					OutputChooser.SVG_CONCEPT_FAMILY_POSET=true;
				} else if (args[i].equals("--output-xml"))
				{
					OutputChooser.XML=true;
				} else if (args[i].equals("--output-tex"))
				{
					OutputChooser.TEX_CONTEXTS=true;
				} else if (args[i].startsWith("--follow-path=")){
					String pathFile=args[i].substring("--follow-path=".length());
					exploPath=new ExplorationPath(pathFile);
					exploPath.parse();
				} else if (inputFile==null)
				{
					inputFile=args[i];
				} else if (outputFolder==null)
				{
					outputFolder=args[i];
				} else {
					throw new Exception(args[i]+" is an unrecognised parameter");
				}
			}
			
			if (outputFolder==null)
				throw new Exception("parameters are missing");
			
			String fileExtension=(inputFile.substring(inputFile.lastIndexOf(".")+1));
			
			System.out.println("file extension : "+fileExtension);
			if ("rcft".equals(fileExtension)||"rcfgz".equals(fileExtension))
			{
				ParseRcft rcftParser=null;
				if ("rcft".equals(fileExtension))
					rcftParser=new ParseRcft(new FileReader(inputFile));
				else
					rcftParser=new ParseRcft(
							new BufferedReader(
									new InputStreamReader(
											new GZIPInputStream(new FileInputStream(inputFile)))));		
				try {
					rcftParser.parse();
					
				} catch (ParserException e) {
					e.printStackTrace();
				}
			
			ExploMultiFCA rca=new ExploMultiFCA( exploPath, rcftParser.getRcf());
			
			
			rca.launchAutomaticRCA(30, outputFolder);	
			}
			
		}

		//launch CLF browser
		else if (args[0].equals("browser")){
			COFNavigationModel model=new COFNavigationModel(args[1]);
			COFController controller = new COFController(model);
			controller.displayViews();
		}
		else if (args[0].equals("visu")){
			VisuFrame f=new VisuFrame();
			f.loadCLF(args[1]);
		}
	}

}
