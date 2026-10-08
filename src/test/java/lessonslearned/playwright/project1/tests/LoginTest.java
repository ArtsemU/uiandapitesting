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
        test.info("Starting login test");
        page.navigate("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
        loginPage.addUserName("Admin");
        loginPage.addPassword("admin123");
        test.info("Clicking on login button");
        loginPage.clickLoginButton();
        homePage.clickTimeLink();

    }

    @Test
    public void test2() {
        LoginPage loginPage = new LoginPage(page);
        HomePage homePage = new HomePage(page);

        test.info("Simuldate error");
        page.navigate("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
        loginPage.addUserName("Admin");
        test.info("set wrong password");
        loginPage.addPassword("wrongpassword");
        test.info("Clicking on login button");
        loginPage.clickLoginButton();
        homePage.clickTimeLink();

    }
}
