package bg.sofia.uni.fmi.mjt.news.exception;

/**
 * Base exception for all News API related exceptions.
 */
public class NewsFeedException extends Exception {
    public NewsFeedException(String message) {
        super(message);
    }

    public NewsFeedException(String message, Throwable cause) {
        super(message, cause);
    }
}
