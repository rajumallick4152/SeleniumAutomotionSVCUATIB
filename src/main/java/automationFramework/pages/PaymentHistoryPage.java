package automationFramework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PaymentHistoryPage extends BasePage {

	private static final By PAYMENT_TAB = By.xpath("//span[@class='p-menuitem-text' and text()='Payment']");

	public PaymentHistoryPage(WebDriver driver) {
		super(driver);
	}

	public void clickPaymentsTab() {
		logger.info("Clicking on Payment Tab");
		clickWithRetry(PAYMENT_TAB);
	}

	public void waitForPaymentData() {
		waitForSpinnerToDisappear();
	}

	public void demoClass1() {
		// Placeholder method
	}

	public void demoClass2() {
		// Placeholder method
	}
}
