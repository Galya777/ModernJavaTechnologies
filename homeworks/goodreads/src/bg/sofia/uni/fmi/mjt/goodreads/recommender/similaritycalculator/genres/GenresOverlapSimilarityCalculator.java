package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;

import java.util.Objects;
import java.util.Set;

/**
 * Calculates the similarity between two books based on the overlap of their genres.
 * Uses the overlap coefficient formula: |A ∩ B| / min(|A|, |B|)
 * where A and B are sets of genres for the two books.
 */
public class GenresOverlapSimilarityCalculator implements SimilarityCalculator {
    
    /**
     * Calculates the similarity between two books based on their genres.
     * The similarity is a value between 0.0 (no common genres) and 1.0 (identical genre sets).
     *
     * @param first  the first book
     * @param second the second book
     * @return the similarity score between 0.0 and 1.0
     * @throws IllegalArgumentException if either book is null
     */
    @Override
    public double calculateSimilarity(Book first, Book second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Books cannot be null");
        }

        Set<String> firstGenres = first.genres();
        Set<String> secondGenres = second.genres();

        if (firstGenres.isEmpty() || secondGenres.isEmpty()) {
            return 0.0;
        }

        // Calculate intersection size
        long intersectionSize = firstGenres.stream()
                .filter(secondGenres::contains)
                .count();

        // Calculate minimum set size
        int minSize = Math.min(firstGenres.size(), secondGenres.size());

        // Avoid division by zero (shouldn't happen due to the empty check above)
        return minSize == 0 ? 0.0 : (double) intersectionSize / minSize;
    }
}
