package automationFramework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class AccountsPage {

	private WebDriver driver;
	private WebDriverWait wait;

	public AccountsPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
	}

	public void clickAccountsTab() {
	    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("ngx-spinner-overlay")));  // Wait before click
	    WebElement accountsTab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Accounts']")));
	    accountsTab.click();
	    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("ngx-spinner-overlay")));  // Wait after click
	}


	public void waitForDataToLoad() {
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("ngx-spinner-overlay")));
	}

	public void scrollToViewBalanceButton() {
		WebElement viewBalanceBtn = wait.until(ExpectedConditions
				.presenceOfElementLocated(By.xpath("//span[contains(text(),'View Balance Components')]")));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", viewBalanceBtn);
	}

	public void clickViewBalanceButton() {
		WebElement viewBalanceBtn = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//span[contains(text(),'View Balance Components')]")));
		try {
			viewBalanceBtn.click();
		} catch (Exception e) {
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewBalanceBtn);
		}
	}

	public void closeBalanceModal() {
		try {
			Thread.sleep(3000); // optional buffer for modal animation
			WebElement closeBtn = wait.until(ExpectedConditions
					.elementToBeClickable(By.xpath("//span[text()='Close' and contains(@class, 'p-button-label')]")));
			try {
				closeBtn.click();
			} catch (Exception e) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);
			}
		} catch (Exception ex) {
			System.out.println("Failed to close balance modal.");
		}
	}
}
