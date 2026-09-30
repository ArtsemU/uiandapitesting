package api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    private static final String CONFIG_FILE = "config/api.properties";
    private static final Properties properties = load();

    private Config() {
    }

    public static String baseUrl() {
        return getRequired("base.url");
    }

    public static String userPassword() {
        return getRequired("user.password");
    }

    public static boolean stubCatalogueWireMock() {
        return getRequiredBoolean("stub.catalogue.wiremock");
    }

    public static boolean stubCatalogueMockito() {
        return getRequiredBoolean("stub.catalogue.mockito");
    }

    private static Properties load() {
        Properties props = new Properties();

        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IllegalStateException("Config file not found on classpath: " + CONFIG_FILE);
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read config file: " + CONFIG_FILE, e);
        }
        log.info("Config loaded from {} — base.url: {}", CONFIG_FILE, props.getProperty("base.url"));
        return props;
    }

    // A JVM system property of the same name (-Dkey=value) overrides the file.
    // A blank system property counts as not set.
    private static String getRequired(String key) {
        String override = System.getProperty(key);
        String value = override == null || override.isBlank() ? properties.getProperty(key) : override;

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required key '" + key + "' in " + CONFIG_FILE);
        }

        return value;
    }

    // Strict on purpose: Boolean.parseBoolean would silently read a typo as false.
    private static boolean getRequiredBoolean(String key) {
        String value = getRequired(key).trim();

        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        if (value.equalsIgnoreCase("false")) {
            return false;
        }
        throw new IllegalStateException("Invalid value '" + value + "' for key '" + key + "': expected true or false");
    }
}
