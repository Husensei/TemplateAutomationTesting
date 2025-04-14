package steps.example;

import driver.DriverFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.example.ExampleLoginPage;

public class ExampleLoginSteps {

    ExampleLoginPage exampleLoginPage;

    public ExampleLoginSteps() {
        exampleLoginPage = new ExampleLoginPage();
    }

    @Given("the user is on the Heroku login page")
    public void theUserIsOnTheHerokuLoginPage() {
        exampleLoginPage.userIsOnTheLoginPage();
    }

    @When("the user enters correct username and password")
    public void theUserEntersCorrectUsernameAndPassword() {
        exampleLoginPage.enterUsername("tomsmith");
        exampleLoginPage.enterPassword("SuperSecretPassword!");
    }

    @And("clicks on the login button")
    public void clicksOnTheLoginButton() {
        exampleLoginPage.clickLoginButton();
    }

    @Then("the user should be redirected to the secure area")
    public void theUserShouldBeRedirectedToTheSecureArea() {
        exampleLoginPage.isSecureAreaVisible();
    }

    @When("the user enters incorrect username and password")
    public void theUserEntersIncorrectUsernameAndPassword() {
        exampleLoginPage.enterUsername("husensei");
        exampleLoginPage.enterPassword("admin123");
    }

    @Then("the user should see the error message")
    public void theUserShouldSeeTheErrorMessage() throws InterruptedException {
        exampleLoginPage.isNotSecureAreVisible();
    }
}
