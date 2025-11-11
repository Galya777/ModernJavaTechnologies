package bg.sofia.uni.fmi.mjt.news;

import bg.sofia.uni.fmi.mjt.news.article.Article;  // Assuming Article is in a different package
import bg.sofia.uni.fmi.mjt.news.exception.NewsFeedException;

import java.util.List;

/**
 * Interface for fetching news articles from the News API.
 */
public interface NewsFeed {
    /**
     * Fetches news articles based on the provided query parameters.
     *
     * @param query the search query (required)
     * @param category the news category (optional, can be null)
     * @param country the country code (optional, can be null)
     * @param page the page number for pagination (starts from 1)
     * @return a list of articles matching the criteria
     * @throws IllegalArgumentException if query is null or empty, or if page is less than 1
     * @throws NewsFeedException if there is an error while fetching the news
     */
    List<Article> getNews(String query, String category, String country, int page) throws NewsFeedException;

    /**
     * Fetches all available news articles by automatically handling pagination.
     *
     * @param query the search query (required)
     * @param category the news category (optional, can be null)
     * @param country the country code (optional, can be null)
     * @param maxPages the maximum number of pages to fetch (must be positive)
     * @return a list of all articles matching the criteria across all pages
     * @throws IllegalArgumentException if query is null or empty, or if maxPages is less than 1
     * @throws NewsFeedException if there is an error while fetching the news
     */
    List<Article> getAllNews(String query, String category, String country, int maxPages) throws NewsFeedException;
}
