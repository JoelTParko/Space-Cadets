import java.io.*;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client extends Thread {
    private Socket client;
    private String name;

    public Client(Socket client, String name) throws IOException {
        this.client = client;
        this.name = name;
    }

    public Socket getSocket() {
        return client;
    }

    public void run() {
        String serverOutput;
        try {
            DataInputStream input = new DataInputStream(client.getInputStream());
            serverOutput = input.readUTF();
            if (serverOutput.equals("Client with that name exists")) {
                System.exit(0);
            }
            System.out.println(serverOutput);
        } catch (IOException e) {
            System.out.println("Error! " + e);
        }
    }

    public static void main(String[] args) throws IOException {
        Client fakeClient = new Client(null, null);
        String input;
        String name = fakeClient.setName();
        Client chatClient = new Client(new Socket(InetAddress.getLocalHost(), 1337), null);
        Socket socket = chatClient.getSocket();
        DataOutputStream clientOutput = new DataOutputStream(socket.getOutputStream());
        clientOutput.writeUTF(name);
        (new Client(socket, name)).start();

        while (true) {
            input = chatClient.getInput();

            clientOutput.writeUTF(input);

        }
    }

    public String getInput() throws IOException {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        return userInput.readLine();
    }

    public String setName() {
        String name = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            name = br.readLine();
        } catch (IOException e) {
            System.out.println("Input error: "+ e);
        }
        return name;
    }
}
