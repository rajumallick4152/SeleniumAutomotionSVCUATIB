package automationFramework.pages;

import automationFramework.utils.ConfigReader;
import automationFramework.utils.ScreenshotUtil;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;

public class LoginPage extends BasePage {

	private static final Logger logger = LoggerFactory.getLogger(LoginPage.class);
	private final WebDriver driver;
	private final WebDriverWait wait;

	private final int captchaWaitSeconds = ConfigReader.getInt("captcha.wait.seconds");
	private final int retryWaitSeconds = ConfigReader.getInt("retry.wait.seconds");

	private final String url = ConfigReader.getAppURL();
	private final String username = ConfigReader.get("username");
	private final String password = ConfigReader.get("password");

	public LoginPage(WebDriver driver) {
		super(driver);
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	public boolean performLogin(ExtentTest test) {
		int maxAttempts = 5;

		for (int attempt = 1; attempt <= maxAttempts; attempt++) {
			try {
				test.info("🔁 Attempt " + attempt + " to login");
				driver.get(url);
				test.info("🟦 Navigated to URL: " + url);

				if (isRejectedOr503Page(test, attempt)) {
					if (attempt < maxAttempts) {
						test.warning("⏳ Retrying after " + retryWaitSeconds + " seconds...");
						Thread.sleep(retryWaitSeconds * 1000L);
						continue;
					} else {
						test.fail("❌ All " + maxAttempts + " attempts failed due to 503/Rejected page.");
						return false;
					}
				}

				enterUsername(test);
				waitForCaptcha(test);
				clickFirstLoginButton(test);
				enterPassword(test);
				clickFinalLoginButton(test);

				if (checkForCBSLoginFailure(test)) {
					test.fail("❌ CBS Login failure detected.");
					return false;
				}

				waitForDashboard(test);
				return true;

			} catch (Exception e) {
				test.fail("❌ Attempt " + attempt + " failed: " + e.getMessage());
				logger.error("❌ Attempt {} failed", attempt, e);
				if (attempt == maxAttempts)
					return false;
			}
		}
		return false;
	}

	private boolean isRejectedOr503Page(ExtentTest test, int attempt) {
		String pageSource = driver.getPageSource().toLowerCase();

		if (pageSource.contains("error 503") || pageSource.contains("requested url was rejected")
				|| pageSource.contains("support id") || pageSource.contains("service unavailable")) {

			String base64 = ScreenshotUtil.captureScreenshotAndSaveBase64(driver, "503_or_rejected_attempt_" + attempt);
			if (base64 != null && !base64.isEmpty()) {
				test.fail("❌ Attempt " + attempt + ": 503 or Rejected error page detected.",
						MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
			} else {
				test.fail("❌ Attempt " + attempt + ": 503 or Rejected error page detected. (No screenshot)");
			}
			logger.error("❌ Attempt {}: Detected error page (503 or Rejected URL)", attempt);
			return true;
		}
		return false;
	}

	private void enterUsername(ExtentTest test) {
		type(By.id("userid"), username);
		logger.info("✅ Entered username: {}", username);
		test.pass("✅ Entered username: " + username);
	}

	private void waitForCaptcha(ExtentTest test) throws InterruptedException {
		logger.info("⏳ Waiting for manual captcha input ({} seconds)", captchaWaitSeconds);
		test.info("⏳ Waiting for manual captcha input (" + captchaWaitSeconds + " seconds)");
		sleep(captchaWaitSeconds * 1000L);
	}

	private void clickFirstLoginButton(ExtentTest test) {
		click(By.xpath("//button[.//span[text()='Login']]"));
		logger.info("✅ Clicked first Login button");
		test.pass("✅ Clicked first Login button");
	}

	private void enterPassword(ExtentTest test) {
		type(By.xpath("//input[@type='password']"), password);
		logger.info("✅ Entered password");
		test.pass("✅ Entered password");
	}

	private void clickFinalLoginButton(ExtentTest test) {
		click(By.xpath("//button[.//span[text()='Login']]"));
		logger.info("✅ Clicked final Login button");
		test.pass("✅ Clicked final Login button");
	}

	private boolean checkForCBSLoginFailure(ExtentTest test) {
		if (handleloginFailureCBSPopupIfPresent()) {
			String base64 = ScreenshotUtil.captureScreenshotAndSaveBase64(driver, "CBS_Login_Failure");
			test.fail("❌ CBS login failure popup detected.",
					MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
			logger.error("❌ CBS login failure popup detected");
			return true;
		}
		return false;
	}

	private void waitForDashboard(ExtentTest test) {
		By dashboardLocator = By.xpath("//span[@class='p-menuitem-text' and text()='Payment']");
		waitForElementToBeVisible(dashboardLocator);
		test.pass("🎉 Login successful for user: " + username);
		logger.info("🎉 Login successful for user: {}", username);
	}
}
