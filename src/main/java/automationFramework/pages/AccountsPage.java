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
	private static final By XLS_ICON = By.xpath("//img[contains(@src,'ic_excel_file.svg') and @alt='excel']");

	public enum FileType {
		PDF, XLS
	}

	public AccountsPage(WebDriver driver) {
		super(driver);
	}

	public void clickAccountsTab() {
		logger.info("Clicking Accounts Tab");
		clickWithRetry(ACCOUNTS_TAB);
		waitForSpinnerToFullyDisappear();
		detectAndLogServiceErrors(); // ✅ Added
		logger.info("✅ Accounts Tab Clicked");
	}

	public void waitForDataToLoad() {
		waitForSpinnerToFullyDisappear();
		detectAndLogServiceErrors(); // ✅ Added
	}

	public void scrollToViewBalanceButton() {
		logger.info("Scrolling to 'View Balance Components' button");
		waitForSpinnerToFullyDisappear();
		detectAndLogServiceErrors(); // ✅ Added
		WebElement viewBalanceBtn = wait.until(presenceOf(VIEW_BALANCE_BUTTON));
		scrollIntoView(viewBalanceBtn);
	}

	public void clickViewBalanceButton() {
		logger.info("Clicking 'View Balance Components' button");
		waitForSpinnerToFullyDisappear();
		clickWithRetry(VIEW_BALANCE_BUTTON);
		waitForSpinnerToFullyDisappear();
		detectAndLogServiceErrors(); // ✅ Added
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
		detectAndLogServiceErrors(); // ✅ Added
	}

	public void downloadStatement(String duration, FileType fileType) {
		try {
			logger.info("▶️ Starting: Download {} Statement in {} format", duration, fileType.name());

			clickAccountsTab(); // already has error detection

			clickWithRetry(DETAILED_STATEMENT);
			waitForSpinnerToFullyDisappear();
			detectAndLogServiceErrors(); // ✅ Added
			logger.info("✅ Detailed Account Statement Button Clicked");

			if (!duration.equalsIgnoreCase("1 Month")) {
				By durationButton = By.xpath("//button[contains(text(),'" + duration + "')]");
				clickWithRetry(durationButton);
				waitForSpinnerToFullyDisappear();
				detectAndLogServiceErrors(); // ✅ Added
				logger.info("✅ {} Button Clicked", duration);
			}

			clickWithRetry(DOWNLOAD_BUTTON);
			waitForSpinnerToFullyDisappear();
			detectAndLogServiceErrors(); // ✅ Added
			logger.info("✅ Download Button Clicked");

			if (fileType == FileType.XLS) {
				clickWithRetry(XLS_ICON);
				waitForSpinnerToFullyDisappear();
				detectAndLogServiceErrors(); // ✅ Added
				logger.info("✅ XLS Format Selected");
			} else {
				logger.info("✅ Default Format (PDF) Selected");
			}

			clickWithRetry(DOWNLOAD_STATEMENT_BUTTON);
			waitForSpinnerToFullyDisappear();
			detectAndLogServiceErrors(); // ✅ Added
			logger.info("✅ Download Statement Button Clicked");

			logger.info("🎉 Statement download for {} ({} format) triggered successfully.", duration, fileType.name());

		} catch (Exception e) {
			logger.error("❌ Error during {} statement download ({}): {}", duration, fileType.name(), e.getMessage());
			throw new RuntimeException("Failed to download " + duration + " statement in " + fileType.name(), e);
		}
	}
}
