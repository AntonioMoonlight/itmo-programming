package client.network;

import common.Request;
import common.Response;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * TCP Client using IO streams to communicate with server
 * Handles connection management, request sending, and response receiving
 */
public class Client {
    private final String serverHost;
    private final int serverPort;
    private final int connectionTimeout;
    private final int readTimeout;
    
    private Socket socket;
    private boolean connected = false;

    public Client(String serverHost, int serverPort) {
        this(serverHost, serverPort, 5000, 10000); // 5s connection, 10s read timeout
    }

    public Client(String serverHost, int serverPort, int connectionTimeout, int readTimeout) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    /**
     * Establishes connection to the server
     */
    public void connect() throws IOException {
        if (connected) {
            return; // Already connected
        }

        try {
            socket = new Socket();
            socket.connect(new java.net.InetSocketAddress(serverHost, serverPort), connectionTimeout);
            socket.setSoTimeout(readTimeout);
            
            connected = true;
            System.out.println("Connected to server at " + serverHost + ":" + serverPort);
            
        } catch (ConnectException e) {
            throw new IOException("Cannot connect to server: " + e.getMessage(), e);
        } catch (SocketTimeoutException e) {
            throw new IOException("Connection timeout", e);
        }
    }

    /**
     * Sends a request to the server and returns the response
     */
    public Response sendRequest(Request request) throws IOException {
        if (!connected) {
            throw new IOException("Not connected to server");
        }

        try {
            // Serialize request to byte array first
            byte[] requestData;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(request);
                oos.flush();
                requestData = baos.toByteArray();
            }
            
            // Send length prefix followed by data
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt(requestData.length);
            dos.write(requestData);
            dos.flush();
            
            // Read response length prefix
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            int responseLength = dis.readInt();
            
            // Read response data
            byte[] responseData = new byte[responseLength];
            dis.readFully(responseData);
            
            // Deserialize response
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(responseData))) {
                Object responseObj = ois.readObject();
                if (!(responseObj instanceof Response)) {
                    throw new IOException("Invalid response from server");
                }
                return (Response) responseObj;
            }
            
        } catch (ClassNotFoundException e) {
            throw new IOException("Error deserializing response", e);
        } catch (SocketTimeoutException e) {
            throw new IOException("Server response timeout", e);
        } catch (IOException e) {
            // Connection might be broken
            connected = false;
            throw new IOException("Communication error: " + e.getMessage(), e);
        }
    }

    /**
     * Attempts to reconnect to the server
     */
    public boolean reconnect() {
        disconnect();
        
        try {
            connect();
            return true;
        } catch (IOException e) {
            System.err.println("Reconnection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if client is connected to server
     */
    public boolean isConnected() {
        return connected && socket != null && socket.isConnected() && !socket.isClosed();
    }

    /**
     * Disconnects from the server
     */
    public void disconnect() {
        connected = false;
        
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // Ignore
        }
        
        socket = null;
    }

    /**
     * Sends request with automatic retry logic for handling server unavailability
     */
    public Response sendRequestWithRetry(Request request, int maxRetries, long retryDelayMs) throws IOException {
        IOException lastException = null;
        
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                if (!isConnected()) {
                    connect();
                }
                
                return sendRequest(request);
                
            } catch (IOException e) {
                lastException = e;
                
                if (attempt < maxRetries) {
                    System.err.println("Request failed (attempt " + (attempt + 1) + "/" + (maxRetries + 1) + "): " + e.getMessage());
                    System.err.println("Retrying in " + retryDelayMs + "ms...");
                    
                    try {
                        Thread.sleep(retryDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted during retry", ie);
                    }
                } else {
                    System.err.println("All retry attempts failed");
                }
            }
        }
        
        throw new IOException("Failed to send request after " + (maxRetries + 1) + " attempts", lastException);
    }
}