
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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;

import org.rcaexplore.conceptorder.generic.GenericConcept;
import org.rcaexplore.conceptorder.generic.GenericRelationalAttribute;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.BinaryAttribute;
import org.rcaexplore.context.Entity;

public class VisuConcept extends Rectangle{
	
	/**
	 * Class VisuAttribute representing classical binary attribute with just a value
	 * */
	public class VisuAttribute {
		String value;
		public String toString() {
			return value;
		}
		
		public void drawVisuAttribute(Graphics2D g2d,int x,int y, Point mouseOver, VisuConcept containingConcept) {
			g2d.drawString(value, x, y);
		}
		
		
		
	}
	/**
	 * Class VisuRelationalAttribute storing the informations to display and use a relational
	 * attribute
	 * */
	public class VisuRelationalAttribute extends VisuAttribute {
		String scalingOp;
		String relation;
		String conceptOrder;
		public String toString(){
			return scalingOp+" "+relation+"("+value+")";
		}
		public void drawVisuAttribute(Graphics2D g2d,int x,int y, Point mouseOver, VisuConcept containingConcept) {
			g2d.drawString(scalingOp+" "+relation, x, y);
			Rectangle r =new Rectangle(x, y-containingConcept.lineHeight, width - 2 * margin, containingConcept.lineHeight);

			if (r.contains(mouseOver)) {
				g2d.setColor(Color.red);
				g2d.drawString("("+value+")", x+g2d.getFontMetrics().stringWidth(scalingOp+" "+relation), y);
				g2d.setColor(Color.blue);
			}
			else
				g2d.drawString("("+value+")", x+g2d.getFontMetrics().stringWidth(scalingOp+" "+relation), y);
			
				
		}
		
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<String> extent=new ArrayList<>();
	//ArrayList<String> intent=new ArrayList<>();
	ArrayList<VisuAttribute> intent=new ArrayList<>();
	String name;
	ArrayList<VisuConcept> children =new ArrayList<>();
	ArrayList<VisuConcept> parents =new ArrayList<>();
	int margin=1;
	int nameHeight;
	int intentHeight;
	int extentHeight;
	int lineHeight;
	int ascent;
	private int arrowLineWidth=3;
	private int arrowDistanceFromConcept = 2;
	private int arrowSize = 10;
	public VisuConcept(GenericConcept c,Graphics2D g2d){
		super(0,0,0,0);
		this.name=c.getName();
		for (Entity e : c.getSimplifiedExtent())
		{
			extent.add(e.getName());
		}
		for (Attribute a : c.getSimplifiedIntent()){
			intent.add(buildVisuAttribute(a));
		}
		computeSize(g2d);
	}
	
	/**
	 * Helper method that build a visu attribute accordingly to the type of a
	 * */
	private VisuAttribute buildVisuAttribute(Attribute a){
		VisuAttribute result=null;
		if (a instanceof BinaryAttribute) {
			result = new VisuAttribute();
			result.value=((BinaryAttribute) a).getValue();
		} else if (a instanceof GenericRelationalAttribute){
			VisuRelationalAttribute relResult = new VisuRelationalAttribute();
			relResult.scalingOp = ((GenericRelationalAttribute) a).getScaling();
			relResult.relation = ((GenericRelationalAttribute) a).getRelation();
			relResult.value = ((GenericRelationalAttribute) a).getConcept().getName();
			relResult.conceptOrder = ((GenericRelationalAttribute) a).getConceptOrderName();
			result=relResult;
		}
		return result;
	}
	
	
	/**
	 * Paint the concept on the given graphics. 
	 * It the mouse is over it then paint it in blue
	 * */
	public void paintConcept(Graphics2D g2d, Point mouseOver){

		if (contains(mouseOver)){
			g2d.setColor(Color.white);
			g2d.fillRect(x, y, width, height);
			g2d.setColor(Color.blue);
		}
		g2d.drawRect(x, y, width, nameHeight(g2d));
		g2d.drawString(name, x+margin+(width-g2d.getFontMetrics().stringWidth(name))/2, y+ascent+margin);
		
		
		g2d.drawRect(x, y+nameHeight, width, intentHeight);
		for (int i=0; i<intent.size();i++)
			intent.get(i).drawVisuAttribute(g2d, x+margin, y+ascent+margin+nameHeight+i*lineHeight, mouseOver, this);

			//g2d.drawString(intent.get(i).toString(), x+margin, y+ascent+margin+nameHeight+i*lineHeight);
	
		g2d.drawRect(x, y+nameHeight+intentHeight, width, extentHeight);
		for (int i=0; i<extent.size();i++)
			g2d.drawString(extent.get(i).toString(), x+margin, y+ascent+margin+nameHeight+intentHeight+i*lineHeight);

		g2d.setColor(Color.black);
	}
	
	private int lineHeight(Graphics2D g2d){
		return g2d.getFontMetrics().getHeight();
	}
	private int nameHeight(Graphics2D g2d){
		return lineHeight(g2d)+2*margin;
	}
	private int extentHeight(Graphics2D g2d){
		return lineHeight(g2d)*extent.size()+2*margin;
	}
	
	private int intentHeight(Graphics2D g2d){
		return lineHeight(g2d)*intent.size()+2*margin;
	}
	private int computeWidth(Graphics2D g2d){
		int width=0;
		width=Math.max(width,g2d.getFontMetrics().stringWidth(name));
		for (String s : extent)
			width=Math.max(width,g2d.getFontMetrics().stringWidth(s));
		for (VisuAttribute s: intent)
			width=Math.max(width,g2d.getFontMetrics().stringWidth(s.toString()));
		return width+2*margin;
	}
	
	private int computeHeight(Graphics2D g2d){
		return nameHeight(g2d)+extentHeight(g2d)+intentHeight(g2d);
	}
	
	public void addChild(VisuConcept c){
		children.add(c);
		c.parents.add(this);
	}
	
	
	/**
	 * Paint the edges going to all the children of the current concept
	 * */
	public void paintChildrenEdges(Graphics2D g2d, Point mouseOver, ArrayList<ArrayList<Integer>> delayedEdges, Rectangle visible) {
		int bottomLeft=0;
		int bottomRight=0;
		
		for (VisuConcept child: children){
			if (child.contains(mouseOver)||this.contains(mouseOver)){
				Integer[] values={this.x+this.width/2, this.y+this.height, child.x+child.width/2, child.y};
				delayedEdges.add(new ArrayList<Integer>(Arrays.asList(values)));
			}
			else {
				if (child.intersects(visible)){
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.drawLine(this.x+this.width/2, this.y+this.height, child.x+child.width/2, child.y);
				}
				else
				{
					if (child.x<x)
						bottomLeft++;
					else
						bottomRight++;
				}
			}
		}
		
		g2d.setColor(Color.LIGHT_GRAY);
		if (bottomLeft!=0){
			g2d.drawString(bottomLeft+"", x+arrowSize, y+height+ascent);
			int[] arrowX={x+arrowLineWidth,x+arrowLineWidth,x+arrowSize-arrowLineWidth,x+arrowSize,x,x};
			int[] arrowY={y+height+arrowDistanceFromConcept+arrowLineWidth,y+height+arrowSize+arrowDistanceFromConcept-arrowLineWidth, y+height+arrowSize+arrowDistanceFromConcept-arrowLineWidth,y+arrowSize+arrowDistanceFromConcept+height,y+arrowSize+arrowDistanceFromConcept+height,y+arrowDistanceFromConcept+height};
			g2d.fillPolygon(arrowX, arrowY, 6);
		}
		if (bottomRight!=0){
			g2d.drawString(bottomRight+"", x+width-g2d.getFontMetrics().stringWidth(bottomRight+"")-arrowSize, y+height+ascent);
			int[] arrowX={x+width-arrowLineWidth,x+width-arrowLineWidth,x+width-arrowSize+arrowLineWidth,x+width-arrowSize,x+width,x+width};
			int[] arrowY={y+arrowDistanceFromConcept+arrowLineWidth+height,y+arrowSize+arrowDistanceFromConcept-arrowLineWidth+height, y+arrowSize+arrowDistanceFromConcept-arrowLineWidth+height,y+arrowSize+arrowDistanceFromConcept+height,y+arrowSize+arrowDistanceFromConcept+height,y+arrowDistanceFromConcept+height};
			g2d.fillPolygon(arrowX, arrowY, 6);
		}
		g2d.setColor(Color.black);
		
	}
	
	/**
	 * Paint the edges going to all the children of the current concept
	 * */
	public void paintInvisibleParentsEdges(Graphics2D g2d, Point mouseOver, ArrayList<ArrayList<Integer>> delayedEdges, Rectangle visible) {
		int topLeft=0;
		int topRight=0;
		for (VisuConcept parent: parents){
			if (!parent.intersects(visible))
				if (this.contains(mouseOver)){
					Integer[] values={this.x+this.width/2, this.y, parent.x+parent.width/2, parent.y+parent.height};
					delayedEdges.add(new ArrayList<Integer>(Arrays.asList(values)));
				}
				else {
					if (parent.x<x)
						topLeft++;
					else
						topRight++;
					/*g2d.setColor(Color.LIGHT_GRAY);
					g2d.drawLine(this.x+this.width/2, this.y, parent.x+parent.width/2, parent.y+parent.height);
					g2d.setColor(Color.black);*/
				}
		}
		g2d.setColor(Color.LIGHT_GRAY);
		
		if (topLeft!=0){
			g2d.drawString(topLeft+"", x+arrowSize, y);
			//g2d.setColor(Color.black);
			int[] arrowX={x+arrowLineWidth,x+arrowLineWidth,x+arrowSize-arrowLineWidth,x+arrowSize,x,x};
			int[] arrowY={y-arrowDistanceFromConcept-arrowLineWidth,y-arrowSize-arrowDistanceFromConcept+arrowLineWidth, y-arrowSize-arrowDistanceFromConcept+arrowLineWidth,y-arrowSize-arrowDistanceFromConcept,y-arrowSize-arrowDistanceFromConcept,y-arrowDistanceFromConcept};
			g2d.fillPolygon(arrowX, arrowY, 6);
			//g2d.setColor(Color.LIGHT_GRAY);
		}
		if (topRight!=0){
			g2d.drawString(topRight+"", x+width-g2d.getFontMetrics().stringWidth(topRight+"")-arrowSize, y);
			int[] arrowX={x+width-arrowLineWidth,x+width-arrowLineWidth,x+width-arrowSize+arrowLineWidth,x+width-arrowSize,x+width,x+width};
			int[] arrowY={y-arrowDistanceFromConcept-arrowLineWidth,y-arrowSize-arrowDistanceFromConcept+arrowLineWidth, y-arrowSize-arrowDistanceFromConcept+arrowLineWidth,y-arrowSize-arrowDistanceFromConcept,y-arrowSize-arrowDistanceFromConcept,y-arrowDistanceFromConcept};
			g2d.fillPolygon(arrowX, arrowY, 6);
		
		}
		g2d.setColor(Color.black);
			
		
	}
	
	public void generateInitialLayout(int x, int y) {
		this.x=x;
		this.y=y;
	}
	private void computeSize(Graphics2D g2d) {
		this.ascent=g2d.getFontMetrics().getAscent();
		this.lineHeight=lineHeight(g2d);
		this.nameHeight=nameHeight(g2d);
		this.intentHeight=intentHeight(g2d);
		this.extentHeight=extentHeight(g2d);
		this.width=computeWidth(g2d);
		this.height=computeHeight(g2d);
		
	}

	public VisuRelationalAttribute getSelectedRelationalAttribute(Point point) {
		for (int i=0; i<intent.size();i++)
		{
			Rectangle r=new Rectangle (x+margin, y+ascent+margin+nameHeight+(i-1)*lineHeight, width - 2 * margin , lineHeight);
			if (r.contains(point)&& intent.get(i) instanceof VisuRelationalAttribute)
				return (VisuRelationalAttribute) intent.get(i);
		}

		
				
		return null;
	}
	
	/*public boolean contains(Point p){
		return p.x>x&&p.x<x+width&&p.y>y&&p.y<y+height;
	}*/
	
}
