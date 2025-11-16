package bg.sofia.uni.fmi.mjt.newsfeed.model;

import java.util.List;
import java.util.Objects;

public class NewsResponse {
    private final String status;
    private final int totalResults;
    private final List<Article> articles;
    private final String code;
    private final String message;

    public NewsResponse(String status, int totalResults, List<Article> articles, String code, String message) {
        this.status = status;
        this.totalResults = totalResults;
        this.articles = articles;
        this.code = code;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean hasError() {
        return "error".equals(status) || code != null || message != null;
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
               ", articles=" + articles +
               ", code='" + code + '\'' +
               ", message='" + message + '\'' +
               '}';
    }
}
