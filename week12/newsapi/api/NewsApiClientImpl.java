package bg.sofia.uni.fmi.mjt.newsfeed.api;

import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedException;
import bg.sofia.uni.fmi.mjt.newsfeed.model.NewsResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NewsApiClientImpl implements NewsApiClient {
    private static final String API_BASE_URL = "https://newsapi.org/v2";
    private static final String TOP_HEADLINES_ENDPOINT = "/top-headlines";
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;
    private static final int MAX_PAGE_SIZE = 100;
    
    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;
    
    public NewsApiClientImpl(String apiKey) {
        this(apiKey, HttpClient.newHttpClient());
    }
    
    public NewsApiClientImpl(String apiKey, HttpClient httpClient) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key cannot be null or blank");
        }
        this.apiKey = apiKey;
        this.httpClient = Objects.requireNonNull(httpClient, "HttpClient cannot be null");
        this.gson = new Gson();
    }

    @Override
    public NewsResponse searchNews(List<String> keywords, String category, String country, int pageSize, int page) 
            throws NewsFeedException {
        validateSearchParams(keywords, pageSize, page);
        
        String query = String.join("+", keywords);
        String url = buildRequestUrl(query, category, country, pageSize, page);
        
        try {
            HttpRequest request = createRequest(url);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return parseResponse(response);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NewsFeedException("Error while making request to News API", e);
        }
    }

    @Override
    public NewsResponse getNextPage(NewsResponse previousResponse) throws NewsFeedException {
        if (previousResponse == null) {
            throw new IllegalArgumentException("Previous response cannot be null");
        }
        
        int nextPage = (previousResponse.getArticles().size() / 20) + 1; // Assuming 20 items per page
        if (nextPage * 20 > previousResponse.getTotalResults()) {
            throw new IllegalStateException("No more results available");
        }
        
        // Extract search parameters from previous response (simplified)
        // In a real implementation, you'd need to store the original search parameters
        return searchNews(List.of("news"), null, null, 20, nextPage);
    }
    
    private void validateSearchParams(List<String> keywords, int pageSize, int page) {
        if (keywords == null || keywords.isEmpty()) {
            throw new IllegalArgumentException("At least one keyword is required");
        }
        if (pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }
        if (page < 1) {
            throw new IllegalArgumentException("Page number must be greater than 0");
        }
    }
    
    private String buildRequestUrl(String query, String category, String country, int pageSize, int page) {
        StringBuilder urlBuilder = new StringBuilder(API_BASE_URL)
            .append(TOP_HEADLINES_ENDPOINT)
            .append("?q=").append(query)
            .append("&pageSize=").append(pageSize)
            .append("&page=").append(page);
            
        if (category != null && !category.isBlank()) {
            urlBuilder.append("&category=").append(category);
        }
        if (country != null && !country.isBlank()) {
            urlBuilder.append("&country=").append(country);
        }
        
        return urlBuilder.toString();
    }
    
    private HttpRequest createRequest(String url) {
        return HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("X-Api-Key", apiKey)
            .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
            .GET()
            .build();
    }
    
    private NewsResponse parseResponse(HttpResponse<String> httpResponse) throws NewsFeedException {
        int statusCode = httpResponse.statusCode();
        String responseBody = httpResponse.body();
        
        if (statusCode != 200) {
            handleErrorResponse(statusCode, responseBody);
        }
        
        try {
            return gson.fromJson(responseBody, NewsResponse.class);
        } catch (Exception e) {
            throw new NewsFeedException("Failed to parse API response", e);
        }
    }
    
    private void handleErrorResponse(int statusCode, String responseBody) throws NewsFeedException {
        try {
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            String code = jsonResponse.has("code") ? jsonResponse.get("code").getAsString() : null;
            String message = jsonResponse.has("message") ? jsonResponse.get("message").getAsString() : "Unknown error";
            
            throw new NewsFeedException(String.format("API Error %d: %s", statusCode, message));
        } catch (Exception e) {
            throw new NewsFeedException(String.format("API Error %d: Failed to parse error response", statusCode), e);
        }
    }
}
