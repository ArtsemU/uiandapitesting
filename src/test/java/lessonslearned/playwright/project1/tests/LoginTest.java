package lessonslearned.playwright.project1.tests;

import lessonslearned.playwright.project1.base.BaseTest;
import lessonslearned.playwright.project1.pages.HomePage;
import lessonslearned.playwright.project1.pages.LoginPage;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void loginVerifyDashboardTest() {
        // Scenario:
        // Go to login page
        // Enter username : Admin
        // Enter password : admin123
        // Click on login button
        // Verify Dashboard page is displayed
        LoginPage loginPage = new LoginPage(page);
        HomePage homePage = new HomePage(page);

        page.navigate("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
        loginPage.addUserName("Admin");
        loginPage.addPassword("admin123");
        loginPage.clickLoginButton();
        homePage.clickTimeLink();

    }
}
