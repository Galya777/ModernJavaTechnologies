package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.composite;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A composite similarity calculator that combines multiple similarity calculators
 * with different weights to produce a combined similarity score.
 */
public class CompositeSimilarityCalculator implements SimilarityCalculator {
    private final Map<SimilarityCalculator, Double> calculatorsWithWeights;
    private double totalWeight;

    /**
     * Creates a new CompositeSimilarityCalculator with no calculators.
     * Use addCalculator() to add calculators with weights.
     */
    public CompositeSimilarityCalculator() {
        this.calculatorsWithWeights = new HashMap<>();
        this.totalWeight = 0.0;
    }

    /**
     * Adds a similarity calculator with the given weight.
     * The weight determines the relative importance of this calculator in the final score.
     *
     * @param calculator the similarity calculator to add
     * @param weight    the weight of this calculator (must be positive)
     * @throws IllegalArgumentException if calculator is null or weight is not positive
     */
    public void addCalculator(SimilarityCalculator calculator, double weight) {
        if (calculator == null) {
            throw new IllegalArgumentException("Calculator cannot be null");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        
        calculatorsWithWeights.put(calculator, weight);
        totalWeight += weight;
    }

    /**
     * Calculates the combined similarity score between two books using all added calculators.
     * The final score is a weighted average of the scores from all calculators.
     *
     * @param first  the first book
     * @param second the second book
     * @return the combined similarity score between 0.0 and 1.0
     * @throws IllegalArgumentException if either book is null
     */
    @Override
    public double calculateSimilarity(Book first, Book second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Books cannot be null");
        }
        
        if (calculatorsWithWeights.isEmpty() || totalWeight <= 0) {
            return 0.0;
        }
        
        double weightedSum = 0.0;
        
        for (Map.Entry<SimilarityCalculator, Double> entry : calculatorsWithWeights.entrySet()) {
            SimilarityCalculator calculator = entry.getKey();
            double weight = entry.getValue();
            
            try {
                double similarity = calculator.calculateSimilarity(first, second);
                // Ensure similarity is within [0, 1] range
                similarity = Math.max(0.0, Math.min(1.0, similarity));
                weightedSum += similarity * weight;
            } catch (Exception e) {
                // If a calculator fails, skip it (treat as 0 contribution)
                continue;
            }
        }
        
        // Normalize by total weight
        return weightedSum / totalWeight;
    }
}
