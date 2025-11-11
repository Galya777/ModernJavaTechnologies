package bg.sofia.uni.fmi.mjt.news.exception;

/**
 * Thrown when the API rate limit has been exceeded.
 */
public class TooManyRequestsException extends NewsFeedException {
    public TooManyRequestsException(String message) {
        super(message);
    }

    public TooManyRequestsException(String message, Throwable cause) {
        super(message, cause);
    }
}
