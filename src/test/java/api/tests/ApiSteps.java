package api.tests;

import api.AccountClient;
import api.Config;
import api.models.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.restassured.response.Response;

public class ApiSteps {
    private static final Logger log = LoggerFactory.getLogger(ApiSteps.class);

    private AccountClient accountClient = new AccountClient();

    public Response createUserCall(UserCredentials uc) {
        return accountClient.createUser(uc);
    }

    public Response generateToken(UserCredentials uc) {
        return accountClient.generateToken(uc);
    }
}
