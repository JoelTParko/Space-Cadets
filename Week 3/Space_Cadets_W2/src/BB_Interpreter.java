import java.util.HashMap;
import java.util.regex.*;
import java.util.*;

public class BB_Interpreter {
    private boolean inWhile;
    private int whileCount = 0;
    private int startIndex = 0;
    private int endIndex;
    private Map<String, Integer> functions = new HashMap<>();
    private Stack<Integer> whileStack = new Stack<>();
    private String[] commands = {"clear", "incr", "decr", "while", "end", ""};
    private Map<String, Integer> variables = new HashMap<>();
    private List<String> fileLines;

    public BB_Interpreter(List<String> fileLines){
        this.fileLines = fileLines;
        this.endIndex = fileLines.size();

        //findFunctions();
    }

    public BB_Interpreter(List<String> fileLines, int startIndex, int endIndex){
        this.fileLines = fileLines;
        this.startIndex = startIndex;
        this.endIndex = endIndex;

        //findFunctions();
    }

    /*
    public void findFunctions(){
        String currentLine;
        String functionName;
        boolean foundFunction;
        int index, index2;
        Pattern pattern = Pattern.compile("^func\\s+(\\w+)\\s*:\\s*$");
        Iterator<String> iterator = fileLines.listIterator(startIndex);
        index = startIndex;

        while(index < endIndex){
            index++;
            currentLine = iterator.next();
            Matcher match = pattern.matcher(currentLine);
            if(match.find()){
                functionName = match.group(1);
            }
        }
    }
*/
    public int next(String currentLine, int index){

        String token;
        int jumpPoint = 0;

        //Removes comments from the code
        currentLine = deCommenter(currentLine);
        /*
        if(checkForFunction(currentLine)){

        }
        */


        token = readToken(currentLine); //Identifies the token, will execute the code if its a basic command/Checks for condition met in while loops
        if(token == "while"){
            if(!inWhile){ //Jumps to the end of a while loop if the condition was met
                jumpPoint = endIndex;
            }else{ //Adds the location of the current while statement to the while stack
                whileStack.push(index - 1);
                whileCount++;
            }
        }else if(token == "end" && whileCount > 0){//Checks if the program should jump back to the previous while statement or not
            jumpPoint = whileStack.pop();
            endIndex = index;
            whileCount--;
        }

        return jumpPoint;
    }
/*
    public boolean checkForFunction(String currentLine){ //checks for a function call
        Pattern pattern = Pattern.compile("^func\\s+(\\w+)\\(\\);\\s*$");
        Matcher match = pattern.matcher(currentLine);
        if(match.find()){
            return true;
        }
        return false;
    }
    */

    public String readToken(String currentLine) {
        String varName;

        //Loop through all of the commands
        for (String token : commands) {
            Pattern pattern = Pattern.compile("^(?:\\s*" + token + "\\s+(\\w+)(?:\\s+not\\s+(\\d+)\\s+do)?\\s*;\\s*)|(?:\\s*" + token + "\\s*;\\s*)$");
            Matcher matcher = pattern.matcher(currentLine);
            if (matcher.find()) { //Checks if the current token matches the one in the BB code
                if (token != "end") {
                    varName = matcher.group(1); //Finds the name of the variable that is being used
                    if (token == "while") {
                        inWhile = whileCheck(varName, matcher.group(2)); //Checks if the while condition has been met
                    } else {
                        executeCommand(token, varName); //Executes one of the three basic commands
                    }
                }
                return token;
            }
        }
        return null;
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
