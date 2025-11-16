package bg.sofia.uni.fmi.mjt.frauddetector.transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public record Transaction(
    String transactionID,
    String accountID,
    double transactionAmount,
    LocalDateTime transactionDate,
    String location,
    Channel channel
) {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");
    private static final String CSV_DELIMITER = ",";
    
    public static Transaction of(String line) {
        Objects.requireNonNull(line, "Line cannot be null");
        
        String[] parts = line.split(CSV_DELIMITER, -1);
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid transaction format");
        }
        
        return new Transaction(
            parts[0].trim(),
            parts[1].trim(),
            Double.parseDouble(parts[2].trim()),
            LocalDateTime.parse(parts[3].trim(), DATE_FORMATTER),
            parts[4].trim(),
            Channel.valueOf(parts[5].trim())
        );
    }
}
