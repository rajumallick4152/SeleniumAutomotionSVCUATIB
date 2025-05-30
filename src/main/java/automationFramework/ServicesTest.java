package automationFramework;

import automationFramework.pages.ServicesPage;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class ServicesTest {

	private WebDriver driver;
	private ServicesPage servicesPage;

	public ServicesTest(WebDriver driver) {
		this.driver = driver;
		this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
		this.servicesPage = new ServicesPage(driver);
	}

	public void checkServicesTab() {
		try {
			System.out.println("Starting Services Tab Test...");
			servicesPage.clickServicesTab();
			servicesPage.waitForPaymentData();
			System.out.println("Services Tab Test completed successfully.");
		} catch (Exception e) {
			System.out.println("Error during Services Tab Test.");
			e.printStackTrace();
		}
	}
}
