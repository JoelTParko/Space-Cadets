import java.io.*;
import java.util.Stack;

public class FileReader {
    public String filePath;
	
    public String file;

    public FileReader(String filePath){
        this.filePath = filePath;
    }
    
    public void readFile() throws Exception{
    	String commandLine;
        String currentLine;
        String[] nextCommand;
        boolean inWhile;
        int whileCount = 0;
        long linePointer;
        long endPoint = 0;
        Stack<Long> whileStack = new Stack<>();
        BB_Interpreter interpreter = new BB_Interpreter();
        File bbCode = new File(filePath);
        RandomAccessFile codeReader = new RandomAccessFile(bbCode, "r");
        
        while ((currentLine=codeReader.readLine())!=null) {
            commandLine = interpreter.deCommenter(currentLine);
            nextCommand = interpreter.readLine(commandLine);
            
            if(nextCommand[0].equals("while")) {
                inWhile = interpreter.executeWhile(nextCommand[1], commandLine);
                
                if (!inWhile) {
                    codeReader.seek(endPoint);
                } else {
                    linePointer = codeReader.getFilePointer();
                    whileStack.push(linePointer - (currentLine.length() + 2));
                    whileCount++;
                }
                
            }else if(nextCommand[0].equals("end") && whileCount>0) {
                endPoint = codeReader.getFilePointer();
                linePointer = whileStack.pop();
                whileCount--;
                codeReader.seek(linePointer);
            }else{
                interpreter.executeCommand(nextCommand[0], nextCommand[1]);
            }
            
        }
        
        codeReader.close();
        interpreter.printState();
    }
    
}