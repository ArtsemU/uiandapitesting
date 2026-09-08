package api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class ErrorResponse {
    private final String code;
    private final String message;

    @JsonCreator
    public ErrorResponse(
            @JsonProperty("code") String code,
            @JsonProperty("message") String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ErrorResponse that)) return false;
        return Objects.equals(code, that.code) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
