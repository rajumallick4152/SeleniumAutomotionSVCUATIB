package automationFramework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportManager {

	private static ExtentReports extent;

	public static ExtentReports getReportInstance() {
		if (extent == null) {
			ExtentSparkReporter spark = new ExtentSparkReporter("test-output/ExtentReport.html");
			spark.config().setReportName("Automation Test Report");
			spark.config().setDocumentTitle("Test Results");
			spark.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.DARK);

			extent = new ExtentReports();
			extent.attachReporter(spark);
			extent.setSystemInfo("OS", System.getProperty("os.name"));
			extent.setSystemInfo("Tester", "Supriya");
		}
		return extent;
	}
}
