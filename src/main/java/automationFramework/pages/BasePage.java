package automationFramework.pages;

import org.openqa.selenium.*;
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

	protected void waitForSpinnerToDisappear() {
		try {
			logger.info("Waiting for spinner to disappear...");
			wait.until(ExpectedConditions.invisibilityOfElementLocated(SPINNER));
			logger.info("Spinner disappeared.");
		} catch (Exception e) {
			logger.warn("Spinner not found or already gone.");
		}
	}

	protected void clickWithRetry(By locator) {
		for (int i = 1; i <= 3; i++) {
			try {
				waitForSpinnerToDisappear();
				WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
				scrollIntoView(element);
				logger.info("Clicking element: {}", locator);
				element.click();
				waitForSpinnerToDisappear();
				return;
			} catch (ElementClickInterceptedException e) {
				logger.warn("Click intercepted attempt {}/3: {}", i, e.getMessage());
				if (i == 3) {
					throw e;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignored) {
				}
			} catch (Exception e) {
				logger.warn("Click failed attempt {}/3: {}", i, e.getMessage());
				if (i == 3) {
					throw new RuntimeException("Click failed after 3 attempts", e);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignored) {
				}
			}
		}
	}

	protected void jsClick(WebElement element) {
		logger.info("Clicking with JS");
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
	}

	protected void scrollIntoView(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
	}
}