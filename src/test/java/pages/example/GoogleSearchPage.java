package pages.example;

import driver.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class GoogleSearchPage {

    private final WebDriver driver;

    @FindBy(name = "q")
    private WebElement searchBox;

    public GoogleSearchPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void goToHomePage() {
        DriverFactory.getDriver().get("https://www.google.com");
    }

    public void searchFor(String keyword) {
        searchBox.sendKeys(keyword + "\n");
    }

    public boolean isResultRelevant(String keyword) {
        return DriverFactory.getDriver().getPageSource().toLowerCase().contains(keyword.toLowerCase());
    }
}
