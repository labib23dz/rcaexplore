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
package org.rcaexplore.cofbrowser.view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rcaexplore.cofbrowser.COFChange;
import org.rcaexplore.cofbrowser.COFChangedEvent;
import org.rcaexplore.cofbrowser.ConceptCourantChangedEvent;
import org.rcaexplore.cofbrowser.controller.COFController;
import org.rcaexplore.cofbrowser.model.COFNavigationModel;
import org.rcaexplore.cofbrowser.model.COListModel;
import org.rcaexplore.cofbrowser.model.StepsListModel;
import org.rcaexplore.conceptorder.generic.GenericConcept;
import org.rcaexplore.conceptorder.generic.GenericRelationalAttribute;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.context.BinaryAttribute;
import org.rcaexplore.context.Entity;


/**
 * La classe JFrameFieldCLF est l'interface graphique permettant l'affichage et l'édition d'informations contenues dans une clf
 *
 * @author xdolques
 *
 */
public class JFrameFieldCOF extends CLFView implements ActionListener, MouseListener, ChangeListener{

	
	private JFrame frame = null;
	private JPanel contentPane = null;
	
	//navigation panel
	private JPanel navigationPanel=null;
	private JTextField currentConceptName = null;
	private JButton validSearch = null;
	private JSpinner threshold = null;
	
	//visualisation panel
	private JPanel vizuPanel=null;
	private JList<GenericConcept> parentsList= null;
	private JList<GenericConcept> childrenList=null;
	private JList<Attribute> intentList= null;
	private JList<Attribute> simplifiedIntentList= null;
	private JList<Entity> extentList= null;
	private JList<GenericConcept> conceptList= null;
    
    //rename panel
    private JPanel renamePanel=null;
    private JTextField newName=null;
    private JButton validRename=null;
    
    //action panel
    private JPanel commandPanel=null;
    private JButton save=null;
    private JButton load=null;
    private JComboBox<String> cofList=null;
    private JComboBox<Integer> stepList=null;
    private JButton modifySingleton=null;
    
    
    //analyzer panel
    private JPanel analyzerPanel=null;
    private JTextArea premiseText=null;
    private JTextArea conclusionText=null;
    private JTextArea coConfig=null;
    
    private COFNavigationModel model; 
    
	public JFrameFieldCOF(COFController controller) {
		this(controller, null);
	}
	
	public JFrameFieldCOF(COFController controller, COFNavigationModel model){
		super(controller); 
		this.model=model;
		buildFrame(model);
		model.addCLFListener(this);
		getController().notifyCurrentConceptChanged(currentConceptName.getText());
	}
	private void buildFrame(COFNavigationModel model) {
		frame = new JFrame();
		//frame.setSize(300, 300);
		//frame.setPreferredSize(new Dimension(300,300));
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		
		 
		initNavigationPanel(model);
		contentPane.add(navigationPanel);
		
		initVizuPanel();
		contentPane.add(vizuPanel);
	
		initAnalyzerPanel();
		contentPane.add(analyzerPanel);
		
		initRenamePanel();
	    contentPane.add(renamePanel);
	
	    initCommandPanel(model);
	    contentPane.add(commandPanel);
	    
		frame.setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		frame.setTitle("Concept Order Family Browser");
		frame.pack();
		
	}
	
	private void initAnalyzerPanel(){
		JScrollPane js0 = new JScrollPane();
		JScrollPane js1 = new JScrollPane();
		JScrollPane js2 = new JScrollPane();
		
		analyzerPanel=new JPanel();
		
		analyzerPanel.setLayout(new GridLayout(1,2));
		
		premiseText=new JTextArea();
		
		premiseText.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Premise"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		js0.setViewportView(premiseText);
		analyzerPanel.add(js0);
		premiseText.setAutoscrolls(true);
		
		conclusionText=new JTextArea();
		conclusionText.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Conclusion"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		js1.setViewportView(conclusionText);
		analyzerPanel.add(js1);
		conclusionText.setAutoscrolls(true);
		
		coConfig=new JTextArea();
		coConfig.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Configuration"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		js2.setViewportView(coConfig);
		analyzerPanel.add(js2);
		coConfig.setAutoscrolls(true);
		
		
	}

	private void updateAnalyzerPanel(){
		String premise="";
		List<Attribute> sIntentSelectedValues= simplifiedIntentList.getSelectedValuesList();
		for (Attribute o : sIntentSelectedValues){
			if (o instanceof GenericRelationalAttribute) {
				GenericRelationalAttribute relAtt = (GenericRelationalAttribute) o;
				
			premise+=ConceptAnalyzer.analyzeRelationalAttribute(relAtt, 0, 15)+"\n";
			} else if (o instanceof BinaryAttribute){
				premise+=((BinaryAttribute) o).getValue();
			}
			
		}
		String conclusion="";
		List<Attribute> intentSelectedValues= intentList.getSelectedValuesList();
		for (Attribute o : intentSelectedValues){
			if (o instanceof GenericRelationalAttribute) {
				GenericRelationalAttribute relAtt = (GenericRelationalAttribute) o;
				
			conclusion+=ConceptAnalyzer.analyzeRelationalAttribute(relAtt, 0, 15)+"\n";
			} else if (o instanceof BinaryAttribute){
				conclusion+=((BinaryAttribute) o).getValue();
			}
			
		}
		
		premiseText.setText(premise);
		conclusionText.setText(conclusion);
		
	}
	
	/**
	 * initialize the panel dedicated to concept renaming
	 */
	private void initRenamePanel()
	{
		
		renamePanel=new JPanel();
		//ancienNom=new JTextField("ancienNom");
		//changeNom.add(ancienNom);
		renamePanel.add(new JLabel("rename current concept as: "));
		newName=new JTextField("newName");
		newName.addActionListener(this);
		newName.setActionCommand("rename");
		renamePanel.add(newName);
		validRename=new JButton("Rename");
		validRename.setActionCommand("rename");
		validRename.addActionListener(this);
		renamePanel.add(validRename);
		renamePanel.setLayout(new BoxLayout(renamePanel,BoxLayout.LINE_AXIS));
		renamePanel.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
	}
	/**
	 *  init the panel dedicated to concept order transformations
	 * @param model
	 */
	private void initCommandPanel(COFNavigationModel model)
	{
		commandPanel=new JPanel();
		commandPanel.setLayout(new BoxLayout(commandPanel,BoxLayout.LINE_AXIS));
		save=new JButton("save last changes");
		save.setActionCommand("save");
		save.addActionListener(this);
		commandPanel.add(save);
		
		
		
		load=new JButton("load modified COF");
		load.setActionCommand("load");
		load.addActionListener(this);
		commandPanel.add(load);
		
		
		
		modifySingleton=new JButton("rename nominal concepts");
		modifySingleton.setActionCommand("changeSingleton");
		modifySingleton.addActionListener(this);
		commandPanel.add(modifySingleton);
		
		
	}
	/**
	 * La méthode initChercheConcept initialise la partie de l'interface dédiée à l'affichage des informations contenues dans un concept de la CLF courante.
	 * 
	 */
	private void initNavigationPanel(COFNavigationModel model)
	{
		
		navigationPanel=new JPanel();

		navigationPanel.add(new JLabel("Step "));
		stepList=new JComboBox<>(new StepsListModel(model));
		stepList.setActionCommand("change step");
		stepList.addActionListener(this);
		navigationPanel.add(stepList);
		navigationPanel.add(new JLabel("Context "));
		cofList=new JComboBox<>(new COListModel(model));
		cofList.setActionCommand("change COF");
		cofList.addActionListener(this);
		navigationPanel.add(cofList);
		
		
		currentConceptName=new JTextField(model.getConceptCourantName());
		currentConceptName.setActionCommand("search");
		currentConceptName.addActionListener(this);

		navigationPanel.add(currentConceptName);
		
		validSearch = new JButton("update");
		validSearch.addActionListener(this);
		validSearch.setActionCommand("search");
		navigationPanel.add(validSearch);
		
		
		navigationPanel.setLayout(new BoxLayout(navigationPanel,BoxLayout.LINE_AXIS));
		navigationPanel.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		
		
	}
	
	
	
	private void initVizuPanel() {
		JScrollPane js0 = new JScrollPane();
		JScrollPane js1 = new JScrollPane();
		JScrollPane js5 = new JScrollPane();
		JScrollPane js2 = new JScrollPane();
		JScrollPane js3 = new JScrollPane();
		JScrollPane js4 = new JScrollPane();
		
		
		vizuPanel =new JPanel();
		JPanel conceptListPanel=new JPanel();
		
		conceptList = new JList<GenericConcept>();
		conceptList.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Concept List"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		conceptList.setCellRenderer(new ConceptCellRenderer());
		conceptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		js0.setViewportView(conceptList);
		
		conceptListPanel.setLayout(new BoxLayout(conceptListPanel, BoxLayout.PAGE_AXIS));
		JPanel thresholdPanel=new JPanel();
		thresholdPanel.add(new JLabel("support >="));
		threshold =new JSpinner(new SpinnerNumberModel(0, //initial value
				0, //min
				Short.MAX_VALUE, //max
				1));
		thresholdPanel.add(threshold);
		thresholdPanel.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		threshold.addChangeListener(this);
		conceptListPanel.add(thresholdPanel);
		conceptListPanel.add(js0);
		vizuPanel.add(conceptListPanel);
		conceptList.setAutoscrolls(true);
		conceptList.addMouseListener(this);
		
		
		parentsList=new JList<GenericConcept>();
		//parentsList =new JList(new ParentsListModel(model));
		parentsList.setBorder(BorderFactory.createCompoundBorder(
	            BorderFactory.createTitledBorder("Parents"),
	            BorderFactory.createEmptyBorder(5,5,5,5)));
		parentsList.setCellRenderer(new ConceptCellRenderer());
		//listContentPane.add(parentsList);
		js1.setViewportView(parentsList);
		vizuPanel.add(js1);
		parentsList.setAutoscrolls(true);
		parentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		parentsList.addMouseListener(this);
		
		childrenList=new JList<GenericConcept>();
		//parentsList =new JList(new ParentsListModel(model));
		childrenList.setBorder(BorderFactory.createCompoundBorder(
	            BorderFactory.createTitledBorder("Children"),
	            BorderFactory.createEmptyBorder(5,5,5,5)));
		childrenList.setCellRenderer(new ConceptCellRenderer());
		//listContentPane.add(parentsList);
		js5.setViewportView(childrenList);
		vizuPanel.add(js5);
		childrenList.setAutoscrolls(true);
		childrenList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		childrenList.addMouseListener(this);
		
	    //liste des éléments de l'extension du concept
		//extentList =new JList(new ExtentListModel(model));
		extentList=new JList<Entity>();
	    extentList.setAutoscrolls(true);
		extentList.setBorder(BorderFactory.createCompoundBorder(
	            BorderFactory.createTitledBorder("Extent"),
	            BorderFactory.createEmptyBorder(5,5,5,5)));
		
		//listContentPane.add(extentList);
		js3.setViewportView(extentList);
	    vizuPanel.add(js3);
	    //liste simplifiée des éléments de l'intension du concept
	    //simplifiedIntentList =new JList(new SimplifiedIntentListModel(model));
		simplifiedIntentList=new JList<Attribute>();
	    simplifiedIntentList.setAutoscrolls(true);
		simplifiedIntentList.setBorder(BorderFactory.createCompoundBorder(
	            BorderFactory.createTitledBorder("S. Intent"),
	            BorderFactory.createEmptyBorder(5,5,5,5)));
		simplifiedIntentList.addMouseListener(this);
		//listContentPane.add(intentList);
		if (simplifiedIntentList==null)
			System.out.println("c'est null");
		 js4.setViewportView(simplifiedIntentList);
	    vizuPanel.add(js4);

	    //liste des éléments de l'intension du concept
	    //intentList =new JList(new IntentListModel(model));
	    intentList = new JList<Attribute>();
	    intentList.setAutoscrolls(true);
	    intentList.setBorder(BorderFactory.createCompoundBorder(
	    		BorderFactory.createTitledBorder("Intent"),
	    		BorderFactory.createEmptyBorder(5,5,5,5)));
	    intentList.addMouseListener(this);
	    //listContentPane.add(intentList);
	    js2.setViewportView(intentList);
	    vizuPanel.add(js2);
	    
	    vizuPanel.setLayout(new BoxLayout(vizuPanel, BoxLayout.LINE_AXIS));
	    vizuPanel.setMinimumSize(new Dimension(0,100));
	}

	@Override
	public void close() {
		frame.dispose();
	}

	@Override
	public void display() {
		frame.setVisible(true);
	}

	public void conceptCourantChanged(ConceptCourantChangedEvent event) {
		System.out.println("change la selection");
		currentConceptName.selectAll();
		currentConceptName.setText(((COFNavigationModel)event.getSource()).getConceptCourantName());
	}

	public void actionPerformed(ActionEvent arg0) {
		System.out.println(arg0.getActionCommand());
		if (arg0.getActionCommand().equals("search"))
		{
				getController().notifyCurrentConceptChanged(currentConceptName.getText());
				System.out.println(currentConceptName.getText());
		}
		else if (arg0.getActionCommand().equals("rename"))
		{
			getController().notifyRenameRequest(currentConceptName.getText(),newName.getText());
		}
		else if (arg0.getActionCommand().equals("save"))
		{
			getController().notifySaveRequest();
		}
		else if (arg0.getActionCommand().equals("load"))
		{
			getController().notifyLoadRequest();
		}
		else if (arg0.getActionCommand().equals("changeSingleton"))
		{
			getController().notifyModifieSingletons(cofList.getSelectedItem().toString());
		}
		else if (arg0.getActionCommand().equals("change COF"))
		{
			System.out.println("change COF to "+cofList.getSelectedItem().toString());
			getController().notifyCOChange(cofList.getSelectedItem().toString());
		}
		else if (arg0.getActionCommand().equals("change step"))
		{
			System.out.println("change step to "+stepList.getSelectedIndex());
			getController().notifyStepChange(stepList.getSelectedIndex());
		}
	}

	private void updateCoConfig(){
		String config="algo: ";
		config+=model.getCurrentConceptOrder().getConstructionAlgorithm()+"\n";
		config+="size: "+model.getCurrentConceptOrder().getConceptNb()+"\n";
		for (String relation : model.getCurrentConceptOrder().getRelations()){
			config+=relation+" ( ";
			for (String scaling : model.getCurrentConceptOrder().getScalingOperators(relation)){
				config+=scaling+",";
			}
			config=config.substring(0, config.length()-1)+")\n";
		}
		coConfig.setText(config);
	}

	public void CLFChanged(COFChangedEvent event) {
		switch(event.getChange()){
		case currentStepChanged :
			
		case currentConceptOrderChanged :
			if (model.getCurrentConceptOrder()!=null)
				updateCoConfig();
			else
				coConfig.setText("");
		case currentConceptChanged :
			conceptList.setListData(model.getAllConceptsInCurrentConceptOrderOverThreshold((Integer)threshold.getValue()));
			if (model.getCurrentConcept()!=null) {
				parentsList.setListData(model.getCurrentConcept().getParents().toArray(new GenericConcept[0]));
				childrenList.setListData(model.getCurrentConcept().getChildren().toArray(new GenericConcept[0]));
				intentList.setListData(model.getCurrentConcept().getIntent().toArray(new Attribute[0]));
				extentList.setListData(model.getCurrentConcept().getExtent().toArray(new Entity[0]));
				simplifiedIntentList.setListData(model.getCurrentConcept().getSimplifiedIntent().toArray(new Attribute[0]));
				
			}
			else {
				parentsList.setListData(new GenericConcept[0]);
				childrenList.setListData(new GenericConcept[0]);
				intentList.setListData(new Attribute[0]);
				extentList.setListData(new Entity[0]);
				simplifiedIntentList.setListData(new Attribute[0]);
			}
				
			
			break;
		default : 
			break;
		}
	}

	@Override
	public void stepChanged() {}
		

	@Override
	public void mouseClicked(MouseEvent arg0) {
		System.out.println("mouse clicked "+arg0.getClickCount());
		if (arg0.getClickCount()==2 
				&& arg0.getSource() instanceof JList
				&& (arg0.getSource()==parentsList ||arg0.getSource()==childrenList ||arg0.getSource()==conceptList)) {
			JList<?> source=(JList<?>)arg0.getSource();
			System.out.println(source.getSelectedValue());
			currentConceptName.setText(source.getSelectedValue().toString());
			getController().notifyCurrentConceptChanged(currentConceptName.getText());
		}
		else if (arg0.getSource()==intentList ||arg0.getSource()==simplifiedIntentList){
			updateAnalyzerPanel();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		CLFChanged(new COFChangedEvent(COFChange.currentConceptChanged));
	}
}