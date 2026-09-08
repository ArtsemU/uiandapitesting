package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Logs every API request and response through SLF4J at INFO. Masks the
 * Authorization header and any "password" field in a request or response body.
 */
public class ApiLoggingFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(ApiLoggingFilter.class);

    private static final String MASKED = "<masked>";
    private static final String BODY_INDENT = "        ";
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Pattern PASSWORD_FIELD =
            Pattern.compile("(\"password\"\\s*:\\s*)\"[^\"]*\"", Pattern.CASE_INSENSITIVE);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                            FilterableResponseSpecification responseSpec,
                            FilterContext ctx) {
        logRequest(requestSpec);
        Response response = ctx.next(requestSpec, responseSpec);
        logResponse(response);
        return response;
    }

    private void logRequest(FilterableRequestSpecification requestSpec) {
        StringBuilder sb = new StringBuilder();
        sb.append("--> ")
                .append(requestSpec.getMethod())
                .append(' ')
                .append(requestSpec.getURI());

        for (Header header : requestSpec.getHeaders()) {
            sb.append('\n').append("    ").append(header.getName()).append(": ").append(maskHeader(header));
        }

        String body = bodyAsString(requestSpec.getBody());
        if (body != null && !body.isEmpty()) {
            appendBody(sb, body);
        }

        log.info(sb.toString());
    }

    private void logResponse(Response response) {
        String body = response.getBody() == null ? "" : response.getBody().asString();

        StringBuilder sb = new StringBuilder();
        sb.append("<-- ")
                .append(response.getStatusCode())
                .append(" (")
                .append(response.getTimeIn(TimeUnit.MILLISECONDS))
                .append(" ms)");

        if (!body.isEmpty()) {
            appendBody(sb, body);
        }

        log.info(sb.toString());
    }

    private void appendBody(StringBuilder sb, String rawBody) {
        String formatted = maskPassword(prettyPrintJson(rawBody));
        sb.append('\n').append("    body:\n").append(indent(formatted));
    }

    private String maskHeader(Header header) {
        if (!"Authorization".equalsIgnoreCase(header.getName())) {
            return header.getValue();
        }

        String value = header.getValue() == null ? "" : header.getValue();
        String prefix = "Bearer ";
        String token = value.startsWith(prefix) ? value.substring(prefix.length()) : value;
        String tail = token.length() <= 4 ? token : token.substring(token.length() - 4);

        return prefix + "<masked, " + token.length() + " chars, ..." + tail + ">";
    }

    private String maskPassword(String body) {
        return PASSWORD_FIELD.matcher(body).replaceAll("$1\"" + MASKED + "\"");
    }

    private String prettyPrintJson(String body) {
        try {
            Object json = mapper.readValue(body, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            return body;
        }
    }

    private String indent(String text) {
        return text.lines().map(line -> BODY_INDENT + line).collect(Collectors.joining("\n"));
    }

    private String bodyAsString(Object body) {
        if (body == null) {
            return null;
        }
        if (body instanceof String) {
            return (String) body;
        }
        try {
            return mapper.writeValueAsString(body);
        } catch (Exception e) {
            return String.valueOf(body);
        }
    }
}
