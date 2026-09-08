package api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public final class CreateUserResponse {
    private final String userID;
    private final String username;
    private final List<Book> books;

    @JsonCreator
    public CreateUserResponse(
            @JsonProperty("userID") String userID,
            @JsonProperty("username") String username,
            @JsonProperty("books") List<Book> books) {
        this.userID = userID;
        this.username = username;
        this.books = books;
    }

    @JsonProperty("userID")
    public String getUserID() {
        return userID;
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
        if (!(o instanceof CreateUserResponse that)) return false;
        return Objects.equals(userID, that.userID) &&
                Objects.equals(username, that.username) &&
                Objects.equals(books, that.books);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, username, books);
    }

    @Override
    public String toString() {
        return "CreateUserResponse{" +
                "userID='" + userID + '\'' +
                ", username='" + username + '\'' +
                ", books=" + books +
                '}';
    }
}
