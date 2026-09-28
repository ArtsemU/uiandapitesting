package bdd;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UiHooks {
    private static final Logger log = LoggerFactory.getLogger(UiHooks.class);

    private final UiSession session;

    public UiHooks(UiSession session) {
        this.session = session;
    }

    @Before("@ui")
    public void startBrowser() {
        log.info("Starting browser");
        session.start();
    }

    // @After hooks run whether the scenario passed or failed, so the browser is always closed.
    @After("@ui")
    public void quitBrowser() {
        log.info("Closing browser");
        session.quit();
    }
}
