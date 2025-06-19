package automationFramework.pages;

import org.openqa.selenium.*;
import automationFramework.utils.ConfigReader;

import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public abstract class BasePage {

	protected WebDriver driver;
	protected WebDriverWait wait;
	protected final Logger logger;

	private static final By SPINNER = By.className("ngx-spinner-overlay");

	public BasePage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		this.logger = LoggerFactory.getLogger(getClass());
	}

	// ✅ Enhanced Spinner Wait (Handles ng-animating class issue)
	protected void waitForSpinnerToFullyDisappear() {
		try {
			logger.info("⏳ Waiting for spinner to fully disappear...");

			wait.until(ExpectedConditions.invisibilityOfElementLocated(SPINNER));

			wait.until(driver -> {
				try {
					WebElement spinner = driver.findElement(SPINNER);
					String classAttr = spinner.getAttribute("class");
					return !classAttr.contains("ng-animating");
				} catch (NoSuchElementException e) {
					return true;
				}
			});

			Thread.sleep(300);
			logger.info("✅ Spinner disappeared.");
		} catch (Exception e) {
			logger.warn("⚠️ Spinner wait skipped or failed gracefully.");
		}
	}

	// ✅ Retry Click with Spinner Handling and Scroll
	protected void clickWithRetry(By locator) {
		for (int i = 1; i <= 3; i++) {
			try {
				waitForSpinnerToFullyDisappear();
				WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
				scrollIntoView(element);
				element.click();
				waitForSpinnerToFullyDisappear();
				return;
			} catch (ElementClickInterceptedException e) {
				logger.warn("⚠️ Click intercepted attempt {}/3: {}", i, e.getMessage());
			} catch (Exception e) {
				logger.warn("⚠️ Click failed attempt {}/3: {}", i, e.getMessage());
			}
			sleep(500);
		}
		throw new RuntimeException("❌ Click failed after 3 attempts: " + locator);
	}

// For checking server and technical error 
	public void detectAndLogServiceErrors() {
		// Config check first
		if (!ConfigReader.getBoolean("error.detection.enabled")) {
			logger.info("⚠️ Error detection is disabled via config.");
			return;
		}

		try {
			// 🔴 503 Error
			By error503 = By.xpath("//*[contains(text(),'503 The service is currently unavailable')]");
			if (!driver.findElements(error503).isEmpty()) {
				logger.error("❌ [503 Error] Service is currently unavailable.");
				throw new RuntimeException("503 Service Unavailable");
			}

			// 🔴 Generic technical issue
			By technicalIssue = By.xpath("//*[contains(text(),'We are currently facing some technical issue')]");
			if (!driver.findElements(technicalIssue).isEmpty()) {
				logger.error("❌ [Generic Technical Error] Facing some technical issue. Try again later.");
				throw new RuntimeException("Generic Technical Error");
			}

			// 🔴 CBS Error
			By errorCBS = By.xpath("//div[contains(text(), 'We are unable to retrieve a response from CBS')]");
			if (!driver.findElements(errorCBS).isEmpty()) {
				logger.error("❌ [CBS Error] Not Getting responses from CBS.");
				throw new RuntimeException("CBS Response Error");
			}

			// 🔴 Logged Out Error
			By loggedOut = By.xpath("//div[contains(text(),'You have been logged out')]");
			if (!driver.findElements(loggedOut).isEmpty()) {
				logger.error("❌ [Session Error] User has been logged out.");
				throw new RuntimeException("User session has expired or user is logged out.");
			}

			// ⚠️ Add more error locators here if needed

		} catch (Exception e) {
			logger.error("❌ Error Detection Triggered: {}", e.getMessage());
			throw e;
		}
	}

	// ✅ JavaScript Click
	protected void jsClick(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
	}

	// ✅ Scroll to Element
	protected void scrollIntoView(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
	}

	// ✅ Safe Direct Click on Element
	public void clickElement(WebElement element) {
		waitForSpinnerToFullyDisappear();
		wait.until(ExpectedConditions.elementToBeClickable(element)).click();
		waitForSpinnerToFullyDisappear();
	}

	// ✅ Utility: Safe Sleep
	protected void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignored) {
		}
	}

	// ✅ Helper: presenceOf (used in AccountsPage)
	protected ExpectedCondition<WebElement> presenceOf(By locator) {
		return ExpectedConditions.presenceOfElementLocated(locator);
	}

	// wait for second to load account statement for 6 months and 12 months
	public void waitForSeconds(int seconds) {
		try {
			Thread.sleep(seconds * 1000L);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

// wait for second to load account statement for 6 months and 12 months
	protected void waitForElementToBeVisible(By locator) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	protected void waitForElementToBeVisible(By locator, int seconds) {
		new WebDriverWait(driver, Duration.ofSeconds(seconds))
				.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	protected void waitForElementToBeClickable(By locator) {
		wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	protected boolean isElementPresent(By locator) {
		try {
			return driver.findElement(locator).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isElementVisible(By locator) {
		try {
			WebElement element = driver.findElement(locator);
			return element != null && element.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	// ✅ Click using By locator
	protected void click(By locator) {
		clickWithRetry(locator); // uses your robust retry logic
	}

	// ✅ Type into an input field(used in otp enter)
	protected void type(By locator, String text) {
		waitForSpinnerToFullyDisappear();
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		element.clear();
		element.sendKeys(text);
		waitForSpinnerToFullyDisappear();
	}

	// ✅ Find single element
	protected WebElement findElement(By locator) {
		return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
	}

	// ✅ Find multiple elements
	protected java.util.List<WebElement> findElements(By locator) {
		return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
	}

	// ✅ Check visibility
	protected boolean isDisplayed(By locator) {
		try {
			return driver.findElement(locator).isDisplayed();
		} catch (NoSuchElementException e) {
			return false;
		}
		
		
	}
}