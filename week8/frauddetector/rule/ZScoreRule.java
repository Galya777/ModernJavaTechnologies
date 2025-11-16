package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.util.DoubleSummaryStatistics;
import java.util.List;

public class ZScoreRule implements Rule {
    private static final double DELTA = 1e-10;
    private final double zScoreThreshold;
    private final double weight;

    public ZScoreRule(double zScoreThreshold, double weight) {
        if (weight < 0 || weight > 1) {
            throw new IllegalArgumentException("Weight must be between 0 and 1");
        }
        this.zScoreThreshold = zScoreThreshold;
        this.weight = weight;
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null || transactions.size() < 2) {
            return false;
        }

        DoubleSummaryStatistics stats = transactions.stream()
            .mapToDouble(Transaction::transactionAmount)
            .summaryStatistics();

        double mean = stats.getAverage();
        double variance = transactions.stream()
            .mapToDouble(t -> Math.pow(t.transactionAmount() - mean, 2))
            .average()
            .orElse(0);
            
        double stdDev = Math.sqrt(variance);
        
        // If standard deviation is too small, we can't calculate meaningful z-scores
        if (stdDev < DELTA) {
            return false;
        }

        return transactions.stream()
            .anyMatch(t -> {
                double zScore = (t.transactionAmount() - mean) / stdDev;
                return zScore > zScoreThreshold;
            });
    }

    @Override
    public double weight() {
        return weight;
    }
}
