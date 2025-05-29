
package automationFramework;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
//import java.util.List;

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
			e.printStackTrace();
		}

		try {
			String url = this.prop.getProperty("appURL");
			String username = this.prop.getProperty("username");
			String password = this.prop.getProperty("password");

			this.driver.get(url);

			WebElement userIdField = this.wait.until(ExpectedConditions.elementToBeClickable(By.id("userid")));
			userIdField.sendKeys(username);

			System.out.println("Please enter the captcha manually. You have 30 seconds...");
			Thread.sleep(30000);

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
			Thread.sleep(5000);
			System.out.println("Successfully Logged in SVC UAT Internet Banking.");

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("ngx-spinner-overlay")));

			WebElement accountsTab = wait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Accounts']")));
			accountsTab.click();

			System.out.println("Accounts tab clicked. Waiting for data to load...");
			WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(60));
			wait1.until(ExpectedConditions.invisibilityOfElementLocated(By.className("ngx-spinner-overlay")));

			WebElement viewBalanceBtn = wait.until(ExpectedConditions
					.presenceOfElementLocated(By.xpath("//span[contains(text(),'View Balance Components')]")));

			((org.openqa.selenium.JavascriptExecutor) driver)
					.executeScript("arguments[0].scrollIntoView({block: 'center'});", viewBalanceBtn);

			try {
				wait.until(ExpectedConditions.elementToBeClickable(viewBalanceBtn)).click();
				System.out.println("Clicked on 'View Balance Components' using normal click.");
			} catch (org.openqa.selenium.ElementClickInterceptedException e) {
				System.out.println("Normal click failed. Trying JavaScript click...");
				((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();",
						viewBalanceBtn);
				System.out.println("Clicked on 'View Balance Components' using JS.");
			}

			// Click on "Close" button
			Thread.sleep(3000); // slight wait in case modal takes time to load
			try {
				WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
						By.xpath("//span[text()='Close' and contains(@class, 'p-button-label')]")));
				try {
					closeButton.click();
					System.out.println("Clicked on 'Close' button using normal click.");
				} catch (Exception clickEx) {
					System.out.println("Normal click on 'Close' failed. Trying JavaScript click...");
					((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();",
							closeButton);
					System.out.println("Clicked on 'Close' button using JavaScript.");
				}
			} catch (Exception ex) {
				System.out.println("Failed to locate or click the 'Close' button.");
				ex.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
