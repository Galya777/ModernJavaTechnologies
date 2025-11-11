package bg.sofia.uni.fmi.mjt.goodreads;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for loading books from a CSV file
 */
public class BookLoader {
    
    /**
     * Loads books from a CSV reader
     *
     * @param reader the reader to read the CSV data from
     * @return a Set of Book objects
     * @throws IllegalArgumentException if the reader is null or if an error occurs during reading
     */
    public static Set<Book> load(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null");
        }

        try (CSVReader csvReader = new CSVReader(reader)) {
            return csvReader.readAll().stream()
                    .skip(1) // Skip header row
                    .map(Book::of)
                    .collect(Collectors.toSet());

        } catch (IOException | CsvException ex) {
            throw new IllegalArgumentException("Could not load dataset", ex);
        }
    }
}
