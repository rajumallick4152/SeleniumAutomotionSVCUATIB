package automationFramework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class AccountsPage extends BasePage {

	public AccountsPage(WebDriver driver) {
		super(driver);
	}

	public void clickAccountsTab() {
		logger.info("Clicking Accounts Tab");
		clickWithRetry(By.xpath("//span[text()='Accounts']"));
	}

	public void waitForDataToLoad() {
		logger.info("Waiting for data to load...");
		waitForSpinnerToDisappear();
	}

	public void scrollToViewBalanceButton() {
		logger.info("Scrolling to 'View Balance Components' button");
		waitForSpinnerToDisappear();
		WebElement viewBalanceBtn = wait.until(ExpectedConditions
				.presenceOfElementLocated(By.xpath("//span[contains(text(),'View Balance Components')]")));
		scrollIntoView(viewBalanceBtn);
		logger.info("Scrolled to button.");
	}

	public void clickViewBalanceButton() {
		logger.info("Clicking 'View Balance Components' button");
		waitForSpinnerToDisappear();
		WebElement viewBalanceBtn = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//span[contains(text(),'View Balance Components')]")));
		try {
			viewBalanceBtn.click();
		} catch (Exception e) {
			logger.warn("Standard click failed. Trying JS click.");
			jsClick(viewBalanceBtn);
		}
		waitForSpinnerToDisappear();
	}

	public void closeBalanceModal() {
		logger.info("Closing balance modal");
		waitForSpinnerToDisappear();
		try {
			WebElement closeBtn = wait.until(ExpectedConditions
					.elementToBeClickable(By.xpath("//span[text()='Close' and contains(@class, 'p-button-label')]")));
			try {
				closeBtn.click();
				logger.info("Modal closed.");
			} catch (Exception e) {
				logger.warn("Standard close failed. Using JS click.");
				jsClick(closeBtn);
			}
		} catch (Exception ex) {
			logger.error("Failed to close balance modal: {}", ex.getMessage());
		}
		waitForSpinnerToDisappear();
	}
}