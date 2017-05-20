package org.rcaexplore.analyzer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.rcaexplore.conceptorder.generic.GenericConcept;
import org.rcaexplore.conceptorder.generic.GenericConceptOrder;
import org.rcaexplore.conceptorder.structure.ConceptOrderFamily;
import org.rcaexplore.context.Attribute;
import org.rcaexplore.io.ParseXMLCOFHistory;

/**
 * The goal of the history analyzer is to list all the elements that have been added or removed in concept posets
 * at each step of the process 
 * */


public class HistoryAnalyzer {
	
	/** store the history of the result passed as parameter*/
	private ArrayList<ConceptOrderFamily<GenericConceptOrder>> cofHistory;
	/** store the diff histories of the cofHistory after analyze*/
	private Hashtable<String,PosetDiffHistory> diffHistories;
	
	/** The Diff class contains the added and removed concepts from a concept order in comparaison to the previous one*/
	class Diff{
		ArrayList<GenericConcept> addedConcepts=new ArrayList<>();
		ArrayList<GenericConcept> removedConcepts=new ArrayList<>();
		
	}

	/** A concept poset chronicle of diff. The history is a table to allow a poset to be present only during some steps of the process  */
	class PosetDiffHistory{
		final String posetName;
		final Diff[] history;
		PosetDiffHistory(String posetName,int size){
			history=new Diff[size];
			this.posetName=posetName;
		}
	}
	
	/** Load the XML file from path as a COF history*/
	public void loadCOFHistory(String path){
		String uri = path;

		cofHistory = new ArrayList<>();

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(true);
			factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();
			parser.parse(uri, new ParseXMLCOFHistory(cofHistory));  

		} catch (Throwable t) {
			t.printStackTrace();
		}

	}
	
	/** Analyze process : takes an xml as input, analyze it and write the result in a text file*/
	public static void main(String[] args) {
		HistoryAnalyzer analyzer=new HistoryAnalyzer();

		analyzer.loadCOFHistory("results/test-div/result.xml");
		analyzer.analyzeHistory();
		FileWriter fw = null;
		try {
			fw=new FileWriter("results/test-div/history.txt");
			analyzer.writeHistory(fw);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	/** Write the history in the writer passed as parameter*/
	public void writeHistory(Writer w) throws IOException{
		for (int i =0 ; i< cofHistory.size(); i++){
			w.write("step "+i+"\n");
			for (PosetDiffHistory ph:diffHistories.values()){
				w.write("\tPoset Name: "+ph.posetName+"\n");
				writePosetHistoryStep(ph, i , w);
			}
		}
	}

	/** Write the diff analyze for a given poset at a given step*/
	private void writePosetHistoryStep(PosetDiffHistory ph, int i, Writer w) throws IOException {
		if (ph.history[i]==null)
			w.write("not computed on this step\n");
		else {
			w.write("\t\tAdded concepts\n");
			for (GenericConcept c: ph.history[i].addedConcepts){
				w.write("\t\t\t+"+c.getName()+": ");
				for (Attribute a : c.getSimplifiedIntent())
					w.write(a+" ");
				w.write("\n");
				
			}
			w.write("\t\tRemoved concepts\n");
			for (GenericConcept c: ph.history[i].removedConcepts){
				w.write("\t\t\t-"+c.getName()+": ");
				for (Attribute a : c.getSimplifiedIntent())
					w.write(a+" ");
				w.write("\n");
			}
		}
	}
	
	/** compute diff histories for every concept order at every step*/
	public void analyzeHistory(){
		diffHistories = new Hashtable<>();
		for (int i = 0 ; i<cofHistory.size() ; i++)
		{
			for (GenericConceptOrder gco : cofHistory.get(i).getConceptOrders()){
				if (!diffHistories.containsKey(gco.getName()))
					diffHistories.put(gco.getName(), new PosetDiffHistory(gco.getName(), cofHistory.size()));
				Diff currentDiff;
				if (i==0)
					currentDiff=generateDiff(null, gco);
				else
					currentDiff=generateDiff(cofHistory.get(i-1).getConceptOrder(gco.getName()),gco);
				diffHistories.get(gco.getName()).history[i]=currentDiff;
			}
		}
		
		
	}

	/** compute the diff of the currentCO with the previous CO (or null if it does not exist)*/
	private Diff generateDiff(GenericConceptOrder previousCO, GenericConceptOrder currentCO) {
		Diff result=new Diff();
		if (previousCO==null)
			for (GenericConcept c : currentCO.getConcepts())
				result.addedConcepts.add(c);
		else
		{
			HashSet<String> previous=new HashSet<>();
			for (GenericConcept c : previousCO.getConcepts())
				previous.add(c.getName());
			
			HashSet<String> current=new HashSet<>();
			for (GenericConcept c : currentCO.getConcepts())
				current.add(c.getName());
			
			for (GenericConcept c : previousCO.getConcepts())
				if (!current.contains(c.getName()))
					result.removedConcepts.add(c);
			
			for (GenericConcept c : currentCO.getConcepts())
				if (!previous.contains(c.getName()))
					result.addedConcepts.add(c);
		}
		return result;
	}
	
}
