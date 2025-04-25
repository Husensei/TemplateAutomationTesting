package steps.example;

import driver.DriverFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import pages.example.HerokuLoginPage;
import utils.LoggerUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HerokuLoginSteps {

    private static final Logger logger = LoggerUtils.getLogger(HerokuLoginSteps.class);
    private final HerokuLoginPage herokuLoginPage;

    public HerokuLoginSteps() {
        this.herokuLoginPage = new HerokuLoginPage(DriverFactory.getDriver());
    }

    @Given("the user is on the Heroku login page")
    public void theUserIsOnTheHerokuLoginPage() {
        logger.info("🧵 [Thread: {}] Running scenario", Thread.currentThread().getName());
        herokuLoginPage.userIsOnTheLoginPage();
    }

    @When("the user enters correct username and password")
    public void theUserEntersCorrectUsernameAndPassword() {
        logger.info("🧵 [Thread: {}] Running scenario", Thread.currentThread().getName());
        herokuLoginPage.enterUsername("tomsmith");
        herokuLoginPage.enterPassword("SuperSecretPassword!");
    }

    @And("clicks on the login button")
    public void clicksOnTheLoginButton() {
        logger.info("🧵 [Thread: {}] Running scenario", Thread.currentThread().getName());
        herokuLoginPage.clickLoginButton();
    }

    @Then("the user should be redirected to the secure area")
    public void theUserShouldBeRedirectedToTheSecureArea() {
        logger.info("🧵 [Thread: {}] Running scenario", Thread.currentThread().getName());
        assertTrue(herokuLoginPage.isSecureAreaVisible(), "Secure area is not visible!");
    }

    @When("the user enters incorrect username and password")
    public void theUserEntersIncorrectUsernameAndPassword() {
        logger.info("🧵 [Thread: {}] Running scenario", Thread.currentThread().getName());
        herokuLoginPage.enterUsername("husensei");
        herokuLoginPage.enterPassword("admin123");
    }

    @Then("the user should see the error message")
    public void theUserShouldSeeTheErrorMessage() {
        logger.info("🧵 [Thread: {}] Running scenario", Thread.currentThread().getName());
        assertTrue(herokuLoginPage.isErrorMessageVisible(), "Error message is not visible!");
    }
}
