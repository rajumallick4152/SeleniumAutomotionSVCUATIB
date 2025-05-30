
package automationFramework.pages;

//import automationFramework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MyChequesPage extends BasePage {

	public MyChequesPage(WebDriver driver) {
		super(driver); // Calls BasePage constructor
	}

	public void clickMyChequesTab() {
		logger.info("Clicking My Cheques Tab");
		clickWithRetry(By.xpath("//span[@class='p-menuitem-text' and text()='My Cheques']"));
	}

	public void waitForPaymentData() {
		waitForSpinnerToDisappear();
	}
}
