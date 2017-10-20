import java.io.*;

import java.util.ArrayList;

import java.util.List;



public class FileReader {
    public String filePath;
	public File file;

    public FileReader(String filePath){
        this.filePath = filePath;
        this.file = new File(filePath);
    }
    
    public List<String> readFile() throws Exception {
        String currentLine;
        List<String> fileLines = new ArrayList<String>();

        BufferedReader br = new BufferedReader(new java.io.FileReader(file));

        //Reads in every line of the program
        while((currentLine = br.readLine()) != null){
            if(!currentLine.isEmpty()){
                fileLines.add(currentLine);
            }
        }
        return  fileLines;

    }



}
