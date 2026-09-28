package bdd;

import api.models.Book;
import api.models.UserCredentials;
import io.restassured.response.Response;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * State shared between the steps of one scenario. PicoContainer creates a fresh
 * instance per scenario and injects it into every glue class that asks for it.
 */
public class ScenarioContext {

    private UserCredentials credentials;
    private String userId;
    private String token;
    private List<Book> catalogue;
    private Book selectedBook;
    // Response of the latest When step, checked by the Then step that follows it.
    private Response lastResponse;

    // Users still present on the server, deleted by the @After("@api") hook.
    private final Map<String, UserCredentials> createdUsers = new LinkedHashMap<>();

    public UserCredentials getCredentials() {
        return credentials;
    }

    public void setCredentials(UserCredentials credentials) {
        this.credentials = credentials;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Book> getCatalogue() {
        return catalogue;
    }

    public void setCatalogue(List<Book> catalogue) {
        this.catalogue = catalogue;
    }

    public Book getSelectedBook() {
        return selectedBook;
    }

    public void setSelectedBook(Book selectedBook) {
        this.selectedBook = selectedBook;
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(Response lastResponse) {
        this.lastResponse = lastResponse;
    }

    public void recordCreatedUser(String userId, UserCredentials credentials) {
        createdUsers.put(userId, credentials);
    }

    public void forgetCreatedUser(String userId) {
        createdUsers.remove(userId);
    }

    public Map<String, UserCredentials> getCreatedUsers() {
        return Collections.unmodifiableMap(createdUsers);
    }
}
