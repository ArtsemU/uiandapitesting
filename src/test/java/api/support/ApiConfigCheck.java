package api.support;

import api.Config;
import org.testng.IExecutionListener;

/**
 * Fails the run at startup when -Denv names an environment without a config file.
 * Without this check, the first access to Config happens while TestNG instantiates a
 * test class, and TestNG reports it as "Cannot instantiate class", hiding the message.
 * Registered through META-INF/services/org.testng.ITestNGListener. Does nothing without
 * -Denv, so UI-only runs do not load the API config.
 */
public class ApiConfigCheck implements IExecutionListener {

    @Override
    public void onExecutionStart() {
        String env = System.getProperty("env");
        if (env != null && !env.isBlank()) {
            Config.baseUrl();
        }
    }
}
