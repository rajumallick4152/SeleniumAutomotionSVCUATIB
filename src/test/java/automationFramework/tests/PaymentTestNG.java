package automationFramework.tests;

import automationFramework.BrowserFactory;
import automationFramework.pages.LoginPage;
import automationFramework.pages.PaymentHistoryPage;
import automationFramework.utils.ExtentReportManager;
import automationFramework.utils.LoggerUtil;
import automationFramework.utils.ScreenshotUtil;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import com.aventstack.extentreports.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PaymentTestNG {

	private WebDriver driver;
	private Properties prop;
	private ExtentReports extent;
	private ExtentTest test;

	@BeforeClass
	public void setUp() throws IOException {
		LoggerUtil.log("Initializing browser and report...");

		// Init browser
		driver = BrowserFactory.startBrowser("chrome");
		driver.manage().window().maximize();

		// Init report
		extent = ExtentReportManager.getReportInstance();

		// Load config
		prop = new Properties();
		InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
		if (input == null) {
			throw new IOException("config.properties not found in classpath");
		}
		prop.load(input);

		LoggerUtil.log("Setup complete.");
	}

	@Test
	public void loginBeforePayment() throws InterruptedException {
		test = extent.createTest("Login Before Payment");
		LoggerUtil.log("Starting login test...");

		String url = prop.getProperty("appURL");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");

		LoginPage loginPage = new LoginPage(driver);
		loginPage.performLogin(url, username, password);

		LoggerUtil.log("Login successful.");
		test.pass("Login successful");
	}

	@Test(dependsOnMethods = "loginBeforePayment", priority = 1)
	public void testPayment() {
		test = extent.createTest("Make Payment Test");
		LoggerUtil.log("Starting payment test...");

		PaymentHistoryPage paymentPage = new PaymentHistoryPage(driver);
		paymentPage.clickPaymentsTab();
		paymentPage.waitForPaymentData();
		paymentPage.makePaymentToPayee();

		LoggerUtil.log("Payment completed successfully.");
		test.pass("Payment completed successfully");
	}

	@Test(dependsOnMethods = "loginBeforePayment", priority = 2)
	public void testInvalidAmountScenarios() {
		test = extent.createTest("Invalid Amount Scenarios Test");
		LoggerUtil.log("Testing invalid amount scenarios...");

		PaymentHistoryPage paymentPage = new PaymentHistoryPage(driver);
		paymentPage.clickPaymentsTab();
		paymentPage.waitForPaymentData();
		paymentPage.testInvalidAmounts();

		LoggerUtil.log("Invalid amount scenario tested successfully.");
		test.pass("Invalid amount scenario tested successfully");
	}

	@AfterMethod
	public void logResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			LoggerUtil.log("❌ Test failed: " + result.getName());
			String screenshotPath = ScreenshotUtil.captureScreenshot(driver, result.getName());
			test.fail("Test Failed: " + result.getThrowable(),
					MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
		} else if (result.getStatus() == ITestResult.SKIP) {
			LoggerUtil.log("⚠️ Test skipped: " + result.getName());
			test.skip("Test Skipped: " + result.getThrowable());
		} else {
			LoggerUtil.log("✅ Test passed: " + result.getName());
		}
	}

	@AfterClass
	public void tearDown() {
		LoggerUtil.log("Flushing extent report...");
		extent.flush();
	}
}
