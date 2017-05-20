package org.rcaexplore.constraint;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class ShowDialog{
	
	
	private JFrame frame = new JFrame();
	private String msg;
	private String title;
	private int msgType;
	
	public ShowDialog(String msg, String title, int msgType)
	{
		this.msg=msg;
		this.title=title;
		this.msgType=msgType;
	}
	
	
	public void showMessageDialog()
	{		
		  JOptionPane.showMessageDialog(frame,msg,title,msgType);	
		  //ListEqualityConstraint.getInstance().getLstConstraint().clear();
	}
	
}
