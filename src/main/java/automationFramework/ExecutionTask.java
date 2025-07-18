/*
 * package automationFramework;
 * 
 * import org.openqa.selenium.WebDriver;
 * 
 * //import automationFramework.pages.LogoutPage;
 * 
 * public class ExecutionTask implements Runnable { private String driverName;
 * private WebDriver driver;
 * 
 * public ExecutionTask(String driverName) { this.driverName = driverName;
 * WebDriver driver = BrowserFactory.startBrowser(); //
 * browser=config.properties
 * 
 * }
 * 
 * @Override public void run() { System.out.println("Running test on browser: "
 * + driverName);
 * 
 * LoginTestCase objLoginTestCase = new LoginTestCase(driver);
 * objLoginTestCase.LoginTest();
 * 
 * 
 * AccountsPageTestCase objAccountsPageTestCase = new
 * AccountsPageTestCase(driver); objAccountsPageTestCase.checkAccountSummary();
 * 
 * 
 * 
 * PaymentHistoryTest objPaymentHistoryTest = new PaymentHistoryTest(driver);
 * objPaymentHistoryTest.checkPaymentHistory();
 * 
 * 
 * 
 * ServicesTest objServicesTest = new ServicesTest(driver);
 * objServicesTest.checkServicesTab();
 * 
 * MyChequesTest objMyChequesTest = new MyChequesTest(driver);
 * objMyChequesTest.CheckMyChequesTabTab();
 * 
 * MyDebitcardsTest objMyDebitcardsTest = new MyDebitcardsTest(driver);
 * objMyDebitcardsTest.CheckMyDebitcardsTab();
 * 
 * HelpTest objHelpTest = new HelpTest(driver); objHelpTest.CheckHelpTab();
 * 
 * SettingsTest objSettingsTest = new SettingsTest(driver);
 * objSettingsTest.CheckSettingsTab();
 * 
 * MoreTest objMoreTest = new MoreTest(driver); objMoreTest.CheckMoreTab();
 * 
 * 
 * LogoutPage logoutPage = new LogoutPage(driver); logoutPage.performLogout();
 * 
 * } }
 */