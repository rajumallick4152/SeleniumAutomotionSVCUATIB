package automationFramework.pages;

//import automationFramework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MorePage extends BasePage {

	public MorePage(WebDriver driver) {
		super(driver); // Calls BasePage constructor
	}

	public void clickMoreTab() {
		logger.info("Clicking More Tab");
		clickWithRetry(By.xpath("//span[@class='p-menuitem-text' and text()='More']"));
	}

	public void waitForPaymentData() {
		waitForSpinnerToDisappear();
	}
}
