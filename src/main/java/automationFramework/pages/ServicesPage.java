package automationFramework.pages;

import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.Random;

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
	private static final By ERROR_TEXT = By.xpath("//div[contains(@class, 'error-message')]");
	private static final By CREDIT_ERROR_MESSAGE = By
			.xpath("//small[contains(text(),'Valid Credited Amount is required')]");
	private static final By DEBIT_ERROR_MESSAGE = By
			.xpath("//small[contains(text(),'Valid Debited Amount is required')]");
	private static final By CLOSE_ERROR_POPUP = By.xpath("//img[@alt='close' and contains(@src, 'ic_closeCircle')]");

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

	public void enterInvalidAmountThresholds(boolean empty, boolean negative) {
		logStep("🔄 Setting invalid threshold values: empty=" + empty + ", negative=" + negative);
		WebElement credit = wait.until(ExpectedConditions.presenceOfElementLocated(CREDIT_AMOUNT_DIV));
		WebElement debit = wait.until(ExpectedConditions.presenceOfElementLocated(DEBIT_AMOUNT_DIV));
		String creditValue = empty ? "" : negative ? "-100" : "100";
		String debitValue = empty ? "" : negative ? "-200" : "200";
		setContentEditableValue(credit, creditValue);
		setContentEditableValue(debit, debitValue);
		logStep("✏️ Invalid thresholds set - Credit: " + creditValue + ", Debit: " + debitValue);
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
		wait.until(ExpectedConditions.or(ExpectedConditions.visibilityOfElementLocated(SUCCESS_TEXT),
				ExpectedConditions.visibilityOfElementLocated(SAVED_SUCCESSFULLY_TEXT)));
		boolean visible = isDisplayed(SUCCESS_TEXT) && isDisplayed(SAVED_SUCCESSFULLY_TEXT);
		logStep("✅ Success popup visibility: " + visible);
		return visible;
	}

	public boolean isErrorMessageVisible() {
		try {
			boolean visible = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_TEXT)).isDisplayed();
			logStep("⚠️ Error message visible: " + visible);
			return visible;
		} catch (Exception e) {
			logStep("⚠️ No error message found.");
			return false;
		}
	}

	public void clickOkay() {
		logStep("🆗 Clicking Okay on success popup...");
		click(OKAY_BUTTON);
	}

	private void setContentEditableValue(WebElement element, String value) {
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().perform();
		((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].innerText = '';", element);
		((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].innerText = arguments[1];",
				element, value);
		((org.openqa.selenium.JavascriptExecutor) driver)
				.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", element);
	}

	private void logStep(String message) {
		logger.info(message);
		if (test != null) {
			test.info(message);
		}
	}

	// Negative test scenarios
	public void testEmptyThresholds() {
		navigateToManageSMS();
		enterInvalidAmountThresholds(true, false);
		clickSave();

		boolean creditErrorVisible = wait.until(ExpectedConditions.visibilityOfElementLocated(CREDIT_ERROR_MESSAGE))
				.isDisplayed();
		boolean debitErrorVisible = wait.until(ExpectedConditions.visibilityOfElementLocated(DEBIT_ERROR_MESSAGE))
				.isDisplayed();

		if (!creditErrorVisible || !debitErrorVisible) {
			throw new AssertionError("❌ Expected validation messages not found for empty credit/debit.");
		}

		logStep("✅ Validation messages found for both empty thresholds.");

		click(CLOSE_ERROR_POPUP);
		logStep("❎ Closed the validation popup.");
	}

	public void testNegativeThresholds() {
		navigateToManageSMS();

		WebElement creditInput = driver.findElement(CREDIT_AMOUNT_DIV);
		WebElement debitInput = driver.findElement(DEBIT_AMOUNT_DIV);

		// Set negative values using JS
		setContentEditableValue(creditInput, "-100");
		setContentEditableValue(debitInput, "-200");

		// Get innerText instead of getAttribute("value") for contenteditable divs
		String creditValue = (String) ((org.openqa.selenium.JavascriptExecutor) driver)
				.executeScript("return arguments[0].innerText;", creditInput);
		String debitValue = (String) ((org.openqa.selenium.JavascriptExecutor) driver)
				.executeScript("return arguments[0].innerText;", debitInput);

		System.out.println("Credit Field Value: " + creditValue);
		System.out.println("Debit Field Value: " + debitValue);

		if ((creditValue != null && creditValue.contains("-")) || (debitValue != null && debitValue.contains("-"))) {
			throw new AssertionError("❌ Negative values still present in field.");
		}

		if ("100".equals(creditValue) && "200".equals(debitValue)) {
			logStep("✅ Negative input converted to positive as expected.");
		} else {
			throw new AssertionError("❌ Unexpected input behavior: " + creditValue + ", " + debitValue);
		}
	}

	public void testInvalidOTPFlow() {
		navigateToManageSMS();
		enterAmountThresholds();
		clickSave();
		enterOTP("000000");
		clickProceed();
		if (isSuccessPopupVisible()) {
			throw new AssertionError("❌ Success popup appeared with invalid OTP.");
		}
	}

	public void testProceedWithoutOTP() {
		navigateToManageSMS();
		enterAmountThresholds();
		clickSave();
		clickProceed();
		if (isSuccessPopupVisible()) {
			throw new AssertionError("❌ Proceeded without OTP.");
		}
	}

	public void testProceedWithoutSave() {
		navigateToManageSMS();
		enterAmountThresholds();
		enterOTP("123456");
		clickProceed();
		if (isSuccessPopupVisible()) {
			throw new AssertionError("❌ Proceeded without Save click.");
		}
	}
}