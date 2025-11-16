package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;

public class FrequencyRule implements Rule {
    private final int transactionCountThreshold;
    private final TemporalAmount timeWindow;
    private final double weight;

    public FrequencyRule(int transactionCountThreshold, TemporalAmount timeWindow, double weight) {
        if (weight < 0 || weight > 1) {
            throw new IllegalArgumentException("Weight must be between 0 and 1");
        }
        this.transactionCountThreshold = transactionCountThreshold;
        this.timeWindow = timeWindow;
        this.weight = weight;
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return false;
        }

        // Sort transactions by date
        transactions.sort((t1, t2) -> t2.transactionDate().compareTo(t1.transactionDate()));
        
        LocalDateTime windowStart = transactions.get(0).transactionDate().minus(timeWindow);
        
        long count = transactions.stream()
            .filter(t -> !t.transactionDate().isBefore(windowStart))
            .count();
            
        return count > transactionCountThreshold;
    }

    @Override
    public double weight() {
        return weight;
    }
}
