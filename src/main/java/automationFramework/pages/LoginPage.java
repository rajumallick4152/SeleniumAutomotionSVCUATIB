package automationFramework.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aventstack.extentreports.ExtentTest;

public class LoginPage {
	private static final Logger logger = LoggerFactory.getLogger(LoginPage.class);

	WebDriver driver;
	WebDriverWait wait;
	private int captchaWaitSeconds;

	public LoginPage(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		loadConfig();
	}

	private void loadConfig() {
		Properties prop = new Properties();
		String configFileName = "config.properties";
		try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
			if (input == null) {
				logger.error("❌ config.properties not found, using default wait time: 10s");
				captchaWaitSeconds = 10;
				return;
			}
			prop.load(input);
			captchaWaitSeconds = Integer.parseInt(prop.getProperty("captcha.wait.seconds", "10"));
			logger.info("✅ Captcha wait time loaded: {} seconds", captchaWaitSeconds);
		} catch (IOException | NumberFormatException ex) {
			logger.error("❌ Error loading config, using default captcha wait time: 10s. Error: {}", ex.getMessage());
			captchaWaitSeconds = 10;
		}
	}

	/**
	 * Modified performLogin method that returns true if login is successful, false
	 * otherwise.
	 */
	public boolean performLogin(String url, String username, String password, ExtentTest test) {
		try {
			driver.get(url);
			logger.info("🟦 Navigated to URL: {}", url);
			test.info("🟦 Navigated to URL: " + url);

			wait.until(ExpectedConditions.elementToBeClickable(By.id("userid"))).sendKeys(username);
			logger.info("✅ Entered username: {}", username);
			test.pass("✅ Entered username: " + username);

			logger.info("⏳ Waiting for manual captcha input ({} seconds)", captchaWaitSeconds);
			test.info("⏳ Waiting for manual captcha input (" + captchaWaitSeconds + " seconds)");
			Thread.sleep(captchaWaitSeconds * 1000L);

			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[text()='Login']]"))).click();
			logger.info("✅ Clicked first Login button");
			test.pass("✅ Clicked first Login button");

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='password']")))
					.sendKeys(password);
			logger.info("✅ Entered password");
			test.pass("✅ Entered password");

			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[text()='Login']]"))).click();
			logger.info("✅ Clicked final Login button");
			test.pass("✅ Clicked final Login button");
             // here new code will be added---
			// 🔄 New Step: Handle Residential Pincode Prompt (enter '1' and click Proceed)
			// 🔄 Generic Step: Always enter '1' if pincode input field is present
			try {
				By pincodeInputLocator = By.xpath("//input[@formcontrolname='frm']");
				By proceedButtonLocator = By.xpath("//span[text()='Proceed']/ancestor::button");

				// Check if the input is visible
				if (wait.until(ExpectedConditions.visibilityOfElementLocated(pincodeInputLocator)).isDisplayed()) {
					logger.info("🔐 Pin code input field detected.");
					test.info("🔐 Pin code input field detected.");

					wait.until(ExpectedConditions.elementToBeClickable(pincodeInputLocator)).sendKeys("1");
					logger.info("✅ Entered pin code: 1");
					test.pass("✅ Entered pin code: 1");

					wait.until(ExpectedConditions.elementToBeClickable(proceedButtonLocator)).click();
					logger.info("➡️ Clicked Proceed.");
					test.pass("➡️ Clicked Proceed.");
				}
			} catch (TimeoutException e) {
				logger.info("ℹ️ Pin code input not found, continuing without it.");
				test.info("ℹ️ Pin code input not found, continuing without it.");
			}

			By dashboardLocator = By.xpath("//span[@class='p-menuitem-text' and text()='Payment']");
			wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardLocator));

			logger.info("🎉 Login successful for user: {}", username);
			test.pass("🎉 Login successful for user: " + username);
			return true;
		} catch (Exception e) {
			logger.error("❌ Login failed: {}", e.getMessage());
			test.fail("❌ Login failed: " + e.getMessage());
			return false;
		}
	}

}
