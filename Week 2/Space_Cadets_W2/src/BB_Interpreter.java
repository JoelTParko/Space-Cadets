import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

public class BB_Interpreter {
	
    public String[] commands = {"clear", "incr", "decr", "while", "end"};
    public Map<String, Integer> variables = new HashMap<>();

    public String[] readLine(String currentLine){
        String command;
        String varName = null;
        String[] instruction;
        
        //Loop through all of the commands
        for (String patternString: commands) {
            Pattern pattern = Pattern.compile("^(?:\\s*"+patternString+"\\s+(\\w+)(?:\\s+not\\s+\\d+\\s+do)?\\s*;\\s*)|(?:\\s*"+patternString+"\\s*;\\s*)$");
            //Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(currentLine);
            
            if(matcher.find()){
                command = currentLine.substring(matcher.start(), matcher.end());
                
                if (patternString != "end") {
                    varName = Character.toString(currentLine.charAt(matcher.end() + 1));
                command = patternString;
                if (command != "end") {
                    varName = matcher.group(1);
                    //varName = Character.toString(currentLine.charAt(matcher.end() + 1));
                }
                
                //Create and return the instruction.
                instruction = new String[]{command, varName};
                return instruction;
            }
            
        }
        
        //Returns null if no instruction is found.
        return  null;
    }
    
    public void executeCommand(String command, String varName){
    	//Execute the command, based on the operator and operand.
        switch (command){
            case "clear":
                variables.put(varName, 0);
                break;
            case "incr":
                variables.put(varName, variables.get(varName)+1);
                break;
            case "decr":
                variables.put(varName, variables.get(varName)-1);
                break;
            default:
        }
    }
    
    public boolean executeWhile(String varName, String line){
        Pattern pattern = Pattern.compile("not");
        Matcher matcher = pattern.matcher(line);
        
        if(matcher.find()){
            if (variables.get(varName) == Character.getNumericValue(line.charAt(matcher.end() + 1))) return  false;
        }
        
        return true;
    }
    
    public void printState(){
    	//Prints the variables at the end.
        for (String test:variables.keySet()) {
            System.out.println(test + variables.get(test));
        }

    }


}
