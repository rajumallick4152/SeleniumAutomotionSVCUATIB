
package automationFramework.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

// Correct Imports for SLF4J (which uses Logback in your setup)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // This is the factory for SLF4J

public class LoginPage {
	// Initialize a logger instance using SLF4J's LoggerFactory
	private static final Logger logger = LoggerFactory.getLogger(LoginPage.class);

	WebDriver driver;
	WebDriverWait wait;
	private int captchaWaitSeconds; // Field to store the wait time from config

	public LoginPage(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		loadConfig(); // Call method to load configuration on object creation
	}

	/**
	 * Loads configuration properties from config.properties file, specifically the
	 * captcha wait time.
	 */
	private void loadConfig() {
		Properties prop = new Properties();
		// Access config.properties from classpath (since it's in src/main/resources)
		String configFileName = "config.properties";
		try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
			if (input == null) {
				logger.error(
						"❌ [FAIL] Configuration file '{}' not found in classpath (src/main/resources). Using default captcha wait time: 10 seconds.",
						configFileName);
				captchaWaitSeconds = 10; // Default if file not found
				return;
			}
			prop.load(input);
			// Get the captcha wait time. Use "10" as a default if property is missing or
			// invalid.
			captchaWaitSeconds = Integer.parseInt(prop.getProperty("captcha.wait.seconds", "10"));
			logger.info("✅ [SUCCESS] Config loaded from '{}': captcha.wait.seconds = {} seconds.", configFileName,
					captchaWaitSeconds);
		} catch (IOException ex) {
			logger.error("❌ [FAIL] Error loading '{}' from classpath. Using default 10 seconds. Error: {}",
					configFileName, ex.getMessage());
			captchaWaitSeconds = 10; // Default value if loading fails
		} catch (NumberFormatException ex) {
			logger.error(
					"❌ [FAIL] Invalid number format for 'captcha.wait.seconds' in '{}'. Using default 10 seconds. Error: {}",
					configFileName, ex.getMessage());
			captchaWaitSeconds = 10; // Default value if parsing fails
		}
	}

	public void performLogin(String url, String username, String password) throws InterruptedException {
		driver.get(url);
		logger.info("🟦 [STEP] Navigated to URL: {}", url);

		wait.until(ExpectedConditions.elementToBeClickable(By.id("userid"))).sendKeys(username);
		logger.info("✅ [SUCCESS] Entered username: {}", username);

		logger.info("⏳ [WAIT] Waiting for manual captcha input. You have {} seconds.", captchaWaitSeconds);
		Thread.sleep(captchaWaitSeconds * 1000L); // Use the loaded value from config.properties

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[text()='Login']]"))).click();
		logger.info("✅ [SUCCESS] Clicked first Login button (after captcha).");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='password']")))
				.sendKeys(password);
		logger.info("✅ [SUCCESS] Entered password.");

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[text()='Login']]"))).click();
		logger.info("✅ [SUCCESS] Clicked final Login button.");

		//logger.info("🎉 [DONE] Login Test Completed successfully for user: {}.", username);
	}
}