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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class OutputChooser {

	public static boolean DOT_CONCEPT_POSET=true;
	public static boolean TEX_CONTEXTS=false;
	public static boolean HTML_CONTEXTS=false;
	public static boolean SVG_CONCEPT_FAMILY_POSET=false;
	public static boolean XML=false;
	public static boolean FULL_INTENT_EXTENT=false;
	
	public static void  graphicalChooser(){
		final Object lock=new Object();
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				
				@Override
				public void run() {
					final JFrame f=new JFrame();
					JPanel p =new JPanel();
					f.add(p);
					
					p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
					f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					
					JCheckBox tex=new JCheckBox("TeX of extended contexts");
					tex.addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent arg0) {
							TEX_CONTEXTS=((JCheckBox)arg0.getSource()).isSelected();
						}
					});
					p.add(tex);

					JCheckBox html=new JCheckBox("HTML of extended contexts");
					html.addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent arg0) {
							HTML_CONTEXTS=((JCheckBox)arg0.getSource()).isSelected();
						}
					});
					p.add(html);
					
					JCheckBox svg=new JCheckBox("svg concept poset");
					svg.addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent arg0) {
							SVG_CONCEPT_FAMILY_POSET=((JCheckBox)arg0.getSource()).isSelected();
						}
					});
					p.add(svg);

					JCheckBox xml=new JCheckBox("xml result");
					xml.addItemListener(new ItemListener() {
						@Override
						public void itemStateChanged(ItemEvent arg0) {
							XML=((JCheckBox)arg0.getSource()).isSelected();
						}
					});
					p.add(xml);
					
					JCheckBox dotFullIntentExtent=new JCheckBox("generate full intent/extent concepts in dot files");
					dotFullIntentExtent.addItemListener(new ItemListener() {
						

						@Override
						public void itemStateChanged(ItemEvent arg0) {
							FULL_INTENT_EXTENT=((JCheckBox)arg0.getSource()).isSelected();
						}
					});
					p.add(dotFullIntentExtent);
					
					
					JButton ok =new JButton("OK");
					ok.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent arg0) {
							synchronized (lock) {
								lock.notify();
								f.dispose();
							}
						}
					});
					p.add(ok);
					
					f.pack();
					f.setVisible(true);
					
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
