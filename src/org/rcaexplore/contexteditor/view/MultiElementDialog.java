package org.rcaexplore.contexteditor.view;
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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MultiElementDialog extends JDialog {

	private final JList<Object> choicelist;
	private JButton ok;
	private JButton cancel;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MultiElementDialog(Frame owner, String title, final String message, final Object[] objects) {
		super(owner, title);
		final JLabel msg=new JLabel(message);
		choicelist=new JList<>(objects);
		ok=new JButton("OK");
		cancel=new JButton("Cancel");
		final JPanel p=new JPanel();
		JScrollPane jsp=new JScrollPane(choicelist);
		add(p);
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
				p.add(msg);
				
				p.add(jsp);
				
				JPanel buttons=new JPanel();
				buttons.add(ok);
				buttons.add(cancel);
				p.add(buttons);
				p.setVisible(true);
				
				
				pack();
				
		
		
		
		
	}
	public void addActionListener(ActionListener l)
	{
		ok.addActionListener(l);
		cancel.addActionListener(l);
	}
	
	
	public static ArrayList<String> showMultiSelectDialog(Frame owner, String title, String message, Object[] objects){
	
		
		final ArrayList<String> result=new ArrayList<String>();
		final MultiElementDialog med=new MultiElementDialog(owner,title, message,objects);
		
		
				med.pack();
				
				

		med.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final String e=arg0.getActionCommand();
				synchronized (result) {
							if (e.equals("OK"))
								for (Object o : med.choicelist.getSelectedValuesList())
									result.add((String)o);
							
								
							result.notify();
							med.setVisible(false);
							}
					}
				
				
			
		});
		
				med.setVisible(true);
	
		synchronized (result) {
			try {
				result.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return result;
		
	}
	
	public static void main(String[] args) {
		JFrame f=new JFrame();
		f.setVisible(true);
		String[] elementList=new String[5];
		elementList[0]="blah";
		elementList[1]="blih";
		elementList[2]="bluh";
		elementList[3]="flah";
		elementList[4]="flih";
		System.out.println(showMultiSelectDialog(f, "test", "choisis ! ", elementList));
	}

	
	

}
