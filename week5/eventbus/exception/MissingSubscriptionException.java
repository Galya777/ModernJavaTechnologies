package bg.sofia.uni.fmi.mjt.eventbus.exception;

/**
 * Thrown when an attempt is made to unsubscribe a subscriber that is not subscribed to the specified event type.
 */
public class MissingSubscriptionException extends Exception {
    public MissingSubscriptionException(String message) {
        super(message);
    }
}
