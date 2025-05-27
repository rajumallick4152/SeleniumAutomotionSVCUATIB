package automationFramework;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.Scanner;

public class LoginTestCase extends BaseTest {

    public static void main(String[] args) throws Exception {
    	LoginTestCase test = new LoginTestCase();
        test.setup();

        try {
            String url = test.prop.getProperty("appURL");
            String username = test.prop.getProperty("username");
            String password = test.prop.getProperty("password");

            test.driver.get(url);

            WebElement userIdField = test.wait.until(ExpectedConditions.elementToBeClickable(By.id("userid")));
            userIdField.sendKeys(username);

            System.out.println("Please enter the captcha manually. You have 10 seconds...");
            Thread.sleep(10000);

            WebElement loginButton1 = test.wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Login']]")));
            loginButton1.click();

            WebElement passwordField = test.wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@type='password' and @placeholder='Enter Password']")));
            passwordField.sendKeys(password);

            WebElement loginButton2 = test.wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Login']]")));
            loginButton2.click();

            System.out.println("Login complete. Waiting for dashboard to load...");
            Thread.sleep(5000);

            System.out.println("Successfully Logged in SVC UAT Internet Banking.");

            // 🛑 Pause execution to keep browser open
            System.out.println("Press ENTER to close the browser...");
            new Scanner(System.in).nextLine();

        } finally {
            // ✅ Now close browser only after user input
            test.tearDown();
        }
    }
}
