package bg.sofia.uni.fmi.mjt.sentimentanalyzer;

import bg.sofia.uni.fmi.mjt.sentimentanalyzer.exceptions.SentimentAnalysisException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ParallelSentimentAnalyzer implements SentimentAnalyzerAPI {
    private final int workersCount;
    private final Set<String> stopWords;
    private final Map<String, SentimentScore> sentimentLexicon;
    private final BlockingQueue<AnalyzerTask> taskQueue;
    private final Map<String, SentimentScore> results;
    private ExecutorService executor;

    public ParallelSentimentAnalyzer(int workersCount, Set<String> stopWords, 
                                   Map<String, SentimentScore> sentimentLexicon) {
        this.workersCount = workersCount;
        this.stopWords = new HashSet<>(stopWords);
        this.sentimentLexicon = new HashMap<>(sentimentLexicon);
        this.taskQueue = new LinkedBlockingQueue<>();
        this.results = new ConcurrentHashMap<>();
    }

    @Override
    public Map<String, SentimentScore> analyze(AnalyzerInput... inputs) throws SentimentAnalysisException {
        if (inputs == null || inputs.length == 0) {
            return Map.of();
        }

        executor = Executors.newFixedThreadPool(workersCount + inputs.length);
        results.clear();

        // Start consumers
        for (int i = 0; i < workersCount; i++) {
            executor.submit(new Consumer());
        }

        // Start producers
        for (AnalyzerInput input : inputs) {
            executor.submit(new Producer(input));
        }

        // Shutdown executor when all tasks are done
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SentimentAnalysisException("Analysis was interrupted", e);
        }

        return new HashMap<>(results);
    }

    private static class AnalyzerTask {
        private final String id;
        private final String text;

        public AnalyzerTask(String id, String text) {
            this.id = id;
            this.text = text;
        }

        public String getId() {
            return id;
        }

        public String getText() {
            return text;
        }
    }

    private class Producer implements Runnable {
        private final AnalyzerInput input;

        public Producer(AnalyzerInput input) {
            this.input = input;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(input.inputReader())) {
                String line;
                while ((line = reader.readLine()) != null) {
                    taskQueue.put(new AnalyzerTask(input.inputID(), line));
                }
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Error reading input", e);
            }
        }
    }

    private class Consumer implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    AnalyzerTask task = taskQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (task == null) {
                        if (executor.isShutdown() && taskQueue.isEmpty()) {
                            break;
                        }
                        continue;
                    }

                    String processedText = processText(task.getText());
                    double score = calculateSentimentScore(processedText);
                    SentimentScore sentiment = SentimentScore.fromScore((int) Math.round(score));
                    
                    results.merge(task.getId(), sentiment, (oldVal, newVal) -> 
                        SentimentScore.fromScore(oldVal.getScore() + newVal.getScore()));
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        private String processText(String text) {
            // Convert to lowercase and remove punctuation
            String processed = text.toLowerCase()
                .replaceAll("[^a-zA-Z\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
            
            // Remove stop words
            StringBuilder result = new StringBuilder();
            for (String word : processed.split("\\s+")) {
                if (!stopWords.contains(word)) {
                    result.append(word).append(" ");
                }
            }
            
            return result.toString().trim();
        }

        private double calculateSentimentScore(String text) {
            if (text.isEmpty()) {
                return 0;
            }

            double totalScore = 0;
            int wordCount = 0;

            for (String word : text.split("\\s+")) {
                SentimentScore score = sentimentLexicon.get(word);
                if (score != null) {
                    totalScore += score.getScore();
                    wordCount++;
                }
            }

            return wordCount > 0 ? totalScore / wordCount : 0;
        }
    }
}
