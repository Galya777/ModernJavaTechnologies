package bg.sofia.uni.fmi.mjt.poll.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PollClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 7777;
    private static final int BUFFER_SIZE = 4096;
    private static final String DISCONNECT_COMMAND = "disconnect";

    private final String host;
    private final int port;
    private SocketChannel socketChannel;
    private final Scanner scanner;

    public PollClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        PollClient client = new PollClient(SERVER_HOST, SERVER_PORT);
        client.start();
    }

    public void start() {
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(host, port));
            System.out.println("Connected to the server.");

            while (true) {
                System.out.print("Enter command: ");
                String message = scanner.nextLine();

                if (message == null || message.isBlank()) {
                    continue;
                }

                if (DISCONNECT_COMMAND.equalsIgnoreCase(message.trim())) {
                    sendMessage(message);
                    break;
                }

                sendMessage(message);
                String reply = receiveMessage();
                System.out.println("Server response: " + reply);
            }
        } catch (IOException e) {
            System.err.println("An error occurred in the client: " + e.getMessage());
        } finally {
            try {
                if (socketChannel != null) {
                    socketChannel.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
            scanner.close();
            System.out.println("Disconnected from server.");
        }
    }

    private void sendMessage(String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap((message + "\n").getBytes(StandardCharsets.UTF_8));
        socketChannel.write(buffer);
    }

    private String receiveMessage() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        int bytesRead = socketChannel.read(buffer);
        if (bytesRead < 0) {
            return "No response from server";
        }
        buffer.flip();
        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);
        return new String(byteArray, StandardCharsets.UTF_8).trim();
    }
}
