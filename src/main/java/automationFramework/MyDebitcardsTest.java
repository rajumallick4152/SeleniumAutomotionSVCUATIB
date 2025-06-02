/*
 * package automationFramework;
 * 
 * public class MyDebitcardsTest {
 * 
 * }
 */

package automationFramework;

import automationFramework.pages.MyDebitcardsPage;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class MyDebitcardsTest {

	private WebDriver driver;
	private MyDebitcardsPage myDebitcardsPage;

	public MyDebitcardsTest(WebDriver driver) {
		this.driver = driver;
		this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		this.myDebitcardsPage = new MyDebitcardsPage(driver);
	}

	public void CheckMyDebitcardsTab() {
		try {
			System.out.println("Starting My DebitCards Tab Test...");

			myDebitcardsPage.clickMyDebitCardsTab();
			myDebitcardsPage.waitForPaymentData();
			// paymentHistoryPage.Demooclass1();
			// paymentHistoryPage.Demooclass2();

			System.out.println("My DebitCards Tab Test completed successfully.");

		} catch (Exception e) {
			System.out.println("Error during My DebitCards Tab Test.");
			e.printStackTrace();
		}
	}
}
