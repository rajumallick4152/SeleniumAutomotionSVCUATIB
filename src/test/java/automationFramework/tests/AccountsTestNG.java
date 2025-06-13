package automationFramework.tests;

import automationFramework.BrowserFactory;
import automationFramework.pages.AccountsPage;
import automationFramework.pages.AccountsPage.FileType;
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

		// Perform login
		String url = prop.getProperty("appURL");
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");

		LoginPage loginPage = new LoginPage(driver);
		loginPage.performLogin(url, username, password);

		// Initialize AccountsPage
		accountsPage = new AccountsPage(driver);
	}

	@Test(priority = 1)
	public void testAccountSummary() {
		System.out.println("Starting Account Summary Test...");
		accountsPage.clickAccountsTab();
		accountsPage.waitForDataToLoad();
		accountsPage.scrollToViewBalanceButton();
		accountsPage.clickViewBalanceButton();
		accountsPage.closeBalanceModal();
		System.out.println("Account Summary And Balance Check Test completed successfully.");
	}

	@Test(priority = 2)
	public void downloadOneMonthStatementPdf() {
		accountsPage.downloadStatement("1 Month", FileType.PDF);
	}

	@Test(priority = 3)
	public void downloadThreeMonthStatementPdf() {
		accountsPage.downloadStatement("3 Months", FileType.PDF);
	}

	@Test(priority = 4)
	public void downloadOneMonthStatementXls() {
		accountsPage.downloadStatement("1 Month", FileType.XLS);
	}

	@Test(priority = 5)
	public void downloadThreeMonthStatementXls() {
		accountsPage.downloadStatement("3 Months", FileType.XLS);
	}

	@Test(priority = 6)
	public void downloaddownloadCustomStatement6MonthsXsl() {
		accountsPage.downloadCustomStatement("6", FileType.XLS);

	}

	@Test(priority = 7)
	public void downloaddownloadCustomStatement12MonthsPdf() {
		accountsPage.downloadCustomStatement("12", FileType.PDF);

	}

	@Test(priority = 8)
	public void downloaddownloadCustomStatement6MonthsPdf() {
		accountsPage.downloadCustomStatement("6", FileType.PDF);

	}

	@Test(priority = 9)
	public void downloaddownloadCustomStatement12MonthsXsl() {
		accountsPage.downloadCustomStatement("12", FileType.XLS);

	}

	// @AfterClass
	/*
	 * public void tearDown() { if (driver != null) { driver.quit(); } }
	 */
}
