package api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class Token {
    private final String token;
    private final String expires;
    private final String status;
    private final String result;

    @JsonCreator
    public Token(
            @JsonProperty("token") String token,
            @JsonProperty("expires") String expires,
            @JsonProperty("status") String status,
            @JsonProperty("result") String result) {
        this.token = token;
        this.expires = expires;
        this.status = status;
        this.result = result;
    }

    public String getToken() {
        return token;
    }

    public String getExpires() {
        return expires;
    }

    public String getStatus() {
        return status;
    }

    public String getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token token1)) return false;
        return Objects.equals(token, token1.token) &&
                Objects.equals(expires, token1.expires) &&
                Objects.equals(status, token1.status) &&
                Objects.equals(result, token1.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, expires, status, result);
    }

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token + '\'' +
                ", expires='" + expires + '\'' +
                ", status='" + status + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
