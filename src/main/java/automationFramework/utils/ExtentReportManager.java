
package automationFramework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager {

	private static ExtentReports extent;

	public static ExtentReports getReportInstance() {
		if (extent == null) {
			String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReports/ExtentReport.html";
			ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

			// Config settings
			sparkReporter.config().setTheme(Theme.STANDARD);
			sparkReporter.config().setReportName("LCode Test Execution");
			sparkReporter.config().setDocumentTitle("LCode Automation Report");

			// ✅ Inject company logo into header using JavaScript
			String js = "document.addEventListener('DOMContentLoaded', function() {"
					+ "var logo = document.createElement('img');"
					+ "logo.src = 'https://raw.githubusercontent.com/rajumallick4152/SeleniumAutomotionSVCUATIB/main/image/Lcode.png';"
					+ "logo.style.height = '30px';" + "logo.style.objectFit = 'contain';"
					+ "logo.style.marginRight = '10px';" + "logo.style.display = 'inline-block';"
					+ "var target = document.querySelector('.nav-logo');"
					+ "if (target) { target.innerHTML = ''; target.appendChild(logo); }" + "});";

			sparkReporter.config().setJs(js);

			extent = new ExtentReports();
			extent.attachReporter(sparkReporter);
		}
		return extent;
	}
}
