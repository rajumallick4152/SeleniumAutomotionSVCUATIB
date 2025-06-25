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

	private static final String SCREENSHOT_DIR = "test-output/screenshots/";

	// ✅ Save screenshot as file and return full path
	public static String captureScreenshotAsFile(WebDriver driver, String screenshotName) {
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String fullPath = SCREENSHOT_DIR + screenshotName + "_" + timestamp + ".png";

		try {
			File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			File dest = new File(fullPath);
			Files.createDirectories(dest.getParentFile().toPath());
			Files.copy(src.toPath(), dest.toPath());
			return fullPath;
		} catch (IOException e) {
			System.err.println("❌ Failed to capture screenshot to file: " + e.getMessage());
			return null;
		}
	}

	// ✅ Capture Base64 from screenshot file (recommended for reports)
	public static String captureScreenshotAndSaveBase64(WebDriver driver, String screenshotName) {
		String filePath = captureScreenshotAsFile(driver, screenshotName);
		if (filePath == null)
			return "";

		try {
			byte[] imageBytes = Files.readAllBytes(new File(filePath).toPath());
			return Base64.getEncoder().encodeToString(imageBytes);
		} catch (IOException e) {
			System.err.println("❌ Failed to convert screenshot to Base64: " + e.getMessage());
			return "";
		}
	}

	// ✅ (Optional) Directly return Base64 without saving (for fast inline use)
	public static String captureScreenshotAsBase64(WebDriver driver) {
		try {
			byte[] imageBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
			return Base64.getEncoder().encodeToString(imageBytes);
		} catch (Exception e) {
			System.err.println("❌ Failed to capture Base64 screenshot: " + e.getMessage());
			return "";
		}
	}

	// ✅ Combined legacy support method
	public static String captureScreenshot(WebDriver driver, String screenshotName) {
		return captureScreenshotAsFile(driver, screenshotName);
	}
}
