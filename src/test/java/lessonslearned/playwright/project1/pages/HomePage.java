package lessonslearned.playwright.project1.pages;

import com.microsoft.playwright.Page;

public class HomePage {

    private final Page page;
    //	private final String timeLink = "getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(\"Time\"))";
    private final String timeLink = "oxd-text oxd-text--span oxd-main-menu-item--name";
    private final String timeLink2 = "a.oxd-main-menu-item[href='/web/index.php/time/viewTimeModule']";

    public HomePage(Page page) {
        this.page = page;
    }

    public void clickTimeLink () {
        //page.click(timeLink);
        page.locator(timeLink2).click();
    }
}
