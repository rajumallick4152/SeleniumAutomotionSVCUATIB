package automationFramework.tests;

import automationFramework.BrowserFactory;
import automationFramework.pages.AccountsPage;
import automationFramework.pages.LoginPage;
import automationFramework.pages.PaymentHistoryPage;
import automationFramework.pages.ServicesPage;
import automationFramework.pages.AccountsPage.FileType;
import automationFramework.utils.*;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.MediaEntityBuilder;

import org.openqa.selenium.WebDriver;
import org.testng.*;
import org.testng.annotations.*;

import java.io.File;
import java.io.InputStream;
import java.time.Duration;
import java.util.*;

public class CombinedTestNGSuite {

	private WebDriver driver;
	private Properties prop;
	private ExtentReports extent;
	private ExtentTest test;
	private boolean loginSuccess = false;
	private List<File> attachments = new ArrayList<>();
	private ServicesPage servicesPage;

	private AccountsPage accountsPage;

	@BeforeClass
	public void setUp() throws Exception {
		LoggerUtil.log("🔧 Initializing browser and loading configuration...");

		driver = BrowserFactory.startBrowser("chrome");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

		extent = ExtentReportManager.getReportInstance();

		prop = new Properties();
		try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
			if (input == null)
				throw new Exception("config.properties not found!");
			prop.load(input);
		}

		LoggerUtil.log("✅ Setup complete.");
	}

	@Test(priority = 0)
	public void loginToApplication() {
		test = extent.createTest("🔐 Login Test");

		String url = prop.getProperty("appURL");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");

		LoginPage loginPage = new LoginPage(driver);
		loginSuccess = loginPage.performLogin(url, username, password, test);

		if (loginSuccess) {
			test.pass("✅ Login successful");
		} else {
			test.fail("❌ Login failed. Stopping further execution.");
			Assert.fail("Login unsuccessful.");
		}
	}

	@Test(priority = 1, dependsOnMethods = "loginToApplication")
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

			Assert.assertTrue(success, "❌ Success popup not displayed.");
			test.pass("✅ Threshold saved and verified successfully.");
		} catch (Exception e) {
			test.fail("❌ Exception in testManageSMSFlow: " + e.getMessage());
			throw e;
		}
	}

	@Test(priority = 2, dependsOnMethods = "loginToApplication")
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

	@Test(priority = 3, dependsOnMethods = "loginToApplication")
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

	@Test(priority = 4, dependsOnMethods = "loginToApplication")
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

	@Test(priority = 5, dependsOnMethods = "loginToApplication")
	public void testValidPaymentFlow() {
		test = extent.createTest("💳 NEFT Payment to Payee");
		PaymentHistoryPage paymentPage = new PaymentHistoryPage(driver);

		try {
			paymentPage.clickPaymentsTab();
			paymentPage.waitForPaymentData();
			paymentPage.makePaymentToPayee(test);
			test.pass("✅ Payment completed successfully.");
		} catch (Exception e) {
			test.fail("❌ Payment test failed: " + e.getMessage());
			throw e;
		}
	}

	@Test(priority = 6, dependsOnMethods = "loginToApplication")
	public void testInvalidAmountScenarios() {
		test = extent.createTest("🚫 Invalid Amount Scenarios");
		PaymentHistoryPage paymentPage = new PaymentHistoryPage(driver);

		try {
			paymentPage.clickPaymentsTab();
			paymentPage.waitForPaymentData();
			paymentPage.testInvalidAmounts(test);
			test.pass("✅ Invalid amount scenarios tested.");
		} catch (Exception e) {
			test.fail("❌ Invalid amount scenario failed: " + e.getMessage());
			throw e;
		}
	}

	private void runDownloadTest(String description, Runnable action) {
		test = extent.createTest(description);
		long start = System.currentTimeMillis();
		action.run();
		test.pass("✅ " + description + " completed in " + (System.currentTimeMillis() - start) + " ms");
	}

	@Test(priority = 7, dependsOnMethods = "loginToApplication")
	public void testAccountSummary() {
		runDownloadTest("Account Summary Test", () -> {
			accountsPage.clickAccountsTab(test);
			accountsPage.waitForDataToLoad(test);
			accountsPage.scrollToViewBalanceButton(test);
			accountsPage.clickViewBalanceButton(test);
			accountsPage.closeBalanceModal(test);
		});
	}

	@Test(priority = 8, dependsOnMethods = "loginToApplication")
	public void downloadOneMonthStatementPdf() {
		runDownloadTest("Download 1 Month PDF", () -> accountsPage.downloadStatement("1 Month", FileType.PDF, test));
	}

	@Test(priority = 9, dependsOnMethods = "loginToApplication")
	public void downloadThreeMonthStatementPdf() {
		runDownloadTest("Download 3 Months PDF", () -> accountsPage.downloadStatement("3 Months", FileType.PDF, test));
	}

	@Test(priority = 10, dependsOnMethods = "loginToApplication")
	public void downloadOneMonthStatementXls() {
		runDownloadTest("Download 1 Month XLS", () -> accountsPage.downloadStatement("1 Month", FileType.XLS, test));
	}

	@Test(priority = 11, dependsOnMethods = "loginToApplication")
	public void downloadThreeMonthStatementXls() {
		runDownloadTest("Download 3 Months XLS", () -> accountsPage.downloadStatement("3 Months", FileType.XLS, test));
	}

	@Test(priority = 12, dependsOnMethods = "loginToApplication")
	public void downloadCustom6MonthsXsl() {
		runDownloadTest("Download Custom 6 Months XLS",
				() -> accountsPage.downloadCustomStatement("6", FileType.XLS, test));
	}

	@Test(priority = 13, dependsOnMethods = "loginToApplication")
	public void downloadCustom12MonthsPdf() {
		runDownloadTest("Download Custom 12 Months PDF",
				() -> accountsPage.downloadCustomStatement("12", FileType.PDF, test));
	}

	@Test(priority = 14, dependsOnMethods = "loginToApplication")
	public void downloadCustom6MonthsPdf() {
		runDownloadTest("Download Custom 6 Months PDF",
				() -> accountsPage.downloadCustomStatement("6", FileType.PDF, test));
	}

	@Test(priority = 15, dependsOnMethods = "loginToApplication")
	public void downloadCustom12MonthsXsl() {
		runDownloadTest("Download Custom 12 Months XLS",
				() -> accountsPage.downloadCustomStatement("12", FileType.XLS, test));
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
