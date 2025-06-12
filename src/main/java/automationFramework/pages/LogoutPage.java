/*
 * package automationFramework.pages;
 * 
 * import org.openqa.selenium.By; import org.openqa.selenium.WebDriver;
 * 
 * public class LogoutPage extends BasePage {
 * 
 * private static final By ARROW_BUTTON =
 * By.xpath("//img[@src='assets/images/ic_arrow_down.svg' and @alt='arrow']");
 * private static final By LOGOUT_BUTTON = By.xpath("//span[text()='Logout']");
 * private static final By OKAY_BUTTON = By.xpath("//span[text()='Okay']");
 * 
 * public LogoutPage(WebDriver driver) { super(driver); }
 * 
 * public void performLogout() { try {
 * logger.info("Starting logout process...");
 * 
 * // Click arrow dropdown using retry logic clickWithRetry(ARROW_BUTTON);
 * 
 * // Click Logout using retry logic clickWithRetry(LOGOUT_BUTTON);
 * 
 * //Click Okay Button to logged out clickWithRetry(OKAY_BUTTON);
 * 
 * logger.info("✅ Logout completed successfully");
 * 
 * } catch (Exception e) { logger.error("❌ Logout failed: {}", e.getMessage(),
 * e); } } }
 */

package automationFramework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class LogoutPage extends BasePage {

	private static final By ARROW_BUTTON = By.xpath("//img[@src='assets/images/ic_arrow_down.svg' and @alt='arrow']");
	private static final By LOGOUT_BUTTON = By.xpath("//span[text()='Logout']");
	private static final By ALL_OKAY_BUTTONS = By.xpath("//span[text()='Okay']");

	public LogoutPage(WebDriver driver) {
		super(driver);
	}

	public void performLogout() {
		try {
			logger.info("Starting logout process...");

			clickWithRetry(ARROW_BUTTON);
			clickWithRetry(LOGOUT_BUTTON);

			waitForSpinnerToFullyDisappear(); // Optional: in case spinner shows after logout

			List<WebElement> okayButtons = driver.findElements(ALL_OKAY_BUTTONS);
			for (WebElement btn : okayButtons) {
				if (btn.isDisplayed() && btn.isEnabled()) {
					logger.info("Clicking visible Okay button...");
					jsClick(btn);  // or clickWithRetry() if you want retry logic
					break;
				}
			}

			logger.info("✅ Logout completed successfully");

		} catch (Exception e) {
			logger.error("❌ Logout failed: {}", e.getMessage(), e);
		}
	}
}
