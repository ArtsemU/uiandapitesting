package bdd;

import api.models.Token;
import api.models.UserCredentials;
import api.steps.ApiSteps;
import io.cucumber.java.After;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ApiHooks {
    private static final Logger log = LoggerFactory.getLogger(ApiHooks.class);

    private final ScenarioContext context;
    private final ApiSteps apiSteps;

    public ApiHooks(ScenarioContext context, ApiSteps apiSteps) {
        this.context = context;
        this.apiSteps = apiSteps;
    }

    /**
     * Deletes users the scenario left behind. Users the scenario deleted itself
     * are removed from the context by the step that deleted them, so they never
     * reach this loop. A failed cleanup is logged and never fails the scenario.
     */
    @After("@api")
    public void deleteCreatedUsers() {
        for (Map.Entry<String, UserCredentials> entry : context.getCreatedUsers().entrySet()) {
            try {
                // A fresh token: the scenario may have failed before it obtained one.
                Response rsToken = apiSteps.generateToken(entry.getValue());
                if (rsToken.statusCode() != 200) {
                    log.warn("Cleanup of user {} could not get a token, status {}",
                            entry.getKey(), rsToken.statusCode());
                    continue;
                }
                Token token = rsToken.as(Token.class);
                Response rs = apiSteps.removeUser(entry.getKey(), token.getToken());
                if (rs.statusCode() != 204) {
                    log.warn("Cleanup of user {} returned status {}", entry.getKey(), rs.statusCode());
                }
            } catch (Exception e) {
                log.warn("Cleanup failed for user {}", entry.getKey(), e);
            }
        }
    }
}
