package server;

import common.Request;
import common.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {
    private final int port;
    private Selector selector;
    private ServerSocketChannel serverChannel;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server running on port" + port);
            run();
        } catch (IOException e) {
            System.err.println("Server connection failed:" + e.getMessage());
        }
    }

    private void run() {
        try {
            while (true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    var key = keys.next();
                    if (key.isAcceptable()) acceptConnection(key);
                    if (key.isReadable()) readFromClient(key);
                    if (key.isWritable()) writeToClient(key);
                    keys.remove();
                }
            }
        } catch (IOException e) {
            System.err.println("Server connection error:" + e.getMessage());
        }
    }

    private void acceptConnection(SelectionKey key) throws IOException{
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = ssc.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("New connection: " + clientChannel.getRemoteAddress());
    }

    private void readFromClient(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(8192);

        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            key.cancel();
            clientChannel.close();
            return;
            }

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Request request = (Request) ois.readObject();
            System.out.println("Received request: " + request.getCommandName());

            // Response response = commandManager.executeCommand(request);
            //key.attach(response);
            key.interestOps(SelectionKey.OP_WRITE);

        } catch (ClassNotFoundException | IOException e) {
            System.err.println("Ошибка десериализации, возможно, данные пришли не полностью.");
        }
    }

    private void writeToClient(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        Response response = (Response) key.attachment();

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {

            oos.writeObject(response);
            byte[] responseBytes = bos.toByteArray();

            ByteBuffer writeBuffer = ByteBuffer.wrap(responseBytes);
            clientChannel.write(writeBuffer);
        }

        key.interestOps(SelectionKey.OP_READ);
    }
}
