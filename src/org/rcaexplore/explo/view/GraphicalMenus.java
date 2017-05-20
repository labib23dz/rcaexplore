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
package org.rcaexplore.explo.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rcaexplore.algo.Algorithm;
import org.rcaexplore.algo.multicontext.ExploMultiFCA;
import org.rcaexplore.constraint.CheckEqualityOperators;
import org.rcaexplore.constraint.ListEqualityConstraint;
import org.rcaexplore.constraint.ShowDialog;
import org.rcaexplore.context.ObjectAttributeContext;
import org.rcaexplore.context.ObjectObjectContext;
import org.rcaexplore.event.UserAction;
import org.rcaexplore.event.UserEvent;
import org.rcaexplore.event.UserListener;
import org.rcaexplore.launch.OutputChooser;
import org.rcaexplore.scaling.AvailableScalingOperators;
import org.rcaexplore.scaling.ScalingOperator;
import org.rcaexplore.visu.VisuFrame;

public class GraphicalMenus extends JFrame implements RCAExploreView{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ExploMultiFCA model;
	
	ArrayList<UserListener> listeners;
	
	public GraphicalMenus(){
		super("RCA-Explore");
		
		this.listeners=new ArrayList<UserListener>();
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				setSize(800, 600);
				setIconImage(Toolkit.getDefaultToolkit().getImage("img/rcaexplore.png"));
				setVisible(true);
				
			}
		});
	}
	
	public void addUserListener(UserListener l){
		listeners.add(l);
	}
	
	private void notifyUserAction(UserAction action, Object... parameters) {
		UserEvent event=new UserEvent(this, action, parameters);
		for (UserListener l : listeners){
			l.notifiedUserAction(event);
		}
	}
	
	
	public synchronized void chooseMode(final Object lock) {
		
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				
				getContentPane().removeAll();
				getContentPane().add(new JLabel("what kind of mode do you want to use: "), BorderLayout.CENTER);
				JPanel buttons=new JPanel();
				buttons.setLayout(new GridLayout(1, 2));
				getContentPane().add(buttons, BorderLayout.SOUTH);
				
				JButton bAuto=new JButton("auto");
				bAuto.addActionListener(new ActionListener() {
					 
		            public void actionPerformed(ActionEvent e)
		            {
		            	notifyUserAction(UserAction.SET_AUTO_MODE);
		               new Thread(){
		            	   public void run(){
		            	synchronized (lock) {
						
		               lock.notify();}}
		               }.start();
		            }
		        });
				JButton bManual=new JButton("manual");
				bManual.addActionListener(new ActionListener() {
					 
		            public void actionPerformed(ActionEvent e)
		            {
		               notifyUserAction(UserAction.SET_MANUAL_MODE);
		               synchronized (lock) {
						
		               lock.notify();
		               }
		            }
		        });
				
				buttons.add(bAuto,BorderLayout.SOUTH);
				buttons.add(bManual,BorderLayout.SOUTH);
				pack();
			}
		});
		
	}
	
	

	@Override
	public void setModel(ExploMultiFCA exploMFca) {
		this.model=exploMFca;
		
		
	}
	private void displayCurrentConfig(final Object lock2){
		getContentPane().removeAll();
		JPanel oaContexts=new JPanel();
		JScrollPane jsp=new JScrollPane(oaContexts);
		oaContexts.setLayout(new BoxLayout(oaContexts, BoxLayout.PAGE_AXIS));
		getContentPane().add(jsp,BorderLayout.CENTER);
		
		oaContexts.add(new JLabel("Object-Attribute Contexts"));
		
		for (ObjectAttributeContext c : model.getCurrentConfig().getSelectedOAContexts())
		{
			oaContexts.add(new JLabel("  "+c.getName()+" ("+model.getCurrentConfig().getAlgo(c)+")"));
		}
		if (! model.isInit()){
			
			oaContexts.add(new JLabel("  "));
			oaContexts.add(new JLabel("Object-Object Contexts"));
			
			for (ObjectObjectContext rc :  model.getCurrentConfig().getSelectedOOContexts())
			{
				oaContexts.add(new JLabel("  "+rc.getRelationName()+" ( "+rc.getSourceContext().getName()+"->"+rc.getTargetContext().getName()+" , "+model.getCurrentConfig().getScalingOperators(rc)+" ) "));
			}
		
		}
		JButton b0=new JButton("back");
		b0.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
               config(lock2);
            }
        });
		getContentPane().add(b0,BorderLayout.SOUTH);
		
		pack();
		
	}
	

	private void chooseConstructionAlgo(final Object lock2){
		getContentPane().removeAll();
		JPanel oaContexts=new JPanel();
		JScrollPane jsp=new JScrollPane(oaContexts);
		getContentPane().add(jsp, BorderLayout.CENTER);
		oaContexts.setLayout(new BoxLayout(oaContexts, BoxLayout.PAGE_AXIS));
		for (final ObjectAttributeContext c : model.getCurrentConfig().getSelectedOAContexts())
		{
			//a panel for each OA context
			JPanel contextPanel=new JPanel();
			contextPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
						
			final JComboBox<Algorithm> algoCombo=new JComboBox<>();
			
			for (Algorithm s : Algorithm.values())
				algoCombo.addItem(s);
				
			algoCombo.setSelectedItem(model.getCurrentConfig().getAlgo(c));
			contextPanel.add(algoCombo);
			
				
			final SpinnerNumberModel parameterModel = new SpinnerNumberModel(0,0,100,1); 
			final JSpinner parameterSpinner = new JSpinner(parameterModel);
			parameterSpinner.setEnabled(model.getCurrentConfig().getAlgo(c).hasParameter());
			if (model.getCurrentConfig().getAlgo(c).hasParameter())
				parameterModel.setValue(model.getCurrentConfig().getAlgoParameter(c));
			contextPanel.add(parameterSpinner);
			
			contextPanel.add(new JLabel("  "+c.getName()));
			
			oaContexts.add(contextPanel);

			algoCombo.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					parameterSpinner.setEnabled(((Algorithm)algoCombo.getSelectedItem()).hasParameter());
					if (((Algorithm)algoCombo.getSelectedItem()).hasParameter()) {
						if (((Algorithm)algoCombo.getSelectedItem()).equals(c.getDefaultAlgo()))
							parameterModel.setValue(c.getDefaultAlgoParameter());
						else
							parameterModel.setValue(((Algorithm)algoCombo.getSelectedItem()).getDefaultParameterValue());
					}
					else 
						notifyUserAction(UserAction.SET_OA_CONSTRUCTION_ALGO,c,algoCombo.getSelectedItem());
				}
			});
			
			parameterModel.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					notifyUserAction(UserAction.SET_OA_CONSTRUCTION_ALGO_WITH_PARAM,c,algoCombo.getSelectedItem(),parameterModel.getValue());
					
				}
			});
			
			
			
			
		}
		JButton b0=new JButton("back");
		b0.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
               config(lock2);
            }
        });
		getContentPane().add(b0,BorderLayout.SOUTH);
		
		
		pack();
	}
	
	private void chooseScalingOperator(final Object lock2){
		getContentPane().removeAll();
		JPanel oaContexts=new JPanel();
		JScrollPane jsp=new JScrollPane(oaContexts);
		getContentPane().add(jsp, BorderLayout.CENTER);
		oaContexts.setLayout(new BoxLayout(oaContexts, BoxLayout.PAGE_AXIS));		
		for (final ObjectObjectContext c : model.getCurrentConfig().getSelectedOOContexts())
		{
			
			JPanel contextPanel=new JPanel();
			contextPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			
			DefaultListModel<String> listModel = new DefaultListModel<>();
			ArrayList<String> scalings=new ArrayList<String>();
					scalings.addAll(AvailableScalingOperators.getAvailableScaling().keySet());
			for (String s : scalings)
				listModel.addElement(s);
			
			
			JList<String> list = new JList<>(listModel); //data has type Object[]
			list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
			
			int[] selectedIndices=new int[model.getCurrentConfig().getScalingOperators(c).size()];
			int k=0;
			for (ScalingOperator s : model.getCurrentConfig().getScalingOperators(c)){
				selectedIndices[k]=scalings.lastIndexOf(s.getName());
				k++;
			}
			
			
			list.setSelectedIndices(selectedIndices);
			
						
			contextPanel.add(list);
			
			
			list.addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent arg0) {					
					List<?> selection=((JList<?>)arg0.getSource()).getSelectedValuesList();
					ArrayList<String> toRemove=new ArrayList<String>();
					ArrayList<String> toAdd=new ArrayList<String>();
					synchronized (model.getCurrentConfig().getScalingOperators(c)) {
						for (ScalingOperator oldS : model.getCurrentConfig().getScalingOperators(c)){
							boolean contains=false;
							for (Object newS :  selection){
								if (newS.equals(oldS.getName()))
									contains=true;
							}
							if (!contains){
								toRemove.add(oldS.getName());
							}
						}
						for (Object newS :  selection){
							boolean contains=false;
							for (ScalingOperator oldS : model.getCurrentConfig().getScalingOperators(c)){
							
								if (newS.equals(oldS.getName()))
									contains=true;
							}
							if (!contains){
								toAdd.add((String) newS);
							}
						}
					}
					for (String s : toRemove)
					{
						notifyUserAction(UserAction.REMOVE_SCALING_OPERATOR, c, s, null);
						System.out.println("remove "+s);
					}
					for (String s : toAdd){
						notifyUserAction(UserAction.ADD_SCALING_OPERATOR, c, s, null);
						System.out.println("add "+s);
					}				
				}					
			});
			contextPanel.add(new JLabel("  "+c.getRelationName()));
			oaContexts.add(contextPanel);
			
		}
		JButton b0=new JButton("back");
		b0.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {	
            /***Amirouche**/	            	
            CheckEqualityOperators checkEqualityOperators = new CheckEqualityOperators();
            HashMap<String, ArrayList<ObjectObjectContext>> constraintOOContext  =  checkEqualityOperators.constructHashMap(ListEqualityConstraint.getInstance().getLstConstraint(), model.getCurrentConfig().getSelectedOOContexts());        		
            if(!checkEqualityOperators.checkEqualityOperatorsOOContexts(constraintOOContext, model))
            {
            	ShowDialog showDialog = new ShowDialog(checkEqualityOperators.getMsgError(), "Error", 0);
            	showDialog.showMessageDialog();            	
            }
            else
            {
            	System.out.println("check operators : success");
            	config(lock2);
            	
            }
            /***Amirouche***/            
            }
        });
		getContentPane().add(b0,BorderLayout.SOUTH);
		
		
		pack();
	}
	
	private void chooseOAContext(final Object lock2){
		getContentPane().removeAll();
		JPanel oaContexts=new JPanel();
		JScrollPane jsp=new JScrollPane(oaContexts);
		getContentPane().add(jsp, BorderLayout.CENTER);
		oaContexts.setLayout(new BoxLayout(oaContexts, BoxLayout.PAGE_AXIS));
		for (final ObjectAttributeContext c : model.getRCF().getOAContexts())
		{
			JPanel contextPanel=new JPanel();
			contextPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			JCheckBox cb=new JCheckBox();
			contextPanel.add(cb);
			
			cb.setSelected(model.getCurrentConfig().getSelectedOAContexts().contains(c));
			cb.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (((JCheckBox)e.getSource()).isSelected()){
						notifyUserAction(UserAction.ADD_OA_CONTEXT, c, null, null);
						
						
					}
					else
					{
						notifyUserAction(UserAction.REMOVE_OA_CONTEXT, c, null, null);
					}
					
					
				}
			});
			contextPanel.add(new JLabel("  "+c.getName()));
			oaContexts.add(contextPanel);
			
		}
		JButton b0=new JButton("back");
		b0.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
               config(lock2);
            }
        });
		getContentPane().add(b0,BorderLayout.SOUTH);
		
		
		pack();
	}
	private void chooseOOContext(final Object lock2){
		getContentPane().removeAll();
		JPanel ooContexts=new JPanel();
		JScrollPane jsp=new JScrollPane(ooContexts);
		getContentPane().add(jsp, BorderLayout.CENTER);
		ooContexts.setLayout(new BoxLayout(ooContexts, BoxLayout.PAGE_AXIS));
		for (final ObjectObjectContext rc : model.getRCF().getOOContexts())
		{
			if(model.getCurrentConfig().getSelectedOAContexts().contains(rc.getSourceContext())
					&& model.getCurrentConfig().getPreviouslySelectedOAContexts().contains(rc.getTargetContext())){
					
				JPanel contextPanel=new JPanel();
				contextPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
				JCheckBox cb=new JCheckBox();
				contextPanel.add(cb);
				
				cb.setSelected(model.getCurrentConfig().getSelectedOOContexts().contains(rc));
				cb.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if (((JCheckBox)e.getSource()).isSelected()){
							System.out.println("notify add");
							notifyUserAction(UserAction.ADD_OO_CONTEXT, rc, null, null);
						}
						else
						{
							System.out.println("notify remove");
							notifyUserAction(UserAction.REMOVE_OO_CONTEXT, rc, null, null);
						}
					}
				});
				contextPanel.add(new JLabel("  "+rc.getRelationName()));
				ooContexts.add(contextPanel);
			}
			
		}
		JButton b0=new JButton("back");
		b0.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
               config(lock2);
            }
        });
		getContentPane().add(b0,BorderLayout.SOUTH);
		
		
		pack();
	}
	
	
	@Override
	public void config(final Object lock2) {
		getContentPane().removeAll();
		
		JPanel buttons=new JPanel();
		
			buttons.setLayout(new BoxLayout(buttons, BoxLayout.PAGE_AXIS));
		
		getContentPane().add(buttons, BorderLayout.CENTER);
		JButton b0=new JButton("continue");
		b0.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
               synchronized (lock2) {
   				
                   lock2.notify();
                   }
            }
        });
		buttons.add(b0);
		JButton b1=new JButton("display configuration");
		b1.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
               displayCurrentConfig(lock2);
            	
            }

        });
		buttons.add(b1);
		JButton b2=new JButton("choose Object-Attribute contexts");
		b2.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
               chooseOAContext(lock2);
            }
        });
		
		buttons.add(b2);
		JButton b4=new JButton("choose construction algorithm");
		b4.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
               chooseConstructionAlgo(lock2);
            }
        });
		buttons.add(b4);
		
		if(!model.isInit()) {
			JButton b5=new JButton("choose an Object-Object contexts");
			b5.addActionListener(new ActionListener() {
				 
	            public void actionPerformed(ActionEvent e)
	            {
	               chooseOOContext(lock2);
	            }
	        });
			buttons.add(b5);
			
			JButton b7=new JButton("choose a scaling operator");
			b7.addActionListener(new ActionListener() {
				 
	            public void actionPerformed(ActionEvent e)
	            {
	               chooseScalingOperator(lock2);
	            }
	        });
			buttons.add(b7);
			
			JButton b9=new JButton("stop the process");
			b9.addActionListener(new ActionListener() {
				 
	            public void actionPerformed(ActionEvent e)
	            {
	               notifyUserAction(UserAction.STOP_PROCESS);
	               synchronized (lock2) {
	   				
	                   lock2.notify();
	                   }
	            }
	        });
			buttons.add(b9);
		}
		pack();
				
	}

	@Override
	public void askStop(final Object lock) {
SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				getContentPane().removeAll();
				getContentPane().add(new JLabel("The number of concept has not changed from the previous step, it may be a sign that you reached a fixed point.Do you want to continue ?\n"), BorderLayout.CENTER);
				JPanel buttons=new JPanel();
				buttons.setLayout(new GridLayout(1, 2));
				getContentPane().add(buttons, BorderLayout.SOUTH);
				
				JButton bAuto=new JButton("yes");
				bAuto.addActionListener(new ActionListener() {
					 
		            public void actionPerformed(ActionEvent e)
		            {
		            	         
		            	synchronized (lock) {
						
		               lock.notify();}
		               
		            }
		        });
				JButton bManual=new JButton("no");
				bManual.addActionListener(new ActionListener() {
					 
		            public void actionPerformed(ActionEvent e)
		            {
		               notifyUserAction(UserAction.STOP_PROCESS);
		               synchronized (lock) {
						
		               lock.notify();
		               }
		            }
		        });
				
				buttons.add(bAuto,BorderLayout.SOUTH);
				buttons.add(bManual,BorderLayout.SOUTH);
				pack();
			}
		});
		
	}

	
	
	@Override
	public void askStopAfter10Steps(final Object lock,final int step) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				getContentPane().removeAll();
				getContentPane().add(new JLabel("You have already computed "+step+" steps, which is an important number and may be a \nsign that the process is looping.\nDo you want to continue ?"), BorderLayout.CENTER);
				JPanel buttons=new JPanel();
				buttons.setLayout(new GridLayout(1, 2));
				getContentPane().add(buttons, BorderLayout.SOUTH);
				
				JButton bAuto=new JButton("yes");
				bAuto.addActionListener(new ActionListener() {
					 
		            public void actionPerformed(ActionEvent e)
		            {
		            	         
		            	synchronized (lock) {
						
		               lock.notify();}
		               
		            }
		        });
				JButton bManual=new JButton("no");
				bManual.addActionListener(new ActionListener() {
					 
		            public void actionPerformed(ActionEvent e)
		            {
		               notifyUserAction(UserAction.STOP_PROCESS);
		               synchronized (lock) {
						
		               lock.notify();
		               }
		            }
		        });
				
				buttons.add(bAuto,BorderLayout.SOUTH);
				buttons.add(bManual,BorderLayout.SOUTH);
				pack();
			}
		});
		
		
	}

	
	@Override
	public void notifyEnd(final String outputFolder) {
		if (OutputChooser.XML
				&& 0 == JOptionPane.showConfirmDialog(
				    this,
				    "Do you want to visualize the generated concepts?",
				    "Visualisation",
				    JOptionPane.YES_NO_OPTION)) {
			
			this.dispose();
			new Thread(new Runnable() {
					@Override
					public void run() {
						VisuFrame f=new VisuFrame();
						f.loadCLF(outputFolder+"/result.xml");
					}
				}).start();
		} else {
			System.exit(0);
		}
	}
	

}
