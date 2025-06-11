package automationFramework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class AccountsPage extends BasePage {

	// Locators
	private static final By ACCOUNTS_TAB = By.xpath("//span[text()='Accounts']");
	private static final By VIEW_BALANCE_BUTTON = By.xpath("//span[contains(text(),'View Balance Components')]");
	private static final By CLOSE_BALANCE_MODAL = By
			.xpath("//span[text()='Close' and contains(@class, 'p-button-label')]");
	private static final By DETAILED_STATEMENT = By.xpath("//span[contains(text(),'Detailed Statement')]");
	private static final String STATEMENT_RANGE_BUTTON_XPATH = "//button[contains(text(),'%s')]";

	private static final By DOWNLOAD_BUTTON = By.xpath("//span[text()='Download']");
	private static final By DOWNLOAD_STATEMENT_BUTTON = By.xpath("//span[text()='Download statement']");
	private static final By PDF_OPTION = By.xpath("(//img[contains(@src,'tick')])[1]");
	private static final By XLS_OPTION = By.xpath("(//img[contains(@src,'tick')])[2]");

	public AccountsPage(WebDriver driver) {
		super(driver);
	}

	// Existing Methods
	public void clickAccountsTab() {
		logger.info("Clicking Accounts Tab");
		clickWithRetry(ACCOUNTS_TAB);
	}

	public void waitForDataToLoad() {
		// logger.info("Waiting for data to load...");
		waitForSpinnerToDisappear();
	}

	public void scrollToViewBalanceButton() {
		logger.info("Scrolling to 'View Balance Components' button");
		waitForSpinnerToDisappear();
		WebElement viewBalanceBtn = wait.until(ExpectedConditions.presenceOfElementLocated(VIEW_BALANCE_BUTTON));
		scrollIntoView(viewBalanceBtn);
		logger.info("Scrolled to button.");
	}

	public void clickViewBalanceButton() {
		logger.info("Clicking 'View Balance Components' button");
		waitForSpinnerToDisappear();
		WebElement viewBalanceBtn = wait.until(ExpectedConditions.elementToBeClickable(VIEW_BALANCE_BUTTON));
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
			WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(CLOSE_BALANCE_MODAL));
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

	// Newly Added Features

	public void clickDetailedStatement() {
		logger.info("Clicking Detailed Statement...");
		clickWithRetry(DETAILED_STATEMENT);
	}

	public void selectStatementRange(String range) {
		logger.info("Selecting range: " + range);
		clickWithRetry(By.xpath(String.format(STATEMENT_RANGE_BUTTON_XPATH, range)));
	}

	public void downloadStatement(String format) {
		logger.info("Initiating statement download in format: " + format);
		clickWithRetry(DOWNLOAD_BUTTON);

		if (format.equalsIgnoreCase("pdf")) {
			clickWithRetry(PDF_OPTION);
		} else if (format.equalsIgnoreCase("xls") || format.equalsIgnoreCase("excel")) {
			clickWithRetry(XLS_OPTION);
		} else {
			logger.warn("Invalid format specified: " + format);
			return;
		}

		clickWithRetry(DOWNLOAD_STATEMENT_BUTTON);
		waitForSpinnerToDisappear();
		logger.info("Download triggered.");
	}

}
