package bg.sofia.uni.fmi.mjt.newsfeed.api;

import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedException;
import bg.sofia.uni.fmi.mjt.newsfeed.model.NewsResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A builder class for constructing news search queries in a fluent manner.
 */
public class NewsQueryBuilder {
    private final NewsApiClient client;
    private final List<String> keywords;
    private String category;
    private String country;
    private int pageSize = 20;
    private int page = 1;

    /**
     * Creates a new NewsQueryBuilder with the specified client.
     *
     * @param client The NewsApiClient to use for executing the query
     * @throws IllegalArgumentException if client is null
     */
    public NewsQueryBuilder(NewsApiClient client) {
        this.client = Objects.requireNonNull(client, "NewsApiClient cannot be null");
        this.keywords = new ArrayList<>();
    }

    /**
     * Adds keywords to search for.
     *
     * @param keywords The keywords to search for
     * @return This builder instance for method chaining
     * @throws IllegalArgumentException if keywords is null or empty
     */
    public NewsQueryBuilder withKeywords(String... keywords) {
        if (keywords == null || keywords.length == 0) {
            throw new IllegalArgumentException("At least one keyword is required");
        }
        this.keywords.addAll(List.of(keywords));
        return this;
    }

    /**
     * Sets the news category to filter by.
     *
     * @param category The category to filter by (e.g., "business", "technology", "sports")
     * @return This builder instance for method chaining
     */
    public NewsQueryBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    /**
     * Sets the country to get news for.
     *
     * @param countryCode The ISO 3166-1 alpha-2 country code (e.g., "us", "gb", "bg")
     * @return This builder instance for method chaining
     */
    public NewsQueryBuilder withCountry(String countryCode) {
        this.country = countryCode;
        return this;
    }

    /**
     * Sets the number of results to return per page.
     *
     * @param pageSize Number of results (1-100)
     * @return This builder instance for method chaining
     * @throws IllegalArgumentException if pageSize is not between 1 and 100
     */
    public NewsQueryBuilder withPageSize(int pageSize) {
        if (pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Sets the page number to retrieve.
     *
     * @param page Page number (1-based)
     * @return This builder instance for method chaining
     * @throws IllegalArgumentException if page is less than 1
     */
    public NewsQueryBuilder withPage(int page) {
        if (page < 1) {
            throw new IllegalArgumentException("Page must be 1 or greater");
        }
        this.page = page;
        return this;
    }

    /**
     * Executes the search query with the current parameters.
     *
     * @return A NewsResponse containing the search results
     * @throws NewsFeedException if there's an error executing the query
     * @throws IllegalStateException if no keywords have been provided
     */
    public NewsResponse execute() throws NewsFeedException {
        if (keywords.isEmpty()) {
            throw new IllegalStateException("At least one keyword is required");
        }
        
        return client.searchNews(keywords, category, country, pageSize, page);
    }
}
