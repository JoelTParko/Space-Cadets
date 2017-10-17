
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class BB_Main {
    public static void main(String[] args)throws Exception{
        String fileName;
        String fileLocation;
        fileName = getInput();
        
        fileLocation = System.getenv("UserProfile") + "\\Documents\\BareBones\\" + fileName + ".txt";
        System.out.println(fileLocation);
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
		
        bbCode.readFile();

    }
    public static String getInput()throws Exception{
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your file name");
        return input.readLine();
    }

}
