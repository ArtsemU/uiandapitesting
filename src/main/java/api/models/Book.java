package api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public final class Book {
    private final String isbn;
    private final String title;
    private final String subTitle;
    private final String author;
    private final String publishDate;
    private final String publisher;
    private final int pages;
    private final String description;
    private final String website;

    @JsonCreator
    public Book(
            @JsonProperty("isbn") String isbn,
            @JsonProperty("title") String title,
            @JsonProperty("subTitle") String subTitle,
            @JsonProperty("author") String author,
            @JsonProperty("publish_date") String publishDate,
            @JsonProperty("publisher") String publisher,
            @JsonProperty("pages") int pages,
            @JsonProperty("description") String description,
            @JsonProperty("website") String website) {
        this.isbn = isbn;
        this.title = title;
        this.subTitle = subTitle;
        this.author = author;
        this.publishDate = publishDate;
        this.publisher = publisher;
        this.pages = pages;
        this.description = description;
        this.website = website;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getAuthor() {
        return author;
    }

    @JsonProperty("publish_date")
    public String getPublishDate() {
        return publishDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getPages() {
        return pages;
    }

    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", author='" + author + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", publisher='" + publisher + '\'' +
                ", pages=" + pages +
                ", description='" + description + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
}
