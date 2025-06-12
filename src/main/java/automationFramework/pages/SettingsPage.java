package automationFramework.pages;

//import automationFramework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SettingsPage extends BasePage {

	public SettingsPage(WebDriver driver) {
		super(driver); // Calls BasePage constructor
	}

	public void clickSettingsTab() {
		logger.info("Clicking Settings Tab");
		clickWithRetry(By.xpath("//span[@class='p-menuitem-text' and text()='Settings']"));
	}

	public void waitForPaymentData() {
		waitForSpinnerToFullyDisappear();
	}
}
