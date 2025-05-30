package automationFramework.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class LoginPage {
	WebDriver driver;
	WebDriverWait wait;

	public LoginPage(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	}

	public void performLogin(String url, String username, String password) throws InterruptedException {
		driver.get(url);
		wait.until(ExpectedConditions.elementToBeClickable(By.id("userid"))).sendKeys(username);
		System.out.println("Waiting for manual captcha input, You have 30 Seconds...");
		Thread.sleep(10000);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[text()='Login']]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='password']")))
				.sendKeys(password);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[text()='Login']]"))).click();

		System.out.println("Login Test Completed successfully");
	}
}
