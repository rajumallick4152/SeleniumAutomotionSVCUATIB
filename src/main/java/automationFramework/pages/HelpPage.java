
package automationFramework.pages;

//import automationFramework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HelpPage extends BasePage {

	public HelpPage(WebDriver driver) {
		super(driver); // Calls BasePage constructor
	}

	public void clickHelpTab() {
		logger.info("Clicking help Tab");
		clickWithRetry(By.xpath("//span[@class='p-menuitem-text' and text()='Help']"));
	}

	public void waitForPaymentData() {
		waitForSpinnerToFullyDisappear();
	}
}
