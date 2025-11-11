package bg.sofia.uni.fmi.mjt.goodreads.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for tokenizing text by removing punctuation, converting to lowercase,
 * and filtering out stopwords.
 */
public class TextTokenizer {
    private static final String STOPWORDS_FILE = "/stopwords.txt";
    private static final String PUNCTUATION_REGEX = "[^a-zA-Z0-9\\s]";
    private static final String WHITESPACE_REGEX = "\\s+";
    
    private final Set<String> stopwords;
    
    /**
     * Creates a new TextTokenizer with default stopwords.
     * 
     * @throws IllegalStateException if the stopwords file cannot be loaded
     */
    public TextTokenizer() {
        this.stopwords = loadStopwords();
    }
    
    /**
     * Tokenizes the input text by:
     * 1. Converting to lowercase
     * 2. Removing punctuation
     * 3. Splitting into words
     * 4. Removing stopwords
     *
     * @param input the text to tokenize
     * @return a list of tokens (words)
     * @throws IllegalArgumentException if input is null
     */
    public List<String> tokenize(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        
        if (input.isBlank()) {
            return List.of();
        }
        
        // Convert to lowercase and remove punctuation
        String cleaned = input.toLowerCase()
                .replaceAll(PUNCTUATION_REGEX, " ")
                .replaceAll(WHITESPACE_REGEX, " ")
                .trim();
                
        if (cleaned.isEmpty()) {
            return List.of();
        }
        
        // Split into words and filter out stopwords
        return Arrays.stream(cleaned.split(WHITESPACE_REGEX))
                .filter(word -> !word.isBlank() && !stopwords.contains(word))
                .collect(Collectors.toList());
    }
    
    private Set<String> loadStopwords() {
        try (InputStream is = getClass().getResourceAsStream(STOPWORDS_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(
                     Objects.requireNonNull(is), StandardCharsets.UTF_8))) {
            
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .collect(Collectors.toSet());
                    
        } catch (IOException | NullPointerException e) {
            throw new IllegalStateException("Could not load stopwords", e);
        }
    }
}
