package bg.sofia.uni.fmi.mjt.poll.server.repository;

import bg.sofia.uni.fmi.mjt.poll.server.model.Poll;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryPollRepository implements PollRepository {
    private final Map<Integer, Poll> polls;
    private final AtomicInteger idGenerator;

    public InMemoryPollRepository() {
        this.polls = new ConcurrentHashMap<>();
        this.idGenerator = new AtomicInteger(0);
    }

    @Override
    public int addPoll(Poll poll) {
        int newId = idGenerator.incrementAndGet();
        polls.put(newId, poll);
        return newId;
    }

    @Override
    public Poll getPoll(int pollId) {
        return polls.get(pollId);
    }

    @Override
    public Map<Integer, Poll> getAllPolls() {
        return new HashMap<>(polls);
    }

    @Override
    public void clearAllPolls() {
        polls.clear();
        idGenerator.set(0);
    }
}
