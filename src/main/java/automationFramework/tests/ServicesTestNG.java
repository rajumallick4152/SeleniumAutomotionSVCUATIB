package automationFramework.tests;

import automationFramework.BrowserFactory;
import automationFramework.pages.LoginPage;
import automationFramework.pages.ServicesPage;
import automationFramework.utils.ExtentReportManager;
import automationFramework.utils.LoggerUtil;
import automationFramework.utils.ScreenshotUtil;
import automationFramework.utils.EmailSender;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.MediaEntityBuilder;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ServicesTestNG {

	private WebDriver driver;
	private Properties prop;
	private ServicesPage servicesPage;
	private ExtentReports extent;
	private ExtentTest test;
	private List<File> attachments = new ArrayList<>();

	@BeforeClass
	public void setUp() throws Exception {
		LoggerUtil.log("🔧 Launching browser...");

		WebDriver driver = BrowserFactory.startBrowser(); // browser=config.properties 

		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		extent = ExtentReportManager.getReportInstance();

		test = extent.createTest("🔐 Login to Application");

		boolean loginSuccess = new LoginPage(driver).performLogin(test);

		if (loginSuccess) {
			test.pass("✅ Login successful");
			LoggerUtil.log("✅ Login successful");
		} else {
			test.fail("❌ Login failed. Stopping setup.");
			LoggerUtil.log("❌ Login failed.");
			throw new RuntimeException("Login failed. Cannot continue tests.");
		}

		LoggerUtil.log("✅ Setup complete.");
	}

	@Test(priority = 1)
	public void testManageSMSFlow() {
		test = extent.createTest("💼 Manage SMS Threshold Test");
		servicesPage = new ServicesPage(driver, test);

		try {
			servicesPage.navigateToManageSMS();
			servicesPage.enterAmountThresholds();
			servicesPage.clickSave();
			servicesPage.enterOTP("123456");
			servicesPage.clickProceed();

			boolean success = servicesPage.isSuccessPopupVisible();
			servicesPage.clickOkay();

			if (success) {
				test.pass("✅ Threshold saved and verified successfully.");
				LoggerUtil.log("✅ Threshold saved and verified successfully.");
			} else {
				throw new AssertionError("❌ Success popup not displayed.");
			}

		} catch (Exception e) {
			LoggerUtil.error("❌ Exception in testManageSMSFlow: ", e);
			test.fail("❌ Test failed due to: " + e.getMessage());
			throw e;
		}
	}

	@Test(priority = 2)
	public void testEmptyThresholds() {
		test = extent.createTest("🚫 Empty Threshold Values Test");
		servicesPage = new ServicesPage(driver, test);
		try {
			servicesPage.testEmptyThresholds();
			test.pass("✅ Validation message displayed for empty thresholds.");
		} catch (AssertionError e) {
			test.fail(e.getMessage());
			throw e;
		}
	}

	@Test(priority = 3)
	public void testNegativeThresholds() {
		test = extent.createTest("🚫 Negative Threshold Values Test");
		servicesPage = new ServicesPage(driver, test);
		try {
			servicesPage.testNegativeThresholds();
			test.pass("✅ Negative values not accepted or auto-corrected by the field.");
		} catch (AssertionError e) {
			test.fail(e.getMessage());
			throw e;
		}
	}

	@Test(priority = 4)
	public void testInvalidOTP() {
		test = extent.createTest("🚫 Invalid OTP Test");
		servicesPage = new ServicesPage(driver, test);
		try {
			servicesPage.testInvalidOTPFlow();
			test.pass("✅ Proper error handled for invalid OTP.");
		} catch (AssertionError e) {
			test.fail(e.getMessage());
			throw e;
		}
	}

	@AfterMethod
	public void handleTestResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			String screenshotPath = ScreenshotUtil.captureScreenshotAsFile(driver, result.getName());
			String base64Screenshot = ScreenshotUtil.captureScreenshotAsBase64(driver);

			attachments.add(new File(screenshotPath));

			try {
				test.fail("❌ Test Failed: " + result.getThrowable(),
						MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
			} catch (Exception e) {
				test.fail("❌ Could not attach screenshot: " + e.getMessage());
			}
		} else if (result.getStatus() == ITestResult.SKIP) {
			test.skip("⚠️ Test Skipped: " + result.getThrowable());
		}
	}

	@AfterClass
	public void tearDown() {
		LoggerUtil.log("🧹 Finalizing report...");
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

				String[] recipients = { "rm4577302@gmail.com", "rajumallick4152@live.com",
						"raju@lcodetechnologies.com", };
				attachments.add(reportFile);

				String body = "Hi,\n\nPlease find the attached automation test report.\n\nThanks,\nSupriya\n\n"
						+ "DISCLAIMER : This message contains information that may be privileged and/or confidential, "
						+ "and is the property of LCode Technologies Private Limited. It is intended only for the person to whom it is addressed. "
						+ "If you are not the intended recipient of this message, you are not authorized to read, print, retain, copy, disseminate, "
						+ "distribute, or use this message in full or in part. In such a case, please notify the sender immediately, and delete all copies of this message.";

				EmailSender.sendEmailWithMultipleAttachments(recipients, "Automation Test Report", body, attachments);
			} else {
				LoggerUtil.log("❌ Report file not found at: " + reportPath);
			}
		} catch (Exception e) {
			LoggerUtil.log("❌ Failed to send email: " + e.getMessage());
		}

		if (driver != null) {
			LoggerUtil.log("✅ Browser session complete. (Not quitting for debug)");
			// driver.quit(); // Uncomment if needed
		}
	}
}