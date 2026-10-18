package api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * API configuration. -Denv=<name> selects config/api-<name>.properties, a complete,
 * standalone file; without -Denv, config/api.properties is used. A -D<key> system
 * property overrides the selected file.
 *
 * <p>Loaded lazily, not in a static initializer: a bad -Denv then surfaces as a plain
 * IllegalStateException ("unknown environment ...") on every access, instead of an
 * ExceptionInInitializerError followed by NoClassDefFoundErrors that hide the message.
 */
public final class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    private static final String ENV_PROPERTY = "env";
    private static final String DEFAULT_CONFIG_FILE = "config/api.properties";

    private static volatile Properties properties;
    private static volatile String configFile;

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

    private static Properties properties() {
        Properties loaded = properties;
        if (loaded == null) {
            synchronized (Config.class) {
                loaded = properties;
                if (loaded == null) {
                    loaded = load();
                    properties = loaded;
                }
            }
        }
        return loaded;
    }

    // Not cached on failure: every access rethrows the same clear message.
    private static Properties load() {
        String env = System.getProperty(ENV_PROPERTY);
        boolean envSet = env != null && !env.isBlank();
        String file = envSet ? "config/api-" + env.trim() + ".properties" : DEFAULT_CONFIG_FILE;

        Properties props = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(file)) {
            if (in == null) {
                throw new IllegalStateException(envSet
                        ? "unknown environment " + env.trim() + " (no " + file + " on the classpath)"
                        : "Config file not found on classpath: " + file);
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read config file: " + file, e);
        }
        configFile = file;
        log.info("API environment: {}, base.url: {}", envSet ? env.trim() : "default",
                valueOf(props, "base.url"));
        return props;
    }

    // A JVM system property of the same name (-Dkey=value) overrides the file.
    // A blank system property counts as not set.
    private static String valueOf(Properties props, String key) {
        String override = System.getProperty(key);
        return override == null || override.isBlank() ? props.getProperty(key) : override;
    }

    private static String getRequired(String key) {
        String value = valueOf(properties(), key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required key '" + key + "' in " + configFile);
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
