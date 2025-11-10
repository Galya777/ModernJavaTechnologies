package bg.sofia.uni.fmi.mjt.eventbus.subscribers;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import bg.sofia.uni.fmi.mjt.eventbus.events.Event;

public class DeferredEventSubscriber<T extends Event<?>> implements Subscriber<T>, Iterable<T> {
    private final Queue<T> events;
    private final int maxSize;

    public DeferredEventSubscriber() {
        this(Integer.MAX_VALUE);
    }

    public DeferredEventSubscriber(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max size must be positive");
        }
        this.maxSize = maxSize;
        this.events = new PriorityQueue<>((e1, e2) -> {
            int priorityCompare = Integer.compare(e1.getPriority(), e2.getPriority());
            if (priorityCompare != 0) {
                return priorityCompare;
            }
            return e1.getTimestamp().compareTo(e2.getTimestamp());
        });
    }

    @Override
    public void onEvent(T event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (events.size() >= maxSize) {
            events.poll();
        }
        events.add(event);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private final PriorityQueue<T> copy = new PriorityQueue<>(events);

            @Override
            public boolean hasNext() {
                return !copy.isEmpty();
            }

            @Override
            public T next() {
                return copy.poll();
            }
        };
    }

    public boolean isEmpty() {
        return events.isEmpty();
    }
}
