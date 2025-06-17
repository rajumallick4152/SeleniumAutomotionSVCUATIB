package automationFramework.tests;

import automationFramework.BrowserFactory;
import automationFramework.pages.AccountsPage;
import automationFramework.pages.AccountsPage.FileType;
import automationFramework.pages.LoginPage;
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
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class AccountsTestNG {

	private WebDriver driver;
	private Properties prop;
	private AccountsPage accountsPage;
	private ExtentReports extent;
	private ExtentTest test;

	@BeforeClass
	public void setUp() throws Exception {
		LoggerUtil.log("🔧 Starting browser and loading properties...");
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

		String url = prop.getProperty("appURL");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");

		test = extent.createTest("Login to Application");
		new LoginPage(driver).performLogin(url, username, password, test);
		test.pass("✅ Login successful");

		accountsPage = new AccountsPage(driver);
		LoggerUtil.log("✅ Setup complete.");
	}

	private void runDownloadTest(String description, Runnable action) {
		test = extent.createTest(description);
		long start = System.currentTimeMillis();
		action.run();
		test.pass("✅ " + description + " completed in " + (System.currentTimeMillis() - start) + " ms");
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
	public void captureFailureScreenshot(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			String screenshotPath = ScreenshotUtil.captureScreenshot(driver, result.getName());
			try {
				test.fail("❌ Test Failed: " + result.getThrowable(),
						MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
			} catch (Exception e) {
				test.fail("❌ Test Failed: " + result.getThrowable());
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
			File reportFile = new File(System.getProperty("user.dir") + "/test-output/ExtentReports/ExtentReport.html");
			for (int i = 0; i < 10 && !reportFile.exists(); i++)
				Thread.sleep(500);

			if (reportFile.exists()) {
				LoggerUtil.log("📧 Sending report via email...");
				String[] recipients = { "rm4577302@gmail.com", "rajumallick4152@live.com",
						"raju@lcodetechnologies.com" };
				EmailSender.sendEmailWithAttachment(recipients, "Accounts Automation Report",
						"Hi,\n\nPlease find the attached Accounts module automation report.\n\nThanks,\nSupriya",
						reportFile);
			} else {
				LoggerUtil.log("❌ Report file not found.");
			}
		} catch (Exception e) {
			LoggerUtil.log("❌ Email sending failed: " + e.getMessage());
		}
	}
}
