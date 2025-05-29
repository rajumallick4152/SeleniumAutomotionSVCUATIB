package automationFramework;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BaseTest {

	public WebDriver driver;

	public WebDriverWait wait;
	public Properties prop;

	public void setup() throws IOException {
		prop = new Properties();
		FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
		prop.load(fis);

		// String browser = prop.getProperty("browser");
		// driver = BrowserFactory.startBrowser(browser);

		if (driver != null) {
			driver.manage().window().maximize();
			driver.manage().deleteAllCookies();
			wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		}
	}

	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}

	public void executrTest() {

	}
	public void checkBalance() {

	}
}