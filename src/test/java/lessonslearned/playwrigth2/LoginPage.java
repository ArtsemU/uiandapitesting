package lessonslearned.playwrigth2;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class LoginPage extends BasePage{

    private final Locator usernameField = page.locator("xpath=//input[@name='username']");
    private final Locator passwordField = page.locator("xpath=//input[@name='password']");
    private final Locator loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login"));

    public LoginPage(Page page) {
        super(page);
    }

    public void fillInUserName(String userName) {
        usernameField.fill(userName);
    }

    public void fillInPassword(String password) {
        passwordField.fill(password);
    }

    public void clickLoginButton() {
        loginButton.click();
    }
}
