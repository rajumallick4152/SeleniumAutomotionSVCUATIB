
package automationFramework;

import automationFramework.pages.MorePage;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class MoreTest {

	private WebDriver driver;
	private MorePage morePage;

	public MoreTest(WebDriver driver) {
		this.driver = driver;
		this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		this.morePage = new MorePage(driver);
	}

	public void CheckMoreTab() {
		try {
			System.out.println("Starting More Tab Test...");

			morePage.clickMoreTab();
			morePage.waitForPaymentData();
			// paymentHistoryPage.Demooclass1();
			// paymentHistoryPage.Demooclass2();

			System.out.println("More Tab Test completed successfully.");

		} catch (Exception e) {
			System.out.println("Error during More Tab Test.");
			e.printStackTrace();
		}
	}
}
