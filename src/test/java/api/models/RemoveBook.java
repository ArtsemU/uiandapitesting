package api.models;

import java.util.Objects;

public final class RemoveBook {
    private final String isbn;
    private final String userId;

    private RemoveBook(Builder builder) {
        this.isbn = builder.isbn;
        this.userId = builder.userId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getUserId() {
        return userId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String isbn;
        private String userId;

        public Builder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public RemoveBook build() {
            return new RemoveBook(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoveBook that)) return false;
        return Objects.equals(isbn, that.isbn) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn, userId);
    }

    @Override
    public String toString() {
        return "RemoveBook{" +
                "isbn='" + isbn + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
