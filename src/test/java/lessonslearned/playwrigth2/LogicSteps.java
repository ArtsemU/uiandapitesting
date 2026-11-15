package lessonslearned.playwrigth2;

import com.microsoft.playwright.Page;

public class LogicSteps extends BaseSteps {

    private final LoginPage loginPage;

    public LogicSteps(Page page) {
        super(page);
        this.loginPage = new LoginPage(page);
    }

    public void logIn(String username, String password) {
        page.navigate(LOGIN_URL);
        loginPage.fillInUserName(username);
        loginPage.fillInPassword(password);
        loginPage.clickLoginButton();
    }

}
