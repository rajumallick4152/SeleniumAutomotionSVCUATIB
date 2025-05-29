package automationFramework;

import automationFramework.pages.AccountsPage;
import org.openqa.selenium.WebDriver;
import java.time.Duration;

public class AccountsPageTestCase {

    private WebDriver driver;
    private AccountsPage accountsPage;

    public AccountsPageTestCase(WebDriver driver) {
        this.driver = driver;
        this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        this.accountsPage = new AccountsPage(driver);
    }

    public void checkAccountSummary() {
        try {
            System.out.println("Starting Account Summary Test...");

            accountsPage.clickAccountsTab();
            accountsPage.waitForDataToLoad();
            accountsPage.scrollToViewBalanceButton();
            accountsPage.clickViewBalanceButton();
            accountsPage.closeBalanceModal();

            System.out.println("Account Summary Test completed successfully.");

        } catch (Exception e) {
            System.out.println("Error during Account Summary Test.");
            e.printStackTrace();
        }
    }
}
