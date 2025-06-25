package automationFramework.tests;

import automationFramework.BrowserFactory;
import automationFramework.pages.AccountsPage;
import automationFramework.pages.AccountsPage.FileType;
import automationFramework.pages.LoginPage;
import automationFramework.utils.ConfigReader;
import automationFramework.utils.ExtentReportManager;
import automationFramework.utils.LoggerUtil;
import automationFramework.utils.ScreenshotUtil;
import automationFramework.utils.EmailSender;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.MediaEntityBuilder;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AccountsTestNG {

	private WebDriver driver;
	private ExtentReports extent;
	private ExtentTest test;
	private boolean loginSuccess = false;
	private AccountsPage accountsPage;
	private List<File> attachments = new ArrayList<>();

	@BeforeClass
	public void setUp() {
		LoggerUtil.log("🔧 Launching browser and initializing report...");

		this.driver = BrowserFactory.startBrowser(); // ✅ Assign to class-level driver
		driver.manage().window().maximize();

		extent = ExtentReportManager.getReportInstance();

		LoggerUtil.log("🌐 Navigating to application URL...");
		driver.get(ConfigReader.getAppURL());

		test = extent.createTest("🔐 Login Before Accounts Tests");

		LoginPage loginPage = new LoginPage(driver);
		loginSuccess = loginPage.performLogin(test);

		if (!loginSuccess) {
			test.fail("❌ Login failed. Halting tests.");
			Assert.fail("Login failed. Stopping execution.");
		} else {
			test.pass("✅ Login successful.");
			accountsPage = new AccountsPage(driver);
		}
	}

	private void runDownloadTest(String description, Runnable action) {
		test = extent.createTest(description);
		try {
			long start = System.currentTimeMillis();
			action.run();
			test.pass("✅ " + description + " completed in " + (System.currentTimeMillis() - start) + " ms");
		} catch (Exception e) {
			test.fail("❌ " + description + " failed: " + e.getMessage());
			Assert.fail(e.getMessage());
		}
	}

	@Test(priority = 1)
	public void testAccountSummary() {
		runDownloadTest("Account Summary Test", () -> {
			accountsPage.clickAccountsTab(test);
			accountsPage.waitForDataToLoad(test);
			accountsPage.scrollToViewBalanceButton(test);
			accountsPage.clickViewBalanceButton(test);
			accountsPage.closeBalanceModal(test);
		});
	}

	@Test(priority = 2)
	public void downloadOneMonthStatementPdf() {
		runDownloadTest("Download 1 Month PDF", () -> accountsPage.downloadStatement("1 Month", FileType.PDF, test));
	}

	@Test(priority = 3)
	public void downloadThreeMonthStatementPdf() {
		runDownloadTest("Download 3 Months PDF", () -> accountsPage.downloadStatement("3 Months", FileType.PDF, test));
	}

	@Test(priority = 4)
	public void downloadOneMonthStatementXls() {
		runDownloadTest("Download 1 Month XLS", () -> accountsPage.downloadStatement("1 Month", FileType.XLS, test));
	}

	@Test(priority = 5)
	public void downloadThreeMonthStatementXls() {
		runDownloadTest("Download 3 Months XLS", () -> accountsPage.downloadStatement("3 Months", FileType.XLS, test));
	}

	@Test(priority = 6)
	public void downloadCustom6MonthsXsl() {
		runDownloadTest("Download Custom 6 Months XLS",
				() -> accountsPage.downloadCustomStatement("6", FileType.XLS, test));
	}

	@Test(priority = 7)
	public void downloadCustom12MonthsPdf() {
		runDownloadTest("Download Custom 12 Months PDF",
				() -> accountsPage.downloadCustomStatement("12", FileType.PDF, test));
	}

	@Test(priority = 8)
	public void downloadCustom6MonthsPdf() {
		runDownloadTest("Download Custom 6 Months PDF",
				() -> accountsPage.downloadCustomStatement("6", FileType.PDF, test));
	}

	@Test(priority = 9)
	public void downloadCustom12MonthsXsl() {
		runDownloadTest("Download Custom 12 Months XLS",
				() -> accountsPage.downloadCustomStatement("12", FileType.XLS, test));
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
				test.fail("❌ Test Failed but screenshot failed to attach: " + e.getMessage());
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

			int wait = 0;
			while (!reportFile.exists() && wait < 5000) {
				Thread.sleep(500);
				wait += 500;
			}

			if (reportFile.exists()) {
				LoggerUtil.log("📧 Sending report via email...");

				String[] recipients = { "rm4577302@gmail.com", "rajumallick4152@live.com",
						"raju@lcodetechnologies.com" };

				attachments.add(reportFile);

				String emailBody = "Hi,\n\nPlease find the attached automation test report.\n\n"
						+ "DISCLAIMER: This message contains privileged/confidential info. "
						+ "If you're not the intended recipient, please delete and notify the sender.\n\n"
						+ "Thanks,\nSupriya";

				EmailSender.sendEmailWithMultipleAttachments(recipients, "Automation Test Report", emailBody,
						attachments);
			} else {
				LoggerUtil.log("❌ Report file not found: " + reportPath);
			}
		} catch (Exception e) {
			LoggerUtil.log("❌ Error sending report: " + e.getMessage());
		}
	}
}
