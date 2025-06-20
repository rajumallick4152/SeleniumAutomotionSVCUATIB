
package automationFramework.pages;

import automationFramework.utils.ScreenshotUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

public class LoginPage extends BasePage {
	private static final Logger logger = LoggerFactory.getLogger(LoginPage.class);

	WebDriver driver;
	WebDriverWait wait;
	private int captchaWaitSeconds;

	public LoginPage(WebDriver driver) {
		super(driver); // ✅ Needed for BasePage methods
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

			// 🛑 Check CBS failure popup
			if (handleloginFailureCBSPopupIfPresent()) {
				logger.error("❌ CBS login failure popup detected.");
				String screenshot = ScreenshotUtil.captureScreenshot(driver, "CBS_Login_Failure");
				test.fail("❌ CBS login failure popup detected.",
						MediaEntityBuilder.createScreenCaptureFromPath(screenshot).build());
				return false;
			}

			if (handleloginFailureCBSPopupIfPresent()) {
				String screenshot = ScreenshotUtil.captureScreenshot(driver, "CBS_Login_Error");
				test.fail("❌ CBS login failure popup detected.",
						MediaEntityBuilder.createScreenCaptureFromPath(screenshot).build());
				return false;
			}

			/*
			 * // 🔄 Handle Residential Pincode Prompt (enter '1' and click Proceed) try {
			 * By pincodeInputLocator = By.xpath("//input[@formcontrolname='frm']"); By
			 * proceedButtonLocator = By.xpath("//span[text()='Proceed']/ancestor::button");
			 * 
			 * WebElement pincodeInput =
			 * wait.until(ExpectedConditions.visibilityOfElementLocated(pincodeInputLocator)
			 * ); if (pincodeInput.isDisplayed()) {
			 * logger.info("🔐 Pin code input field detected.");
			 * test.info("🔐 Pin code input field detected.");
			 * 
			 * wait.until(ExpectedConditions.elementToBeClickable(pincodeInputLocator)).
			 * sendKeys("1"); logger.info("✅ Entered pin code: 1");
			 * test.pass("✅ Entered pin code: 1");
			 * 
			 * wait.until(ExpectedConditions.elementToBeClickable(proceedButtonLocator)).
			 * click(); logger.info("➡️ Clicked Proceed.");
			 * test.pass("➡️ Clicked Proceed."); } } catch (TimeoutException e) {
			 * logger.info("ℹ️ Pin code input not found, continuing without it.");
			 * test.info("ℹ️ Pin code input not found, continuing without it."); }
			 */

			// ✅ Wait for dashboard
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
