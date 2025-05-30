package automationFramework;

import automationFramework.pages.MyChequesPage;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class MyChequesTest {

	private WebDriver driver;
	private MyChequesPage myChequesPage;

	public MyChequesTest(WebDriver driver) {
		this.driver = driver;
		this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		this.myChequesPage = new MyChequesPage(driver);
	}

	public void CheckMyChequesTabTab() {
		try {
			System.out.println("Starting My Cheques Tab Test...");

			myChequesPage.clickMyChequesTab();
			myChequesPage.waitForPaymentData();
			// paymentHistoryPage.Demooclass1();
			// paymentHistoryPage.Demooclass2();

			System.out.println("My Cheques Tab Test completed successfully.");

		} catch (Exception e) {
			System.out.println("Error during My Cheques Tab Test.");
			e.printStackTrace();
		}
	}
}
