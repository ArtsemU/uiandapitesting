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

    private static String getRequired(String key) {
        String value = properties.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required key '" + key + "' in " + CONFIG_FILE);
        }

        return value;
    }
}
