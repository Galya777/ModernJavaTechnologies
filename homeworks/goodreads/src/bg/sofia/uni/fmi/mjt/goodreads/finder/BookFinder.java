package bg.sofia.uni.fmi.mjt.goodreads.finder;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of BookFinderAPI that provides methods to search and filter books.
 */
public class BookFinder implements BookFinderAPI {
    private final Set<Book> books;
    private final TextTokenizer tokenizer;
    private final Set<String> allGenres;

    /**
     * Creates a new BookFinder with the given set of books.
     *
     * @param books the set of books to search within
     * @throws IllegalArgumentException if books is null or empty
     */
    public BookFinder(Set<Book> books) {
        if (books == null || books.isEmpty()) {
            throw new IllegalArgumentException("Books set cannot be null or empty");
        }
        
        this.books = Set.copyOf(books);
        this.tokenizer = new TextTokenizer();
        this.allGenres = books.stream()
                .map(Book::genres)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Book> allBooks() {
        return Set.copyOf(books);
    }

    @Override
    public Set<String> allGenres() {
        return Set.copyOf(allGenres);
    }

    @Override
    public List<Book> searchByAuthor(String authorName) {
        if (authorName == null || authorName.isBlank()) {
            throw new IllegalArgumentException("Author name cannot be null or blank");
        }
        
        String searchTerm = authorName.toLowerCase().trim();
        return books.stream()
                .filter(book -> book.author().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchByGenres(Set<String> genres, MatchOption option) {
        if (genres == null || genres.isEmpty()) {
            throw new IllegalArgumentException("Genres set cannot be null or empty");
        }
        if (option == null) {
            throw new IllegalArgumentException("Match option cannot be null");
        }
        
        // Normalize genre names (trim and lowercase)
        Set<String> normalizedGenres = genres.stream()
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .map(String::trim)
                .filter(genre -> !genre.isEmpty())
                .collect(Collectors.toSet());
                
        if (normalizedGenres.isEmpty()) {
            return List.of();
        }
        
        return books.stream()
                .filter(book -> {
                    Set<String> bookGenres = book.genres().stream()
                            .map(String::toLowerCase)
                            .collect(Collectors.toSet());
                            
                    if (option == MatchOption.MATCH_ALL) {
                        return bookGenres.containsAll(normalizedGenres);
                    } else { // MATCH_ANY
                        return normalizedGenres.stream().anyMatch(bookGenres::contains);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchByKeywords(Set<String> keywords, MatchOption option) {
        if (keywords == null || keywords.isEmpty()) {
            throw new IllegalArgumentException("Keywords set cannot be null or empty");
        }
        if (option == null) {
            throw new IllegalArgumentException("Match option cannot be null");
        }
        
        // Normalize keywords (trim, lowercase, and remove empty)
        Set<String> normalizedKeywords = keywords.stream()
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .map(String::trim)
                .filter(keyword -> !keyword.isEmpty())
                .collect(Collectors.toSet());
                
        if (normalizedKeywords.isEmpty()) {
            return List.of();
        }
        
        return books.stream()
                .filter(book -> {
                    // Tokenize and normalize both title and description
                    List<String> titleTokens = tokenizer.tokenize(book.title());
                    List<String> descTokens = tokenizer.tokenize(book.description());
                    
                    // Combine all tokens from title and description
                    Set<String> allTokens = new HashSet<>();
                    allTokens.addAll(titleTokens);
                    allTokens.addAll(descTokens);
                    
                    if (option == MatchOption.MATCH_ALL) {
                        return allTokens.containsAll(normalizedKeywords);
                    } else { // MATCH_ANY
                        return normalizedKeywords.stream().anyMatch(allTokens::contains);
                    }
                })
                .collect(Collectors.toList());
    }
}
