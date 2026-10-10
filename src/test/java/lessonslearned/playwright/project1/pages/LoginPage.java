package lessonslearned.playwright.project1.pages;

import com.microsoft.playwright.Page;

public class LoginPage {

    private Page page;

    private final String userNameTextBox = "input[name='username']";
    private final String passwordTextBox = "input[name='password']";
    private final String loginButton = "button[type='submit']";

    public LoginPage(Page page) {
        this.page = page;
    }

    public void addUserName(String userName) {
        page.fill(userNameTextBox, userName);
    }

    public void addPassword(String password) {
        page.fill(passwordTextBox, password);
    }

    public void clickLoginButton() {
        page.click(loginButton);
    }

}
