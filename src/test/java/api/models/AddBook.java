package api.models;

import java.util.List;
import java.util.Objects;

public final class AddBook {
    private final String userId;
    private final List<Book> collectionOfIsbns;

    private AddBook(Builder builder) {
        this.userId = builder.userId;
        this.collectionOfIsbns = builder.collectionOfIsbns;
    }

    public String getUserId() {
        return userId;
    }

    public List<Book> getCollectionOfIsbns() {
        return collectionOfIsbns;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String userId;
        private List<Book> collectionOfIsbns;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder collectionOfIsbns(List<Book> collectionOfIsbns) {
            this.collectionOfIsbns = collectionOfIsbns;
            return this;
        }

        public AddBook build() {
            return new AddBook(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddBook addBook)) return false;
        return Objects.equals(userId, addBook.userId) &&
                Objects.equals(collectionOfIsbns, addBook.collectionOfIsbns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, collectionOfIsbns);
    }

    @Override
    public String toString() {
        return "AddBook{" +
                "userId='" + userId + '\'' +
                ", collectionOfIsbns=" + collectionOfIsbns +
                '}';
    }
}
