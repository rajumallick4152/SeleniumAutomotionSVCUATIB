package automationFramework.tests;

import automationFramework.BrowserFactory;
import automationFramework.pages.LoginPage;
import automationFramework.pages.PaymentHistoryPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PaymentTestNG {
	private WebDriver driver;
	private Properties prop;

	@BeforeClass
	public void setUp() throws IOException {
		driver = BrowserFactory.startBrowser("chrome");
		driver.manage().window().maximize();

		prop = new Properties();
		InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
		if (input == null) {
			throw new IOException("config.properties not found in classpath");
		}
		prop.load(input);
	}

	@Test
	public void loginBeforePayment() throws InterruptedException {
		String url = prop.getProperty("appURL");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");

		LoginPage loginPage = new LoginPage(driver);
		loginPage.performLogin(url, username, password);
	}

	@Test(dependsOnMethods = "loginBeforePayment", priority = 1)
	public void testPayment() {
		PaymentHistoryPage paymentPage = new PaymentHistoryPage(driver);
		paymentPage.clickPaymentsTab();
		paymentPage.waitForPaymentData();
		paymentPage.makePaymentToPayee();
	}

	@Test(dependsOnMethods = "loginBeforePayment", priority = 2)
	public void testInvalidAmountScenarios() {
		PaymentHistoryPage paymentPage = new PaymentHistoryPage(driver);
		paymentPage.clickPaymentsTab();
		paymentPage.waitForPaymentData();
		paymentPage.testInvalidAmounts(); // Make sure this method exists in your PaymentHistoryPage
	}

	/*
	 * @AfterClass public void tearDown() { // Uncomment when you want to close the
	 * browser after tests
	 * 
	 * if (driver != null) { driver.quit(); }
	 * 
	 * }
	 */
}
