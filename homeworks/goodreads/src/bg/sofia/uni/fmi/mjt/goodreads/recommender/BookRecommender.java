package bg.sofia.uni.fmi.mjt.goodreads.recommender;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * Implementation of BookRecommenderAPI that provides book recommendations
 * based on a similarity calculator.
 */
public class BookRecommender implements BookRecommenderAPI {
    private final Set<Book> books;
    private final SimilarityCalculator similarityCalculator;

    /**
     * Creates a new BookRecommender with the given set of books and similarity calculator.
     *
     * @param books the set of books to recommend from
     * @param calculator the similarity calculator to use
     * @throws IllegalArgumentException if books is null or empty, or calculator is null
     */
    public BookRecommender(Set<Book> books, SimilarityCalculator calculator) {
        if (books == null || books.isEmpty()) {
            throw new IllegalArgumentException("Books set cannot be null or empty");
        }
        if (calculator == null) {
            throw new IllegalArgumentException("Similarity calculator cannot be null");
        }
        
        this.books = Set.copyOf(books);
        this.similarityCalculator = calculator;
    }

    @Override
    public SortedMap<Book, Double> recommendBooks(Book originBook, int maxN) {
        if (originBook == null) {
            throw new IllegalArgumentException("Origin book cannot be null");
        }
        if (maxN <= 0) {
            throw new IllegalArgumentException("Max number of recommendations must be positive");
        }

        // Use a TreeMap with reverse order comparator to sort by similarity score in descending order
        SortedMap<Book, Double> recommendations = new TreeMap<>(
            Map.Entry.<Book, Double>comparingByValue().reversed()
                .thenComparing(entry -> entry.getKey().title())
        );

        // Calculate similarity for each book (excluding the origin book)
        books.stream()
            .filter(book -> !book.equals(originBook))
            .forEach(book -> {
                double similarity = similarityCalculator.calculateSimilarity(originBook, book);
                recommendations.put(book, similarity);
            });

        // Return top N recommendations
        return recommendations.entrySet().stream()
            .limit(maxN)
            .collect(TreeMap::new, 
                    (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                    Map::putAll);
    }
}
