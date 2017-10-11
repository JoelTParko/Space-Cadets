import java.io.*;
import java.util.Stack;

public class BB_Main {
    public static void main(String[] args)throws Exception{
        Stack whileStack = new Stack();
        boolean inWhile;
        int whileCount = 0;
        String currentLine;
        String[] nextCommand = new String[2];
        long linePointer = 0;
        long endPoint = 0;
        BB_Interpreter interpreter = new BB_Interpreter();
        File bbCode = new File("C:\\Users\\Joelt\\Space Cadets Programs\\BareBones.txt");
        RandomAccessFile codeReader = new RandomAccessFile(bbCode, "r");


        while ((currentLine=codeReader.readLine())!=null) {
            nextCommand = interpreter.readLine(currentLine);
            if(nextCommand[0].equals("while")) {
                inWhile = interpreter.executeWhile(nextCommand[1], currentLine);
                if (!inWhile) {
                    codeReader.seek(endPoint);
                } else {
                    linePointer = codeReader.getFilePointer();
                    whileStack.push(linePointer - (currentLine.length() + 2));
                    whileCount++;

                }

            }else if(nextCommand[0].equals("end") && whileCount>0) {
                    endPoint = codeReader.getFilePointer();
                    linePointer = (long)whileStack.pop();
                    whileCount--;
                    codeReader.seek(linePointer);
            }else {
                interpreter.executeCommand(nextCommand[0], nextCommand[1]);

            }
        }
        interpreter.testCode();
        codeReader.close();

    }

}
