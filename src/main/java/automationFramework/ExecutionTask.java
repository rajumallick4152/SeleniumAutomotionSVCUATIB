package automationFramework;

import org.openqa.selenium.WebDriver;

public class ExecutionTask implements Runnable {
	private String driverName;
	private WebDriver driver;

	public ExecutionTask(String driverName) {
		this.driverName = driverName;
		driver = BrowserFactory.startBrowser(driverName);
	}

	@Override
	public void run() {
		System.out.println("Running test on browser: " + driverName);

		LoginTestCase objLoginTestCase = new LoginTestCase(driver);
		objLoginTestCase.LoginTest();

		/*
		 * AccountsPageTestCase objAccountsPageTestCase = new
		 * AccountsPageTestCase(driver); objAccountsPageTestCase.checkAccountSummary();
		 */

		PaymentHistoryTest objPaymentHistoryTest = new PaymentHistoryTest(driver);
		objPaymentHistoryTest.checkPaymentHistory();

		/*
		 * ServicesTest objServicesTest = new ServicesTest(driver);
		 * objServicesTest.checkServicesTab();
		 */

		/*
		 * MyChequesTest objMyChequesTest = new MyChequesTest(driver);
		 * objMyChequesTest.CheckMyChequesTabTab();
		 * 
		 * MyDebitcardsTest objMyDebitcardsTest = new MyDebitcardsTest(driver);
		 * objMyDebitcardsTest.CheckMyDebitcardsTab();
		 */

	}
}
