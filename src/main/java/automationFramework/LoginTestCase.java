package automationFramework;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.Scanner;

public class LoginTestCase extends BaseTest {

	public WebDriver driver;

	public LoginTestCase(WebDriver driver) {
		this.driver = driver;
		if (this.driver != null) {
			this.driver.manage().window().maximize();
			this.driver.manage().deleteAllCookies();
			wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		}
		
	}

	public void executrTest() {

		try {
			this.setup();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			String url = this.prop.getProperty("appURL");
			String username = this.prop.getProperty("username");
			String password = this.prop.getProperty("password");

			this.driver.get(url);

			WebElement userIdField = this.wait.until(ExpectedConditions.elementToBeClickable(By.id("userid")));
			userIdField.sendKeys(username);

			System.out.println("Please enter the captcha manually. You have 10 seconds...");
			try {
				Thread.sleep(40000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			WebElement loginButton1 = this.wait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[text()='Login']]")));
			loginButton1.click();

			WebElement passwordField = this.wait.until(ExpectedConditions.visibilityOfElementLocated(
					By.xpath("//input[@type='password' and @placeholder='Enter Password']")));
			passwordField.sendKeys(password);

			WebElement loginButton2 = this.wait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[text()='Login']]")));
			loginButton2.click();

			System.out.println("Login complete. Waiting for dashboard to load...");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("Successfully Logged in SVC UAT Internet Banking.");

			// 🛑 Pause execution to keep browser open
			System.out.println("Press ENTER to close the browser...");
			new Scanner(System.in).nextLine();

		} finally {
			// ✅ Now close browser only after user input
			this.tearDown();
		}

	}

}
