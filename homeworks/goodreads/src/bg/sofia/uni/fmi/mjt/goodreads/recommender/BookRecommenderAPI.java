package bg.sofia.uni.fmi.mjt.goodreads.recommender;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;

import java.util.SortedMap;

/**
 * Interface for recommending books based on similarity
 */
public interface BookRecommenderAPI {

    /**
     * Searches for books that are similar to the provided one.
     *
     * @param originBook the book we should calculate similarity with.
     * @param maxN       the maximum number of entries returned
     * @return a SortedMap of books with their similarity scores to originBook,
     *         ordered by their similarity score in descending order
     * @throws IllegalArgumentException if the originBook is null or maxN is less than or equal to 0
     */
    SortedMap<Book, Double> recommendBooks(Book originBook, int maxN);
}
