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

	// Centralized spinner wait
	private void waitForSpinnerToDisappear() {
		try {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("ngx-spinner-overlay")));
		} catch (Exception e) {
			System.out.println("Spinner may not have disappeared properly.");
		}
	}

	public void clickAccountsTab() {
		waitForSpinnerToDisappear();
		WebElement accountsTab = wait
				.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Accounts']")));
		accountsTab.click();
		waitForSpinnerToDisappear();
	}

	public void waitForDataToLoad() {
		waitForSpinnerToDisappear();
	}

	public void scrollToViewBalanceButton() {
		waitForSpinnerToDisappear();
		WebElement viewBalanceBtn = wait.until(ExpectedConditions
				.presenceOfElementLocated(By.xpath("//span[contains(text(),'View Balance Components')]")));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", viewBalanceBtn);
	}

	public void clickViewBalanceButton() {
		waitForSpinnerToDisappear();
		WebElement viewBalanceBtn = wait.until(ExpectedConditions
				.elementToBeClickable(By.xpath("//span[contains(text(),'View Balance Components')]")));
		try {
			viewBalanceBtn.click();
		} catch (Exception e) {
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewBalanceBtn);
		}
		waitForSpinnerToDisappear();
	}

	public void closeBalanceModal() {
		waitForSpinnerToDisappear();
		try {
			Thread.sleep(3000); // Optional: allow modal animation to complete
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
		waitForSpinnerToDisappear();
	}
}
