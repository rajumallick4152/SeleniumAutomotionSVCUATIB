package automationFramework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager {

	private static ExtentReports extent;

	public static ExtentReports getReportInstance() {
		if (extent == null) {
			// Report path
			String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReports/ExtentReport.html";
			ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

			// Theme & branding
			sparkReporter.config().setTheme(Theme.STANDARD);
			sparkReporter.config().setDocumentTitle("LCode Automation Report");
			sparkReporter.config().setReportName("SVC Internet Banking Test Execution");

			// Add LCode logo using JavaScript
			String js = "document.addEventListener('DOMContentLoaded', function() {"
					+ "var logo = document.createElement('img');"
					+ "logo.src = 'https://raw.githubusercontent.com/rajumallick4152/SeleniumAutomotionSVCUATIB/main/image/Lcode.png';"
					+ "logo.style.height = '40px';" + "logo.style.objectFit = 'contain';"
					+ "logo.style.marginRight = '10px';" + "logo.style.display = 'inline-block';"
					+ "var target = document.querySelector('.nav-logo');"
					+ "if (target) { target.innerHTML = ''; target.appendChild(logo); }" + "});";
			sparkReporter.config().setJs(js);

			extent = new ExtentReports();
			extent.attachReporter(sparkReporter);

			// System/environment info
			extent.setSystemInfo("Tester", "Supriya");
			extent.setSystemInfo("Organization", "LCode");
			extent.setSystemInfo("Environment", "UAT");
			extent.setSystemInfo("OS", System.getProperty("os.name"));
			extent.setSystemInfo("Browser", "Chrome");
		}
		return extent;
	}
}
