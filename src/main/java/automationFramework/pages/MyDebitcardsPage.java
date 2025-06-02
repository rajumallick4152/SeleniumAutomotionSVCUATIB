/*
 * package automationFramework.pages;
 * 
 * public class MyDebitcardsPage {
 * 
 * }
 */

package automationFramework.pages;

//import automationFramework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MyDebitcardsPage extends BasePage {

	public MyDebitcardsPage(WebDriver driver) {
		super(driver); // Calls BasePage constructor
	}

	public void clickMyDebitCardsTab() {
		logger.info("Clicking My Debit Cards  Tab");
		clickWithRetry(By.xpath("//span[@class='p-menuitem-text' and text()='My Debit cards']"));
	}

	public void waitForPaymentData() {
		waitForSpinnerToDisappear();
	}
}
