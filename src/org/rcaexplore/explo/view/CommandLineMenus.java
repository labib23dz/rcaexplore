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

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;

import org.rcaexplore.algo.Algorithm;
import org.rcaexplore.algo.multicontext.ExploMultiFCA;
import org.rcaexplore.context.ObjectAttributeContext;
import org.rcaexplore.context.ObjectObjectContext;
import org.rcaexplore.event.UserAction;
import org.rcaexplore.event.UserEvent;
import org.rcaexplore.event.UserListener;
import org.rcaexplore.scaling.AvailableScalingOperators;
import org.rcaexplore.scaling.ScalingOperator;


//TODO obsolete class
public class CommandLineMenus implements RCAExploreView {

	InputStream in;
	PrintStream out;
	ExploMultiFCA model;
	
	Scanner scan;
	
	ArrayList<UserListener> listeners;
	
	public CommandLineMenus(InputStream in, PrintStream out){
		this.in=in;
		this.listeners=new ArrayList<UserListener>();
		scan=new Scanner(in);
		this.out=out;
	}
	
	public void setModel(ExploMultiFCA model){
		this.model=model;
	}
	
	public void addUserListener(UserListener l){
		listeners.add(l);
	}
	
	public void notifyUserAction(UserAction action){
		notifyUserAction(action, null,null);
	}
	
	private void notifyUserAction(UserAction action, Object param1, Object param2) {
		UserEvent event=new UserEvent(this, action, param1, param2, null);
		for (UserListener l : listeners){
			l.notifiedUserAction(event);
		}
		
		
	}

	public void chooseMode(final Object lock) {
		
		new Thread(){
			public void run(){
				System.out.println("This is the menu of the interactive RCA process");
				System.out.println("What kind of mode do you want to use: ");
				System.out.println("0 : automatic");
				System.out.println("1 : manual");
				
				int i=-1;
				try {
					i=scan.nextInt();
				}
				catch(InputMismatchException e){
					System.err.println("your answer must be an integer");
					scan.next();
				}
				if (i==0){
					notifyUserAction(UserAction.SET_AUTO_MODE);
				}
				else if (i==1){
					notifyUserAction(UserAction.SET_MANUAL_MODE);
				}
				else{
					System.out.println("I can't understand your answer, can you try it again please");
					chooseMode(lock);
				}
				synchronized (lock) {
					lock.notify();
				}
				
			}
		}.start();
		
	}
	
	public void config(final Object lock){
		new Thread(){
			public void run(){
				System.out.println("configure next step");
				System.out.println("0: continue");
				System.out.println("1: display configuration");
				System.out.println("2: add an Object-Attribute context");
				System.out.println("3: remove an Object-Attribute context");
				System.out.println("4: choose construction algorithm");
				if (!model.isInit())
				{
					System.out.println("5: add an Object-Object context");
					System.out.println("6: remove an Object-Object context");
					System.out.println("7: add a scaling operator");
					System.out.println("8: remove a scaling operator");
					System.out.println("9: stop the process");
				}
				int answer=-1;
				try {
					answer=scan.nextInt();
					}
				catch(InputMismatchException e){
					System.err.println("your answer must be an integer");
					scan.next();
				}
				if (answer==0){
					synchronized (lock) {
						lock.notify();
					}
					return;
				}
				else if (answer==1){
					displayCurrentConfig();
				}
				else if (answer==2){
					addOAContext();
				}
				else if (answer==3 ){
					removeOAContext();
				}
				else if (answer==4 ){
					chooseConstructionAlgo();
				}
				else if (answer==5 && !model.isInit()) {
					addRelationalContext();
				}
				else if (answer==6 && !model.isInit()) {
					removeRelationalContext();
				}
				else if (answer==7 && !model.isInit()){
					addScalingOperator();
				}
				else if (answer==8 && !model.isInit()){
					removeScalingOperator();
				}
				else if (answer==9){
					notifyUserAction(UserAction.STOP_PROCESS);
					synchronized (lock) {
						lock.notify();
					}
					return;
				}
				
				else{
					out.println("I can't understand your answer, can you try it again please");
				}
				config(lock);
			}
		}.start();
		
	}
	

	private void removeRelationalContext() {
		
		System.out.println("choose Object-Object context to remove: ");
		System.out.println("0:Cancel");
		int i=1;
		for (ObjectObjectContext rc : model.getCurrentConfig().getSelectedOOContexts()){
			System.out.println(i+": "+rc.getRelationName());
			i++;
		}
		
		int answer=-1;
		try {
			answer=scan.nextInt();
			}
		catch(InputMismatchException e){
			System.err.println("your answer must be an integer");
			scan.next();
		}
		if (answer==0){
			return;
		}
		else if (answer>0 && answer < i){
			ObjectObjectContext toRemove=model.getCurrentConfig().getSelectedOOContexts().get(answer-1);
			
			notifyUserAction(UserAction.REMOVE_OO_CONTEXT, toRemove, null);

		}
		else{
			System.out.println("I can't understand your answer, action cancelled");
			return;
		}
		
	}
	
	private void removeScalingOperator(){
		ObjectObjectContext chosenRC=null;
		System.out.println("choose context to remove operator to: ");
		System.out.println("0:Cancel");
		int i = 1;
		for (ObjectObjectContext rc : model.getCurrentConfig().getSelectedOOContexts()){
			System.out.println(i+": "+rc.getRelationName()+" operators: ");
			for (ScalingOperator s : model.getCurrentConfig().getScalingOperators(rc)){
				System.out.println("\t\t"+s.getName());
			}
			i++;
		}
		int answer=-1;
		try {
			answer=scan.nextInt();
			}
		catch(InputMismatchException e){
			System.err.println("your answer must be an integer");
			scan.next();
		}
		if (answer==0){
			return;
		}
		else if (answer>0 && answer < i){
			chosenRC=model.getCurrentConfig().getSelectedOOContexts().get(answer-1);
		}
		else{
			System.out.println("I can't understand your answer, action cancelled");
			return;
		}
		
		if (chosenRC!=null){
			
			
			if (model.getCurrentConfig().getScalingOperators(chosenRC).size()==1){
				System.out.println("a relational context must have at least one scaling operator, operation cancelled");
				return;
			}
			else
			{
				System.out.println("choose operator to remove to the relational context "+chosenRC.getRelationName());
				System.out.println("0:Cancel");
				int j =1;
				for (ScalingOperator s : model.getCurrentConfig().getScalingOperators(chosenRC)){
					System.out.println(j+":"+s.getName());
					j++;
				}
				
				answer=-1;
				try {
					answer=scan.nextInt();
					}
				catch(InputMismatchException e){
					System.err.println("your answer must be an integer");
					scan.next();
				}
				if (answer==0){
					return;
				}
				else if (answer>0 && answer < j){
					notifyUserAction(UserAction.REMOVE_SCALING_OPERATOR, chosenRC, model.getCurrentConfig().getScalingOperators(chosenRC).get(answer-1));
					
					
				}
				else{
					System.out.println("I can't understand your answer, action cancelled");
					return;
				}
				
			}
			
			
		} else
			return;
		
		
	}
	
	private void addRelationalContext() {
//		System.out.println("Object-Object contexts that can be added");
//		System.out.println("(note: at one given step an object-object context can be added");
//		System.out.println("only if its target existed in the previous step)");
//		
		ArrayList<ObjectObjectContext> addableContexts= new ArrayList<ObjectObjectContext>();
		
		
		for (ObjectObjectContext rc : model.getRCF().getOOContexts()){
			if (!model.getCurrentConfig().getSelectedOOContexts().contains(rc)
					&&model.getCurrentConfig().getSelectedOAContexts().contains(rc.getSourceContext())
					&& model.getCurrentConfig().getPreviouslySelectedOAContexts().contains(rc.getTargetContext())){
				addableContexts.add(rc);
			}
		}
		out.println("choose Object-Object context to add: ");
		out.println("0:Cancel");
		int i=1;
		for (ObjectObjectContext rc : addableContexts){
			System.out.println(i+": "+rc.getRelationName());
			i++;
		}
		
		int answer=-1;
		try {
			answer=scan.nextInt();
			}
		catch(InputMismatchException e){
			System.err.println("your answer must be an integer");
			scan.next();
		}
		if (answer==0){
			return;
		}
		else if (answer>0 && answer < i){
			notifyUserAction(UserAction.ADD_OO_CONTEXT, addableContexts.get(answer-1), null);			
		
		}
		else{
			System.out.println("I can't understand your answer, action cancelled");
			return;
		}
		
	}
	
	
	
	private void addScalingOperator(){
		ObjectObjectContext chosenRC=null;
		out.println("choose context to add operator to: ");
		out.println("0:Cancel");
		int i = 1;
		for (ObjectObjectContext rc : model.getCurrentConfig().getSelectedOOContexts()){
			out.println(i+": "+rc.getRelationName()+" operators: ");
			for (ScalingOperator s : model.getCurrentConfig().getScalingOperators(rc)){
				out.println("\t\t"+s.getName());
			}
			i++;
		}
		int answer=-1;
		try {
			answer=scan.nextInt();
			}
		catch(InputMismatchException e){
			System.err.println("your answer must be an integer");
			scan.next();
		}
		if (answer==0){
			return;
		}
		else if (answer>0 && answer < i){
			chosenRC=model.getCurrentConfig().getSelectedOOContexts().get(answer-1);
		}
		else{
			out.println("I can't understand your answer, action cancelled");
			return;
		}
		
		if (chosenRC!=null){
			ArrayList<String> operatorsToAdd=new ArrayList<String>();
			
			for (String s : AvailableScalingOperators.getAvailableScaling().keySet()){
				if (! model.getCurrentConfig().getScalingOperators(chosenRC).contains(s)){
					operatorsToAdd.add(s);
				}
			}
			
			if (operatorsToAdd.isEmpty()){
				out.println("no more scaling operator to add, action cancelled");
			}
			else
			{
				out.println("choose operator to add to the relational context "+chosenRC.getRelationName());
				out.println("0:Cancel");
				int j =1;
				for (String s : operatorsToAdd){
					out.println(j+":"+s);
					j++;
				}
				
				answer=-1;
				try {
					answer=scan.nextInt();
					}
				catch(InputMismatchException e){
					System.err.println("your answer must be an integer");
					scan.next();
				}
				if (answer==0){
					return;
				}
				else if (answer>0 && answer < j){
					notifyUserAction(UserAction.ADD_SCALING_OPERATOR,chosenRC,operatorsToAdd.get(answer-1));
					
				}
				else{
					System.out.println("I can't understand your answer, action cancelled");
					return;
				}
				
			}
			
			
		} else
			return;
		
		
	}
	
	
	private void chooseConstructionAlgo(){
		ObjectAttributeContext chosenFC=null;
		out.println("choose context to change construction algorithm of: ");
		out.println("0:Cancel");
		int i = 1;
		for (ObjectAttributeContext fc : model.getCurrentConfig().getSelectedOAContexts()){
			out.println(i+": "+fc.getName()+"(algo: "+model.getCurrentConfig().getAlgo(fc)+")");
			
			i++;
		}
		int answer=-1;
		try {
			answer=scan.nextInt();
			}
		catch(InputMismatchException e){
			System.err.println("your answer must be an integer");
			scan.next();
		}
		if (answer==0){
			return;
		}
		else if (answer>0 && answer < i){
			chosenFC=model.getCurrentConfig().getSelectedOAContexts().get(answer-1);
		}
		else{
			out.println("I can't understand your answer, action cancelled");
			return;
		}
		
		if (chosenFC!=null){
			out.println("choose construction algo for the formal context "+chosenFC.getName());
			out.println("0:Cancel");
			int j =1;
			for (Algorithm s : Algorithm.values()){
				out.println(j+":"+s.getName());
				j++;
			}
			
			answer=-1;
			try {
				answer=scan.nextInt();
				}
			catch(InputMismatchException e){
				System.err.println("your answer must be an integer");
				scan.next();
			}
			if (answer==0)
				return;
			else if (answer>0 && answer < j){
				notifyUserAction(UserAction.SET_OA_CONSTRUCTION_ALGO, chosenFC, Algorithm.values()[answer-1]);
			}
			else{
				System.out.println("I can't understand your answer, action cancelled");
				return;
			}
		} 
	}
	
	
	
	public void addOAContext(){
		ArrayList<ObjectAttributeContext> toAdd=new ArrayList<ObjectAttributeContext>();
		for(ObjectAttributeContext c : model.getRCF().getOAContexts()){
			if (!model.getCurrentConfig().getSelectedOAContexts().contains(c)){
				toAdd.add(c);
			}
		}
		if (toAdd.isEmpty())
		{
			System.out.println("No context to add");
			return;
		}
		else{
			System.out.println("Contexts to add:");
			int i=1;
			System.out.println("0:Cancel");
			for (ObjectAttributeContext c : toAdd)
			{
				System.out.println("\t"+i+":"+c.getName());
				i++;
			}
			int answer=-1;
			try {
				answer=scan.nextInt();
				}
			catch(InputMismatchException e){
				System.err.println("your answer must be an integer");
				scan.next();
			}
			if (answer==0){
				return;
			}
			else if (answer>0 && answer < i){
				notifyUserAction(UserAction.ADD_OA_CONTEXT, toAdd.get(answer-1),null);
				return;
			}
			else{
				System.out.println("I can't understand your answer, action cancelled");
				return;
			}
		
		}
	}

	public void removeOAContext(){
		if (model.getCurrentConfig().getSelectedOAContexts().isEmpty())
		{
			out.println("No context to remove");
			return;
		}
		else{
			out.println("Chose a context to remove:");
			int i=1;
			out.println("0:Cancel");
			for (ObjectAttributeContext c : model.getCurrentConfig().getSelectedOAContexts())
			{
				out.println("\t"+i+":"+c.getName());
				i++;
			}
			int answer=-1;
			try {
				answer=scan.nextInt();
				}
			catch(InputMismatchException e){
				System.err.println("your answer must be an integer");
				scan.next();
			}
			if (answer==0){
				return;
			}
			else if (answer>0 && answer < i){
				ObjectAttributeContext contextToRemove=model.getCurrentConfig().getSelectedOAContexts().get(answer-1);
				out.println("The following Object-Object contexts are going to be removed");
				for(Iterator<ObjectObjectContext> it=model.getCurrentConfig().getSelectedOOContexts().iterator();it.hasNext();){
					ObjectObjectContext rc=it.next();
					if (rc.getSourceContext()==contextToRemove)
						out.println("\t"+rc.getRelationName()+" ( "+rc.getSourceContext().getName()+"->"+rc.getTargetContext().getName()+" , "+model.getCurrentConfig().getScalingOperators(rc)+" ) ");
				}
				out.println(".");
				notifyUserAction(UserAction.REMOVE_OA_CONTEXT, contextToRemove,null);
				return;
			}
			else{
				out.println("I can't understand your answer, action cancelled");
				return;
			}
		
		}
	}
	
	private void displayCurrentConfig() {
		
		out.println("Current configuration: ");
		out.println("\t object-attribute contexts: ");
		for (ObjectAttributeContext c : model.getCurrentConfig().getSelectedOAContexts())
		{
			out.println("\t\t"+c.getName()+" ("+model.getCurrentConfig().getAlgo(c)+")");
		}
		if (!model.isInit()){
			out.println("\t object-object contexts: ");
			for (ObjectObjectContext rc :  model.getCurrentConfig().getSelectedOOContexts())
			{
				out.println("\t\t"+rc.getRelationName()+" ( "+rc.getSourceContext().getName()+"->"+rc.getTargetContext().getName()+" , "+model.getCurrentConfig().getScalingOperators(rc)+" ) ");
			}
		}
	}

	public void askStop(final Object lock) {
		
		new Thread(){

			public void run(){
				out.println("The number of concept has not changed from the previous step, it may be a sign that you reached a fixed point");
				out.println("Do you want to continue ?");
				out.println("0 : yes");
				out.println("1 : no");
				
				int i=-1;
				try {
				i=scan.nextInt();
				}
				catch(InputMismatchException e){
					System.err.println("your answer must be an integer");
					scan.next();
				}
				if (i==0){
					synchronized (lock) {
						lock.notify();
					}
					return;
				}
				else if (i==1){
					notifyUserAction(UserAction.STOP_PROCESS);
					synchronized (lock) {
						lock.notify();
					}
				}
				else{
					out.println("I can't understand your answer, try again.");
					askStop(lock);
				}
		
			}
		}.start();
	}

	public void askStopAfter10Steps(final Object lock, final int step) {
		
		
		new Thread(){

			public void run(){
				out.println("You have already computed "+step+" steps, which is an important number and may be a ");
				out.println("sign that the process is looping.");
				out.println("Do you want to continue ?");
				out.println("0 : yes");
				out.println("1 : no");
				
				int i=-1;
				try {
				i=scan.nextInt();
				}
				catch(InputMismatchException e){
					System.err.println("your answer must be an integer");
					scan.next();
				}
				if (i==0){
					synchronized (lock) {
						lock.notify();
					}
					return;
				}
				else if (i==1){
					notifyUserAction(UserAction.STOP_PROCESS);
					synchronized (lock) {
						lock.notify();
					}
				}
				else{
					out.println("I can't understand your answer, try again.");
					askStopAfter10Steps(lock, step);
				}
			}
		}.start();

		
	}

	

	@Override
	public void notifyEnd(String outputFolder) {
		out.println("END.");
		
	}
}
