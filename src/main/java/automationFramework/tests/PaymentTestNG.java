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
import java.util.*;

public class PaymentTestNG {

	private WebDriver driver;
	private ExtentReports extent;
	private ExtentTest test;
	private boolean loginSuccess = false;
	private List<File> attachments = new ArrayList<>();

	@BeforeClass
	public void setUp() {
		LoggerUtil.log("🌐 Initializing browser and extent report...");

		driver = BrowserFactory.startBrowser(); // ✅ correct class-level driver
		driver.manage().window().maximize();

		extent = ExtentReportManager.getReportInstance();
		LoggerUtil.log("✅ Setup complete.");
	}

	@Test
	public void loginBeforePayment() {
		test = extent.createTest("🔐 Login Before Payment");

		LoginPage loginPage = new LoginPage(driver);
		loginSuccess = loginPage.performLogin(test);

		if (!loginSuccess) {
			test.fail("❌ Login failed. Stopping further tests.");
			Assert.fail("Login unsuccessful.");
		} else {
			test.pass("✅ Login successful.");
		}
	}

	@Test(dependsOnMethods = "loginBeforePayment", priority = 1)
	public void testPayment() {
		test = extent.createTest("💸 NEFT Payment to Payee");

		PaymentHistoryPage paymentPage = new PaymentHistoryPage(driver);
		paymentPage.clickPaymentsTab();
		paymentPage.waitForPaymentData();
		paymentPage.makePaymentToPayee(test);

		test.pass("✅ Payment completed successfully.");
	}

	@Test(dependsOnMethods = "loginBeforePayment", priority = 2)
	public void testInvalidAmountScenarios() {
		test = extent.createTest("🚫 Invalid Amount Scenarios");

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
			attachments.add(screenshotFile);

			try {
				test.fail("❌ Test Failed: " + result.getThrowable(),
						MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
			} catch (Exception e) {
				test.fail("❌ Screenshot error: " + e.getMessage());
			}
		} else if (result.getStatus() == ITestResult.SKIP) {
			test.skip("⚠️ Test Skipped: " + result.getThrowable());
		} else {
			test.pass("✅ Test Passed");
		}
	}

	@AfterClass
	public void tearDown() {
		LoggerUtil.log("🧹 Flushing extent report...");
		extent.flush();

		try {
			String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReports/ExtentReport.html";
			File reportFile = new File(reportPath);

			if (reportFile.exists()) {
				LoggerUtil.log("📧 Sending report via email...");

				String[] recipients = {
						"rm4577302@gmail.com",
						"rajumallick4152@live.com",
						"raju@lcodetechnologies.com"
				};

				attachments.add(reportFile);

				String emailBody = "Hi,\n\nPlease find the attached automation test report.\n\n"
						+ "Thanks,\nSupriya";

				EmailSender.sendEmailWithMultipleAttachments(
						recipients,
						"Automation Test Report",
						emailBody,
						attachments
				);
			} else {
				LoggerUtil.log("❌ Report file not found: " + reportPath);
			}
		} catch (Exception e) {
			LoggerUtil.log("❌ Email send failed: " + e.getMessage());
		}
	}
}
