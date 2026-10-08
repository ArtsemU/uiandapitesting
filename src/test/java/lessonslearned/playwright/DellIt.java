package lessonslearned.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class DellIt {
    public static void main(String[] args) {
        try (Playwright pw = Playwright.create();){
            Browser browser = pw.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();
            page.navigate("https://google.com");
            System.out.println(page.title());
            browser.close();
        }
    }
}
