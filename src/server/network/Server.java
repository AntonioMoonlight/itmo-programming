package server.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.CommandManager;
import server.CollectionManager;
import common.Request;
import common.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

/**
 * Handles client connections and processes requests in a single thread
 */
public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    private static final int BUFFER_SIZE = 8192;
    
    private final int port;
    private final RequestProcessor requestProcessor;
    
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private boolean running = false;
    

    private final Map<SocketChannel, ClientHandler> clients = new HashMap<>();

    public Server(int port, CommandManager commandManager, CollectionManager collectionManager) {
        this.port = port;
        this.requestProcessor = new RequestProcessor(commandManager);
    }

    public void start() throws IOException {
        logger.info("Starting server on port {}", port);
        
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        running = true;
        logger.info("Server started successfully on port {}", port);
        
        run();
    }

    private void run() {
        while (running) {
            try {
                int readyChannels = selector.select(1000);
                
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isAcceptable()) {
                        handleAccept(key);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    } else if (key.isWritable()) {
                        handleWrite(key);
                    }
                }
            } catch (IOException e) {
                logger.error("Error in server main loop", e);
                if (running) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        
        if (clientChannel != null) {
            logger.info("New client connected: {}", clientChannel.getRemoteAddress());
            
            clientChannel.configureBlocking(false);
            SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
            
            ClientHandler clientHandler = new ClientHandler(clientChannel);
            clients.put(clientChannel, clientHandler);
            clientKey.attach(clientHandler);
        }
    }

    private void handleRead(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ClientHandler clientHandler = (ClientHandler) key.attachment();
        
        try {
            Request request = clientHandler.readRequest();
            if (request != null) {
                logger.info("Received request: {} from {}", request.getCommandName(), 
                           clientChannel.getRemoteAddress());
                
                Response response = requestProcessor.processRequest(request);
                clientHandler.queueResponse(response);
                
                key.interestOps(SelectionKey.OP_WRITE);
            }
        } catch (IOException e) {
            logger.warn("Error reading from client {}: {}", clientChannel, e.getMessage());
            closeClient(clientChannel, key);
        } catch (ClassNotFoundException e) {
            logger.error("Error deserializing request from client {}", clientChannel, e);

            Response errorResponse = new Response(false, "Invalid request format");
            clientHandler.queueResponse(errorResponse);
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    private void handleWrite(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ClientHandler clientHandler = (ClientHandler) key.attachment();
        
        try {
            boolean writeComplete = clientHandler.writeResponse();
            if (writeComplete) {
                logger.info("Response sent to {}", clientChannel.getRemoteAddress());

                key.interestOps(SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            logger.warn("Error writing to client {}: {}", clientChannel, e.getMessage());
            closeClient(clientChannel, key);
        }
    }

    private void closeClient(SocketChannel clientChannel, SelectionKey key) {
        try {
            logger.info("Closing connection to client: {}", clientChannel.getRemoteAddress());
            clients.remove(clientChannel);
            key.cancel();
            clientChannel.close();
        } catch (IOException e) {
            logger.error("Error closing client connection", e);
        }
    }

    public void stop() {
        logger.info("Stopping server...");
        running = false;
        
        try {

            for (SocketChannel client : new ArrayList<>(clients.keySet())) {
                client.close();
            }
            clients.clear();
            
            if (serverChannel != null) {
                serverChannel.close();
            }
            if (selector != null) {
                selector.close();
            }
            logger.info("Server stopped");
        } catch (IOException e) {
            logger.error("Error stopping server", e);
        }
    }

    /**
     * Handles individual client connection state
     */
    private static class ClientHandler {
        private final SocketChannel channel;
        private final ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        private final ByteBuffer writeBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        private byte[] pendingData = null;
        private int expectedLength = -1;
        private Response queuedResponse = null;

        public ClientHandler(SocketChannel channel) {
            this.channel = channel;
        }

        public Request readRequest() throws IOException, ClassNotFoundException {
            int bytesRead = channel.read(readBuffer);
            if (bytesRead == -1) {
                throw new IOException("Client disconnected");
            }
            
            if (bytesRead == 0) {
                return null;
            }

            readBuffer.flip();
            
            // First, read the length of the serialized object
            if (expectedLength == -1 && readBuffer.remaining() >= 4) {
                expectedLength = readBuffer.getInt();
                pendingData = new byte[expectedLength];
            }
            
            // Then read the actual data
            if (expectedLength > 0 && pendingData != null) {
                int remaining = Math.min(readBuffer.remaining(), expectedLength - (pendingData.length - expectedLength));
                readBuffer.get(pendingData, pendingData.length - expectedLength, remaining);
                expectedLength -= remaining;
                
                if (expectedLength == 0) {
                    // Complete object received
                    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(pendingData))) {
                        Request request = (Request) ois.readObject();
                        
                        // Reset for next request
                        expectedLength = -1;
                        pendingData = null;
                        readBuffer.clear();
                        
                        return request;
                    }
                }
            }
            
            readBuffer.compact();
            return null;
        }

        public void queueResponse(Response response) {
            this.queuedResponse = response;
        }
        // positive if successful, false if response is partial
        public boolean writeResponse() throws IOException {
            if (queuedResponse == null) {
                return true;
            }

            if (writeBuffer.position() == 0) {
                // Serialize response
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                     ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    
                    oos.writeObject(queuedResponse);
                    oos.flush();
                    
                    byte[] data = baos.toByteArray();
                    writeBuffer.putInt(data.length); // Length prefix
                    writeBuffer.put(data);
                    writeBuffer.flip();
                } catch (IOException e) {
                    throw new IOException("Error serializing response", e);
                }
            }

            channel.write(writeBuffer);
            
            if (!writeBuffer.hasRemaining()) {
                writeBuffer.clear();
                queuedResponse = null;
                return true;
            }
            
            return false; // Write incomplete
        }
    }

    public RequestProcessor getRequestProcessor() {
        return requestProcessor;
    }
}