package automationFramework;

import org.openqa.selenium.WebDriver;

public class ExecutionTask implements Runnable{
	private String driverName;
	private WebDriver driver;
	
	public ExecutionTask(String driverName) {
		this.driverName = driverName;
		driver = BrowserFactory.startBrowser(driverName);
	}

	@Override
	public void run() {
		
		LoginTestCase objLoginTestCase = new LoginTestCase(driver);
		objLoginTestCase.executrTest();
		
		
		
	} 
	
	

}
