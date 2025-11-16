package bg.sofia.uni.fmi.mjt.newsfeed.api;

import bg.sofia.uni.fmi.mjt.newsfeed.exception.NewsFeedException;
import bg.sofia.uni.fmi.mjt.newsfeed.model.NewsResponse;

import java.util.List;

/**
 * Client for interacting with the News API.
 */
public interface NewsApiClient {
    /**
     * Searches for news articles based on the given criteria.
     *
     * @param keywords  List of keywords to search for (required)
     * @param category  News category (optional)
     * @param country   Country code (optional)
     * @param pageSize  Number of results to return per page (1-100)
     * @param page      Page number to return
     * @return NewsResponse containing the search results
     * @throws NewsFeedException if there's an error communicating with the API
     * @throws IllegalArgumentException if required parameters are missing or invalid
     */
    NewsResponse searchNews(List<String> keywords, String category, String country, int pageSize, int page) 
        throws NewsFeedException;

    /**
     * Fetches the next page of results from a previous search.
     *
     * @param previousResponse The previous response to get the next page for
     * @return NewsResponse containing the next page of results
     * @throws NewsFeedException if there's an error communicating with the API
     * @throws IllegalStateException if there are no more results
     */
    NewsResponse getNextPage(NewsResponse previousResponse) throws NewsFeedException;
}
