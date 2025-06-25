package automationFramework.listeners;

import automationFramework.utils.ExtentReportManager;
import automationFramework.utils.ScreenshotUtil;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import org.openqa.selenium.WebDriver;
import org.testng.*;

public class TestListener implements ITestListener {

	private static ExtentReports extent = ExtentReportManager.getReportInstance();
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	private static ThreadLocal<WebDriver> driverRef = new ThreadLocal<>();

	public static void setDriver(WebDriver driver) {
		driverRef.set(driver);
	}

	public static ExtentTest getLogger() {
		return test.get();
	}

	@Override
	public void onTestStart(ITestResult result) {
		ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
		test.set(extentTest);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		test.get().log(Status.PASS, "✅ Test Passed");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		test.get().log(Status.FAIL, "❌ Test Failed: " + result.getThrowable());

		WebDriver driver = driverRef.get();
		if (driver != null) {
			String screenshotPath = ScreenshotUtil.captureScreenshot(driver, result.getMethod().getMethodName());
			if (screenshotPath != null) {
				try {
					test.get().addScreenCaptureFromPath(screenshotPath);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onFinish(ITestContext context) {
		extent.flush();
	}
}
