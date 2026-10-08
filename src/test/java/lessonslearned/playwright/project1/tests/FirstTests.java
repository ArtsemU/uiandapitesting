package lessonslearned.playwright.project1.tests;

import lessonslearned.playwright.project1.base.BaseTest;
import org.testng.annotations.Test;

public class FirstTests extends BaseTest {

    @Test
    public void verifyGoogleTitle(){
        page.navigate("https://www.google.com/ncr");
        if (page.isVisible("button:has-text('Accept All')")) {
            page.click("button:has-text('Accept All')");
        }
        System.out.println("Page title is: " + page.title());
    }
}
