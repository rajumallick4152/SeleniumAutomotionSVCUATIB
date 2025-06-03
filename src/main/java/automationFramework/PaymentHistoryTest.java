package automationFramework;

import automationFramework.pages.PaymentHistoryPage;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class PaymentHistoryTest {

	private WebDriver driver;
	private PaymentHistoryPage paymentHistoryPage;

	public PaymentHistoryTest(WebDriver driver) {
		this.driver = driver;
		this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		this.paymentHistoryPage = new PaymentHistoryPage(driver);
	}

	public void checkPaymentHistory() {
		try {
			System.out.println("Starting Payment History Test...");

			paymentHistoryPage.clickPaymentsTab();
			paymentHistoryPage.waitForPaymentData();
			paymentHistoryPage.makePaymentToPayee();
			// paymentHistoryPage.demoClass1();
			// paymentHistoryPage.demoClass2();

			System.out.println("Payment History Test completed successfully.");

		} catch (Exception e) {
			System.out.println("Error during Payment History Test.");
			e.printStackTrace();
		}
	}
}