package automationFramework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PaymentHistoryPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public PaymentHistoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    private void waitForSpinnerToDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("ngx-spinner-overlay")));
        } catch (Exception e) {
            System.out.println("Spinner may not have disappeared properly.");
        }
    }

    public void clickPaymentHistoryTab() {
        waitForSpinnerToDisappear();
        WebElement paymentHistoryTab = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Payment History']"))
        );
        paymentHistoryTab.click();
        waitForSpinnerToDisappear();
    }

    public void waitForPaymentData() {
        waitForSpinnerToDisappear();
    }

    public void clickViewPaymentDetailsButton() {
        waitForSpinnerToDisappear();
        WebElement viewButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'View Payment Details')]"))
        );
        try {
            viewButton.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewButton);
        }
        waitForSpinnerToDisappear();
    }

    public void closePaymentDetailsModal() {
        waitForSpinnerToDisappear();
        try {
            Thread.sleep(2000); // Optional delay for animation
            WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[text()='Close' and contains(@class, 'p-button-label')]"))
            );
            try {
                closeBtn.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);
            }
        } catch (Exception ex) {
            System.out.println("Failed to close payment details modal.");
        }
        waitForSpinnerToDisappear();
    }
}
