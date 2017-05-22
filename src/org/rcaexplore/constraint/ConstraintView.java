package org.rcaexplore.constraint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;




public class ConstraintView extends JDialog {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private JComboBox<String> comboKeys;
	private JLabel lblRelationsContext = new JLabel("Relations :");;
	private JList<String> listRelationalContext = new JList<>();
	private HashMap<String, ArrayList<String>> lstConstraint;
	
	
	public ConstraintView(HashMap<String, ArrayList<String>> lstConstraint) throws HeadlessException {
		
		super();		
		this.lstConstraint = lstConstraint;
		this.PropertyConstraintDialog();
		this.initComponent(this.lstConstraint);	
		loadListRelationContext((String)this.comboKeys.getSelectedItem());		
		// TODO Auto-generated constructor stub		
	}

	public void setListRelationContext(DefaultListModel<String> listModel) {
		this.listRelationalContext.setModel(listModel);;
	}

	public void PropertyConstraintDialog()
	{
		this.setTitle("Constraint");
		this.setSize(500,400);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setModal(true);
		
	}
	
	
	private void initComponent(HashMap<String, ArrayList<String>> lstConstraint){
		
		
        comboKeys = new JComboBox<String>();        
        comboKeys.setPreferredSize(new Dimension(200, 25));
        
        for (String name : lstConstraint.keySet())
        {
        	comboKeys.addItem(name);
        }
        
        comboKeys.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
			if (e.getStateChange()==ItemEvent.SELECTED)
			{
				loadListRelationContext((String)e.getItem());				
			}
			}
		});
        
        listRelationalContext.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		listRelationalContext.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		listRelationalContext.setBorder(new LineBorder(Color.BLUE));
        
		
		JPanel panelComponents = new JPanel();	    
		//pan.setPreferredSize(new Dimension(220, 100));
        panelComponents.setBorder(BorderFactory.createTitledBorder("Equality Constraint"));        
		panelComponents.setLayout(new GridBagLayout());		
		panelComponents.add(comboKeys,new GridBagConstraints( 0,0,1,1,0.0,0.0,
				GridBagConstraints.FIRST_LINE_START,
				GridBagConstraints.NONE,
				new Insets(5,5,5,5), 0,0));
		panelComponents.add(lblRelationsContext,new GridBagConstraints( 0,1,1,1,0.0,0.0,
				GridBagConstraints.FIRST_LINE_START,
				GridBagConstraints.NONE,
				new Insets(5,5,5,5), 0,0));
		
		panelComponents.add(listRelationalContext,new GridBagConstraints( 0,2,1,1,0.0,0.0,
				GridBagConstraints.FIRST_LINE_START,
				GridBagConstraints.NONE,
				new Insets(5,5,5,5), 0,0));
		
		JPanel panelConstraint = new JPanel();
		panelConstraint.setLayout(new BorderLayout());
		panelConstraint.add(panelComponents, BorderLayout.NORTH);
		//pan.add(comboNames);
		//pan.add(lblRelationsContext);
		//pan.add(listRelationContext);
        
		
    //JPanel content = new JPanel();	    
    //content.add(pan);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(panelConstraint, BorderLayout.WEST);
		
	}
	
	private void loadListRelationContext (String key)
	{	
		
		DefaultListModel<String> listModel = new DefaultListModel<>();
		
		for (String s : this.lstConstraint.get(key))
			listModel.addElement(s);
		
		setListRelationContext(listModel);		
	}
	
	
}
