import java.beans.Expression;
import java.util.HashMap;
import java.util.regex.*;
import java.util.*;

public class BB_Interpreter
{
    private boolean inWhile;
    private int whileCount = 0;
    private int startIndex = 0;
    private int endIndex;
    private Map<String, Integer> functions = new HashMap<>();
    private Stack<Integer> whileStack = new Stack<>();
    private String[] commands = {"clear", "incr", "decr", "while", "end", ""};
    private HashMap<String, Integer> variables = new HashMap<>();
    private List<String> fileLines;

    public BB_Interpreter(List<String> fileLines)
    {
        this.fileLines = fileLines;
        this.endIndex = fileLines.size();

        //findFunctions();
    }

    public BB_Interpreter(List<String> fileLines, int startIndex, int endIndex)
    {
        this.fileLines = fileLines;
        this.startIndex = startIndex;
        this.endIndex = endIndex;

        //findFunctions();
    }

    /*
    public void findFunctions()
    {
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
    
    public int next(String currentLine, int index)
    {

        String token;
        int jumpPoint = 0;

        //Removes comments from the code
        currentLine = deCommenter(currentLine);
        /*
        if(checkForFunction(currentLine)){

        }
        */


        token = readToken(currentLine); //Identifies the token, will execute the code if its a basic command/Checks for condition met in while loops
        if(token == "while")
        {
            if(!inWhile)
            { //Jumps to the end of a while loop if the condition was met
                jumpPoint = endIndex;
            }
            else    //Adds the location of the current while statement to the while stack
            {
                whileStack.push(index - 1);
                whileCount++;
            }
        }
        else if(token == "end" && whileCount > 0)   //Checks if the program should jump back to the previous while statement or not
        {
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

    public String readToken(String currentLine)
    {
        String varName;

        Matcher ifMatcher = ifPattern.matcher(currentLine);
        if (ifMatcher.matches())
        {
        	ifStatement(ifMatcher.group(1));
        }
        
        if (ifCounter == 0) 
        {
        	//Loop through all of the commands
            for (String token : commands)
            {
                Pattern pattern = Pattern.compile("(?:\\s*" + token + "\\s+(\\w+)(?:\\s+not\\s+(\\d+)\\s+do)?\\s*;\\s*)|(?:\\s*" + token + "\\s*;\\s*)");
                Matcher matcher = pattern.matcher(currentLine);
                if (matcher.matches())     //Checks if the current token matches the one in the BB code
                {
                    if (token != "end")
                    {
                        varName = matcher.group(1); //Finds the name of the variable that is being used
                        if (token == "while")
                        {
                            inWhile = whileCheck(varName, matcher.group(2)); //Checks if the while condition has been met
                        }
                        else
                        {
                            executeCommand(token, varName); //Executes one of the three basic commands
                        }
                    }
                    return token;
                }
            }
            Matcher assignmentMatcher = assignmentPattern.matcher(currentLine);
            if (assignmentMatcher.matches())
            {
                Attempt evaluationAttempt = evaluate(assignmentMatcher.group(2));
                if (evaluationAttempt.isSuccess)
                {
                    variables.put(assignmentMatcher.group(1), evaluationAttempt.result);
                }
            }
        }
        else if (ifCounter > 0 && currentLine.contains("endIf"))
        {
        	//ifSkip = false;
        	ifCounter--;
        }
        
        
        return null;
    }

    private static Pattern ifPattern = Pattern.compile("if (\\S*) then;");
    private static Pattern equivalencePattern = Pattern.compile("(\\w+)\\s*=\\s*(\\w+)");
    private int ifCounter = 0;
    
    private void ifStatement(String expression) {
    	/*Evaluates the expression, and then determines if it is true or false
    	 * If it is true, it will not skip anything and will continue to execute
    	 * If it is false, it will skip the if statement.
    	 * 
    	 * Limitations: Can only compared 2 variables.
    	 */
    	Matcher expressionMatcher = equivalencePattern.matcher(expression);
    	if (expressionMatcher.matches())
    	{
    		if (!(variables.get(expressionMatcher.group(1)) == variables.get(expressionMatcher.group(2)))) 
        	{
    			ifCounter++;
        	}
    	}
    }
    
    private static Pattern assignmentPattern = Pattern.compile("\\s*(\\w+)\\s*=(.*);\\s*");
    private static Pattern intPattern = Pattern.compile("\\s*(\\d+)\\s*");
    private static Pattern varPattern = Pattern.compile("\\s*[+-]?\\s*(\\w+)\\s*");
    private static Pattern bracketsPattern = Pattern.compile("\\s*\\((.+)\\)\\s*");
    private static Pattern additionPattern = Pattern.compile("(.*)\\+(.*)");
    private static Pattern multiplicationPattern = Pattern.compile("(.+)\\*(.+)");
    private static Pattern subtractionPattern = Pattern.compile("(.*)-(.*)");
    private static Pattern divisionPattern = Pattern.compile("(.*)/(.*)");

    //Evaluates a mathematical expression
    private Attempt evaluate(String expression)
    {
        Matcher bracketsMatcher = bracketsPattern.matcher(expression);
        if (bracketsMatcher.matches())
            return evaluate(bracketsMatcher.group(1));

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
        for (String test:variables.keySet())
        {
            System.out.println(test + variables.get(test));
        }

    }

    public String deCommenter(String currentLine){
        return currentLine.split("//")[0];
    }


}