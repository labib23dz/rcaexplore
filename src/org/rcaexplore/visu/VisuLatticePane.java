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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.JPanel;

import org.rcaexplore.conceptorder.generic.CompareGenericConcepts;
import org.rcaexplore.conceptorder.generic.GenericConcept;
import org.rcaexplore.conceptorder.generic.GenericConceptOrder;
import org.rcaexplore.visu.VisuConcept.VisuRelationalAttribute;

public class VisuLatticePane extends JPanel implements MouseListener, MouseMotionListener, ComponentListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<VisuConcept> visuConcepts =new ArrayList<>();
	int minConceptSeparation = 10;
	int yoffset=10;
	int xoffset=10;
	int interLayer=30;
	private GenericConceptOrder co;
	private int step;
	private Point mouseOver=new Point(0,0);
	boolean clicked=false;
	VisuConcept selectedVisuConcept=null;
	private Rectangle visible=new Rectangle(0,0,0,0);;
	private final VisuFrame parentFrame;
	public VisuLatticePane(VisuFrame parentFrame)
	{
		super();
		this.parentFrame=parentFrame;
		setBackground(Color.white);
		
	}
	
	
	
	public int width() {
		int right=0;
		for (VisuConcept c: visuConcepts)
			right=Math.max(right, c.x+c.width);
		return right;
		
	}
	
	public int height(){
		int height=0;
		for (VisuConcept c: visuConcepts)
			height=Math.max(height, c.y+c.height);
		return height;
		
	}
	
	public GenericConceptOrder getCO(){
		return co;
	}
	
	public void setCO(GenericConceptOrder co){
		this.co=co;
	}
	
	
	/**
	 * Generate the layout of visuconcepts by layer
	 * */
	public void generateLayoutOfConcepts(){
		//init the layers
		ArrayList<ArrayList<VisuConcept>> conceptLayers=new ArrayList<>();
		ArrayList<VisuConcept> layer=new ArrayList<>();
		conceptLayers.add(layer);
		
		//select the layer for each concept and create the visuConcepts
		ArrayList<GenericConcept> concepts= new ArrayList<>();
		concepts.addAll(co.getConcepts());
		Collections.sort(concepts, Collections.reverseOrder(new CompareGenericConcepts()));
		Hashtable<GenericConcept, Integer> conceptLayerNumber=new Hashtable<>();
		Hashtable<GenericConcept, VisuConcept> conceptsMap=new Hashtable<>();
		//this part needs to be synchronized to prevent interference with painting
		synchronized (visuConcepts) {
			
			for (GenericConcept c : concepts)
			{
				if (!conceptLayerNumber.containsKey(c))
					conceptLayerNumber.put(c, 0);
				
				if (conceptLayerNumber.get(c)>=conceptLayers.size()){
					layer=new ArrayList<>();
					conceptLayers.add(layer);
				}
				
				for (GenericConcept child:c.getChildren())
					if (!conceptLayerNumber.containsKey(child))
						conceptLayerNumber.put(child, conceptLayerNumber.get(c)+1);
					else
						conceptLayerNumber.put(child, Math.max(conceptLayerNumber.get(child), conceptLayerNumber.get(c)+1));
				
				VisuConcept vc=new VisuConcept(c,(Graphics2D) getGraphics());
				visuConcepts.add(vc); 
				conceptsMap.put(c, vc);
				//conceptLayers.get(conceptLayerNumber.get(c)).add(vc);
			}
		
		}
		HashSet<GenericConcept> conceptAdded=new HashSet<>();
		LinkedList<GenericConcept> nextConcepts=new LinkedList<>();
		
		//add children in layers using a depth-first approach
		
		for(int i =0; i< concepts.size()&&conceptAdded.size()!=concepts.size();i++){
			if (!conceptAdded.contains(concepts.get(i))){
				nextConcepts.add(concepts.get(i));
				while(!nextConcepts.isEmpty()){
					GenericConcept currentConcept=nextConcepts.removeLast();
					if (!conceptAdded.contains(currentConcept)){
						conceptAdded.add(currentConcept);
						conceptLayers.get(conceptLayerNumber.get(currentConcept)).add(conceptsMap.get(currentConcept));
						nextConcepts.addAll(currentConcept.getChildren());
					}
				}	
			}
		}
		
		for (GenericConcept c: concepts)
			for (GenericConcept child : c.getChildren()){
				//add children
				conceptsMap.get(c).addChild(conceptsMap.get(child));
			}
		
		//find the largest conceptLayer
		int maxWidth=0;
		for (ArrayList<VisuConcept> c1 : conceptLayers)
			maxWidth=Math.max(maxWidth, conceptLayerWidth(minConceptSeparation, c1));	
		
		
		int y=yoffset;
		
		
		for (ArrayList<VisuConcept> c2: conceptLayers){
			int layerWidth=conceptLayerWidth(0,c2);
			System.out.println("maxWidth:"+maxWidth);
			System.out.println("layerWidth:"+layerWidth);
			System.out.println("layerSize:"+layer.size());
			int conceptSeparation=(maxWidth-layerWidth)/(c2.size()+1);
			System.out.println("conceptSeparation:"+conceptSeparation);
			generateLayerLayout(xoffset+(maxWidth-conceptLayerWidth(conceptSeparation, c2))/2, y, conceptSeparation,c2);
			y+=interLayer+conceptLayerHeight(c2);
		}
		
		setPreferredSize(new Dimension(width()+xoffset,height()+yoffset));
		addMouseMotionListener(this);
		addMouseListener(this);
		addComponentListener(this);
	}
	private void updateRectangle(){
		visible=new Rectangle(getBounds().x*-1, getBounds().y*-1, getParent().getBounds().width, getParent().getBounds().height);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d=(Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		ArrayList<ArrayList<Integer>> delayedEdges=new ArrayList<>();
		
		
		/*for (VisuConcept vc : visuConcepts){
			//if (visible.intersects(vc.getRectangle()))
			//if (visible.contains(vc.getRectangle()))
				vc.paintChildrenEdges(g2d, mouseOver, delayedEdges,visible);
		}*/
		//don't paint if the list of concepts is in creation
		synchronized (visuConcepts) {
			
			for (VisuConcept vc : visuConcepts)
				if (vc.intersects(visible)||vc.contains(mouseOver)) {
					vc.paintChildrenEdges(g2d, mouseOver, delayedEdges,visible);
					vc.paintInvisibleParentsEdges(g2d, mouseOver, delayedEdges, visible);
				}
			
			for (VisuConcept vc : visuConcepts)
				if (vc.intersects(visible)||vc.contains(mouseOver))
					vc.paintConcept(g2d, mouseOver);
		}
		g2d.setColor(Color.blue);
		for ( ArrayList<Integer> r : delayedEdges)
		{
			g2d.drawLine(r.get(0),r.get(1),r.get(2),r.get(3));
		}
		g2d.setColor(Color.red);
		for( ArrayList<Integer> r : delayedEdges){
			g2d.fillOval(r.get(0)-4,r.get(1)-4,8,8);
			g2d.fillOval(r.get(2)-4,r.get(3)-4,8,8);
		}
		g2d.setColor(Color.black);
		
	
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (selectedVisuConcept!=null){
			VisuRelationalAttribute targetConcept=selectedVisuConcept.getSelectedRelationalAttribute(e.getPoint());
			System.out.println(targetConcept);
			if (targetConcept==null)
				clicked=!clicked;
			else
			{
				System.out.println("changeTab");
				parentFrame.changeTab(targetConcept.conceptOrder, getStep());
				parentFrame.selectConcept(targetConcept.conceptOrder,targetConcept.value);
				//TODO selected the right tab and the right concepts
				
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(!clicked){
			this.mouseOver=new Point(e.getPoint());
			VisuConcept old=selectedVisuConcept;
			if (selectedVisuConcept==null||!selectedVisuConcept.contains(mouseOver)){
				selectedVisuConcept=null;
				for (VisuConcept vc: visuConcepts)
					if (vc.contains(mouseOver))
						selectedVisuConcept=vc;
			}
			// if a new concept is hovered or if the cursor leaves a concept then we repaint
			// if the cursor is moving in the same concept, then we repaint for relational attribute
			//selection
			if (selectedVisuConcept!=old)
				repaint();
			else if (selectedVisuConcept!=null)
				repaint(selectedVisuConcept);
		}
	}



	@Override
	public void componentHidden(ComponentEvent arg0) {}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		updateRectangle();
		repaint();
		
	}



	@Override
	public void componentResized(ComponentEvent arg0) {
		updateRectangle();
		repaint();
	}



	@Override
	public void componentShown(ComponentEvent arg0) {}

	private static int conceptLayerHeight(ArrayList<VisuConcept> layer) {
		int h=0;
		
		for (VisuConcept c: layer)
			h=Math.max(h, c.height);
		return h;
		
	}

	private static int conceptLayerWidth(int conceptSeparation,ArrayList<VisuConcept> layer){
		int w=0;
		
		for (VisuConcept c: layer)
			w+= c.width;
		return w+(layer.size()+1)*conceptSeparation;
		
		
	}



	public static void generateLayerLayout(int x, int y, int conceptSeparation, ArrayList<VisuConcept> layer) {
		
		int xOffset=x+conceptSeparation;
		for (int i =0; i<layer.size();i++){
			VisuConcept c = layer.get(i);
			c.setLocation(xOffset, y);
			xOffset+=conceptSeparation+c.width;
		
		}
	}



	public int getStep() {
		return step;
	}



	public void setStep(int step) {
		this.step = step;
	}



	public void selectConcept(String value) {
		
	}

}
