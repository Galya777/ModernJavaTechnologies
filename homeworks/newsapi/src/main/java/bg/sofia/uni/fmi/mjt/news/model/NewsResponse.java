package bg.sofia.uni.fmi.mjt.news.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents the response from the News API.
 */
public class NewsResponse {
    private String status;
    private int totalResults;
    private List<Article> articles;
    private String code;
    private String message;

    public NewsResponse() {
        // For JSON deserialization
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<Article> getArticles() {
        return articles != null ? List.copyOf(articles) : List.of();
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles != null ? List.copyOf(articles) : List.of();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return "ok".equalsIgnoreCase(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsResponse that = (NewsResponse) o;
        return totalResults == that.totalResults &&
               Objects.equals(status, that.status) &&
               Objects.equals(articles, that.articles) &&
               Objects.equals(code, that.code) &&
               Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, totalResults, articles, code, message);
    }

    @Override
    public String toString() {
        return "NewsResponse{" +
                "status='" + status + '\'' +
                ", totalResults=" + totalResults +
                ", articles=" + articles.size() +
                '}';
    }
}
