package automationFramework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AccountsPage extends BasePage {

	private static final By ACCOUNTS_TAB = By.xpath("//span[@class='p-menuitem-text' and text()='Accounts']");
	private static final By VIEW_BALANCE_BUTTON = By.xpath(
			"//span[@class='font-semibold text-blue-500 cursor-pointer text-small' and text()='View Balance Components']");
	private static final By CLOSE_BALANCE_MODAL = By
			.xpath("//span[text()='Close' and contains(@class, 'p-button-label')]");
	private static final By DETAILED_STATEMENT = By.xpath("//span[contains(text(),'Detailed Statement')]");
	private static final By DOWNLOAD_BUTTON = By
			.xpath("//span[contains(@class,'text-blue-500') and contains(text(),'Download')]");
	private static final By DOWNLOAD_STATEMENT_BUTTON = By.xpath("//span[text()='Download statement']");

	public AccountsPage(WebDriver driver) {
		super(driver);
	}

	public void clickAccountsTab() {
		logger.info("Clicking Accounts Tab");
		clickWithRetry(ACCOUNTS_TAB);
		waitForSpinnerToFullyDisappear();
		logger.info("✅ Accounts Tab Clicked");
	}

	public void waitForDataToLoad() {
		waitForSpinnerToFullyDisappear();
	}

	public void scrollToViewBalanceButton() {
		logger.info("Scrolling to 'View Balance Components' button");
		waitForSpinnerToFullyDisappear();
		WebElement viewBalanceBtn = wait.until(presenceOf(VIEW_BALANCE_BUTTON));
		scrollIntoView(viewBalanceBtn);
	}

	public void clickViewBalanceButton() {
		logger.info("Clicking 'View Balance Components' button");
		waitForSpinnerToFullyDisappear();
		clickWithRetry(VIEW_BALANCE_BUTTON);
		waitForSpinnerToFullyDisappear();
	}

	public void closeBalanceModal() {
		logger.info("Closing balance modal");
		waitForSpinnerToFullyDisappear();
		try {
			clickWithRetry(CLOSE_BALANCE_MODAL);
			logger.info("✅ Modal closed.");
		} catch (Exception ex) {
			logger.error("❌ Failed to close balance modal: {}", ex.getMessage());
		}
		waitForSpinnerToFullyDisappear();
	}

	public void downloadStatement(String duration) {
		try {
			logger.info("▶️ Starting: Download {} Statement in PDF format", duration);

			clickAccountsTab();

			clickWithRetry(DETAILED_STATEMENT);
			waitForSpinnerToFullyDisappear();
			logger.info("✅ Detailed Account Statement Button Clicked");

			if (!duration.equalsIgnoreCase("1 Month")) {
				By durationButton = By.xpath("//button[contains(text(),'" + duration + "')]");
				clickWithRetry(durationButton);
				waitForSpinnerToFullyDisappear();
				logger.info("✅ {} Button Clicked", duration);
			}

			clickWithRetry(DOWNLOAD_BUTTON);
			logger.info("✅ Download Button Clicked");

			clickWithRetry(DOWNLOAD_STATEMENT_BUTTON);
			logger.info("✅ Download Statement Button Clicked");

			logger.info("🎉 Statement download for {} (PDF) triggered successfully.", duration);

		} catch (Exception e) {
			logger.error("❌ Error during {} statement download: {}", duration, e.getMessage());
			throw new RuntimeException("Failed to download " + duration + " statement", e);
		}
	}
}
