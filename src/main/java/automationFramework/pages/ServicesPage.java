
package automationFramework.pages;

import org.openqa.selenium.By;
//import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class ServicesPage {

	private final WebDriver driver;
	private final WebDriverWait wait;
	private static final Logger logger = LoggerFactory.getLogger(ServicesPage.class);

	public ServicesPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	private void waitForSpinnerToDisappear() {
		try {
			logger.info("Waiting for spinner to disappear...");
			Thread.sleep(10);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("ngx-spinner-overlay")));
			logger.info("Spinner disappeared.");
		} catch (Exception e) {
			logger.warn("Spinner did not disappear properly or wasn't found.");
		}
	}

	private void clickWithRetry(By locator) {
		for (int i = 0; i < 3; i++) {
			try {
				waitForSpinnerToDisappear();
				WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
				logger.info("Attempting to click element: {}", locator);
				element.click();
				logger.info("Click successful.");
				waitForSpinnerToDisappear();
				break;
			} catch (Exception e) {
				logger.warn("Click failed, retrying ({}/3): {}", (i + 1), e.getMessage());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignored) {
				}
			}
		}
	}

	public void clickServicesTab() {
		logger.info("Clicking Payment Tab");
		clickWithRetry(By.xpath("//span[@class='p-menuitem-text' and text()='Services']"));
	}

	public void waitForPaymentData() {
		waitForSpinnerToDisappear();
	}

	public void Demooclass1() {

	}

	public void Demooclass2() {

	}
}
