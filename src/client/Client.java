package client;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;

        try {
            System.out.println("Connecting to the server...");
            Socket socket = new Socket(host, port);
            System.out.println("Connection successful.");
            socket.close();
        } catch (IOException e) {
            System.err.println("Connection to the server failed.");
        }
    }
}
