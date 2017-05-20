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
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rcaexplore.algo.Algorithm;
import org.rcaexplore.contexteditor.controller.ContextChangeEvent;
import org.rcaexplore.contexteditor.controller.ContextChangeListener;
import org.rcaexplore.contexteditor.model.EditorFormalContextModel;

public class FCParametersPanel extends JPanel implements ContextChangeListener {

	/**
	 * automatically added serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private final JComboBox<Algorithm> algoCombo;
	private final SpinnerNumberModel parameterModel;
	private final JSpinner parameterSpinner;
	private final JTextField description;
	private final EditorFrame editorFrame;
	private final EditorFormalContextModel aocModel;
	public FCParametersPanel(EditorFormalContextModel aocModel, EditorFrame editorFrame) {
		super();
		this.editorFrame=editorFrame;
		this.aocModel=aocModel;
		aocModel.addContextChangeListener(this);
		
		algoCombo=new JComboBox<>();
		
		for (Algorithm s : Algorithm.values())
			algoCombo.addItem(s);
			
		algoCombo.setSelectedItem(aocModel.getAlgo());
		
		
			
		parameterModel = new SpinnerNumberModel(0,0,100,1); 
		parameterSpinner = new JSpinner(parameterModel);
		parameterSpinner.setEnabled(((Algorithm)algoCombo.getSelectedItem()).hasParameter());
		if (aocModel.getAlgo().hasParameter())
			parameterModel.setValue(aocModel.getAlgoParameter());
		
		algoCombo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				parameterSpinner.setEnabled(((Algorithm)algoCombo.getSelectedItem()).hasParameter());
				new Thread(new Runnable() {
					
					@Override
					public void run() {
				if (((Algorithm)algoCombo.getSelectedItem()).hasParameter()) {
					parameterModel.setValue(((Algorithm)algoCombo.getSelectedItem()).getDefaultParameterValue());
				}
				else 
							FCParametersPanel.this.editorFrame
							.fireActionEvent("Set algo name\n"
									+ FCParametersPanel.this.aocModel.getName()
									+ "\n"
									+ algoCombo.getSelectedItem().toString());
						}
					}).start();
					
			}
		});
		
		parameterModel.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						FCParametersPanel.this.editorFrame
						.fireActionEvent("Set algo name and parameter\n"
								+ FCParametersPanel.this.aocModel.getName()
								+ "\n" + algoCombo.getSelectedItem().toString()
								+ "\n" + parameterModel.getValue().toString());
						
					}
				}).start();

			}
		});
		
		
		description=new JTextField(aocModel.getDescription());
		description.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyChar() == KeyEvent.VK_ENTER)
					FCParametersPanel.this.editorFrame.fireActionEvent("Set description\n" + FCParametersPanel.this.aocModel.getName() + "\n" + description.getText());
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
		add(new JLabel("algo:"));
		add(algoCombo);
		add(parameterSpinner);
		add(new JLabel("description:"));
		add(description);
	}
	
	@Override
	public void contextChanged(final ContextChangeEvent e) {
		switch (e.getAction()){
		case ContextChangeEvent.ALGO_CHANGED :
			
				
						algoCombo.setSelectedItem(Algorithm.getAlgo(e.getNewName()));
									
			break;
		case ContextChangeEvent.ALGO_PARAM_CHANGED : 
			parameterModel.setValue(Integer.parseInt(e.getNewParam()));
			break;
		case ContextChangeEvent.DESCRIPTION_CHANGED :
						description.setText(e.getNewName());
			break;
		}
		
		
		
	}

	
	
}
