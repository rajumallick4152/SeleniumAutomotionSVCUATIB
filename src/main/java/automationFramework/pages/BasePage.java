package automationFramework.pages;

import org.openqa.selenium.*;
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
}
