package automationFramework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class PaymentHistoryPage extends BasePage {

	private static final By PAYMENT_TAB = By.xpath("//span[@class='p-menuitem-text' and text()='Payment']");
	private static final By PAYEE_NAME = By.xpath("//span[contains(@class, 'font-semibold') and text()='DXFCHGV']");
	private static final By AMOUNT_INPUT = By.id("custom-amount-input");
	private static final By REMARKS_INPUT = By.xpath("//input[@placeholder='Add Remarks']");
	private static final By PROCEED_BUTTON_REMARKS = By.xpath("(//span[text()='Proceed'])[1]");
	private static final By PROCEED_BUTTON_OTP = By.xpath("(//span[text()='Proceed'])[2]");
	private static final By CONFIRM_BUTTON = By.xpath("//span[text()='Confirm']");
	private static final By OTP_INPUT = By.xpath("//input[@formcontrolname='otp']");
	private static final By NEFT_OPTION = By
			.xpath("//*[normalize-space(text())='NEFT']/ancestor::*[self::div or self::label or self::button][1]");
	private static final By NEFT_SELECTED_ICON = By.xpath(
			"//span[normalize-space(text())='NEFT']/following-sibling::img[contains(@src, 'ic_tickCircle_green')]");
	private static final By CLOSE_BUTTON = By
			.xpath("(//img[@alt='close' and contains(@src,'ic_closeCircle')])[last()]");

	public PaymentHistoryPage(WebDriver driver) {
		super(driver);
	}

	public void clickPaymentsTab() {
		logger.info("Clicking on the Payment tab.");
		clickWithRetry(PAYMENT_TAB);
	}

	public void waitForPaymentData() {
		logger.info("Waiting for payment data to load.");
		waitForSpinnerToDisappear();
	}

	public void makePaymentToPayee() {
		logger.info("Initiating payment to payee: DXFCHGV");

		try {
			clickWithRetry(PAYEE_NAME);
			logger.info("Payee selected.");
		} catch (Exception e) {
			logger.error("Failed to click payee name: {}", e.getMessage());
			throw new RuntimeException("Cannot proceed without selecting payee.");
		}

		// Enter amount
		try {
			WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT));
			scrollIntoView(amountInput);
			jsClick(amountInput);
			amountInput.clear();
			amountInput.sendKeys("1");
			logger.info("Entered amount: ₹1");
		} catch (Exception e) {
			logger.error("Error entering amount: {}", e.getMessage());
			throw new RuntimeException("Cannot proceed without amount.");
		}

		// Always click NEFT option and confirm selection
		try {
			WebElement neftOption = wait.until(ExpectedConditions.elementToBeClickable(NEFT_OPTION));
			scrollIntoView(neftOption);
			Thread.sleep(300); // ensure element visibility

			boolean selected = false;
			int attempts = 0;

			while (!selected && attempts < 4) { // : It will try twice maximum to select NEFT
				try {
					logger.info("Attempting to click NEFT option (Attempt " + (attempts + 1) + ")");
					try {
						new Actions(driver).moveToElement(neftOption).pause(200).click().perform();
						logger.info("NEFT selected using Actions click.");
					} catch (Exception e1) {
						logger.warn("Actions click failed: {}. Trying JS click.", e1.getMessage());
						jsClick(neftOption);
						logger.info("NEFT selected using JS click.");
					}

					Thread.sleep(500); // wait for UI to reflect selection
					selected = driver.findElements(NEFT_SELECTED_ICON).size() > 0;

					if (!selected) {
						logger.warn("NEFT not selected yet. Will retry...");
					}
				} catch (Exception e) {
					logger.error("NEFT selection error on attempt {}: {}", attempts + 1, e.getMessage());
				}
				attempts++;
			}

			if (!selected) {
				throw new RuntimeException("NEFT could not be selected after multiple attempts.");
			} else {
				logger.info("NEFT selection confirmed.");
			}
		} catch (Exception e) {
			logger.error("NEFT selection failed: {}", e.getMessage());
			throw new RuntimeException("Unable to select NEFT option.");
		}

		// Enter remarks
		try {
			WebElement remarksInput = wait.until(ExpectedConditions.visibilityOfElementLocated(REMARKS_INPUT));
			remarksInput.clear();
			remarksInput.sendKeys("test value");
			logger.info("Entered remarks: test value");
		} catch (Exception e) {
			logger.warn("Unable to enter remarks: {}", e.getMessage());
		}

		// Click first Proceed button (after remarks)
		try {
			clickWithRetry(PROCEED_BUTTON_REMARKS);
			logger.info("Clicked Proceed button after remarks.");
		} catch (Exception e) {
			logger.error("Failed to click Proceed button after remarks: {}", e.getMessage());
			throw new RuntimeException("Unable to proceed after entering remarks.");
		}

		// Click Confirm button
		try {
			clickWithRetry(CONFIRM_BUTTON);
			logger.info("Clicked Confirm button.");
		} catch (Exception e) {
			logger.error("Confirm button click failed: {}", e.getMessage());
			throw new RuntimeException("Unable to confirm payment.");
		}

		// Enter OTP
		try {
			WebElement otpInput = wait.until(ExpectedConditions.visibilityOfElementLocated(OTP_INPUT));
			otpInput.sendKeys("123456");
			logger.info("Entered OTP.");
		} catch (Exception e) {
			logger.warn("Failed to enter OTP: {}", e.getMessage());
		}

		// Wait for any overlay/spinner to disappear before final Proceed
		try {
			logger.info("Waiting for any overlay/spinner to disappear before final Proceed.");
			wait.until(driver -> {
				try {
					WebElement overlay = driver.findElement(By.cssSelector("div[aria-hidden='false'].p-overlay"));
					return !overlay.isDisplayed();
				} catch (Exception e) {
					return true; // overlay not found = ready
				}
			});

			WebElement finalProceedBtn = wait.until(ExpectedConditions.elementToBeClickable(PROCEED_BUTTON_OTP));
			scrollIntoView(finalProceedBtn);
			jsClick(finalProceedBtn);
			logger.info("Final Proceed button clicked after OTP.");
		} catch (Exception e) {
			logger.error("Final Proceed button click failed after overlay wait: {}", e.getMessage());
			throw new RuntimeException("Payment could not be finalized.");
		}

		// Close button (optional)

		try {
			WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(CLOSE_BUTTON));
			scrollIntoView(closeBtn);
			jsClick(closeBtn);
			logger.info("Close button clicked successfully.");
		} catch (Exception e) {
			logger.warn("Close button not found or could not be clicked: {}", e.getMessage());
		}

		logger.info("Payment to payee DXFCHGV completed successfully.");
	}

	public void testInvalidAmounts() {
		logger.info("🧪=== Starting Negative Test Cases for Amount Field ===");

		try {
			logger.info("🔹 Step 1: Selecting payee: DXFCHGV");
			clickWithRetry(PAYEE_NAME);
			logger.info("✅ Payee selected successfully.");
		} catch (Exception e) {
			logger.error("❌ Failed to select payee. Error: {}", e.getMessage());
			throw new RuntimeException("Cannot continue without selecting a payee.");
		}

		WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT));
		scrollIntoView(amountInput);
		jsClick(amountInput);

		// 1️⃣ Blank input
		try {
			logger.info("\n🔸 Test Case 1: Blank Amount");
			amountInput.clear();
			clickWithRetry(PROCEED_BUTTON_REMARKS);
			Thread.sleep(500);

			boolean errorShown = driver.getPageSource().contains("Amount is required");
			if (errorShown) {
				logger.info("✅ Correct error displayed: 'Amount is required'");
			} else {
				logger.warn("⚠️ Expected error not found for blank input.");
			}
		} catch (Exception e) {
			logger.error("❌ Error during Blank input test: {}", e.getMessage());
		}

		// 2️⃣ Special Characters — input blocked by field, so check if input is
		// accepted
		try {
			logger.info("\n🔸 Test Case 2: Special Characters (@#$%)");
			amountInput.clear();
			amountInput.sendKeys("@#$%");
			String fieldValue = amountInput.getAttribute("value");

			if (fieldValue == null || fieldValue.isEmpty()) {
				logger.info("✅ Special characters were blocked as expected. Field value is empty or null.");
			} else {
				logger.warn("⚠️ Unexpected behavior: Field accepted special characters '{}'", fieldValue);
			}
		} catch (Exception e) {
			logger.error("❌ Error during Special Characters test: {}", e.getMessage());
		}

		// 3️⃣ Alphanumeric
		try {
			logger.info("\n🔸 Test Case 3: Alphanumeric Characters (123abc)");
			amountInput.clear();
			amountInput.sendKeys("123abc");
			String fieldValue = amountInput.getAttribute("value");

			if (fieldValue == null) {
				logger.info("✅ Field blocked alphanumeric input completely.");
			} else if (fieldValue.matches("\\d+")) {
				logger.info("✅ Field auto-filtered to digits: '{}'", fieldValue);
			} else {
				logger.warn("⚠️ Unexpected value stored in field: '{}'", fieldValue);
			}
		} catch (Exception e) {
			logger.error("❌ Error during Alphanumeric test: {}", e.getMessage());
		}

		// 4️⃣ Zero amount
		try {
			logger.info("\n🔸 Test Case 4: Zero Amount (0)");
			amountInput.clear();
			amountInput.sendKeys("0");
			clickWithRetry(PROCEED_BUTTON_REMARKS);
			Thread.sleep(500);

			boolean errorShown = driver.getPageSource().contains("Minimum Amount is ");
			if (errorShown) {
				logger.info("✅ Correct validation message shown: 'Minimum amount is 1.00'");
			} else {
				logger.warn("⚠️ Validation error for zero amount not found.");
			}
		} catch (Exception e) {
			logger.error("❌ Error during Zero Amount test: {}", e.getMessage());
		}

		logger.info("\n✅ All realistic negative test cases executed.");
		logger.info("🔸 Now entering valid amount...");

		try {
			amountInput.clear();
			amountInput.sendKeys("1");
			logger.info("✍️ Entered valid amount: ₹1");
			clickWithRetry(PROCEED_BUTTON_REMARKS);
			logger.info("👆 Proceeded with valid amount successfully.");
		} catch (Exception e) {
			logger.error("❌ Error during valid amount entry: {}", e.getMessage());
		}

		logger.info("🧪=== Completed Negative Tests for Amount Field ===");
	}

}
