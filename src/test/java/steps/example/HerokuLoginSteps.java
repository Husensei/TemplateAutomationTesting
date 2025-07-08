package steps.example;

import driver.DriverFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import pages.example.HerokuLoginPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HerokuLoginSteps {

    private final HerokuLoginPage herokuLoginPage;

    public HerokuLoginSteps() {
        this.herokuLoginPage = new HerokuLoginPage(DriverFactory.getDriver());
    }

    @Given("the user is on the Heroku login page")
    public void theUserIsOnTheHerokuLoginPage() {
        herokuLoginPage.userIsOnTheLoginPage();
    }

    @When("the user enters correct username and password")
    public void theUserEntersCorrectUsernameAndPassword() {
        herokuLoginPage.enterUsername("tomsmith");
        herokuLoginPage.enterPassword("SuperSecretPassword!");
    }

    @And("clicks on the login button")
    public void clicksOnTheLoginButton() {
        herokuLoginPage.clickLoginButton();
    }

    @Then("the user should be redirected to the secure area")
    public void theUserShouldBeRedirectedToTheSecureArea() {
        assertTrue(herokuLoginPage.isSecureAreaVisible(), "Secure area is not visible!");
    }

    @When("the user enters incorrect username and password")
    public void theUserEntersIncorrectUsernameAndPassword() {
        herokuLoginPage.enterUsername("husensei");
        herokuLoginPage.enterPassword("admin123");
    }

    @Then("the user should see the error message")
    public void theUserShouldSeeTheErrorMessage() {
        assertTrue(herokuLoginPage.isErrorMessageVisible(), "Error message is not visible!");
    }
}
