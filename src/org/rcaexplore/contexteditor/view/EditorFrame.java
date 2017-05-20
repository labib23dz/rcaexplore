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

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.simple.parser.ParseException;
import org.rcaexplore.constraint.ConstraintDialogView;
import org.rcaexplore.constraint.ListEqualityConstraint;
import org.rcaexplore.constraint.ParseJSON;
import org.rcaexplore.constraint.ShowDialog;
import org.rcaexplore.contexteditor.controller.ContextEvent;
import org.rcaexplore.contexteditor.controller.ContextListener;
import org.rcaexplore.contexteditor.model.ContextModelWithBitSet;
import org.rcaexplore.contexteditor.model.EditorContextFamilyModel;
import org.rcaexplore.contexteditor.model.EditorFormalContextModel;
import org.rcaexplore.contexteditor.model.EditorRelationalContextModel;

/**
 * The class EditorFrame is the main graphic class of the editor 
 * */

public class EditorFrame extends JFrame implements ContextListener, ActionListener, ChangeListener, KeyListener, TableModelListener {

	private static final long serialVersionUID = 1L;

	private final JTabbedPane tabs;

	private ArrayList<ActionListener> listeners;

	private final EditorContextFamilyModel model;

	/**status of the current context such as number of objects and attributes*/
	private JLabel labStatus;
	
	private EditorContextPanel editorContextPanel;

	public EditorFrame(EditorContextFamilyModel cfm) {
		listeners=new ArrayList<ActionListener>();
		tabs = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);
		editorContextPanel = new EditorContextPanel(this);
		
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					EditorFrame.this.setSize(800, 600);
					EditorFrame.this.setIconImage(Toolkit.getDefaultToolkit().getImage("img/rcaexplore.png"));
			
					EditorFrame.this.setTitle("RCAExplore Editor");
					setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
					getContentPane().setLayout(new BorderLayout());
		
					buildMenus();
					
					getContentPane().add(editorContextPanel,BorderLayout.NORTH);
					
					buildStatusBar();
			
					getContentPane().add(tabs,BorderLayout.CENTER);
					
				}
			});
		

		
		
		model = cfm;
		
		tabs.addChangeListener(EditorFrame.this);
		cfm.addContextListener(EditorFrame.this);
		this.setLocationByPlatform(true);
	}


	private void buildStatusBar() {
		
		labStatus = new JLabel();
		getContentPane().add(labStatus,BorderLayout.SOUTH);
			
	}

	private void buildMenus() {
		final JMenuBar menuBar = new JMenuBar();

		final JMenu menFile = new JMenu("File");
		final JMenuItem itLoad = new JMenuItem("Load RCF");
		itLoad.addActionListener(EditorFrame.this);
		final JMenuItem itSave = new JMenuItem("Save");
		itSave.addActionListener(EditorFrame.this);
		final JMenuItem itSaveAs = new JMenuItem("Save As...");
		itSaveAs.addActionListener(EditorFrame.this);
		final JMenuItem itExportTex= new JMenuItem("Export as TeX");
		itExportTex.addActionListener(EditorFrame.this);
		final JMenuItem itExportGalicia= new JMenuItem("Export as Galicia format");
		itExportGalicia.addActionListener(EditorFrame.this);
		final JMenuItem itLaunchRCA=new JMenuItem("Launch RCA");
		itLaunchRCA.addActionListener(EditorFrame.this);
		final JMenuItem itVisualizeConcepts=new JMenuItem("Visualize Concepts");
		itVisualizeConcepts.addActionListener(EditorFrame.this);
		
		menFile.add(itLoad);
		menFile.add(itSave);
		menFile.add(itSaveAs);
		menFile.add(itExportTex);
		menFile.add(itExportGalicia);
		menFile.add(itLaunchRCA);
		menFile.add(itVisualizeConcepts);
		menuBar.add(menFile);

		
		JMenu menRCF = new JMenu("RCF");
		
		JMenuItem itImportCtx = new JMenuItem("Import OA context (CSV)");
		itImportCtx.addActionListener(EditorFrame.this);
		itImportCtx.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		
		JMenuItem itAddCtx = new JMenuItem("Add formal context");
		itAddCtx.addActionListener(EditorFrame.this);
		itAddCtx.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		JMenuItem itRemCtx = new JMenuItem("Remove formal context");
		itRemCtx.addActionListener(EditorFrame.this);
		JMenuItem itAddRel = new JMenuItem("Add relational context");
		itAddRel.addActionListener(EditorFrame.this);
		JMenuItem itRemRel = new JMenuItem("Remove relational context");
		itRemRel.addActionListener(EditorFrame.this);
		JMenuItem itAddOpRel = new JMenuItem("Add opposite relational context");
		itAddOpRel.addActionListener(EditorFrame.this);
		JMenuItem itSetName = new JMenuItem("Set context name");
		itAddRel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		itSetName.addActionListener(EditorFrame.this);
		JMenuItem itAddRow = new JMenuItem("Add row");
		itAddRow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
		itAddRow.addActionListener(EditorFrame.this);
		JMenuItem itAddCol = new JMenuItem("Add column");
		itAddCol.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		itAddCol.addActionListener(EditorFrame.this);
		JMenuItem itRemRow = new JMenuItem("Remove row");
		itRemRow.addActionListener(EditorFrame.this);
		JMenuItem itRemCol = new JMenuItem("Remove column");
		itRemCol.addActionListener(EditorFrame.this);
		JMenuItem itRowName = new JMenuItem("Set row name");
		itRowName.addActionListener(EditorFrame.this);
		JMenuItem itColName = new JMenuItem("Set column name");;
		itColName.addActionListener(EditorFrame.this);
		JMenuItem itGenClf = new JMenuItem("Generate CLF");
		itGenClf.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.ALT_MASK));
		itGenClf.addActionListener(EditorFrame.this);
		menRCF.add(itImportCtx);
		menRCF.add(itAddCtx);
		menRCF.add(itRemCtx);
		menRCF.add(itAddRel);
		menRCF.add(itRemRel);
		menRCF.add(itAddOpRel);
		menRCF.add(itSetName);
		menRCF.addSeparator();
		menRCF.add(itAddRow);
		menRCF.add(itAddCol);
		menRCF.add(itRemRow);
		menRCF.add(itRemCol);
		menRCF.add(itRowName);
		menRCF.add(itColName);
		menRCF.addSeparator();
		
		
		/*******************************************
		 ************* Ajout Amirouche*************
		 *****************************************/
		final JMenu menConstraint = new JMenu("Constraint");
		JMenuItem itLoadCnst = new JMenuItem("Load Constraint");
		JMenuItem itViewCnst = new JMenuItem("View Constraint");
		itLoadCnst.addActionListener(EditorFrame.this);
		itViewCnst.addActionListener(EditorFrame.this);
		/*******************************************
		 ************* Fin Amirouche*************
		 *****************************************/
		menConstraint.add(itLoadCnst);
		menConstraint.add(itViewCnst);
		
		menuBar.add(menRCF);
		menuBar.add(menConstraint);
		

		setJMenuBar(menuBar);
		
	}

	public void addActionListener(ActionListener al) {
		listeners.add(al);
	}

	public void contextChanged(final ContextEvent e) {
		
		
		if (e.getAction().equals("new Context")) {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					ContextTable c=new ContextTable(e.getContext());
					e.getContext().addTableModelListener(EditorFrame.this);
					
					JPanel jp=new JPanel();
					jp.setLayout(new BorderLayout());
					JScrollPane jsp=new JScrollPane();
					if (e.getContext() instanceof EditorFormalContextModel)
						jp.add(new FCParametersPanel((EditorFormalContextModel) e.getContext(), EditorFrame.this), BorderLayout.NORTH);
					else
						jp.add(new RCParametersPanel((EditorRelationalContextModel) e.getContext(), EditorFrame.this), BorderLayout.NORTH);
					
					jp.add(jsp, BorderLayout.CENTER);
					jsp.setViewportView(c);
					String contextName=e.getContextName();
					boolean tabAdded=false;
					
					
					for (int i=0;i<tabs.getTabCount();i++) {
						if (tabs.getTitleAt(i).compareTo(contextName)>0) {
							tabs.insertTab(contextName,
									(e.getContext() instanceof EditorFormalContextModel) ? new ImageIcon(getClass().getResource("/img/iconOA.png")) : new ImageIcon(getClass().getResource("/img/iconOO.png")),
									jp,
									contextName,
									i );
							tabAdded=true;
							break;
						}
					}
					if (!tabAdded)
						tabs.insertTab(contextName,
								(e.getContext() instanceof EditorFormalContextModel) ? new ImageIcon(getClass().getResource("/img/iconOA.png")) : new ImageIcon(getClass().getResource("/img/iconOO.png")),
								jp,
								contextName,
								tabs.getTabCount() );
						
				}
			});
			
			updateContextName();
		}
		if (e.getAction().equals("remove Context")) {
			int indexToremove=0;
			for (int i=0;i<tabs.getTabCount();i++) {
				String title=tabs.getTitleAt(i);
				if (title.equals(e.getContextName())){
					indexToremove=i;
					break;
				}
			}
			tabs.remove(indexToremove);
		}
		else if (e.getAction().equals("new Context name")) {
			tabs.setTitleAt(tabs.indexOfTab(e.getContextName()),e.getContext().getName());
			updateContextName();
		}
		else if (e.getAction().equals("file Changed")) {
			this.setTitle("RCAExplore Editor: "+model.getCurrentFile().getName());
		}
		updateStatusBar();
	}

	

	private String inputContextName() {
		String s = (String) JOptionPane.showInputDialog(this,"Context name:","Customized Dialog",JOptionPane.PLAIN_MESSAGE,null,null,"context"+model.familySize());
		if ( s != null ) {
			if ( s.equals("") )
				JOptionPane.showMessageDialog(this, "Context name cannot be null.");
			else if ( model.existsContext(s) )
				JOptionPane.showMessageDialog(this, "Context name already exists.");
			else
				return s;
		}
		
		return null;
	}

	public void actionPerformed(final ActionEvent e) {
		final JFrame owner=this;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
		if (e.getActionCommand().equals("Import OA context (CSV)")) {
			JFileChooser jfc=new JFileChooser(".");
			jfc.setFileFilter(new FileNameExtensionFilter("CSV file (*.csv)", "csv"));
			
			if ( JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(owner) ) {
				String filePath=jfc.getSelectedFile().getAbsolutePath();
				fireActionEvent("Import OA context\n"+filePath);
			}
		}
		if (e.getActionCommand().equals("Add formal context")) {
			System.out.println("ajout contexte");
			String s = inputContextName();
			if (s==null) return;
			fireActionEvent("Add formal context\n"+s);
		}
		else if (e.getActionCommand().equals("Remove formal context")) {
			if (model.familySize() < 1)
				JOptionPane.showMessageDialog(owner, "No existing context to remove.");
			else {
				String toRemove = (String)JOptionPane.showInputDialog(owner,"Context to remove:","Customized Dialog",JOptionPane.PLAIN_MESSAGE,null,model.getFormalContexts().toArray(),null);
				JOptionPane.showMessageDialog(owner, "Warning: it will also remove the relational context related to the context removed");
				if (toRemove!=null)
					fireActionEvent("Remove formal context\n"+toRemove);
				
			}
		}
		else if (e.getActionCommand().equals("Save")||e.getActionCommand().equals("Save As...")) {
			if (e.getActionCommand().equals("Save")&&model.getCurrentFile()!=null)
				fireActionEvent("Save");
			else {
				JFileChooser jfc=new JFileChooser(".");
				jfc.setFileFilter(new FileNameExtensionFilter("Compressed RCFT file", "rcfgz"));
				jfc.addChoosableFileFilter(new FileNameExtensionFilter("RCFT file", "rcft"));
				if ( JFileChooser.APPROVE_OPTION==jfc.showSaveDialog(owner) ) {
					String filePath=jfc.getSelectedFile().getAbsolutePath();
					String extension = null;
					switch (jfc.getFileFilter().getDescription()) {
					case "RCFT file":extension="rcft";break;
					case "Compressed RCFT file":extension="rcfgz";break;
					}
					if (!filePath.endsWith("."+extension))
						filePath+="."+extension;
					fireActionEvent("Save As\n"+filePath);
				}	
			}
		}
		else if (e.getActionCommand().equals("Export as TeX")) {
			JFileChooser jfc=new JFileChooser(".");
			if ( JFileChooser.APPROVE_OPTION==jfc.showSaveDialog(owner) ) {
				String filePath=jfc.getSelectedFile().getAbsolutePath();
				fireActionEvent("Export as TeX\n"+filePath);
			}
		}
		else if (e.getActionCommand().equals("Export as Galicia format")) {
			JFileChooser jfc=new JFileChooser(".");
			if ( JFileChooser.APPROVE_OPTION==jfc.showSaveDialog(owner) ) {
				String filePath=jfc.getSelectedFile().getAbsolutePath();
				fireActionEvent("Export as Galicia format\n"+filePath);
			}
		}
		else if (e.getActionCommand().equals("Launch RCA")) {
			fireActionEvent("Launch RCA");
		}
		else if (e.getActionCommand().equals("Visualize Concepts")) {
			fireActionEvent("Visualize Concepts");
		}
		else if (e.getActionCommand().equals("Load RCF")) {
			JFileChooser jfc=new JFileChooser(".");
			jfc.setFileFilter(new FileNameExtensionFilter("RCAExplore RCF file format (*.rcfgz, *.rcft)", "rcfgz", "rcft"));
			
			if ( JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(owner) ) {
				String filePath=jfc.getSelectedFile().getAbsolutePath();
				fireActionEvent("Load RCF\n"+filePath);
			}
		}
		else if (e.getActionCommand().equals("Add relational context")) {
			if (model.familySize() < 1)
				JOptionPane.showMessageDialog(owner, "It is not possible to create a relational context without having created formal contexts.");
			else {
				String source = (String)JOptionPane.showInputDialog(owner,"Choose the source context:","Customized Dialog",JOptionPane.PLAIN_MESSAGE,null,model.getFormalContexts().toArray(),null);
				if ( source != null ) {
					String target = (String)JOptionPane.showInputDialog(owner,"Choose the target context:","Customized Dialog",JOptionPane.PLAIN_MESSAGE,null,model.getFormalContexts().toArray(),null);
					if (target != null) {
						String name=inputContextName();
						if ( name != null)
							fireActionEvent("Add relational context\n"+name+"\n"+source+"\n"+target);
					}
				}
			}
		}
		else if (e.getActionCommand().equals("Remove relational context")) {
			if (model.getRelationalContexts().size()<1)
				JOptionPane.showMessageDialog(owner, "No existing relational contexts to remove.");
			else
			{
				String toRemove = (String)JOptionPane.showInputDialog(owner,"Choose the context to remove:","Customized Dialog",JOptionPane.PLAIN_MESSAGE,null,model.getRelationalContexts().toArray(),null);
				if (toRemove!=null){
					fireActionEvent("Remove relational context\n"+toRemove+"\n");
				}
			}
		}
		else if (e.getActionCommand().equals("Add opposite relational context")){
			if (model.getRelationalContexts().size() ==0)
				JOptionPane.showMessageDialog(owner, "It is not possible to create an opposite relational context without having created a relational context.");
			else {
				String original = (String)JOptionPane.showInputDialog(owner,"Choose the relational context:","Customized Dialog",JOptionPane.PLAIN_MESSAGE,null,model.getRelationalContexts().toArray(),null);
				if (original != null) {
					String name=inputContextName();
					if (name!=null)
						fireActionEvent("Add opposite relational context\n"+name+"\n"+original);
				}
			}
		}
		else if (e.getActionCommand().equals("Set context name")) {
			String oldName=(String)JOptionPane.showInputDialog(owner,"Choose context:","Customized Dialog",JOptionPane.PLAIN_MESSAGE,	null,model.getContexts().toArray(),null);
			if ( oldName != null) {
				String newName = inputContextName();
				if (newName != null) 
					fireActionEvent("Set context name\n"+oldName+"\n"+newName);
			}
		}
		else if (e.getActionCommand().equals("Add row")) {
			if ( selectedContext() == null )
				JOptionPane.showMessageDialog(owner, "It is not possible to create columns before having created a context.");
			else
				fireActionEvent("Add row\n"+selectedContext());
		}
		else if (e.getActionCommand().equals("Add column"))	{
			if ( selectedContext() == null )
				JOptionPane.showMessageDialog(owner, "It is not possible to create rows before having created a context.");
			else
				fireActionEvent("Add column\n"+selectedContext());
		}
		else if (e.getActionCommand().equals("Remove column")) {
			if ( selectedContext() == null )
				JOptionPane.showMessageDialog(owner, "It is not possible to delete columns before having created a context.");
			else if ( model.getAttributes(selectedContext()).length==0)
				return;
			else {
				/*String column = (String)JOptionPane.showInputDialog(this,"Choose the column to remove",	"Customized Dialog",JOptionPane.PLAIN_MESSAGE,null,model.getAttributes(selectedContext()),null);
				if ( column != null ) 
					fireActionEvent("Remove column\n"+selectedContext()+"\n"+column);*/
				
				ArrayList<String> columns = MultiElementDialog.showMultiSelectDialog(owner, "Remove columns", "Choose the columns to remove",  model.getAttributes(selectedContext()));
				for (String c : columns)
				{
						fireActionEvent("Remove column\n"+selectedContext()+"\n"+c);

					
				}
			}
		}
		else if (e.getActionCommand().equals("Remove row")) {
			if ( selectedContext() == null )
				JOptionPane.showMessageDialog(owner, "It is not possible to delete rows before having created a context.");
			else if ( model.getObjects(selectedContext()).length == 1 )
				return;
			else {
				ArrayList<String> rows = MultiElementDialog.showMultiSelectDialog(owner, "Remove rows", "Choose the rows to remove",  model.getObjects(selectedContext()));
				if (rows.size()==model.getObjects(selectedContext()).length)
					JOptionPane.showMessageDialog(owner, "At least one object must remain.");
				else
					for (String r : rows)
					{
							fireActionEvent("Remove row\n"+selectedContext()+"\n"+r);
					}
				
			}
		}
		else if (e.getActionCommand().equals("Set row name")) {
			ArrayList<String> rows = MultiElementDialog.showMultiSelectDialog(owner, "Rename rows", "Choose the rows to rename",  model.getObjects(selectedContext()));
			
			for (String row : rows){
				String newName = (String) JOptionPane.showInputDialog(owner,"New row name:","Rename row",JOptionPane.PLAIN_MESSAGE,null,null,row);
				if ( newName != null ) {
					if ( "".equals(newName) )
						JOptionPane.showMessageDialog(owner,"Name cannot be empty.");
					else if ( Arrays.asList(model.getObjects(selectedContext())).contains(newName) )
						JOptionPane.showMessageDialog(owner,"Name already existing");
					else
						fireActionEvent("Set row name\n"+selectedContext()+"\n"+row+"\n"+newName);
				}
			}
		}
		else if (e.getActionCommand().equals("Set column name")) {
			ArrayList<String> columns = MultiElementDialog.showMultiSelectDialog(owner, "Rename columns", "Choose the columns to rename",  model.getAttributes(selectedContext()));
			for (String col:columns){
				String newName = (String) JOptionPane.showInputDialog(owner,"New column name:","Rename column",JOptionPane.PLAIN_MESSAGE,null,null,col);
				if ( newName != null ) {
					if ( "".equals(newName) )
						JOptionPane.showMessageDialog(owner,"Name cannot be empty.");
					else if ( Arrays.asList(model.getAttributes(selectedContext())).contains(newName) )
						JOptionPane.showMessageDialog(owner,"Name already existing");
					else
						fireActionEvent("Set column name\n"+selectedContext()+"\n"+col+"\n"+newName);
				}
			}
		}
		/** 
		 * Amirouche
		 */
		else if (e.getActionCommand().equals("Load Constraint"))
		{
			ParseJSON parseJson = ParseJSON.getInstance();
			try {
				parseJson.parseJson();				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
		}
		else if (e.getActionCommand().equals("View Constraint"))
		{
			if (ListEqualityConstraint.getInstance().getLstConstraint().isEmpty())
			{
				ShowDialog showDialog = new ShowDialog("no constraint loaded", "Information", 1);
				showDialog.showMessageDialog();
			}
			else
			{
				ConstraintDialogView constraintDialog = new ConstraintDialogView(ListEqualityConstraint.getInstance().getLstConstraint());
				constraintDialog.setVisible(true);
			}
			
		}
		
		/**
		 * Fin Amirouche
		 */
			}
			}).start();
	}

	private void updateStatusBar() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if ( selectedContext() != null ) {
					String statusTxt = "";
					String selectedContext = selectedContext();
						
					if ( model.getFormalContexts().contains(selectedContext) )
						statusTxt += "Formal context ";
					else
						statusTxt += "Relational context ";
					statusTxt += selectedContext;
					ContextModelWithBitSet m= model.getContextModel(selectedContext);
					statusTxt+=" (rows: "+m.getRowCount()+" , cols: "+(m.getColumnCount()-1)+")";
					labStatus.setText(statusTxt);
				}}
		});
	}

	private void updateContextName() {
		editorContextPanel.setCtxName( selectedContext() );
	}

	private String selectedContext() {
		if ( tabs.getSelectedIndex() == -1 )
			return null;
		else
			return tabs.getTitleAt(tabs.getSelectedIndex());
	}

	protected void fireActionEvent(final String actionString) {
		for(final ActionListener al: listeners)
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					
					al.actionPerformed( new ActionEvent(this,1,actionString) );
					
				}
			});
	}

	public void stateChanged(ChangeEvent e) {
		updateContextName();
		updateStatusBar();
	}

	public void keyPressed(KeyEvent e) {
		if ( e.getSource() == editorContextPanel.getFldCtxName() && e.getKeyChar() == KeyEvent.VK_ENTER )
			fireActionEvent("Set context name\n" + selectedContext() + "\n" + editorContextPanel.getCtxName() );
	}

	public void keyReleased(KeyEvent e) {}

	public void keyTyped(KeyEvent e) {}

	@Override
	public void tableChanged(TableModelEvent arg0) {
		updateStatusBar();
	}
 
}
