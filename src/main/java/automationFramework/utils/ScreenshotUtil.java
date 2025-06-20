package automationFramework.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class ScreenshotUtil {

	// ✅ Original method (unchanged) — for backward compatibility
	public static String captureScreenshot(WebDriver driver, String screenshotName) {
		return captureScreenshotAsFile(driver, screenshotName);
	}

	// ✅ Save screenshot as file (for email or disk)
	public static String captureScreenshotAsFile(WebDriver driver, String screenshotName) {
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String path = "test-output/screenshots/" + screenshotName + "_" + timestamp + ".png";

		File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		File dest = new File(path);
		try {
			Files.createDirectories(dest.getParentFile().toPath());
			Files.copy(src.toPath(), dest.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}

	// ✅ Capture screenshot as Base64 (for ExtentReports)
	public static String captureScreenshotAsBase64(WebDriver driver) {
		try {
			byte[] imageBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
			return Base64.getEncoder().encodeToString(imageBytes);
		} catch (Exception e) {
			System.out.println("❌ Failed to capture Base64 screenshot: " + e.getMessage());
			return "";
		}
	}
}
