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
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

		extent = ExtentReportManager.getReportInstance();

		prop = new Properties();
		InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
		if (input == null)
			throw new Exception("config.properties not found!");
		prop.load(input);

		String url = prop.getProperty("appURL");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");

		test = extent.createTest("Login to Application");
		LoginPage loginPage = new LoginPage(driver);
		loginPage.performLogin(url, username, password,test);
		test.pass("✅ Login successful");

		accountsPage = new AccountsPage(driver);
		LoggerUtil.log("✅ Setup complete.");
	}

	@Test(priority = 1)
	public void testAccountSummary() {
		test = extent.createTest("Account Summary Test");
		accountsPage.clickAccountsTab();
		accountsPage.waitForDataToLoad();
		accountsPage.scrollToViewBalanceButton();
		accountsPage.clickViewBalanceButton();
		accountsPage.closeBalanceModal();
		test.pass("✅ Account Summary verified.");
	}

	@Test(priority = 2)
	public void downloadOneMonthStatementPdf() {
		test = extent.createTest("Download 1 Month PDF");
		accountsPage.downloadStatement("1 Month", FileType.PDF);
		test.pass("✅ 1 month PDF downloaded");
	}

	@Test(priority = 3)
	public void downloadThreeMonthStatementPdf() {
		test = extent.createTest("Download 3 Month PDF");
		accountsPage.downloadStatement("3 Months", FileType.PDF);
		test.pass("✅ 3 months PDF downloaded");
	}

	@Test(priority = 4)
	public void downloadOneMonthStatementXls() {
		test = extent.createTest("Download 1 Month XLS");
		accountsPage.downloadStatement("1 Month", FileType.XLS);
		test.pass("✅ 1 month XLS downloaded");
	}

	@Test(priority = 5)
	public void downloadThreeMonthStatementXls() {
		test = extent.createTest("Download 3 Month XLS");
		accountsPage.downloadStatement("3 Months", FileType.XLS);
		test.pass("✅ 3 months XLS downloaded");
	}

	@Test(priority = 6)
	public void downloadCustom6MonthsXsl() {
		test = extent.createTest("Download Custom 6 Months XLS");
		accountsPage.downloadCustomStatement("6", FileType.XLS);
		test.pass("✅ Custom 6 months XLS downloaded");
	}

	@Test(priority = 7)
	public void downloadCustom12MonthsPdf() {
		test = extent.createTest("Download Custom 12 Months PDF");
		accountsPage.downloadCustomStatement("12", FileType.PDF);
		test.pass("✅ Custom 12 months PDF downloaded");
	}

	@Test(priority = 8)
	public void downloadCustom6MonthsPdf() {
		test = extent.createTest("Download Custom 6 Months PDF");
		accountsPage.downloadCustomStatement("6", FileType.PDF);
		test.pass("✅ Custom 6 months PDF downloaded");
	}

	@Test(priority = 9)
	public void downloadCustom12MonthsXsl() {
		test = extent.createTest("Download Custom 12 Months XLS");
		accountsPage.downloadCustomStatement("12", FileType.XLS);
		test.pass("✅ Custom 12 months XLS downloaded");
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
			String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReports/ExtentReport.html";
			File reportFile = new File(reportPath);

			// Wait up to 5 seconds for file to be generated
			int wait = 0;
			while (!reportFile.exists() && wait < 5000) {
				Thread.sleep(500);
				wait += 500;
			}

			if (reportFile.exists()) {
				LoggerUtil.log("📧 Sending report via email...");
				String[] recipients = { "rm4577302@gmail.com", "rajumallick4152@live.com",
						"raju@lcodetechnologies.com" };
				EmailSender.sendEmailWithAttachment(recipients, "Accounts Automation Report",
						"Hi,\n\nPlease find the attached Accounts module automation report.\n\nThanks,\nSupriya",
						reportFile);
			} else {
				LoggerUtil.log("❌ Report file not found at path: " + reportPath);
			}
		} catch (Exception e) {
			LoggerUtil.log("❌ Email sending failed: " + e.getMessage());
		}
	}
}
