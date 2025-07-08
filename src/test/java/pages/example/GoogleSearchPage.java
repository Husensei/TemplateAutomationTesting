package pages.example;

import driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class GoogleSearchPage {

    private final WebDriver driver;
    private By searchBox = By.name("q");

    public GoogleSearchPage(WebDriver driver) {
        this.driver = driver;
    }

    public void goToHomePage() {
        driver.get("https://www.google.com");
    }

    public void searchFor(String keyword) {
        driver.findElement(searchBox).sendKeys(keyword + "\n");
    }

    public boolean isResultRelevant(String keyword) {
        return driver.getPageSource().toLowerCase().contains(keyword.toLowerCase());
    }
}
