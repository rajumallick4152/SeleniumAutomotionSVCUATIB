package automationFramework;

import automationFramework.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoginTestCase {
	private WebDriver driver;
	private Properties prop;

	public LoginTestCase(WebDriver driver) {
		this.driver = driver;
		this.prop = new Properties();
		this.driver.manage().window().maximize();

		try {
			InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
			if (input == null) {
				throw new IOException("config.properties not found in classpath");
			}
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void LoginTest() {
		try {
			String url = prop.getProperty("appURL");
			String username = prop.getProperty("username");
			String password = prop.getProperty("password");

			LoginPage loginPage = new LoginPage(driver);
			loginPage.performLogin(url, username, password);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
