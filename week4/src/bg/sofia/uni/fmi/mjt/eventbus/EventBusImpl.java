package modernJava.week4.src.bg.sofia.uni.fmi.mjt.eventbus;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;
import bg.sofia.uni.fmi.mjt.eventbus.exception.MissingSubscriptionException;
import bg.sofia.uni.fmi.mjt.eventbus.subscribers.Subscriber;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EventBusImpl implements EventBus {
    private final Map<Class<? extends Event<?>>, List<Subscriber<?>>> subscribers;
    private final Map<Class<? extends Event<?>>, List<Event<?>>> eventLogs;

    public EventBusImpl() {
        this.subscribers = new HashMap<>();
        this.eventLogs = new HashMap<>();
    }

    @Override
    public <T extends Event<?>> void subscribe(Class<T> eventType, Subscriber<? super T> subscriber) {
        validateEventType(eventType);
        Objects.requireNonNull(subscriber, "Subscriber cannot be null");

        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(subscriber);
    }

    @Override
    public <T extends Event<?>> void unsubscribe(Class<T> eventType, Subscriber<? super T> subscriber) 
            throws MissingSubscriptionException {
        validateEventType(eventType);
        Objects.requireNonNull(subscriber, "Subscriber cannot be null");

        List<Subscriber<?>> eventSubscribers = subscribers.get(eventType);
        if (eventSubscribers == null || !eventSubscribers.remove(subscriber)) {
            throw new MissingSubscriptionException("Subscriber is not registered for the specified event type");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event<?>> void publish(T event) {
        Objects.requireNonNull(event, "Event cannot be null");

        Class<? extends Event<?>> eventType = (Class<? extends Event<?>>) event.getClass();
        
        // Log the event
        eventLogs.computeIfAbsent(eventType, k -> new ArrayList<>()).add(event);

        // Notify subscribers
        List<Subscriber<?>> eventSubscribers = subscribers.getOrDefault(eventType, Collections.emptyList());
        for (Subscriber<?> subscriber : eventSubscribers) {
            ((Subscriber<T>) subscriber).onEvent(event);
        }
    }

    @Override
    public void clear() {
        subscribers.clear();
        eventLogs.clear();
    }

    @Override
    public Collection<? extends Event<?>> getEventLogs(Class<? extends Event<?>> eventType, 
                                                     Instant from, Instant to) {
        validateEventType(eventType);
        Objects.requireNonNull(from, "From timestamp cannot be null");
        Objects.requireNonNull(to, "To timestamp cannot be null");

        if (from.equals(to)) {
            return Collections.emptyList();
        }

        List<Event<?>> logs = eventLogs.getOrDefault(eventType, Collections.emptyList());
        List<Event<?>> result = new ArrayList<>();

        for (Event<?> event : logs) {
            if (!event.getTimestamp().isBefore(from) && event.getTimestamp().isBefore(to)) {
                result.add(event);
            }
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public <T extends Event<?>> Collection<Subscriber<?>> getSubscribersForEvent(Class<T> eventType) {
        validateEventType(eventType);
        return Collections.unmodifiableList(
            subscribers.getOrDefault(eventType, Collections.emptyList())
        );
    }

    private void validateEventType(Class<?> eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }
    }
}
