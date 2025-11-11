package bg.sofia.uni.fmi.mjt.goodreads.book;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record Book(String title, String author, String description, Set<String> genres, 
                  double avgRating, int numRatings, String url) {
    
    public static Book of(String[] tokens) {
        if (tokens == null || tokens.length != 8) {
            throw new IllegalArgumentException("Invalid book data");
        }

        String title = tokens[1];
        String author = tokens[2];
        String description = tokens[3];
        
        // Parse genres from "[genre1, genre2, genre3]" format
        Set<String> genres = Arrays.stream(tokens[4]
                .replaceAll("[\\[\\]]", "")
                .split(",\s*"))
            .filter(genre -> !genre.isBlank())
            .collect(Collectors.toSet());
            
        double avgRating = Double.parseDouble(tokens[5]);
        int numRatings = Integer.parseInt(tokens[6]);
        String url = tokens[7];
        
        return new Book(title, author, description, genres, avgRating, numRatings, url);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Double.compare(book.avgRating, avgRating) == 0 && 
               numRatings == book.numRatings &&
               Objects.equals(title, book.title) &&
               Objects.equals(author, book.author) &&
               Objects.equals(description, book.description) &&
               Objects.equals(genres, book.genres) &&
               Objects.equals(url, book.url);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(title, author, description, genres, avgRating, numRatings, url);
    }
}
