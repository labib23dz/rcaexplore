package org.rcaexplore.constraint;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.rcaexplore.algo.multicontext.ExploMultiFCA;
import org.rcaexplore.context.ObjectObjectContext;

/**Class to parse the constraint JSON file
 * */
public class ParseJSON {
	
	/** Single pre-initialized instance  */
	private static ParseJSON instance = new ParseJSON();
	
	
	/**The only static method to recover the single instance*/
    public static ParseJSON getInstance() {
         return ParseJSON.instance;
    }
	
    
	public void parseJson(String filePath) throws IOException, ParseException {
		// TODO Auto-generated method stub
		
		ListEqualityConstraint lstEqualityConstraint = ListEqualityConstraint.getInstance();
		lstEqualityConstraint.getLstConstraint().clear();
		
		String fileExtension=(filePath.substring(filePath.lastIndexOf(".")+1));
		
		if (fileExtension.equals("json"))
		{
			JSONParser parser = new JSONParser();		
			try 
			{
				Object obj = parser.parse(new FileReader(filePath));						
				JSONObject jsonObject = (JSONObject) obj;		
				//récupérer l'object json Equality			
				JSONObject equality = (JSONObject) jsonObject.get("Equality");						
				if(equality != null) 			
				{								
					//Récupérer l'ensemble des clé de l'object json "Equality"
					HashSet<String> keysEquality = new HashSet<String>(equality.keySet());				
					Iterator<String> it = keysEquality.iterator();					
						//Récupération des tableaux par clé
						//TODO ajouter les tableau et les clé dans une arrays Liste.
						while(it.hasNext())
						{
							String key = it.next();
							if ((equality.get(key) instanceof JSONArray))  				
							{
								JSONArray relations= (JSONArray) equality.get(key); 											
								//*************parcourir le tableau des relation*********
								for(int i=0;i<relations.size();i++)
								{
									String relation = (String) relations.get(i);
									//System.out.println(json_data);
									lstEqualityConstraint.putOne(key, relation); 
								}
							}
							else
							{
								ShowDialog showDialog = new ShowDialog("the value of the key \""+key+"\" must be a table.", "Error",0);
								showDialog.showMessageDialog();
							}
								//**************Fin parcours********
						}
									
					if (lstEqualityConstraint.checkFrequence())
						System.out.println("Parse Json file : succes");					
					else
					{
						System.out.println("Parse Json file : failed");
						lstEqualityConstraint.getLstConstraint().clear();
						
					}
					System.out.println(lstEqualityConstraint.getLstConstraint());
					//ShowDialog showDialog = new ShowDialog("Loading constraints : success.", "Information",1);
					//showDialog.showMessageDialog();
					ConstraintView constraintView = new ConstraintView(ListEqualityConstraint.getInstance().getLstConstraint());
					constraintView.setVisible(true);
	   		}
				else
				{
					ShowDialog showDialog = new ShowDialog("there is no key \"Equality\" in your json file.", "Error",0);
					showDialog.showMessageDialog();				
				}
				
			}		
			catch(ParseException e)
			{
				ShowDialog showDialog = new ShowDialog("Your json file is not valid", "Error", 0);
				showDialog.showMessageDialog();
			}
		}
		else
		{
			ShowDialog showDialog = new ShowDialog("the constraint file must have \"json\" extention", "Error", 0);
			showDialog.showMessageDialog();
		}
												
	}

}
