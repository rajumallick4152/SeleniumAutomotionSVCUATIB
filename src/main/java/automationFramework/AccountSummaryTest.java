
package automationFramework;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class AccountSummaryTest extends BaseTest {

	public WebDriver driver;

	public AccountSummaryTest(WebDriver driver) {
		this.driver = driver;
		if (this.driver != null) {
			this.driver.manage().window().maximize();
			this.driver.manage().deleteAllCookies();
			wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		}
	}

	@Override
	public void checkBalance() {
		try {
			// Debug: Print any overlays
			List<WebElement> overlays = driver
					.findElements(By.xpath("//*[contains(@style,'z-index') and contains(@style,'position: fixed')]"));
			for (WebElement overlay : overlays) {
				System.out.println("Found overlay: " + overlay.getAttribute("outerHTML"));
			}

			// Wait for spinner overlay to disappear
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("ngx-spinner-overlay")));

			// Wait and click the Accounts tab
			WebElement accountsTab = wait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Accounts']")));
			accountsTab.click();

			System.out.println("Accounts tab clicked. Waiting for data to load...");

			// Wait for balance or table to appear
			// wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[contains(text(),'Account
			// Summary')]")));

			// System.out.println("Account Summary is visible. Test passed.");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
