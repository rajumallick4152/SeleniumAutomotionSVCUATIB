package automationFramework.tests;

import automationFramework.BrowserFactory;
import automationFramework.pages.AccountsPage;
import automationFramework.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class AccountsTestNG {

	private WebDriver driver;
	private Properties prop;
	private AccountsPage accountsPage;

	private String statementRange;
	private String fileFormat;

	@BeforeClass
	public void setUp() throws Exception {
		driver = BrowserFactory.startBrowser("chrome");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

		// Load config.properties
		prop = new Properties();
		InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
		if (input == null) {
			throw new Exception("config.properties not found!");
		}
		prop.load(input);

		// Read statement values from config
		statementRange = prop.getProperty("statement.range");
		fileFormat = prop.getProperty("statement.format");

		// Perform login
		String url = prop.getProperty("appURL");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");

		LoginPage loginPage = new LoginPage(driver);
		loginPage.performLogin(url, username, password);

		// Initialize AccountsPage
		accountsPage = new AccountsPage(driver);
	}

	@Test
	public void testAccountSummary() {
		System.out.println("Starting Account Summary Test...");
		accountsPage.clickAccountsTab();
		accountsPage.waitForDataToLoad();
		accountsPage.scrollToViewBalanceButton();
		accountsPage.clickViewBalanceButton();
		accountsPage.closeBalanceModal();
		System.out.println("Account Summary Test completed successfully.");
	}

	@Test(priority = 2)
	public void testStatementDownloadAndShare() {
		System.out.println("Starting Detailed Statement Test...");

		accountsPage.clickAccountsTab();
		accountsPage.waitForDataToLoad();

		accountsPage.clickDetailedStatement();

		// Use values from config.properties
		accountsPage.selectStatementRange(statementRange);
		accountsPage.downloadStatement(fileFormat);

		System.out.println("Detailed Statement Test completed successfully.");
	}

	@AfterClass
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}
}
