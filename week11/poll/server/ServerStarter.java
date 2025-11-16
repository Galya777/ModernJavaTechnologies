package bg.sofia.uni.fmi.mjt.poll.server;

import bg.sofia.uni.fmi.mjt.poll.server.repository.InMemoryPollRepository;

public class ServerStarter {
    private static final int SERVER_PORT = 7777;

    public static void main(String[] args) {
        PollRepository pollRepository = new InMemoryPollRepository();
        PollServer server = new PollServer(SERVER_PORT, pollRepository);
        
        System.out.println("Starting Poll Server on port " + SERVER_PORT + "...");
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.stop();
        }));
        
        server.start();
    }
}
