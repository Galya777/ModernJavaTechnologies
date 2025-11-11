package bg.sofia.uni.fmi.mjt.news.exception;

/**
 * Thrown when the API key is invalid or missing.
 */
public class ApiKeyInvalidException extends NewsFeedException {
    public ApiKeyInvalidException(String message) {
        super(message);
    }

    public ApiKeyInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
