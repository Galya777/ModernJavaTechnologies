package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;

/**
 * Interface for calculating similarity between two books
 */
public interface SimilarityCalculator {
    /**
     * Calculates the similarity between two books.
     *
     * @param first  the first book
     * @param second the second book
     * @return a similarity score between 0.0 (not similar) and 1.0 (identical)
     * @throws IllegalArgumentException if first or second is null
     */
    double calculateSimilarity(Book first, Book second);
}
