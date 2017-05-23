package org.rcaexplore.constraint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rcaexplore.StepConfiguration;
import org.rcaexplore.context.ObjectObjectContext;
import org.rcaexplore.scaling.ScalingOperator;

public class InterpretationLanguage {

	private String interpretation="";
	private HashMap<String, ArrayList<ObjectObjectContext>> mapSourceRelations = new HashMap<String, ArrayList<ObjectObjectContext>>() ;
	private StepConfiguration currentConfiguration;
	
	public InterpretationLanguage(StepConfiguration currentConfiguration)
	{
		this.currentConfiguration = currentConfiguration;
		buildMapSourceRelations(this.currentConfiguration.getSelectedOOContexts());
		buildIterpretation(this.mapSourceRelations);
	};

	
	public String getInterpretation() {
		return interpretation;
	}


	public void setInterpretation(String interpretation) {
		this.interpretation += interpretation;
	}
	
	
	public HashMap<String, ArrayList<ObjectObjectContext>> getMapSourceRelations() {
		return mapSourceRelations;
	}

	
	private void buildMapSourceRelations(ArrayList<ObjectObjectContext> objectObjectContexts)
	{

		for (ObjectObjectContext objectObjectContext : objectObjectContexts )
		{				
			String source = objectObjectContext.getSourceContext().getName();
			putOne(source, objectObjectContext);
		}
	}
	
	public boolean putOne(String key, ObjectObjectContext value) {    
	    if (mapSourceRelations.containsKey(key)) {    
	    	mapSourceRelations.get(key).add(value);    
	      return true;    
	    } else {    
	    	ArrayList<ObjectObjectContext> values = new ArrayList<>();    
	      values.add(value);    
	      mapSourceRelations.put(key, values);    
	      return false;    
	    }    
	  }


	private void buildIterpretation(HashMap<String, ArrayList<ObjectObjectContext>> mapSourceRelations)
	{		
		for(Map.Entry<String, ArrayList<ObjectObjectContext>> entry : mapSourceRelations.entrySet()) 
		{	
			String source = entry.getKey(); 			
			this.interpretation += "Group of " + source + " that\n ";    					
			for (ObjectObjectContext ooContext : entry.getValue())
			{
				String relationName = ooContext.getRelationName();
				String target = ooContext.getTargetContext().getName();
				
				ArrayList<ScalingOperator> scalingOperators = this.currentConfiguration.getScalingOperators(ooContext);
				for (ScalingOperator scaling : scalingOperators )
				{					
					switch (scaling.getClass().getSimpleName())
					{
					case "ExistentialScaling":					
						this.interpretation += "\t"+ relationName + " at least one " + target + " in the " + target + " group \n";  
						break;
					case "ForAllExistentialScaling":
						this.interpretation += "\t"+ relationName +" only the "+ target + " in the " + target + " group \n";
						break;		
					case "ContainsExistScaling":
						this.interpretation += "\t" + relationName + " all the " + target + " in the " + target + " group\n"; 
						break;
					case "ContainsExistNScaling":
						String x="";
						Pattern p = Pattern.compile("contains(.+)percent");
						Matcher m = p.matcher(scaling.getName());
						if( m.matches())
						{
							x = m.group(1);
						}
						this.interpretation += "\t" + relationName  +" " +x+"% of " + target + " in the " + target + " group\n";						   
						break;	
					case "ForNearlyAllExistentialScaling":					
						this.interpretation += "\t Not found yet\n" ;						   
						break;
					}
				}
			}			
		}		
	}
	
	
}
