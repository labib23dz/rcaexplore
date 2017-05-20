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
package org.rcaexplore.cofbrowser.controller;
import org.rcaexplore.cofbrowser.model.COFNavigationModel;
import org.rcaexplore.cofbrowser.view.CLFView;
import org.rcaexplore.cofbrowser.view.JFrameFieldCOF;


public class COFController {
	public CLFView view = null;
	
	private COFNavigationModel model = null;
	
	public COFController (COFNavigationModel model){
		//init model
		this.model = model;
		//init the view
		view = new JFrameFieldCOF(this, model);
	}

	public void displayViews(){
		view.display();
	}
	
	public void closeViews(){
		view.close();
	}
	
	public void notifyCurrentConceptChanged(String chaineConcept){
		model.setCurrentConcept(chaineConcept);
		System.out.println("concept changé");
	}

	public void notifyRenameRequest(String text, String text2) {
		model.renameAll(text,text2);
		
	}

	public void notifySaveRequest() {
		model.saveModel();
		
	}

	public void notifyGenerateRequest() {
		model.generatePicture();
		
	}

	public void notifyLoadRequest() {
		model.loadModel();
		
	}

	public void notifyModifieSingletons(String string) {
		model.modifySingletonsInCO(string);
		
	}
	
	public static void main(String[] args) {
		COFNavigationModel model=new COFNavigationModel("data/rlq/result.xml");
		COFController controller = new COFController(model);
		controller.displayViews();
	}

	public void notifyStepChange(int selectedIndex) {
		model.setCurrentStep(selectedIndex);
		
	}

	public void notifyCOChange(String string) {
		model.setCurrentConceptOrder(string);
		
	}

	
	
}