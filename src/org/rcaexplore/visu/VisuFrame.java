
/*
 * Copyright (c) 2014, ENGEES. All rights reserved.
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

package org.rcaexplore.visu;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.rcaexplore.conceptorder.generic.GenericConceptOrder;
import org.rcaexplore.conceptorder.structure.ConceptOrderFamily;
import org.rcaexplore.io.ParseXMLCOFHistory;

/**
 * VisuFrame is the container of the visualisation of concept order family
 * */

public class VisuFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<ConceptOrderFamily<GenericConceptOrder>> cofHistory;
	private JTabbedPane tabs=new JTabbedPane();
	private ArrayList<VisuLatticePane> visuLatticePanes=new ArrayList<VisuLatticePane>();
	
	/**
	 * Constructor without parameter
	 * */
	public VisuFrame(){
		super();
		
		init();
		
	}

	/**
	 * Initialize the frame
	 * */
	private void init() {
		setSize(800,600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setContentPane(tabs);
		
		setVisible(true);
		
	}
	
	public void changeTab(String conceptOrderName, int step){
		for (int i=0;i<visuLatticePanes.size();i++){
			VisuLatticePane p =visuLatticePanes.get(i);
			if (p.getCO().getName().equals(conceptOrderName) && p.getStep()==step) {
				tabs.setSelectedIndex(i);
				return;
			}
			
			
					
		}
	}
	
	/**
	 * Create a tab with a concept order
	 * */
	private void createTab(GenericConceptOrder co, int step){
		JScrollPane js=new JScrollPane();
		VisuLatticePane drawingPane=new VisuLatticePane(this);
		visuLatticePanes.add(drawingPane);
		drawingPane.setCO(co);
		drawingPane.setStep(step);
		js.setViewportView(drawingPane);
		tabs.add("step "+step+" "+co.getName(), js);
		drawingPane.generateLayoutOfConcepts();
		js.addComponentListener(drawingPane);
		js.getVerticalScrollBar().setUnitIncrement(100);
		js.getHorizontalScrollBar().setUnitIncrement(100);
		js.revalidate();
		
	}
	
	/**
	 * Load a concept lattice family history from a file
	 * */
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
		int step=0;
		for (ConceptOrderFamily<GenericConceptOrder> cof : cofHistory){
			
			for (GenericConceptOrder co : cof.getConceptOrders())
				createTab(co,step);
			step++;
		}
		repaint();
	}
	

	
	
	public static void main(String[] args) {
		VisuFrame f=new VisuFrame();
		//f.loadCLF("results/test-perf/result.xml");
		//f.loadCLF("results/data-jst/exists/result.xml");
		f.loadCLF("/home/xdolques/test treillis/result.xml");
		//f.loadCLF("results/akdm-1/result.xml");
		//f.loadCLF("results/test/result.xml");
		
		
		
		//f.repaint();
		
		
		
	}

	public void selectConcept(String conceptOrder, String value) {
		int selectedTab = tabs.getSelectedIndex();
		visuLatticePanes.get(selectedTab).selectConcept(value);
	}

	

	
}
