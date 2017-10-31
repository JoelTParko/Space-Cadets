import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server extends Thread{

    private Socket socket;
    private static ArrayList<Socket> socketList = new ArrayList();
    private ArrayList<String> socketNames = new ArrayList();

    public Server(Socket socket) throws IOException{
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void run(){
        String userInput;
        String clientName;

        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            clientName = input.readUTF();
            for (String name: socketNames) {
                if(clientName.equals(name)){
                    DataOutputStream rejectReplica = new DataOutputStream(socket.getOutputStream());
                    rejectReplica.writeUTF("Client with that name exists");
                    Thread.currentThread().interrupt();
                }
            }
            while(true){
                //System.out.println("IT WORKED");
                userInput = input.readUTF();
                System.out.println("IT WORKED");

                for (int i = 0; i < socketList.size(); i++) {
                    DataOutputStream output = new DataOutputStream(socketList.get(i+1).getOutputStream());
                    output.writeUTF("MESSAGE RECEIVED: " + userInput);
                }
            }


        }catch (IOException e){
            System.out.println("Error with input");
        }



    }

    public static void main(String[] args) throws IOException{
        Server chatServer = new Server(new Socket());
        int number, temp;
        String userInput;
        ServerSocket serverSocket = new ServerSocket(1337, 1, InetAddress.getLocalHost());
        Socket socket = chatServer.getSocket();


        while(true) {
            socket = serverSocket.accept();

            socketList.add(socket);
            (new Server(socket)).start();


        }
    }
}
