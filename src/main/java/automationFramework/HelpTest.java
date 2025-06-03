
package automationFramework;

import automationFramework.pages.HelpPage;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class HelpTest {

	private WebDriver driver;
	private HelpPage helpPage;

	public HelpTest(WebDriver driver) {
		this.driver = driver;
		this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		this.helpPage = new HelpPage(driver);
	}

	public void CheckHelpTab() {
		try {
			System.out.println("Starting Help Tab Test...");

			helpPage.clickHelpTab();
			helpPage.waitForPaymentData();
			// paymentHistoryPage.Demooclass1();
			// paymentHistoryPage.Demooclass2();

			System.out.println("Help Tab Test completed successfully.");

		} catch (Exception e) {
			System.out.println("Error during Help Tab Test.");
			e.printStackTrace();
		}
	}
}
