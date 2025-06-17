/*
 * package automationFramework.tests;
 * 
 * import automationFramework.BrowserFactory; import
 * automationFramework.pages.LoginPage; import org.openqa.selenium.WebDriver;
 * import org.testng.annotations.*;
 * 
 * import java.io.IOException; import java.io.InputStream; import
 * java.util.Properties;
 * 
 * public class LoginTestNG { private WebDriver driver; private Properties prop;
 * 
 * @BeforeClass public void setUp() throws IOException { driver =
 * BrowserFactory.startBrowser("chrome"); driver.manage().window().maximize();
 * 
 * prop = new Properties(); InputStream input =
 * getClass().getClassLoader().getResourceAsStream("config.properties"); if
 * (input == null) { throw new
 * IOException("config.properties not found in classpath"); } prop.load(input);
 * }
 * 
 * @Test public void testLogin() throws InterruptedException { String url =
 * prop.getProperty("appURL"); String username = prop.getProperty("username");
 * String password = prop.getProperty("password");
 * 
 * LoginPage loginPage = new LoginPage(driver); loginPage.performLogin(url,
 * username, password,test); }
 * 
 * @AfterClass public void tearDown() {
 * 
 * if (driver != null) { driver.quit(); } } }
 */