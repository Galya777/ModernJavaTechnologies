package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LocationsRule implements Rule {
    private final int threshold;
    private final double weight;

    public LocationsRule(int threshold, double weight) {
        if (weight < 0 || weight > 1) {
            throw new IllegalArgumentException("Weight must be between 0 and 1");
        }
        this.threshold = threshold;
        this.weight = weight;
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return false;
        }

        Set<String> uniqueLocations = transactions.stream()
            .map(Transaction::location)
            .collect(Collectors.toSet());
            
        return uniqueLocations.size() > threshold;
    }

    @Override
    public double weight() {
        return weight;
    }
}
