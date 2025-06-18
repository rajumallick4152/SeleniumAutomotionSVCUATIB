package automationFramework.pages;

import com.aventstack.extentreports.ExtentTest;

import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ServicesPage extends BasePage {

	private static final By SERVICES_TAB = By.xpath("//span[text()='Services']");
	private static final By MANAGE_SMS = By.xpath("//span[text()='Manage SMS']");

	private static final By CREDIT_AMOUNT_DIV = By
			.xpath("(//div[@class='w-full input-field' and @contenteditable='true'])[1]");
	private static final By DEBIT_AMOUNT_DIV = By
			.xpath("(//div[@class='w-full input-field' and @contenteditable='true'])[2]");

	private static final By SAVE_BUTTON = By.xpath("//span[text()='Save']");
	private static final By OTP_INPUT = By.xpath("//input[@formcontrolname='otp']");
	private static final By PROCEED_BUTTON = By.xpath("//span[text()='Proceed']");
	private static final By SUCCESS_TEXT = By.xpath("//span[text()='Success']");
	private static final By SAVED_SUCCESSFULLY_TEXT = By.xpath("//span[text()='Saved Successfully']");
	private static final By OKAY_BUTTON = By.xpath("//span[text()='Okay']");

	private ExtentTest test;

	public ServicesPage(WebDriver driver, ExtentTest test) {
		super(driver);
		this.test = test;
	}

	public void navigateToManageSMS() {
		logStep("🔄 Navigating to Services > Manage SMS...");
		click(SERVICES_TAB);
		click(MANAGE_SMS);
		logStep("✅ Reached Manage SMS section.");
	}

	public void enterAmountThresholds() {
		logStep("🔄 Generating random credit and debit amounts...");

		int creditAmount = new Random().nextInt(9901) + 100;
		int debitAmount = new Random().nextInt(9901) + 100;

		logStep("✏️ Entering SMS thresholds - Credit: " + creditAmount + ", Debit: " + debitAmount);

		WebElement credit = wait.until(ExpectedConditions.presenceOfElementLocated(CREDIT_AMOUNT_DIV));
		WebElement debit = wait.until(ExpectedConditions.presenceOfElementLocated(DEBIT_AMOUNT_DIV));

		setContentEditableValue(credit, String.valueOf(creditAmount));
		setContentEditableValue(debit, String.valueOf(debitAmount));

		logStep("✅ Amounts entered successfully with Credit: " + creditAmount + ", Debit: " + debitAmount);
	}

	public void clickSave() {
		logStep("💾 Clicking Save button...");
		click(SAVE_BUTTON);
	}

	public void enterOTP(String otp) {
		logStep("🔐 Entering OTP: " + otp);
		type(OTP_INPUT, otp);
		logStep("✅ OTP entered.");
	}

	public void clickProceed() {
		logStep("➡️ Clicking Proceed...");
		click(PROCEED_BUTTON);
	}

	public boolean isSuccessPopupVisible() {
		wait.until(ExpectedConditions.or(
			ExpectedConditions.visibilityOfElementLocated(SUCCESS_TEXT),
			ExpectedConditions.visibilityOfElementLocated(SAVED_SUCCESSFULLY_TEXT)
		));
		boolean visible = isDisplayed(SUCCESS_TEXT) && isDisplayed(SAVED_SUCCESSFULLY_TEXT);
		logStep("✅ Success popup visibility: " + visible);
		return visible;
	}


	public void clickOkay() {
		logStep("🆗 Clicking Okay on success popup...");
		click(OKAY_BUTTON);
	}

	private void setContentEditableValue(WebElement element, String value) {
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().perform();

		// Clear existing value
		((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].innerText = '';", element);

		// Set new value
		((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].innerText = arguments[1];",
				element, value);

		// Trigger input event
		((org.openqa.selenium.JavascriptExecutor) driver)
				.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", element);
	}

	private void logStep(String message) {
		logger.info(message);
		if (test != null) {
			test.info(message);
		}
	}
}
