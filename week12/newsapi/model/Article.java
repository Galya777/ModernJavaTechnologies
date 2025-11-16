package bg.sofia.uni.fmi.mjt.newsfeed.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Article {
    private final Source source;
    private final String author;
    private final String title;
    private final String description;
    private final String url;
    private final String urlToImage;
    private final LocalDateTime publishedAt;
    private final String content;

    public Article(Source source, String author, String title, String description, 
                  String url, String urlToImage, LocalDateTime publishedAt, String content) {
        this.source = source;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.content = content;
    }

    public Source getSource() {
        return source;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(source, article.source) &&
               Objects.equals(author, article.author) &&
               Objects.equals(title, article.title) &&
               Objects.equals(description, article.description) &&
               Objects.equals(url, article.url) &&
               Objects.equals(urlToImage, article.urlToImage) &&
               Objects.equals(publishedAt, article.publishedAt) &&
               Objects.equals(content, article.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, author, title, description, url, urlToImage, publishedAt, content);
    }

    @Override
    public String toString() {
        return "Article{" +
               "source=" + source +
               ", author='" + author + '\'' +
               ", title='" + title + '\'' +
               ", description='" + description + '\'' +
               ", url='" + url + '\'' +
               ", urlToImage='" + urlToImage + '\'' +
               ", publishedAt=" + publishedAt +
               ", content='" + content + '\'' +
               '}';
    }
}
