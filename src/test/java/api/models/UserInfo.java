package api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public final class UserInfo {
    private final String userId;
    private final String username;
    private final List<Book> books;

    @JsonCreator
    public UserInfo(
            @JsonProperty("userId") String userId,
            @JsonProperty("username") String username,
            @JsonProperty("books") List<Book> books) {
        this.userId = userId;
        this.username = username;
        this.books = books;
    }

    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public List<Book> getBooks() {
        return books;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInfo userInfo)) return false;
        return Objects.equals(userId, userInfo.userId) &&
                Objects.equals(username, userInfo.username) &&
                Objects.equals(books, userInfo.books);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, books);
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", books=" + books +
                '}';
    }
}
