package automationFramework.tests;

import automationFramework.BrowserFactory;
import automationFramework.pages.LoginPage;
import automationFramework.pages.PaymentHistoryPage;
import automationFramework.utils.ExtentReportManager;
import automationFramework.utils.LoggerUtil;
import automationFramework.utils.ScreenshotUtil;
import automationFramework.utils.EmailSender;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import com.aventstack.extentreports.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PaymentTestNG {

	private WebDriver driver;
	private Properties prop;
	private ExtentReports extent;
	private ExtentTest test;
	private boolean loginSuccess = false;
	private List<File> attachments = new ArrayList<>(); // store screenshots

	@BeforeClass
	public void setUp() throws IOException {
		LoggerUtil.log("Initializing browser and report...");

		driver = BrowserFactory.startBrowser("chrome");
		driver.manage().window().maximize();

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
		loginSuccess = loginPage.performLogin(url, username, password, test);

		if (!loginSuccess) {
			test.fail("❌ Login failed. Halting further tests.");
			Assert.fail("Login unsuccessful. Stopping test suite.");
		} else {
			test.pass("✅ Login successful.");
		}
	}

	@Test(dependsOnMethods = "loginBeforePayment", priority = 1)
	public void testPayment() {
		test = extent.createTest("NEFT transaction for Payment to payee");

		PaymentHistoryPage paymentPage = new PaymentHistoryPage(driver);
		paymentPage.clickPaymentsTab();
		paymentPage.waitForPaymentData();
		paymentPage.makePaymentToPayee(test);

		test.pass("✅ Payment completed successfully.");
	}

	@Test(dependsOnMethods = "loginBeforePayment", priority = 2)
	public void testInvalidAmountScenarios() {
		test = extent.createTest("Invalid Amount Scenarios");

		PaymentHistoryPage paymentPage = new PaymentHistoryPage(driver);
		paymentPage.clickPaymentsTab();
		paymentPage.waitForPaymentData();
		paymentPage.testInvalidAmounts(test);

		test.pass("✅ Invalid amount scenarios tested.");
	}

	@AfterMethod
	public void logResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			String screenshotPath = ScreenshotUtil.captureScreenshot(driver, result.getName());
			File screenshotFile = new File(screenshotPath);
			attachments.add(screenshotFile); // add screenshot for email

			try {
				test.fail("❌ Test Failed: " + result.getThrowable(),
						MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
			} catch (Exception e) {
				test.fail("❌ Test Failed: " + result.getThrowable());
			}
		} else if (result.getStatus() == ITestResult.SKIP) {
			test.skip("⚠️ Test Skipped: " + result.getThrowable());
		} else {
			test.pass("✅ Test Passed");
		}
	}

	@AfterClass
	public void tearDown() {
		LoggerUtil.log("Flushing extent report...");
		extent.flush();

		try {
			String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReports/ExtentReport.html";
			File reportFile = new File(reportPath);

			int wait = 0;
			while (!reportFile.exists() && wait < 5000) {
				Thread.sleep(500);
				wait += 500;
			}

			if (reportFile.exists()) {
				LoggerUtil.log("📧 Sending report via email...");

				String[] recipients = { "rm4577302@gmail.com", "rajumallick4152@live.com", "raju@lcodetechnologies.com" };

				// add the main report file to attachments
				attachments.add(reportFile);

				String emailBody = "Hi,\n\n" + "Please find the attached automation test report.\n\n"
						+ "DISCLAIMER: This message contains information that may be privileged and/or confidential, "
						+ "and is the property of LCode Technologies Private Limited. It is intended only for the person "
						+ "to whom it is addressed. If you are not the intended recipient of this message, you are not "
						+ "authorized to read, print, retain, copy, disseminate, distribute, or use this message in full "
						+ "or in part. In such a case, please notify the sender immediately, and delete all copies of this message.\n\n"
						+ "Thanks,\nSupriya";

				EmailSender.sendEmailWithMultipleAttachments(recipients, "Automation Test Report", emailBody,
						attachments);
			} else {
				LoggerUtil.log("❌ Report file not found at: " + reportPath);
			}
		} catch (Exception e) {
			LoggerUtil.log("❌ Error sending email: " + e.getMessage());
		}
	}
}
