package bg.sofia.uni.fmi.mjt.newsfeed.exception;

public class NewsFeedException extends Exception {
    public NewsFeedException(String message) {
        super(message);
    }

    public NewsFeedException(String message, Throwable cause) {
        super(message, cause);
    }
}
