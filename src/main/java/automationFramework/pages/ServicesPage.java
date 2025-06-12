package automationFramework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ServicesPage extends BasePage {

	private static final By SERVICES_TAB = By.xpath("//span[@class='p-menuitem-text' and text()='Services']");

	public ServicesPage(WebDriver driver) {
		super(driver);
	}

	public void clickServicesTab() {
		logger.info("Clicking on Services Tab");
		clickWithRetry(SERVICES_TAB);
	}

	public void waitForPaymentData() {
		waitForSpinnerToFullyDisappear();
	}

	public void demoMethod1() {
		// Placeholder for future use
	}

	public void demoMethod2() {
		// Placeholder for future use
	}
}
