package factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class WebDriverFactory {
    private static final Logger log = LoggerFactory.getLogger(WebDriverFactory.class);

    public enum Browser {
        CHROME,
        EDGE,
        SAFARI
    }


    private static final String HEADLESS_WINDOW_SIZE = "--window-size=1920,1080";

    /**
     * Creates a driver configured from the {@code browser} and {@code headless} properties.
     */
    public static WebDriver createDriver() {

        // `browser` and `headless` are JVM system properties, passed with -D on the Maven
        // command line (e.g. `mvn test -Dbrowser=edge -Dheadless=true`) or as VM options in
        // an IDE run configuration, and read directly here. They are unrelated to pom.xml's
        // `suiteXmlFile` property: that one exists because surefire's own plugin configuration
        // consumes it, whereas this code needs no pom.xml involvement at all.
        Browser browser = Browser.valueOf(System.getProperty("browser", "CHROME").toUpperCase());
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        return createDriver(browser, headless);
    }

    public static WebDriver createDriver(Browser browser, boolean headless) {

        log.info("Creating {} driver, headless={}", browser, headless);

        WebDriver driver = switch (browser) {
            case CHROME -> createChromeDriver(headless);
            case EDGE -> createEdgeDriver(headless);
            case SAFARI -> createSafariDriver(headless);
        };

        // A headless window has no screen to maximize into; its size comes from HEADLESS_WINDOW_SIZE.
        // Safari never runs headless here, so it is always maximized.
        if (!headless || browser == Browser.SAFARI) {
            driver.manage().window().maximize();
        }

        return driver;
    }

    public static WebDriver createRemoteDriver() {

        try {
            ChromeOptions options = new ChromeOptions();
            URL hubUrl = new URL("http://localhost:4444/wd/hub");
            return new RemoteWebDriver(hubUrl, options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to create remote WebDriver", e);
        }
    }

    private static WebDriver createChromeDriver(boolean headless) {

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new", HEADLESS_WINDOW_SIZE);
        }

        return new ChromeDriver(options);
    }


    private static WebDriver createEdgeDriver(boolean headless) {

        WebDriverManager.edgedriver().setup();

        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new", HEADLESS_WINDOW_SIZE);
        }

        return new EdgeDriver(options);
    }


    private static WebDriver createSafariDriver(boolean headless) {

        // SafariDriver встроен в macOS
        // отдельная загрузка драйвера не нужна

        // Safari is not Chromium-based: it has no --headless=new flag and no supported headless
        // mode, so the request is deliberately ignored rather than passed on as a flag.
        if (headless) {
            log.warn("Headless mode was requested but is not supported for SAFARI; starting a visible browser");
        }

        return new SafariDriver();
    }
}
