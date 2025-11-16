package bg.sofia.uni.fmi.mjt.sentimentanalyzer;

import java.io.StringReader;
import java.util.Map;
import java.util.Set;

public class ExampleUsage {
    public static void main(String[] args) {
        // Example stop words
        Set<String> stopWords = Set.of("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours");
        
        // Example sentiment lexicon
        Map<String, SentimentScore> lexicon = Map.of(
            "love", SentimentScore.VERY_POSITIVE,
            "hate", SentimentScore.VERY_NEGATIVE,
            "good", SentimentScore.POSITIVE,
            "bad", SentimentScore.NEGATIVE,
            "excellent", SentimentScore.VERY_POSITIVE,
            "terrible", SentimentScore.VERY_NEGATIVE,
            "happy", SentimentScore.POSITIVE,
            "sad", SentimentScore.NEGATIVE
        );

        // Create analyzer with 4 worker threads
        ParallelSentimentAnalyzer analyzer = new ParallelSentimentAnalyzer(4, stopWords, lexicon);

        // Create some test inputs
        AnalyzerInput input1 = new AnalyzerInput("doc1", new StringReader("I love programming!"));
        AnalyzerInput input2 = new AnalyzerInput("doc2", new StringReader("I hate bugs."));
        AnalyzerInput input3 = new AnalyzerInput("doc3", new StringReader("This is a good example."));
        AnalyzerInput input4 = new AnalyzerInput("doc4", new StringReader("The weather is terrible today."));

        try {
            // Analyze the inputs
            Map<String, SentimentScore> results = analyzer.analyze(input1, input2, input3, input4);

            // Print the results
            results.forEach((docId, sentiment) -> 
                System.out.printf("%s: %s (Score: %d)%n", 
                    docId, sentiment.getDescription(), sentiment.getScore()));
                    
        } catch (Exception e) {
            System.err.println("Error during sentiment analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
