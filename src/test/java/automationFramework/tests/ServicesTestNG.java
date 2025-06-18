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
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class ServicesTestNG {

	private WebDriver driver;
	private Properties prop;
	private ServicesPage servicesPage;
	private ExtentReports extent;
	private ExtentTest test;

	@BeforeClass
	public void setUp() throws Exception {
		LoggerUtil.log("🔧 Launching browser and loading properties...");
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

		test = extent.createTest("🔐 Login to Application");
		new LoginPage(driver).performLogin(url, username, password, test);
		test.pass("✅ Login successful");
		LoggerUtil.log("✅ Login successful");

		LoggerUtil.log("✅ Setup complete.");
	}

	@Test(priority = 1)
	public void testManageSMSFlow() {
		test = extent.createTest("💼 Manage SMS Threshold Test");
		servicesPage = new ServicesPage(driver, test); // ✅ Pass current test instance

		try {
			servicesPage.navigateToManageSMS();
			servicesPage.enterAmountThresholds(); // No argument needed now

			servicesPage.clickSave();
			servicesPage.enterOTP("123456"); // Replace with real OTP if needed
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
				EmailSender.sendEmailWithAttachment(recipients, "Services Automation Report",
						"Hi,\n\nPlease find the attached Services module automation report.\n\nThanks,\nSupriya",
						reportFile);
			} else {
				LoggerUtil.log("❌ Report file not found.");
			}
		} catch (Exception e) {
			LoggerUtil.log("❌ Email sending failed: " + e.getMessage());
		}

		if (driver != null) {
			LoggerUtil.log("🛑 Browser not closed.");
		}
	}
}
