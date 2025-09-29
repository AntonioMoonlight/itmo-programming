package client;

import common.Request;
import common.Response;

import java.io.*;
import java.net.Socket;

public class NetworkClient {
    private final String host;
    private final int port;
    private Socket socket;
    private ObjectOutputStream objectSender;
    private ObjectInputStream objectReceiver;

    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        this.socket = new Socket(host, port);
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();

        this.objectSender = new ObjectOutputStream(outputStream);
        this.objectReceiver = new ObjectInputStream(inputStream);
    }

    public Response sendRequest(Request request) throws IOException, ClassNotFoundException {
        objectSender.writeObject(request);
        objectSender.flush();

        return (Response) objectReceiver.readObject();
    }

    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
