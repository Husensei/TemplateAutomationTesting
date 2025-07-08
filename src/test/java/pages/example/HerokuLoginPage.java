package pages.example;

import driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HerokuLoginPage {

    private final WebDriver driver;
    private final By inputUsername = By.id("username");
    private final By inputPassword = By.id("password");
    private final By loginButton = By.xpath("//button[contains(.,'Login')]");
    private final By successMessage = By.xpath("//div[@class='flash success']");
    private final By errorMessage = By.xpath("//div[@class='flash error']");

    public HerokuLoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void userIsOnTheLoginPage() {
        driver.get("https://the-internet.herokuapp.com/login");
    }

    public void enterUsername(String username) {
        driver.findElement(inputUsername).clear();
        driver.findElement(inputUsername).sendKeys(username);
    }

    public void enterPassword(String password) {
        driver.findElement(inputPassword).clear();
        driver.findElement(inputPassword).sendKeys(password);
    }

    public void clickLoginButton() {
        driver.findElement(loginButton).click();
    }

    public boolean isSecureAreaVisible() {
        return driver.findElement(successMessage).isDisplayed();
    }

    public boolean isErrorMessageVisible() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
        return driver.findElement(errorMessage).isDisplayed();
    }
}
