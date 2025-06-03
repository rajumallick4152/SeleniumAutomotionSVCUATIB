
package automationFramework;

import automationFramework.pages.SettingsPage;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class SettingsTest {

	private WebDriver driver;
	private SettingsPage settingsPage;

	public SettingsTest(WebDriver driver) {
		this.driver = driver;
		this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		this.settingsPage = new SettingsPage(driver);
	}

	public void CheckSettingsTab() {
		try {
			System.out.println("Starting Settings Tab Test...");

			settingsPage.clickSettingsTab();
			settingsPage.waitForPaymentData();
			// paymentHistoryPage.Demooclass1();
			// paymentHistoryPage.Demooclass2();

			System.out.println("Settings Tab Test completed successfully.");

		} catch (Exception e) {
			System.out.println("Error during Settings Tab Test.");
			e.printStackTrace();
		}
	}
}
