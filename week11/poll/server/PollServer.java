package bg.sofia.uni.fmi.mjt.poll.server;

import bg.sofia.uni.fmi.mjt.poll.server.model.Poll;
import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PollServer {
    private static final int BUFFER_SIZE = 4096;
    private static final String HOST = "localhost";
    private static final String DISCONNECT_COMMAND = "disconnect";
    private static final String CREATE_POLL_COMMAND = "create-poll";
    private static final String LIST_POLLS_COMMAND = "list-polls";
    private static final String SUBMIT_VOTE_COMMAND = "submit-vote";

    private final int port;
    private final PollRepository pollRepository;
    private boolean isServerWorking;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private final Map<SocketChannel, String> clients;

    public PollServer(int port, PollRepository pollRepository) {
        this.port = port;
        this.pollRepository = pollRepository;
        this.clients = new ConcurrentHashMap<>();
    }

    public void start() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(HOST, port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            isServerWorking = true;

            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isReadable()) {
                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            String clientInput = getClientInput(clientChannel);
                            if (clientInput == null) {
                                continue;
                            }
                            
                            String response = processClientInput(clientInput, clientChannel);
                            sendResponse(clientChannel, response);
                            
                            if (DISCONNECT_COMMAND.equals(clientInput.trim())) {
                                clientChannel.close();
                                clients.remove(clientChannel);
                            }
                        } else if (key.isAcceptable()) {
                            accept(selector, key);
                        }
                        keyIterator.remove();
                    }
                } catch (IOException e) {
                    System.err.println("Error in server loop: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to start server", e);
        }
    }

    public void stop() {
        isServerWorking = false;
        if (selector != null) {
            selector.wakeup();
            try {
                selector.close();
            } catch (IOException e) {
                System.err.println("Error closing selector: " + e.getMessage());
            }
        }
        if (serverSocketChannel != null) {
            try {
                serverSocketChannel.close();
            } catch (IOException e) {
                System.err.println("Error closing server socket: " + e.getMessage());
            }
        }
        pollRepository.clearAllPolls();
        clients.clear();
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        clients.put(clientChannel, "");
        System.out.println("Client connected: " + clientChannel.getRemoteAddress());
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            clients.remove(clientChannel);
            return null;
        }
        buffer.flip();
        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);
        return new String(clientInputBytes, StandardCharsets.UTF_8).trim();
    }

    private void sendResponse(SocketChannel clientChannel, String response) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap((response + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
        while (buffer.hasRemaining()) {
            clientChannel.write(buffer);
        }
    }

    private String processClientInput(String input, SocketChannel clientChannel) {
        String[] tokens = input.split(\\" \\s+");
        String command = tokens[0].toLowerCase();
        
        try {
            return switch (command) {
                case CREATE_POLL_COMMAND -> handleCreatePoll(tokens);
                case LIST_POLLS_COMMAND -> handleListPolls();
                case SUBMIT_VOTE_COMMAND -> handleSubmitVote(tokens);
                case DISCONNECT_COMMAND -> "{\"status\":\"OK\",\"message\":\"Disconnected from server.\"}";
                default -> "{\"status\":\"ERROR\",\"message\":\"Unknown command: " + command + "\"}";
            };
        } catch (Exception e) {
            return "{\"status\":\"ERROR\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }

    private String handleCreatePoll(String[] tokens) {
        if (tokens.length < 3) {
            throw new IllegalArgumentException("Usage: create-poll <question> <option-1> <option-2> [... <option-N>]");
        }

        String question = tokens[1];
        Set<String> options = new HashSet<>();
        for (int i = 2; i < tokens.length; i++) {
            options.add(tokens[i]);
        }

        if (options.size() < 2) {
            throw new IllegalArgumentException("A poll must have at least 2 options");
        }

        Map<String, Integer> optionsMap = new HashMap<>();
        for (String option : options) {
            optionsMap.put(option, 0);
        }

        Poll poll = new Poll(question, optionsMap);
        int pollId = pollRepository.addPoll(poll);

        return String.format(
            "{\"status\":\"OK\",\"message\":\"Poll %d created successfully.\"}",
            pollId
        );
    }

    private String handleListPolls() {
        Map<Integer, Poll> polls = pollRepository.getAllPolls();
        if (polls.isEmpty()) {
            return "{\"status\":\"ERROR\",\"message\":\"No active polls available.\"}";
        }

        StringBuilder jsonBuilder = new StringBuilder("{\"status\":\"OK\",\"polls\":{");
        boolean firstPoll = true;
        
        for (Map.Entry<Integer, Poll> entry : polls.entrySet()) {
            if (!firstPoll) {
                jsonBuilder.append(",");
            }
            firstPoll = false;
            
            int pollId = entry.getKey();
            Poll poll = entry.getValue();
            
            jsonBuilder.append(String.format(\"%d\":{\"question\":\"%s\",\"options\":{", 
                pollId, poll.question()));
            
            boolean firstOption = true;
            for (Map.Entry<String, Integer> option : poll.options().entrySet()) {
                if (!firstOption) {
                    jsonBuilder.append(",");
                }
                firstOption = false;
                jsonBuilder.append(String.format(\"%s\":%d", option.getKey(), option.getValue()));
            }
            
            jsonBuilder.append("}}");
        }
        
        jsonBuilder.append("}}");
        return jsonBuilder.toString();
    }

    private String handleSubmitVote(String[] tokens) {
        if (tokens.length != 3) {
            throw new IllegalArgumentException("Usage: submit-vote <poll-id> <option>");
        }

        int pollId;
        try {
            pollId = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Poll ID must be a number");
        }

        String option = tokens[2];
        Poll poll = pollRepository.getPoll(pollId);
        
        if (poll == null) {
            throw new IllegalArgumentException("Poll with ID " + pollId + " does not exist.");
        }
        
        if (!poll.hasOption(option)) {
            throw new IllegalArgumentException("Invalid option. Option " + option + " does not exist.");
        }

        poll.addVote(option);
        
        return String.format(
            "{\"status\":\"OK\",\"message\":\"Vote submitted successfully for option: %s\"}",
            option
        );
    }
}
