package automationFramework.tests;

import automationFramework.BrowserFactory;
import automationFramework.pages.LoginPage;
import automationFramework.pages.PaymentHistoryPage;
import automationFramework.utils.ExtentReportManager;
import automationFramework.utils.LoggerUtil;
import automationFramework.utils.ScreenshotUtil;
import automationFramework.utils.EmailSender;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import com.aventstack.extentreports.*;

import java.io.File;
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

		driver = BrowserFactory.startBrowser("chrome");
		driver.manage().window().maximize();

		// Report instance
		extent = ExtentReportManager.getReportInstance();

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

		String url = prop.getProperty("appURL");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");

		LoginPage loginPage = new LoginPage(driver);
		loginPage.performLogin(url, username, password);

		test.pass("Login successful");
	}

	@Test(dependsOnMethods = "loginBeforePayment", priority = 1)
	public void testPayment() {
		test = extent.createTest("Make Payment Test");

		PaymentHistoryPage paymentPage = new PaymentHistoryPage(driver);
		paymentPage.clickPaymentsTab();
		paymentPage.waitForPaymentData();
		paymentPage.makePaymentToPayee();

		test.pass("Payment completed successfully");
	}

	/*
	 * @Test(dependsOnMethods = "loginBeforePayment", priority = 2) public void
	 * testInvalidAmountScenarios() { test =
	 * extent.createTest("Invalid Amount Scenarios");
	 * 
	 * PaymentHistoryPage paymentPage = new PaymentHistoryPage(driver);
	 * paymentPage.clickPaymentsTab(); paymentPage.waitForPaymentData();
	 * paymentPage.testInvalidAmounts();
	 * 
	 * test.pass("Invalid amount scenarios tested"); }
	 */

	@AfterMethod
	public void logResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			String screenshotPath = ScreenshotUtil.captureScreenshot(driver, result.getName());
			try {
				test.fail("Test Failed: " + result.getThrowable(),
						MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
			} catch (Exception e) {
				test.fail("Test Failed: " + result.getThrowable());
			}
		} else if (result.getStatus() == ITestResult.SKIP) {
			test.skip("Test Skipped: " + result.getThrowable());
		} else {
			test.pass("Test Passed");
		}
	}

	@AfterClass
	public void tearDown() {
		LoggerUtil.log("Flushing extent report...");
		extent.flush();

		try {
			String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReports/ExtentReport.html";
			File reportFile = new File(reportPath);

			// Wait for the report file to be generated (max 5 seconds)
			int wait = 0;
			while (!reportFile.exists() && wait < 5000) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					LoggerUtil.log("❌ Report wait interrupted: " + e.getMessage());
				}
				wait += 500;
			}

			if (reportFile.exists()) {
				LoggerUtil.log("📧 Sending report via email...");

				// Define multiple recipients
				String[] recipients = { "rm4577302@gmail.com", "rajumallick4152@live.com", "raju@lcodetechnologies.com" };

				EmailSender.sendEmailWithAttachment(recipients, "Automation Test Report",
						"Hi,\n\nPlease find the attached automation test report.\n\nThanks,\nSupriya", reportFile);
			} else {
				LoggerUtil.log("❌ Report file not found at: " + reportPath);
			}
		} catch (Exception e) {
			LoggerUtil.log("❌ Error sending email: " + e.getMessage());
		}
	}
}
