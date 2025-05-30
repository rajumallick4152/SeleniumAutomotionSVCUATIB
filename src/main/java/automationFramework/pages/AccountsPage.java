package automationFramework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class AccountsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final Logger logger = LoggerFactory.getLogger(AccountsPage.class);

    public AccountsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private void waitForSpinnerToDisappear() {
        try {
            logger.info("Waiting for spinner to disappear...");
           Thread.sleep(10);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("ngx-spinner-overlay")));
            logger.info("Spinner disappeared.");
        } catch (Exception e) {
            logger.warn("Spinner did not disappear properly or wasn't found.");
        }
    }

    private void clickWithRetry(By locator) {
        for (int i = 0; i < 3; i++) {
            try {
                waitForSpinnerToDisappear();
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                logger.info("Attempting to click element: {}", locator);
                element.click();
                logger.info("Click successful.");
                waitForSpinnerToDisappear();
                break;
            } catch (Exception e) {
                logger.warn("Click failed, retrying ({}/3): {}", (i + 1), e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public void clickAccountsTab() {
        logger.info("Clicking Accounts Tab");
        clickWithRetry(By.xpath("//span[text()='Accounts']"));
    }

    public void waitForDataToLoad() {
        logger.info("Waiting for data to load...");
        waitForSpinnerToDisappear();
    }

    public void scrollToViewBalanceButton() {
        logger.info("Scrolling to 'View Balance Components' button");
        waitForSpinnerToDisappear();
        WebElement viewBalanceBtn = wait.until(ExpectedConditions
                .presenceOfElementLocated(By.xpath("//span[contains(text(),'View Balance Components')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", viewBalanceBtn);
        logger.info("Scrolled to button.");
    }

    public void clickViewBalanceButton() {
        logger.info("Clicking 'View Balance Components' button");
        waitForSpinnerToDisappear();
        WebElement viewBalanceBtn = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//span[contains(text(),'View Balance Components')]")));
        try {
            viewBalanceBtn.click();
            logger.info("Button click successful.");
        } catch (Exception e) {
            logger.warn("Using JS click for 'View Balance Components'");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewBalanceBtn);
        }
        waitForSpinnerToDisappear();
    }

    public void closeBalanceModal() {
        logger.info("Closing balance modal");
        waitForSpinnerToDisappear();
        try {
            //Thread.sleep(3000);
            WebElement closeBtn = wait.until(ExpectedConditions
                    .elementToBeClickable(By.xpath("//span[text()='Close' and contains(@class, 'p-button-label')]")));
            try {
                closeBtn.click();
                logger.info("Modal closed.");
            } catch (Exception e) {
                logger.warn("Using JS click to close modal.");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);
            }
        } catch (Exception ex) {
            logger.error("Failed to close balance modal: {}", ex.getMessage());
        }
        waitForSpinnerToDisappear();
    }
}
