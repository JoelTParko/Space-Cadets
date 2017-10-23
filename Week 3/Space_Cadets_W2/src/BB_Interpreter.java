import javafx.geometry.Pos;

import java.util.HashMap;
import java.util.regex.*;
import java.util.*;

public class BB_Interpreter {
    private boolean inWhile;
    private int whileCount = 0;
    private int startIndex = 0;
    private int endIndex;
    private int funcDepth;
    private String returnVariable;
    private Map<String, Integer[]> functions = new HashMap<>();
    private Stack<Integer> whileStack = new Stack<>();
    private Map<String, Integer> variables = new HashMap<>();
    private List<String> fileLines;

    public BB_Interpreter(List<String> fileLines){
        this.fileLines = fileLines;
        this.endIndex = fileLines.size();
        this.funcDepth = 0;
        findFunctions();
    }

    public BB_Interpreter(List<String> fileLines, int funcDepth){
        this.fileLines = fileLines;
        this.endIndex = fileLines.size();
        this.funcDepth = funcDepth;
        findFunctions();
    }

    public int getReturnValue(){
        return variables.get(returnVariable);
    }

    public void findFunctions(){
        String currentLine;
        String funcName ="";
        boolean funcFound = false;
        Integer[] locations = {0,0};
        int index;

        Pattern startFunc = Pattern.compile("^func\\s+(\\w+)\\(\\)\\s*:\\s*$");
        Pattern endFunc = Pattern.compile("^fEnd;$");
        Iterator<String> iterator = fileLines.listIterator(startIndex);
        index = startIndex;

        while(index < endIndex){
            index++;
            currentLine = iterator.next();
            Matcher match = startFunc.matcher(currentLine);
            Matcher match2 = endFunc.matcher(currentLine);
            if(match.find()){
                locations[0] = index;
                funcName = match.group(1);
                funcFound = true;

            }else if(!match2.find()&&funcFound){
                locations[1] = index;
                functions.put(funcName,locations);
            }

        }
    }

    public int next(String currentLine, int index){
        StringBuilder funcName = new StringBuilder();
        String token;
        StringBuilder varName = new StringBuilder();
        int returnJump = 0;
        //Removes comments from the code
        currentLine = deCommenter(currentLine);

        if(checkForFunction(currentLine, funcName, varName)){
            int funcIndex;
            int funcJump;
            int returnValue;
            returnJump = index;

            BB_Interpreter functionInterpreter = new BB_Interpreter(fileLines, funcDepth+1);
            PositionableIterator iterator = new PositionableIterator(fileLines);
            iterator.moveTo(functions.get(funcName.toString())[0]);
            funcIndex = functions.get(funcName.toString())[0];
            do{
                funcIndex++;
                currentLine = iterator.next();
                funcJump = functionInterpreter.next(currentLine, funcIndex);
                if(funcJump>0){
                    iterator.moveTo(funcJump);
                    funcIndex = funcJump;
                }else if(funcJump == -1){
                    returnValue = functionInterpreter.getReturnValue();
                    
                    variables.put(varName.toString(),variables.get(varName.toString())+returnValue);
                    funcIndex = functions.get(funcName.toString())[1];
                }
            }while(funcIndex< functions.get(funcName.toString())[1]);
        }else{
            token = readToken(currentLine); //Identifies the token, will execute the code if its a basic command/Checks for condition met in while loops
            if(token == "while"){
                if(!inWhile){ //Jumps to the end of a while loop if the condition was met
                    returnJump = endIndex;
                }else{ //Adds the location of the current while statement to the while stack
                    whileStack.push(index - 1);
                    whileCount++;
                }
            }else if(token == "end" && whileCount > 0){//Checks if the program should jump back to the previous while statement or not
                returnJump = whileStack.pop();
                endIndex = index;
                whileCount--;
            }else if(token.contains("return")){
                returnVariable = token.substring(6);
                return -1;
            }
        }
        return returnJump;
    }

    public boolean checkForFunction(String currentLine, StringBuilder fName, StringBuilder varName){ //checks for a function call

        for (String funcName: functions.keySet()) {
            String patternString = "^(?:(\\w+)\\s*=\\s*)?"+funcName+"\\((?:\\w*(,\\s*\\w*)*)?\\);\\s*$";
            Pattern pattern = Pattern.compile(patternString);
            Matcher match = pattern.matcher(currentLine);
            if(match.find()){
                fName.append(funcName);
                for (String var: variables.keySet()) {
                    String test = match.group(1);
                    if (var.equals(test)){
                        varName.append(match.group(1));
                    }
                }
                return true;
            }
        }
        return false;
    }


    public String readToken(String currentLine) {
        String[] commands = {"clear", "incr", "decr", "while", "end","return", ""};
        String varName;
        Pattern pattern;
        //Loop through all of the commands
        for (String token : commands) {
            if(funcDepth==0) {
                 pattern = Pattern.compile("^(?:"+token + "\\s+(\\w+)(?:\\s+not\\s+(\\d+)\\s+do)?\\s*;\\s*)|(?:" + token + "(\\w+|\\d+)?;\\s*)$");
            }else{
                StringBuilder tabs = new StringBuilder();
                for (int i = 1; i <= funcDepth ; i++) {
                    tabs = tabs.append("\\t");
                }
                pattern = Pattern.compile("^"+tabs.toString()+"(?:"+token + "\\s+(\\w+)(?:\\s+not\\s+(\\d+)\\s+do)?\\s*;\\s*)|(?:" + token + "\\s*;\\s*)$");
            }
            Matcher matcher = pattern.matcher(currentLine);
            if (matcher.find()) { //Checks if the current token matches the one in the BB code
                if (token != "end" && token != "return") {
                    varName = matcher.group(1); //Finds the name of the variable that is being used
                    if (token == "while") {
                        inWhile = whileCheck(varName, matcher.group(2)); //Checks if the while condition has been met
                    } else {
                        executeCommand(token, varName); //Executes one of the three basic commands
                    }
                }else if(token == "return"){
                    return "return"+matcher.group(1);
                }
                return token;
            }

        }
        return "";
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
    
    public boolean whileCheck(String varName, String value){

        if (variables.get(varName) == Integer.parseInt(value)) {
            return false;
        }
        return true;
    }
    
    public void printState(){
    	//Prints the variables at the end.
        for (String test:variables.keySet()) {
            System.out.println(test + variables.get(test));
        }

    }

    public String deCommenter(String currentLine){
        return currentLine.split("//")[0];
    }


}
