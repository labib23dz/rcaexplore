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
package org.rcaexplore.contexteditor.view;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.rcaexplore.contexteditor.controller.ContextChangeEvent;
import org.rcaexplore.contexteditor.controller.ContextChangeListener;
import org.rcaexplore.contexteditor.model.EditorRelationalContextModel;
import org.rcaexplore.scaling.AvailableScalingOperators;

public class RCParametersPanel extends JPanel implements ContextChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JComboBox<String> scalingOp;
	private JTextField description;
	private final EditorFrame editorFrame;
	private final EditorRelationalContextModel rc;
	public RCParametersPanel(EditorRelationalContextModel rc, EditorFrame editorFrame) {
		super();
		this.editorFrame=editorFrame;
		this.rc=rc;
		rc.addContextChangeListener(this);
		
		scalingOp=new JComboBox<>(AvailableScalingOperators.getAvailableScaling().keySet().toArray(new String[0]));
		
		scalingOp.addActionListener(new ActionListener() {
					@Override
			public void actionPerformed(ActionEvent arg0) {
				RCParametersPanel.this.editorFrame.fireActionEvent("Set scaling name\n" + RCParametersPanel.this.rc.getName() + "\n" + ((JComboBox<?>)arg0.getSource()).getSelectedItem());
			}
		});
		System.out.println("itemcount "+scalingOp.getItemCount());
		for (int i=0;i<scalingOp.getItemCount();i++)
		{
			System.out.println(scalingOp.getItemAt(i));
			System.out.println(rc.getScalingOperator());
			if (scalingOp.getItemAt(i).equals(rc.getScalingOperator())){
				scalingOp.setSelectedIndex(i);
				break;
			}
		}
		System.out.println("selectedIndex "+scalingOp.getSelectedIndex());
		description=new JTextField(rc.getDescription());
		description.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyChar() == KeyEvent.VK_ENTER)
					RCParametersPanel.this.editorFrame.fireActionEvent("Set description\n" + RCParametersPanel.this.rc.getName() + "\n" + description.getText());
			}
			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyTyped(KeyEvent arg0) {}
		});
		description.setColumns(30);
		buildPanel();
	}

	private void buildPanel()
	{
		
		
		setLayout(new FlowLayout());
		add(new JLabel("scaling operator:"));
		add(scalingOp);
		add(new JLabel("description:"));
		add(description);
	}
	
	@Override
	public void contextChanged(final ContextChangeEvent e) {
		switch (e.getAction()){
		case ContextChangeEvent.SCALING_OP_CHANGED :
			
			
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						String name=e.getNewName();
						for (int i=0;i<scalingOp.getItemCount();i++)
						{
							if (scalingOp.getItemAt(i).equals(name)){
								if (scalingOp.getSelectedIndex()!=i)
									scalingOp.setSelectedIndex(i);
								break;
								
							}
						}
						
						
					}
				});
			
			break;
		case ContextChangeEvent.DESCRIPTION_CHANGED :
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {

					description.setText(e.getNewName());
					
					
					
				}
			});
			
			break;
		}
		
		
		
	}

	
	
}
