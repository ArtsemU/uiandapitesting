package lessonslearned.playwrigth2;

import com.microsoft.playwright.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTests {

    private Playwright playwright;
    protected Page page;

    @BeforeMethod
    public void setUp() {
        playwright = Playwright.create();
        Browser browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false));
        BrowserContext context = browser.newContext();
        page = context.newPage();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        playwright.close();   // закрывает браузер, контекст и страницу
    }
}
