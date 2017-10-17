
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class BB_Main {
	
    public static void main(String[] args)throws Exception{
        String fileName = getInput();
        String fileLocation;
        
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
		
		FileReader bbCode = new FileReader(fileLocation);
        String fileName;
        fileName = getInput();
        FileReader bbCode = new FileReader("C:\\Users\\Joelt\\Space Cadets Programs\\Barebones\\" + fileName + ".txt");
        bbCode.readFile();
    }
    
    public static String getInput()throws Exception{
    	//Gets a file name input from the user.
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your file name");
        return input.readLine();
    }

}
