/*
 * package automationFramework.tests;
 * 
 * import automationFramework.BrowserFactory; import
 * automationFramework.pages.LoginPage; import
 * automationFramework.utils.ExtentReportManager; import
 * automationFramework.utils.LoggerUtil;
 * 
 * import com.aventstack.extentreports.ExtentReports; import
 * com.aventstack.extentreports.ExtentTest;
 * 
 * import org.openqa.selenium.WebDriver; import org.testng.Assert; import
 * org.testng.annotations.*;
 * 
 * import java.time.Duration;
 * 
 * public class LoginTestNG {
 * 
 * private WebDriver driver; private ExtentReports extent; private ExtentTest
 * test; private LoginPage loginPage;
 * 
 * @BeforeClass public void setUp() {
 * LoggerUtil.log("🔧 Launching browser for login test...");
 * 
 * driver = BrowserFactory.startBrowser(); // ✅ Class-level driver
 * driver.manage().window().maximize();
 * driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
 * 
 * extent = ExtentReportManager.getReportInstance(); loginPage = new
 * LoginPage(driver); }
 * 
 * @Test(priority = 0) public void testLogin() { test =
 * extent.createTest("🔐 Login Test");
 * 
 * boolean loginSuccess = loginPage.performLogin(test);
 * 
 * if (loginSuccess) { test.pass("✅ Login successful"); } else {
 * test.fail("❌ Login failed"); Assert.fail("Login failed."); } }
 * 
 * @AfterClass public void tearDown() { extent.flush();
 * LoggerUtil.log("🧹 Extent report flushed."); // Optionally quit browser:
 * driver.quit(); } }
 */