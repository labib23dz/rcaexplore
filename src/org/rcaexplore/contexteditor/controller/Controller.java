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
package org.rcaexplore.contexteditor.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.rcaexplore.contexteditor.model.EditorContextFamilyModel;
import org.rcaexplore.contexteditor.view.EditorFrame;
import org.rcaexplore.explo.controller.ExplorationController;
import org.rcaexplore.explo.view.GraphicalMenus;
import org.rcaexplore.launch.OutputChooser;
import org.rcaexplore.visu.VisuFrame;

/**
 * Main controller class of the editor. Instantiate the view and model and modify the model.
 * */
public class Controller implements ActionListener{

	EditorContextFamilyModel cfm;
	EditorFrame ef;
	public Controller(EditorContextFamilyModel cfm, EditorFrame ef) {
		this.cfm=cfm;
		this.ef=ef;
		ef.addActionListener(this);
	}
		

	public void actionPerformed(final ActionEvent e) {
		System.err.println("Is running on EDT "+SwingUtilities.isEventDispatchThread());
		String[] actionString = e.getActionCommand().split("\n");
		System.out.println(e.getActionCommand());
		if (actionString[0].equals("Add formal context"))
			cfm.addFormalContext(actionString[1]);
		else if (actionString[0].equals("Remove formal context"))
			cfm.removeFormalContext(actionString[1]);
		else if (actionString[0].equals("Save As"))
			cfm.saveAsRCF(new File(actionString[1]));
		else if (actionString[0].equals("Save"))
			cfm.save();
		else if (actionString[0].equals("Export as TeX"))
			try {
				cfm.exportAsTex(new File(actionString[1]));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		else if (actionString[0].equals("Export as Galicia format"))
			try {
				cfm.exportAsGalicia(new File(actionString[1]));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		else if (actionString[0].equals("Launch RCA"))
			launchRCA();
		else if (actionString[0].equals("Visualize Concepts"))
			launchVisu();
		else if (actionString[0].equals("Load RCF")) {
			try {
				cfm.loadRCF(new File(actionString[1]));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else if (actionString[0].equals("Import OA context")) {
			cfm.importOAContextCSV(new File(actionString[1]));
		}
		else if (actionString[0].equals("Add relational context"))
			cfm.addRelationnalContext(actionString[1],actionString[2],actionString[3]);
		else if (actionString[0].equals("Remove relational context"))
			cfm.removeRelationalContext(actionString[1]);
		else if (actionString[0].equals("Add opposite relational context"))
			cfm.addOppositeRelationnalContext(actionString[1], actionString[2]);
		else if (actionString[0].equals("Set context name"))
			cfm.renameContext(actionString[1],actionString[2]);
		else if (actionString[0].equals("Set scaling name"))
			cfm.renameScaling(actionString[1], actionString[2]);
		else if (actionString[0].equals("Set description"))
			cfm.changeDescription(actionString[1],actionString[2]);
		else if (actionString.length>1&&!cfm.getFormalContexts().contains(actionString[1])) //vérifie si le contexte n'est pas relationnel pour pouvoir effectuer des modifications sur la structure des tables
			return;
		else if (actionString[0].equals("Add row"))
			cfm.addRow(actionString[1]);
		else if (actionString[0].equals("Add column"))
			cfm.addColumn(actionString[1]);
		else if (actionString[0].equals("Remove column")){
			cfm.removeColumn(actionString[1], actionString[2]);
		}
		else if (actionString[0].equals("Remove row"))
			cfm.removeRow(actionString[1], actionString[2]);
		else if (actionString[0].equals("Set row name"))
			cfm.renameRow(actionString[1], actionString[2], actionString[3]);
		else if (actionString[0].equals("Set column name"))
			cfm.renameCol(actionString[1], actionString[2], actionString[3]);
		else if (actionString[0].equals("Set algo name"))
			cfm.renameAlgo(actionString[1], actionString[2]);
		else if (actionString[0].equals("Set algo name and parameter"))
			cfm.renameAlgo(actionString[1], actionString[2], actionString[3]);
		
	}

	private void launchVisu() {
		
		ef.setVisible(false);
		//chose the output folder
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String inputFile;
				JFileChooser jfc=new JFileChooser(".");
				if ( JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(new JFrame()) ) {
					inputFile=jfc.getSelectedFile().getAbsolutePath();
					
				}
				else { 
					ef.setVisible(true);
					return;
				} 
				ef.dispose();
				VisuFrame f=new VisuFrame();
				f.loadCLF(inputFile);
			}
		}).start();
	}



	private void launchRCA() {
		ef.setVisible(false);
		
		
		//chose the output folder
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String outputFolder;
				JFileChooser jfc=new JFileChooser(".");
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if ( JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(new JFrame()) ) {
					outputFolder=jfc.getSelectedFile().getAbsolutePath();
					
				} else { 
					ef.setVisible(true);
					return;
				}
				ef.dispose();
				OutputChooser.graphicalChooser();
				ExplorationController explo =new ExplorationController(cfm.genRCF(), outputFolder, new GraphicalMenus());
				explo.start();
				
			}
		}).start();
	}

}
