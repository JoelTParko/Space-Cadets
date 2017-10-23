import java.io.*;
import java.util.Iterator;
import java.util.Queue;
import java.util.List;

public class BB_Main {
	
    public static void main(String[] args) throws Exception{
        String fileName = getInput();
        String fileLocation;
        String currentLine;
        int jumpPoint;
        int index = 0;
        List<String> fileLines;
        
        //Construct the file path.
        fileLocation = System.getenv("UserProfile") + "\\Documents\\BareBones\\" + fileName + ".txt";
        //Test if the file exists.
		File testFile = new File(fileLocation);
		if (testFile.exists()) {
			System.out.println("File verified");
		}
		else {
			System.out.println("File doesn't exist, exiting program.");
			System.exit(0);
		}
		
		FileReader reader = new FileReader(fileLocation);
		fileLines = reader.readFile();

		BB_Interpreter mainInterpreter = new BB_Interpreter(fileLines); //Initialising the interpreter
		PositionableIterator iterator = new PositionableIterator(fileLines); //Allows the program to jump between different points in the BareBones code


        do{
            index++;
            currentLine = iterator.next(); //Gets the next line of BareBones code
            jumpPoint = mainInterpreter.next(currentLine, index); //Interprets the BB code
            if(jumpPoint != 0){//If the program should simply move on to the next line, jumpPoint is set to 0
                iterator.moveTo(jumpPoint); //Moves the iterator to the appropriate position in the code, sets the index to the new position in the code
                index = jumpPoint;
            }

        }while(iterator.hasNext());
        mainInterpreter.printState();

    }
    
    public static String getInput()throws Exception{
    	//Gets a file name input from the user.
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your file name");
        return input.readLine();

    }

}