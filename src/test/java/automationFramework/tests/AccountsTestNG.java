package automationFramework.tests;

import automationFramework.BrowserFactory;
import automationFramework.pages.AccountsPage;
import automationFramework.pages.AccountsPage.FileType;
import automationFramework.pages.LoginPage;
import automationFramework.utils.ExtentReportManager;
import automationFramework.utils.ScreenshotUtil;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.MediaEntityBuilder;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

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
		driver = BrowserFactory.startBrowser("chrome");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

		// Report setup
		extent = ExtentReportManager.getReportInstance();

		// Load config.properties
		prop = new Properties();
		InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
		if (input == null) {
			throw new Exception("config.properties not found!");
		}
		prop.load(input);

		// Perform login
		String url = prop.getProperty("appURL");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");

		test = extent.createTest("Login to App");
		LoginPage loginPage = new LoginPage(driver);
		loginPage.performLogin(url, username, password);
		test.pass("Login successful");

		// Initialize AccountsPage
		accountsPage = new AccountsPage(driver);
	}

	@Test(priority = 1)
	public void testAccountSummary() {
		test = extent.createTest("Account Summary Test");
		accountsPage.clickAccountsTab();
		accountsPage.waitForDataToLoad();
		accountsPage.scrollToViewBalanceButton();
		accountsPage.clickViewBalanceButton();
		accountsPage.closeBalanceModal();
		test.pass("Account Summary and Balance Check completed.");
	}

	@Test(priority = 2)
	public void downloadOneMonthStatementPdf() {
		test = extent.createTest("Download 1 Month PDF Statement");
		accountsPage.downloadStatement("1 Month", FileType.PDF);
		test.pass("Downloaded 1 month PDF");
	}

	@Test(priority = 3)
	public void downloadThreeMonthStatementPdf() {
		test = extent.createTest("Download 3 Month PDF Statement");
		accountsPage.downloadStatement("3 Months", FileType.PDF);
		test.pass("Downloaded 3 months PDF");
	}

	@Test(priority = 4)
	public void downloadOneMonthStatementXls() {
		test = extent.createTest("Download 1 Month XLS Statement");
		accountsPage.downloadStatement("1 Month", FileType.XLS);
		test.pass("Downloaded 1 month XLS");
	}

	@Test(priority = 5)
	public void downloadThreeMonthStatementXls() {
		test = extent.createTest("Download 3 Month XLS Statement");
		accountsPage.downloadStatement("3 Months", FileType.XLS);
		test.pass("Downloaded 3 months XLS");
	}

	@Test(priority = 6)
	public void downloaddownloadCustomStatement6MonthsXsl() {
		test = extent.createTest("Download Custom 6 Months XLS");
		accountsPage.downloadCustomStatement("6", FileType.XLS);
		test.pass("Downloaded custom 6 months XLS");
	}

	@Test(priority = 7)
	public void downloaddownloadCustomStatement12MonthsPdf() {
		test = extent.createTest("Download Custom 12 Months PDF");
		accountsPage.downloadCustomStatement("12", FileType.PDF);
		test.pass("Downloaded custom 12 months PDF");
	}

	@Test(priority = 8)
	public void downloaddownloadCustomStatement6MonthsPdf() {
		test = extent.createTest("Download Custom 6 Months PDF");
		accountsPage.downloadCustomStatement("6", FileType.PDF);
		test.pass("Downloaded custom 6 months PDF");
	}

	@Test(priority = 9)
	public void downloaddownloadCustomStatement12MonthsXsl() {
		test = extent.createTest("Download Custom 12 Months XLS");
		accountsPage.downloadCustomStatement("12", FileType.XLS);
		test.pass("Downloaded custom 12 months XLS");
	}

	@AfterMethod
	public void captureFailureScreenshot(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			String screenshotPath = ScreenshotUtil.captureScreenshot(driver, result.getName());
			try {
				test.fail("Test Failed: " + result.getThrowable(),
						MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (result.getStatus() == ITestResult.SKIP) {
			test.skip("Test Skipped: " + result.getThrowable());
		}
	}

	@AfterClass
	public void tearDown() {
		// Browser বন্ধ করবি না – debugging এর জন্য open থাকুক
		// if (driver != null) {
		// driver.quit();
		// }
		extent.flush();
	}
}
