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

	// Changed: Two separate Proceed buttons with different locators
	private static final By PROCEED_BUTTON_REMARKS = By.xpath("(//span[text()='Proceed'])[1]"); // First Proceed button
																								// after remarks
	private static final By PROCEED_BUTTON_OTP = By.xpath("(//span[text()='Proceed'])[2]"); // Second Proceed button
																							// after OTP

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

		// Select NEFT option if not already selected
		try {
			WebElement neftOption = wait.until(ExpectedConditions.presenceOfElementLocated(NEFT_OPTION));
			scrollIntoView(neftOption);
			Thread.sleep(300); // Small wait to ensure visibility

			boolean isSelected = driver.findElements(NEFT_SELECTED_ICON).size() > 0;

			if (isSelected) {
				logger.info("NEFT is already selected. No click needed.");
			} else {
				logger.info("NEFT not selected. Attempting click...");
				try {
					new Actions(driver).moveToElement(neftOption).pause(200).click().perform();
					logger.info("Selected NEFT using Actions click.");
				} catch (Exception actionsClickEx) {
					logger.warn("Actions click failed: {}. Trying JS click.", actionsClickEx.getMessage());
					try {
						jsClick(neftOption);
						logger.info("Selected NEFT using JS click.");
					} catch (Exception jsEx) {
						logger.error("JS click also failed: {}", jsEx.getMessage());
						throw new RuntimeException("Unable to select NEFT option.");
					}
				}
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

		// Click the final close button (usually to close success popup or summary)
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
}
