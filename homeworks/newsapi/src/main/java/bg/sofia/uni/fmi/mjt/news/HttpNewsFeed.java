package bg.sofia.uni.fmi.mjt.news;

import bg.sofia.uni.fmi.mjt.news.exception.ApiKeyInvalidException;
import bg.sofia.uni.fmi.mjt.news.exception.NewsFeedException;
import bg.sofia.uni.fmi.mjt.news.exception.TooManyRequestsException;
import bg.sofia.uni.fmi.mjt.news.model.Article;
import bg.sofia.uni.fmi.mjt.news.model.NewsResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of NewsFeed that fetches news from the News API.
 */
public class HttpNewsFeed implements NewsFeed, AutoCloseable {
    private static final String API_BASE_URL = "https://newsapi.org/v2/top-headlines";
    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;
    
    private final HttpClient httpClient;
    private final String apiKey;
    private final Gson gson;
    
    /**
     * Creates a new HttpNewsFeed with the specified API key.
     *
     * @param apiKey the News API key (must not be null or empty)
     * @throws IllegalArgumentException if apiKey is null or empty
     */
    public HttpNewsFeed(String apiKey) {
        this(apiKey, HttpClient.newHttpClient());
    }
    
    /**
     * Creates a new HttpNewsFeed with the specified API key and HttpClient.
     * This constructor is mainly for testing purposes.
     *
     * @param apiKey the News API key (must not be null or empty)
     * @param httpClient the HttpClient to use for requests (must not be null)
     * @throws IllegalArgumentException if apiKey or httpClient is null, or if apiKey is empty
     */
    public HttpNewsFeed(String apiKey, HttpClient httpClient) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }
        this.apiKey = apiKey.trim();
        this.httpClient = Objects.requireNonNull(httpClient, "HttpClient cannot be null");
        
        // Configure Gson with custom LocalDateTime deserializer
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .create();
    }
    
    @Override
    public List<Article> getNews(String query, String category, String country, int page) throws NewsFeedException {
        validateQuery(query);
        if (page < 1) {
            throw new IllegalArgumentException("Page must be greater than 0");
        }
        
        String url = buildUrl(query, category, country, page, DEFAULT_PAGE_SIZE);
        NewsResponse response = makeRequest(url);
        
        return response.getArticles();
    }
    
    @Override
    public List<Article> getAllNews(String query, String category, String country, int maxPages) throws NewsFeedException {
        validateQuery(query);
        if (maxPages < 1) {
            throw new IllegalArgumentException("Max pages must be greater than 0");
        }
        
        List<Article> allArticles = new ArrayList<>();
        int currentPage = 1;
        int totalResults = Integer.MAX_VALUE;
        
        while (currentPage <= maxPages && allArticles.size() < totalResults) {
            String url = buildUrl(query, category, country, currentPage, DEFAULT_PAGE_SIZE);
            NewsResponse response = makeRequest(url);
            
            if (currentPage == 1) {
                totalResults = response.getTotalResults();
            }
            
            allArticles.addAll(response.getArticles());
            currentPage++;
            
            // If we've got fewer results than the page size, we've reached the end
            if (response.getArticles().size() < DEFAULT_PAGE_SIZE) {
                break;
            }
        }
        
        return allArticles;
    }
    
    private String buildUrl(String query, String category, String country, int page, int pageSize) {
        StringBuilder urlBuilder = new StringBuilder(API_BASE_URL)
                .append("?q=").append(urlEncode(query))
                .append("&pageSize=").append(pageSize)
                .append("&page=").append(page);
        
        if (category != null && !category.isBlank()) {
            urlBuilder.append("&category=").append(urlEncode(category));
        }
        
        if (country != null && !country.isBlank()) {
            urlBuilder.append("&country=").append(urlEncode(country));
        }
        
        urlBuilder.append("&apiKey=").append(apiKey);
        
        return urlBuilder.toString();
    }
    
    private NewsResponse makeRequest(String url) throws NewsFeedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .build();
        
        try {
            HttpResponse<String> response = httpClient.send(
                request, 
                HttpResponse.BodyHandlers.ofString()
            );
            
            int statusCode = response.statusCode();
            String responseBody = response.body();
            
            if (statusCode == 401) {
                throw new ApiKeyInvalidException("Invalid API key");
            } else if (statusCode == 429) {
                throw new TooManyRequestsException("API rate limit exceeded");
            } else if (statusCode < 200 || statusCode >= 300) {
                throw new NewsFeedException("Request failed with status: " + statusCode);
            }
            
            NewsResponse newsResponse = gson.fromJson(responseBody, NewsResponse.class);
            
            if (newsResponse == null) {
                throw new NewsFeedException("Failed to parse response");
            }
            
            if (!newsResponse.isSuccess()) {
                String message = newsResponse.getMessage() != null ? 
                    newsResponse.getMessage() : "Unknown error";
                throw new NewsFeedException("API error: " + message);
            }
            
            return newsResponse;
            
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NewsFeedException("Failed to fetch news", e);
        }
    }
    
    private void validateQuery(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Query cannot be null or empty");
        }
    }
    
    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
    
    @Override
    public void close() {
        // Cleanup resources if needed
    }
    
    /**
     * Custom deserializer for LocalDateTime to handle the ISO-8601 format from the API.
     */
    private static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        
        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, 
                                       JsonDeserializationContext context) 
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), FORMATTER);
        }
    }
}
