package steps.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.example.GoogleSearchPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GoogleSearchSteps {

    GoogleSearchPage googleSearchPage;

    public GoogleSearchSteps() {
        googleSearchPage = new GoogleSearchPage();
    }

    @Given("I am on the Google homepage")
    public void iAmOnTheGoogleHomepage() {
        googleSearchPage.goToHomePage();
    }

    @When("I search for {string}")
    public void iSearchFor(String keyword) {
        googleSearchPage.searchFor(keyword);
    }

    @Then("I should see results related to {string}")
    public void iShouldSeeResultsRelatedTo(String keyword) {
        assertTrue(googleSearchPage.isResultRelevant(keyword), "Results do not contain expected keyword");
    }
}
