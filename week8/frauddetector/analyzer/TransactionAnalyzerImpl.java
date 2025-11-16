package bg.sofia.uni.fmi.mjt.frauddetector.analyzer;

import bg.sofia.uni.fmi.mjt.frauddetector.rule.Rule;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TransactionAnalyzerImpl implements TransactionAnalyzer {
    private final List<Transaction> transactions;
    private final Map<String, List<Transaction>> transactionsByAccount;
    private final List<Rule> rules;
    private static final double DELTA = 1e-10;

    public TransactionAnalyzerImpl(Reader reader, List<Rule> rules) {
        if (reader == null || rules == null) {
            throw new IllegalArgumentException("Reader and rules cannot be null");
        }

        double totalWeight = rules.stream()
            .mapToDouble(Rule::weight)
            .sum();
            
        if (Math.abs(totalWeight - 1.0) > DELTA) {
            throw new IllegalArgumentException("Sum of rule weights must be 1.0");
        }

        this.rules = List.copyOf(rules);
        this.transactions = new ArrayList<>();
        this.transactionsByAccount = new HashMap<>();
        
        loadTransactions(reader);
    }

    private void loadTransactions(Reader reader) {
        try (BufferedReader br = new BufferedReader(reader)) {
            // Skip header
            br.readLine();
            
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    Transaction transaction = Transaction.of(line);
                    transactions.add(transaction);
                    
                    transactionsByAccount
                        .computeIfAbsent(transaction.accountID(), k -> new ArrayList<>())
                        .add(transaction);
                } catch (IllegalArgumentException e) {
                    // Skip invalid transactions
                    System.err.println("Skipping invalid transaction: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading transactions", e);
        }
    }

    @Override
    public List<Transaction> allTransactions() {
        return List.copyOf(transactions);
    }

    @Override
    public List<String> allAccountIDs() {
        return transactions.stream()
            .map(Transaction::accountID)
            .distinct()
            .collect(Collectors.toList());
    }

    @Override
    public Map<Channel, Integer> transactionCountByChannel() {
        return transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::channel,
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));
    }

    @Override
    public double amountSpentByUser(String accountID) {
        if (accountID == null || accountID.isBlank()) {
            throw new IllegalArgumentException("Account ID cannot be null or blank");
        }
        
        return transactionsByAccount.getOrDefault(accountID, List.of()).stream()
            .mapToDouble(Transaction::transactionAmount)
            .sum();
    }

    @Override
    public List<Transaction> allTransactionsByUser(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("Account ID cannot be null or blank");
        }
        
        return List.copyOf(transactionsByAccount.getOrDefault(accountId, List.of()));
    }

    @Override
    public double accountRating(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("Account ID cannot be null or blank");
        }
        
        List<Transaction> userTransactions = transactionsByAccount.getOrDefault(accountId, List.of());
        if (userTransactions.isEmpty()) {
            return 0.0;
        }
        
        return rules.stream()
            .filter(rule -> rule.applicable(userTransactions))
            .mapToDouble(Rule::weight)
            .sum();
    }

    @Override
    public SortedMap<String, Double> accountsRisk() {
        SortedMap<String, Double> riskScores = new TreeMap<>();
        
        for (String accountId : transactionsByAccount.keySet()) {
            double riskScore = accountRating(accountId);
            riskScores.put(accountId, riskScore);
        }
        
        // Create a new map sorted by value in descending order
        SortedMap<String, Double> sortedByRisk = new TreeMap<>(
            (a, b) -> {
                int compare = Double.compare(riskScores.get(b), riskScores.get(a));
                return compare != 0 ? compare : a.compareTo(b);
            }
        );
        
        sortedByRisk.putAll(riskScores);
        return sortedByRisk;
    }
}
