package api.tests;

import api.AccountClient;
import api.BookstoreClient;
import api.models.AddBook;
import api.models.Book;
import api.models.RemoveBook;
import api.models.UserCredentials;
import io.restassured.response.Response;

import java.util.List;

public class ApiSteps {

    private AccountClient accountClient = new AccountClient();
    private BookstoreClient  bookstoreClient = new BookstoreClient();

    public Response createUserCall(UserCredentials uc) {
        return accountClient.createUser(uc);
    }

    public Response generateToken(UserCredentials uc) {
        return accountClient.generateToken(uc);
    }

    public Response getUserData(String userId, String token) {
        return accountClient.getUser(userId, token);
    }

    public Response removeUser(String userId, String token) {
        return accountClient.deleteUser(userId, token);
    }

    public Response getAllBooks() {
        return bookstoreClient.getAllBooks();
    }

    public Response addBookToUser(String userId, String isbn, String token) {

        AddBook addBook = AddBook.builder()
                .userId(userId)
                .collectionOfIsbns(List.of(new Book(isbn)))
                .build();

        return bookstoreClient.addBook(addBook, token);
    }

    public Response removeBook(String userId, String isbn, String token) {

        RemoveBook rmoveBook = RemoveBook.builder()
                .userId(userId)
                .isbn(isbn)
                .build();

        return bookstoreClient.removeBook(rmoveBook, token);
    }

    public Response removeAllBooksFromUser(String userId, String token) {
        return bookstoreClient.removeAllBooks(userId, token);
    }
}
