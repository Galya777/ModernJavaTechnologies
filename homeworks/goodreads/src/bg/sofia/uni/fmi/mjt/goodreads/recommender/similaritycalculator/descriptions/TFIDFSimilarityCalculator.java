package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Calculates the similarity between two books based on the TF-IDF (Term Frequency-Inverse Document Frequency)
 * of their descriptions.
 */
public class TFIDFSimilarityCalculator implements SimilarityCalculator {
    private final Map<String, Double> idfMap;
    private final TextTokenizer tokenizer;
    
    /**
     * Creates a new TFIDFSimilarityCalculator with the given collection of books.
     *
     * @param books the collection of books to analyze for IDF calculation
     * @throws IllegalArgumentException if books is null or empty, or if any book has a null description
     */
    public TFIDFSimilarityCalculator(Collection<Book> books) {
        if (books == null || books.isEmpty()) {
            throw new IllegalArgumentException("Books collection cannot be null or empty");
        }
        
        this.tokenizer = new TextTokenizer();
        this.idfMap = calculateIDF(books);
    }
    
    @Override
    public double calculateSimilarity(Book first, Book second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Books cannot be null");
        }
        
        String desc1 = first.description();
        String desc2 = second.description();
        
        if (desc1 == null || desc2 == null || desc1.isBlank() || desc2.isBlank()) {
            return 0.0;
        }
        
        // Tokenize and get term frequencies
        List<String> tokens1 = tokenizer.tokenize(desc1);
        List<String> tokens2 = tokenizer.tokenize(desc2);
        
        if (tokens1.isEmpty() || tokens2.isEmpty()) {
            return 0.0;
        }
        
        // Calculate TF-IDF vectors
        Map<String, Double> tfidf1 = calculateTFIDFVector(tokens1);
        Map<String, Double> tfidf2 = calculateTFIDFVector(tokens2);
        
        // Calculate cosine similarity
        return cosineSimilarity(tfidf1, tfidf2);
    }
    
    private Map<String, Double> calculateTFIDFVector(List<String> tokens) {
        // Calculate term frequencies (TF)
        Map<String, Double> tfMap = new HashMap<>();
        int totalTerms = tokens.size();
        
        for (String term : tokens) {
            tfMap.put(term, tfMap.getOrDefault(term, 0.0) + 1.0);
        }
        
        // Convert to TF-IDF
        Map<String, Double> tfidf = new HashMap<>();
        
        for (Map.Entry<String, Double> entry : tfMap.entrySet()) {
            String term = entry.getKey();
            double tf = entry.getValue() / totalTerms;
            double idf = idfMap.getOrDefault(term, 0.0);
            tfidf.put(term, tf * idf);
        }
        
        return tfidf;
    }
    
    private double cosineSimilarity(Map<String, Double> vec1, Map<String, Double> vec2) {
        // Get all unique terms from both vectors
        Set<String> allTerms = new java.util.HashSet<>();
        allTerms.addAll(vec1.keySet());
        allTerms.addAll(vec2.keySet());
        
        if (allTerms.isEmpty()) {
            return 0.0;
        }
        
        // Calculate dot product
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (String term : allTerms) {
            double v1 = vec1.getOrDefault(term, 0.0);
            double v2 = vec2.getOrDefault(term, 0.0);
            
            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }
        
        // Avoid division by zero
        if (norm1 <= 0 || norm2 <= 0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    private Map<String, Double> calculateIDF(Collection<Book> books) {
        Map<String, Integer> docFreq = new HashMap<>();
        int totalDocs = 0;
        
        // Count document frequency for each term
        for (Book book : books) {
            if (book.description() != null && !book.description().isBlank()) {
                List<String> tokens = tokenizer.tokenize(book.description());
                Set<String> uniqueTokens = Set.copyOf(tokens);
                
                for (String token : uniqueTokens) {
                    docFreq.put(token, docFreq.getOrDefault(token, 0) + 1);
                }
                
                totalDocs++;
            }
        }
        
        if (totalDocs == 0) {
            return Collections.emptyMap();
        }
        
        // Calculate IDF for each term: log(totalDocs / docFreq)
        Map<String, Double> idfMap = new HashMap<>();
        double logTotalDocs = Math.log(totalDocs);
        
        for (Map.Entry<String, Integer> entry : docFreq.entrySet()) {
            double idf = logTotalDocs - Math.log(entry.getValue());
            idfMap.put(entry.getKey(), idf);
        }
        
        return idfMap;
    }
}
