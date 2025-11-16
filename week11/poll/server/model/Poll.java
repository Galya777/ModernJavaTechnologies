package bg.sofia.uni.fmi.mjt.poll.server.model;

import java.util.Map;

public record Poll(String question, Map<String, Integer> options) {
    public void addVote(String option) {
        options.merge(option, 1, Integer::sum);
    }
    
    public boolean hasOption(String option) {
        return options.containsKey(option);
    }
}
