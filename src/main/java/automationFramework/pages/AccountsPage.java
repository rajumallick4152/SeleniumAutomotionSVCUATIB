package automationFramework.pages;

import com.aventstack.extentreports.ExtentTest;
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

	public void clickAccountsTab(ExtentTest test) {
		logger.info("Clicking Accounts Tab");
		test.info("🔘 Clicking Accounts Tab");
		clickWithRetry(ACCOUNTS_TAB);
		waitForSpinnerToFullyDisappear();
		detectAndLogServiceErrors();
		logger.info("✅ Accounts Tab Clicked");
		test.pass("✅ Accounts Tab clicked successfully.");
	}

	public void waitForDataToLoad(ExtentTest test) {
		logger.info("Waiting for data to load...");
		test.info("⌛ Waiting for data to load...");
		waitForSpinnerToFullyDisappear();
		detectAndLogServiceErrors();
		logger.info("✅ Data load completed.");
		test.pass("✅ Data loaded successfully.");
	}

	public void scrollToViewBalanceButton(ExtentTest test) {
		logger.info("Scrolling to 'View Balance Components' button.");
		test.info("📜 Scrolling to 'View Balance Components' button");
		waitForSpinnerToFullyDisappear();
		detectAndLogServiceErrors();
		WebElement viewBalanceBtn = wait.until(presenceOf(VIEW_BALANCE_BUTTON));
		scrollIntoView(viewBalanceBtn);
		logger.info("✅ Scrolled to 'View Balance Components' button.");
		test.pass("✅ Scrolled to View Balance button.");
	}

	public void clickViewBalanceButton(ExtentTest test) {
		logger.info("Clicking 'View Balance Components' button.");
		test.info("🖱️ Clicking 'View Balance Components' button");
		waitForSpinnerToFullyDisappear();
		clickWithRetry(VIEW_BALANCE_BUTTON);
		waitForSpinnerToFullyDisappear();
		detectAndLogServiceErrors();
		logger.info("✅ View Balance modal opened.");
		test.pass("✅ View Balance modal opened.");
	}

	public void closeBalanceModal(ExtentTest test) {
		logger.info("Attempting to close balance modal.");
		test.info("🧩 Closing balance modal");
		waitForSpinnerToFullyDisappear();
		try {
			clickWithRetry(CLOSE_BALANCE_MODAL);
			logger.info("✅ Balance modal closed.");
			test.pass("✅ Modal closed.");
		} catch (Exception ex) {
			logger.error("❌ Failed to close balance modal: " + ex.getMessage());
			test.fail("❌ Failed to close balance modal: " + ex.getMessage());
		}
		waitForSpinnerToFullyDisappear();
		detectAndLogServiceErrors();
	}

	private void ensureDetailedStatementVisible() {
		try {
			logger.info("🔍 Checking if 'Detailed Statement' is visible.");
			waitForElementToBeVisible(DETAILED_STATEMENT, 3);
			logger.info("✅ 'Detailed Statement' is visible.");
		} catch (Exception e) {
			logger.warn("⚠️ 'Detailed Statement' not visible. Clicking Accounts Tab first.");
			clickWithRetry(ACCOUNTS_TAB);
			waitForElementToBeVisible(DETAILED_STATEMENT);
			logger.info("✅ 'Detailed Statement' found after clicking Accounts Tab.");
		}
	}

	public void downloadStatement(String duration, FileType fileType, ExtentTest test) {
		try {
			logger.info("Initiating statement download for: " + duration + " in format: " + fileType.name());
			test.info("▶️ Downloading " + duration + " statement in " + fileType.name());
			ensureDetailedStatementVisible();
			clickWithRetry(DETAILED_STATEMENT);
			waitForSpinnerToFullyDisappear();
			detectAndLogServiceErrors();

			if (!"1 Month".equalsIgnoreCase(duration)) {
				logger.info("Selecting duration button: " + duration);
				clickWithRetry(By.xpath("//button[contains(text(),'" + duration + "')]"));
				waitForSpinnerToFullyDisappear();
				detectAndLogServiceErrors();
			}

			clickWithRetry(DOWNLOAD_BUTTON);
			logger.info("Clicked Download button.");
			waitForSpinnerToFullyDisappear();

			if (fileType == FileType.XLS) {
				logger.info("Clicking XLS icon.");
				clickWithRetry(XLS_ICON);
				waitForSpinnerToFullyDisappear();
			}

			clickWithRetry(DOWNLOAD_STATEMENT_BUTTON);
			logger.info("Triggered statement download.");
			waitForSpinnerToFullyDisappear();

			test.pass("🎉 " + duration + " Statement (" + fileType.name() + ") download triggered.");

		} catch (Exception e) {
			logger.error("❌ Error downloading " + duration + " statement: " + e.getMessage());
			test.fail(
					"❌ Error during " + duration + " statement download (" + fileType.name() + "): " + e.getMessage());
			throw new RuntimeException("Failed to download " + duration + " statement in " + fileType.name(), e);
		}
	}

	public void downloadCustomStatement(String months, FileType fileType, ExtentTest test) {
		try {
			logger.info("Starting custom statement download for " + months + " months in format: " + fileType.name());
			test.info("▶️ Starting Custom Statement download for " + months + " months in " + fileType.name());

			ensureDetailedStatementVisible();
			clickWithRetry(DETAILED_STATEMENT);
			detectAndLogServiceErrors();
			waitForSpinnerToFullyDisappear();

			By customButton = By.xpath("//button[contains(text(),'Custom')]");
			By doneButton = By.xpath("//span[text()='Done' and contains(@class,'p-button-label')]");
			By noRecordsText = By.xpath("//span[contains(text(),'No records found')]");
			By okayButton = By.xpath("//span[@class='p-button-label ng-star-inserted' and text()='Okay']");
			String labelFor = months.equals("6") ? "6months" : "12months";
			By monthOption = By.xpath("//label[@for='" + labelFor + "']");

			boolean dataFound = false;

			for (int attempt = 1; attempt <= 3; attempt++) {
				logger.info("Attempt " + attempt + " for custom data fetch.");
				test.info("🔁 Attempt " + attempt + " to fetch custom statement data");

				clickWithRetry(customButton);
				detectAndLogServiceErrors();

				clickWithRetry(monthOption);
				detectAndLogServiceErrors();

				clickWithRetry(doneButton);
				detectAndLogServiceErrors();

				wait.until(driver -> isElementPresent(noRecordsText) || isElementVisible(DOWNLOAD_BUTTON));
				waitForSpinnerToFullyDisappear();
				detectAndLogServiceErrors();

				if (isElementPresent(noRecordsText)) {
					logger.warn("⚠️ No records found on attempt " + attempt);
					test.warning("⚠️ No records found on attempt " + attempt);

					if (attempt < 3) {
						logger.info("Clicking Okay to dismiss toast.");
						clickWithRetry(okayButton);
						waitForSpinnerToFullyDisappear();
						detectAndLogServiceErrors();
					} else {
						logger.error("❌ No data found after 3 attempts.");
						test.fail("❌ No data found after 3 attempts. Aborting download.");
						return;
					}
				} else {
					dataFound = true;
					break;
				}
			}

			if (!dataFound) {
				logger.warn("⚠️ No data found after all retries.");
				test.warning("⚠️ No records found after 3 retries. Exiting.");
				return;
			}

			clickWithRetry(DOWNLOAD_BUTTON);
			waitForSpinnerToFullyDisappear();

			if (fileType == FileType.XLS) {
				logger.info("Clicking XLS icon for custom statement.");
				clickWithRetry(XLS_ICON);
			}

			clickWithRetry(DOWNLOAD_STATEMENT_BUTTON);
			logger.info("✅ Custom statement download triggered.");
			test.pass("🎉 Custom statement download triggered for " + months + " months in " + fileType.name());

		} catch (Exception e) {
			logger.error("❌ Custom statement download failed: " + e.getMessage());
			test.fail("❌ Custom statement download failed (" + months + " months - " + fileType.name() + "): "
					+ e.getMessage());
			throw new RuntimeException("Custom statement download failed for " + months + " months", e);
		}
	}
}
