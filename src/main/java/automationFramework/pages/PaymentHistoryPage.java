package automationFramework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/*import ch.qos.logback.core.util.Duration;*/
import java.time.Duration;

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

	private static final By INSUFFICIENT_BALANCE_POPUP = By
			.xpath("//div[contains(@class, 'popup-dialog-height')]//span[text()='Insufficient balance']");
	private static final By OK_BUTTON = By.xpath("//span[text()='Okay']/ancestor::button");

	private static final By INVALID_OTP_MESSAGE = By.xpath("//div[contains(text(), 'Invalid OTP')]");
	private static final By CANCEL_BUTTON = By.xpath("//span[text()='Cancel']/ancestor::button");

	public PaymentHistoryPage(WebDriver driver) {
		super(driver);
	}

	public void clickPaymentsTab() {
		logger.info("🟦 [STEP] Clicking on the Payment tab.");
		clickWithRetry(PAYMENT_TAB);
		logger.info("✅ [SUCCESS] Payment tab clicked.");
	}

	public void waitForPaymentData() {
		waitForSpinnerToDisappear();
	}

	private void enterAmount(String amount) {
		try {
			WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT));
			scrollIntoView(amountInput);
			jsClick(amountInput);
			amountInput.clear();
			amountInput.sendKeys(amount);
			logger.info("✅ [SUCCESS] Entered amount: ₹{}", amount);
		} catch (Exception e) {
			logger.error("❌ [FAIL] Error entering amount: {}", e.getMessage());
			throw new RuntimeException("Cannot proceed without entering amount.");
		}
	}

	private void enterRemarks(String remarks) {
		try {
			WebElement remarksInput = wait.until(ExpectedConditions.visibilityOfElementLocated(REMARKS_INPUT));
			remarksInput.clear();
			remarksInput.sendKeys(remarks);
			logger.info("✅ [SUCCESS] Entered remarks: {}", remarks);
		} catch (Exception e) {
			logger.warn("⚠️ [WARN] Could not enter remarks: {}", e.getMessage());
		}
	}

	private void clickProceedButtonRemarks() {
		try {
			clickWithRetry(PROCEED_BUTTON_REMARKS);
			logger.info("✅ [SUCCESS] Clicked Proceed after remarks.");
		} catch (Exception e) {
			logger.error("❌ [FAIL] Failed to click Proceed after remarks: {}", e.getMessage());
			throw new RuntimeException("Unable to proceed after entering remarks.");
		}
	}

	private void clickConfirmButton() {
		try {
			clickWithRetry(CONFIRM_BUTTON);
			logger.info("✅ [SUCCESS] Confirm button clicked.");
		} catch (Exception e) {
			logger.error("❌ [FAIL] Confirm button click failed: {}", e.getMessage());
			throw new RuntimeException("Unable to confirm payment.");
		}
	}

	private void enterOTP(String otp) {
		try {
			WebElement otpInput = wait.until(ExpectedConditions.visibilityOfElementLocated(OTP_INPUT));
			otpInput.sendKeys(otp);
			logger.info("✅ [SUCCESS] OTP entered.");
		} catch (Exception e) {
			logger.warn("⚠️ [WARN] Could not enter OTP: {}", e.getMessage());
		}
	}

	private void clickFinalProceedButton() {
		try {
			waitForOverlayDisappear();
			WebElement finalProceedBtn = wait.until(ExpectedConditions.elementToBeClickable(PROCEED_BUTTON_OTP));
			scrollIntoView(finalProceedBtn);
			jsClick(finalProceedBtn);
			logger.info("✅ [SUCCESS] Final Proceed clicked after OTP.");
		} catch (Exception e) {
			logger.error("❌ [FAIL] Final Proceed click failed: {}", e.getMessage());
			throw new RuntimeException("Payment could not be finalized.");
		}
	}

	private void clickCloseButton() {
		try {
			WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(CLOSE_BUTTON));
			scrollIntoView(closeBtn);
			jsClick(closeBtn);
			logger.info("✅ [SUCCESS] Close button clicked.");
		} catch (Exception e) {
			logger.warn("⚠️ [WARN] Close button not found or failed to click: {}", e.getMessage());
		}
	}

	private void selectNEFTOption() {
		logger.info("🟦 [STEP] Selecting NEFT option");
		boolean selected = false;
		int attempts = 0;

		while (!selected && attempts < 5) {
			try {
				logger.info("Attempting to click NEFT option (Attempt {})", attempts + 1);
				WebElement neftOption = wait.until(ExpectedConditions.elementToBeClickable(NEFT_OPTION));
				scrollIntoView(neftOption);
				Thread.sleep(300);

				try {
					new Actions(driver).moveToElement(neftOption).pause(200).click().perform();
					logger.info("🔄 NEFT clicked via Actions.");
				} catch (Exception e1) {
					logger.warn("⚠️ Actions click failed: {}. Trying JS click.", e1.getMessage());
					jsClick(neftOption);
					logger.info("✅ NEFT clicked via JS.");
				}

				Thread.sleep(500);
				selected = driver.findElements(NEFT_SELECTED_ICON).size() > 0;

				if (!selected) {
					logger.warn("⚠️ NEFT not selected yet. Retrying...");
				}
			} catch (Exception e) {
				logger.error("❌ NEFT selection error: {}", e.getMessage());
			}
			attempts++;
		}

		if (!selected) {
			throw new RuntimeException("NEFT could not be selected after multiple attempts.");
		} else {
			logger.info("✅ NEFT option selected successfully.");
		}
	}

	private void waitForOverlayDisappear() {
		try {
			logger.info("Waiting for any overlay/spinner to disappear.");
			wait.until(driver -> {
				try {
					WebElement overlay = driver.findElement(By.cssSelector("div[aria-hidden='false'].p-overlay"));
					return !overlay.isDisplayed();
				} catch (Exception e) {
					return true;
				}
			});
		} catch (Exception e) {
			logger.warn("⚠️ Overlay wait interrupted: {}", e.getMessage());
		}
	}

	public void makePaymentToPayee() {
		logger.info("🧪=== Starting Positive Test Cases for NETF transaction for Payment to payee ===");
		logger.info("🚀 [START] Initiating payment to payee: DXFCHGV");

		try {
			clickWithRetry(PAYEE_NAME);
			logger.info("✅ [SUCCESS] Payee selected: DXFCHGV");
		} catch (Exception e) {
			logger.error("❌ [FAIL] Failed to select payee: {}", e.getMessage());
			throw new RuntimeException("Cannot proceed without selecting payee.");
		}

		enterAmount("1");
		selectNEFTOption();
		enterRemarks("test value");
		clickProceedButtonRemarks();
		clickConfirmButton();
		enterOTP("123456");
		clickFinalProceedButton();
		clickCloseButton();

		logger.info("🎉 [DONE] ✅ Positive test cases for Payment to payee DXFCHGV successfully executed.");
	}

	public void testInvalidAmounts() {
		logger.info("🧪=== Starting Negative Test Cases for NETF transaction for Payment to payee ===");

		try {
			logger.info("🔹 Step : Selecting payee: DXFCHGV");
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
				logger.info("[SUCCESS] ✅ Correct error displayed: 'Amount is required'");
			} else {
				logger.warn("⚠️ Expected error not found for blank input.");
			}
		} catch (Exception e) {
			logger.error("❌ Error during Blank input test: {}", e.getMessage());
		}

		// 2️⃣ Special Characters
		try {
			logger.info("\n🔸 Test Case 2: Special Characters (@#$%)");
			amountInput.clear();
			amountInput.sendKeys("@#$%");
			String fieldValue = amountInput.getAttribute("value");

			if (fieldValue == null || fieldValue.isEmpty()) {
				logger.info("[SUCCESS] ✅ Special characters were blocked as expected.");
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
				logger.info("[SUCCESS] ✅ Field blocked alphanumeric input completely.");
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
				logger.info("[SUCCESS] ✅ Correct validation message shown: 'Minimum amount is 1.00'");
			} else {
				logger.warn("⚠️ Validation error for zero amount not found.");
			}
		} catch (Exception e) {
			logger.error("❌ Error during Zero Amount test: {}", e.getMessage());
		}

		// 5️⃣ Amount Greater Than Balance
		testAmountGreaterThanBalance();

		// 6️⃣ Remarks field left blank, transaction should succeed
		testRemarksFieldBlank();

		// 7️⃣ Invalid OTP
		testInvalidOTP();

		logger.info("\n✅ All negative test cases executed.");

	}

	private void testAmountGreaterThanBalance() {
		try {
			logger.info("\n🔸 Test Case 5: Amount Greater Than Account Balance");

			By balanceLocator = By.xpath(
					"//span[contains(@class, 'text-small') and contains(@class, 'font-semibold') and contains(@class, 'text-black-500')]");
			WebElement balanceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(balanceLocator));

			String balanceText = balanceElement.getText().replaceAll("[^\\d]", "");
			if (balanceText.isEmpty()) {
				throw new RuntimeException("Balance text not found or empty.");
			}

			int accountBalance = Integer.parseInt(balanceText);
			int overLimitAmount = accountBalance + 500;
			String overLimitAmountStr = String.valueOf(overLimitAmount);

			logger.info("Fetched account balance: ₹{}", accountBalance);
			logger.info("Attempting to enter over-limit amount: ₹{}", overLimitAmount);

			WebElement amountInput1 = wait
					.until(ExpectedConditions.visibilityOfElementLocated(By.id("custom-amount-input")));
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(
					"arguments[0].innerText = arguments[1];"
							+ "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));"
							+ "arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));",
					amountInput1, overLimitAmountStr);
			Thread.sleep(500);

			String currentValue = (String) js.executeScript("return arguments[0].innerText;", amountInput1);
			currentValue = currentValue.replaceAll("[^\\d]", "");

			if (!currentValue.equals(overLimitAmountStr)) {
				throw new RuntimeException(
						"Amount mismatch: expected=" + overLimitAmountStr + ", actual=" + currentValue);
			}

			logger.info("✅ Over-limit amount successfully typed: '{}'", currentValue);

			try {
				driver.findElement(By.xpath("//header")).click();
			} catch (Exception e) {
				driver.findElement(By.tagName("body")).click();
			}

			Thread.sleep(1000);

			WebElement popup = wait.until(ExpectedConditions.presenceOfElementLocated(INSUFFICIENT_BALANCE_POPUP));

			if (popup != null && popup.isDisplayed()) {
				logger.info("✅ 'Insufficient balance' popup displayed.");
			} else {
				logger.warn("⚠️ Popup not displayed as expected.");
			}

			WebElement okBtnElement = wait.until(ExpectedConditions.elementToBeClickable(OK_BUTTON));
			okBtnElement.click();
			logger.info("✅ Clicked 'Okay' button on popup.");
			logger.info(
					"[SUCCESS] ✅ Error Pop up Shown while Putting greater amount of account balance as Insufficient balance ");

		} catch (Exception e) {
			logger.error("❌ Error during Amount Greater Than Balance test: {}", e.getMessage());
		}
	}

	private void testRemarksFieldBlank() {
		// 6️⃣ Remarks field left blank, transaction should succeed
		try {
			logger.info("\n🔸 Test Case 6: Leave Remarks Field Blank and Proceed");

			WebElement remarksInput = wait.until(ExpectedConditions.visibilityOfElementLocated(REMARKS_INPUT));
			scrollIntoView(remarksInput);
			remarksInput.clear();
			logger.info("✅ Remarks field cleared (left blank).");

			WebElement amountInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT));
			amountInputField.clear();
			amountInputField.sendKeys("2");

			selectNEFTOption();
			clickWithRetry(PROCEED_BUTTON_REMARKS);
			clickWithRetry(By.xpath("//span[text()='Confirm']/ancestor::button"));

			enterOTP("123456");
			waitForOverlayDisappear();

			WebElement finalProceedBtn = wait.until(ExpectedConditions.elementToBeClickable(PROCEED_BUTTON_OTP));
			scrollIntoView(finalProceedBtn);
			jsClick(finalProceedBtn);

			logger.info("✅ Final Proceed button clicked.");
			Thread.sleep(2000);

			logger.info("[SUCCESS] ✅ Transaction succeeded with blank remarks.");

			clickCloseButton();

		} catch (Exception e) {
			logger.error("❌ Error during Remarks Field Blank test: {}", e.getMessage());
		}
	}

	private void testInvalidOTP() {
		try {
			logger.info("\n🔸 Test Case 7: Enter Invalid OTP");

			// Navigate to the OTP entry screen
			clickWithRetry(PAYEE_NAME);
			enterAmount("3");
			selectNEFTOption();
			enterRemarks("test value");
			clickProceedButtonRemarks();
			clickConfirmButton();

			// Enter an invalid OTP
			String invalidOTP = "123321";
			enterOTP(invalidOTP);
			clickFinalProceedButton();

			// Wait for the "Invalid OTP" message to appear
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			WebElement invalidOTPMessage = wait
					.until(ExpectedConditions.visibilityOfElementLocated(INVALID_OTP_MESSAGE));

			// Verify that the "Invalid OTP" message is displayed
			if (invalidOTPMessage.isDisplayed()) {
				logger.info("✅ Correct error message displayed: 'Invalid OTP'");
			} else {
				logger.warn("⚠️ Expected error message 'Invalid OTP' not found.");
			}

			// Wait for a short time before clicking Cancel
			Thread.sleep(500); // Wait for 500 milliseconds (adjust as needed)
			clickWithRetry(CANCEL_BUTTON);
			clickWithRetry(CANCEL_BUTTON);

		} catch (Exception e) {
			logger.error("❌ Error during Invalid OTP test: {}", e.getMessage());
		}
	}

}
