
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BB_Main {
    public static void main(String[] args)throws Exception{
        String fileName;
        fileName = getInput();
        FileReader bbCode = new FileReader("C:\\Users\\Joelt\\Space Cadets Programs\\Barebones\\" + fileName + ".txt");
        bbCode.readFile();

    }
    public static String getInput()throws Exception{
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your file name");
        return input.readLine();
    }

}
