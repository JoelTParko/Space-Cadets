
import java.beans.Expression;

import javafx.geometry.Pos;

import java.util.HashMap;
import java.util.regex.*;
import java.util.*;

public class BB_Interpreter
{
    private boolean inWhile;
    private int whileCount = 0;
    private int startIndex = 0;
    private int endIndex;
    private int funcDepth;
    private String returnVariable;
    private Map<String, Integer[]> functions = new HashMap<>();
    private Stack<Integer> whileStack = new Stack<>();
    private HashMap<String, Integer> variables = new HashMap<>();
    private List<String> fileLines;

    public BB_Interpreter(List<String> fileLines)
    {
        this.fileLines = fileLines;
        this.endIndex = fileLines.size();
        this.funcDepth = 0;
        findFunctions();
    }

    public BB_Interpreter(List<String> fileLines, int funcDepth, List<Integer> paraValues, int startPoint){
        this.fileLines = fileLines;
        this.endIndex = fileLines.size();
        this.funcDepth = funcDepth;

        String[] varNames = null;
        String declareLine;

        declareLine = deCommenter(fileLines.get(startPoint-1));
        Pattern func = Pattern.compile("^func\\s+(\\w+)\\((\\w*(?:,\\s*\\w*)*)\\)\\s*:\\s*$");
        Matcher match = func.matcher(declareLine);

        if(match.find()){
            varNames =  match.group(2).split(",");
        }

        Iterator it = paraValues.iterator();
        for (String name: varNames) {
            variables.put(name.replaceAll("\\s*",""), (int)it.next());
        }
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

        Pattern startFunc = Pattern.compile("^func\\s+(\\w+)\\((?:\\w*(,\\s*\\w*)*)\\)\\s*:\\s*$");
        Pattern endFunc = Pattern.compile("^fEnd;$");
        Iterator<String> iterator = fileLines.listIterator(startIndex);
        index = startIndex;

        while(index < endIndex){
            index++;
            currentLine = iterator.next();
            currentLine = deCommenter(currentLine);
            Matcher match = startFunc.matcher(currentLine);
            Matcher match2 = endFunc.matcher(currentLine);
            if(match.find()){
                locations[0] = index;
                funcName = match.group(1);
                funcFound = true;

            }else if(match2.find()&&funcFound){
                locations[1] = index;
                functions.put(funcName,locations);
                funcFound = false;
                locations = new Integer[2];
            }

        }
    }
    public int next(String currentLine, int index){
        StringBuilder parameters = new StringBuilder();
        StringBuilder funcName = new StringBuilder();
        String token;
        StringBuilder varName = new StringBuilder();
        int returnJump = 0;
        //Removes comments from the code
        currentLine = deCommenter(currentLine);

        if(checkForFunction(currentLine, funcName, varName, parameters)){
            int funcIndex;
            int funcJump;
            int returnValue;
            List<Integer> paraValues = new ArrayList<>();
            returnJump = index;
            String[] parameterList = parameters.toString().split(",");

            for (String para: parameterList) {
                paraValues.add(evaluate(para).result);
            }
            BB_Interpreter functionInterpreter = new BB_Interpreter(fileLines, funcDepth+1,paraValues, functions.get(funcName.toString())[0]);
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
                    variables.put(varName.toString(),returnValue);
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

    public boolean checkForFunction(String currentLine, StringBuilder fName, StringBuilder varName, StringBuilder parameters){ //checks for a function call

        for (String funcName: functions.keySet()) {
            String patternString = "^(?:(\\w+)\\s*=\\s*)?"+funcName+"\\((.*(,\\s*.*)*)?\\);\\s*$";
            Pattern pattern = Pattern.compile(patternString);
            Matcher match = pattern.matcher(currentLine);
            if(match.find()){
                fName.append(funcName);
                parameters.append(match.group(2));
                varName.append(match.group(1));
                return true;
            }
        }
        return false;
    }

    public String readToken(String currentLine)
    {
        String[] commands = {"clear", "incr", "decr", "while", "end","return"};
        String varName;

        if(ifSkips.isEmpty()) ifSkips.add(false);

        Matcher ifMatcher = ifPattern.matcher(currentLine);
        if (ifMatcher.matches())
        {
        	ifStatement(ifMatcher.group(1));
        }

        if (currentLine.contains("endIf"))
        {
        	ifDepth--;
        }
        else if (currentLine.contains("else"))
        {
        	ifSkips.set(ifDepth, !ifSkips.get(ifDepth));
        }
                	
        
        if (ifSkips.get(ifDepth) == false) {
            //Loop through all of the commands
            Pattern pattern = Pattern.compile("");
            for (String token : commands) {
                if (funcDepth == 0) {
                    pattern = Pattern.compile("^(?:" + token + "\\s+(\\w+)(?:\\s+not\\s+(\\d+)\\s+do)?\\s*;\\s*)|(?:" + token + "(\\w+|\\d+)?;\\s*)$");
                } else {
                    StringBuilder tabs = new StringBuilder();
                    for (int i = 1; i <= funcDepth; i++) {
                        tabs = tabs.append("\\t");
                        pattern = Pattern.compile("^" + tabs.toString() + "(?:" + token + "\\s+(\\w+)(?:\\s+not\\s+(\\d+)\\s+do)?\\s*;\\s*)|(?:" + token + "\\s*;\\s*)$");
                    }
                }

                Matcher matcher = pattern.matcher(currentLine);

                if (matcher.matches()) { //Checks if the current token matches the one in the BB code
                    if (token != "end" && token != "return") {
                        varName = matcher.group(1); //Finds the name of the variable that is being used
                        if (token == "while") {
                            inWhile = whileCheck(varName, matcher.group(2)); //Checks if the while condition has been met
                        } else {
                            executeCommand(token, varName); //Executes one of the three basic commands
                        }
                    } else if (token == "return") {
                        return "return" + matcher.group(1);
                    }
                    return token;
                }

            }

            Matcher assignmentMatcher = assignmentPattern.matcher(currentLine);
            if (assignmentMatcher.matches()) {
                Attempt evaluationAttempt = evaluate(assignmentMatcher.group(2));
                if (evaluationAttempt.isSuccess) {
                    variables.put(assignmentMatcher.group(1), evaluationAttempt.result);
                }
            }
        }
        return "";
    }

    private static Pattern ifPattern = Pattern.compile("if\\s+(\\w+\\s*=\\s*\\w+)\\s+then;");
    private static Pattern equivalencePattern = Pattern.compile("(\\w+)\\s*=\\s*(\\w+)");
    private ArrayList<Boolean> ifSkips = new ArrayList<Boolean>();
    private int ifDepth = 0;
    
    private void ifStatement(String expression){
    	/*Evaluates the expression, and then determines if it is true or false
    	 * If it is true, it will not skip anything and will continue to execute
    	 * If it is false, it will skip the if statement.
    	 * 
    	 * Limitations: Can only compare 2 variables.
    	 */
        Matcher expressionMatcher=equivalencePattern.matcher(expression);
        if(expressionMatcher.matches())
        {
            if(variables.get(expressionMatcher.group(1))==variables.get(expressionMatcher.group(2)))
            {
                ifDepth++;
                ifSkips.add(ifDepth,false);
            }
            else
            {
                ifDepth++;
            ifSkips.add(ifDepth,true);
            }
        }
    }

    private static Pattern assignmentPattern = Pattern.compile("\\s*(\\w+)\\s*=(.*);\\s*");
    private static Pattern intPattern = Pattern.compile("\\s*(\\d+)\\s*");
    private static Pattern varPattern = Pattern.compile("\\s*[+-]?\\s*(\\w+)\\s*");
    private static Pattern bracketsPattern = Pattern.compile("\\s*\\((.+)\\)\\s*");
    private static Pattern additionPattern = Pattern.compile("(.+)\\+(.+)");
    private static Pattern multiplicationPattern = Pattern.compile("(.+)\\*(.+)");
    private static Pattern subtractionPattern = Pattern.compile("(.+)-(.+)");
    private static Pattern divisionPattern = Pattern.compile("(.+)/(.+)");
    private static Pattern moduloPattern = Pattern.compile("(.+)%(.+)");

    //Evaluates a mathematical expression
    private Attempt evaluate(String expression)
    {
        Matcher bracketsMatcher = bracketsPattern.matcher(expression);
        if (bracketsMatcher.matches())
        {
            Attempt inner = evaluate(bracketsMatcher.group(1));
            if (inner.isSuccess)
                return inner;
        }

        Matcher additionMatcher = additionPattern.matcher(expression);
        if (additionMatcher.matches())
        {
            Attempt leftAttempt = evaluate(additionMatcher.group(1));
            Attempt rightAttempt = evaluate(additionMatcher.group(2));
            if (leftAttempt.isSuccess && rightAttempt.isSuccess)
                return new Attempt(true, leftAttempt.result + rightAttempt.result);
        }

        Matcher subtractionMatcher = subtractionPattern.matcher(expression);
        if (subtractionMatcher.matches())
        {
            Attempt leftAttempt = evaluate(subtractionMatcher.group(1));
            Attempt rightAttempt = evaluate(subtractionMatcher.group(2));
            if (leftAttempt.isSuccess && rightAttempt.isSuccess)
                return new Attempt(true, leftAttempt.result - rightAttempt.result);
        }

        Matcher moduloMatcher = moduloPattern.matcher(expression);
        if (moduloMatcher.matches())
        {
            Attempt leftAttempt = evaluate(moduloMatcher.group(1));
            Attempt rightAttempt = evaluate(moduloMatcher.group(2));
            if (leftAttempt.isSuccess && rightAttempt.isSuccess)
                return new Attempt(true, leftAttempt.result % rightAttempt.result);
        }

        Matcher multiplicationMatcher = multiplicationPattern.matcher(expression);
        if (multiplicationMatcher.matches())
        {
            Attempt leftAttempt = evaluate(multiplicationMatcher.group(1));
            Attempt rightAttempt = evaluate(multiplicationMatcher.group(2));
            if (leftAttempt.isSuccess && rightAttempt.isSuccess)
                return new Attempt(true, leftAttempt.result * rightAttempt.result);
        }

        Matcher divisionMatcher = divisionPattern.matcher(expression);
        if (divisionMatcher.matches())
        {
            Attempt leftAttempt = evaluate(divisionMatcher.group(1));
            Attempt rightAttempt = evaluate(divisionMatcher.group(2));
            if (leftAttempt.isSuccess && rightAttempt.isSuccess)
                return new Attempt(true, leftAttempt.result / rightAttempt.result);
        }



        Matcher intMatcher = intPattern.matcher(expression);
        if (intMatcher.matches())
            return new Attempt(true, Integer.parseInt(intMatcher.group(1)));

        Matcher varMatcher = varPattern.matcher(expression);
        if (varMatcher.matches() && variables.containsKey(varMatcher.group(1)))
            return new Attempt(true, variables.get(varMatcher.group(1)));

        return new Attempt(false, 0);
    }

    public class Attempt
    {
        public boolean isSuccess;
        public int result;

        public Attempt(boolean isSuccess, int result)
        {
            this.isSuccess = isSuccess;
            this.result = result;
        }
    }
    
    public void executeCommand(String command, String varName)
    {
        //Execute the command, based on the operator and operand.
        switch (command)
        {
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
    
    public boolean whileCheck(String varName, String value)
    {

        if (variables.get(varName) == Integer.parseInt(value))
        {
            return false;
        }
        return true;
    }
    
    public void printState()
    {
        //Prints the variables at the end.
        System.out.println("Final State:");
        for (String test:variables.keySet())
        {
            System.out.println(test + " = " + variables.get(test));
        }

    }

    public String deCommenter(String currentLine){
        return currentLine.split("//")[0];
    }


}
