package factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverFactory {
    private static final Logger log = LoggerFactory.getLogger(WebDriverFactory.class);

    public enum Browser {
        CHROME,
        EDGE,
        SAFARI
    }


    public static WebDriver createDriver(Browser browser) {

        return switch (browser) {
            case CHROME -> createChromeDriver();
            case EDGE -> createEdgeDriver();
            case SAFARI -> createSafariDriver();
        };
    }


    private static WebDriver createChromeDriver() {

        WebDriverManager.chromedriver().setup();

        return new ChromeDriver();
    }


    private static WebDriver createEdgeDriver() {

        WebDriverManager.edgedriver().setup();

        return new EdgeDriver();
    }


    private static WebDriver createSafariDriver() {

        // SafariDriver встроен в macOS
        // отдельная загрузка драйвера не нужна

        return new SafariDriver();
    }
}
