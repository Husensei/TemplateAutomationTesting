package pages.example;

import driver.DriverFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utils.SeleniumUtils;

public class ExampleLoginPage {

    @FindBy(id = "username")
    private WebElement inputUsername;

    @FindBy(id = "password")
    private WebElement inputPassword;

    @FindBy(xpath = "//button[contains(.,'Login')]")
    private WebElement loginButton;

    @FindBy(xpath = "//div[@class='flash success']")
    private WebElement successMessage;

    @FindBy(xpath = "//div[@class='flash error']")
    private WebElement errorMessage;

    public ExampleLoginPage() {
        PageFactory.initElements(DriverFactory.getDriver(), this);
    }

    public void userIsOnTheLoginPage() {
        DriverFactory.getDriver().get("https://the-internet.herokuapp.com/login");
    }

    public void enterUsername(String username) {
        inputUsername.clear();
        inputUsername.sendKeys(username);
    }

    public void enterPassword(String password) {
        inputPassword.clear();
        inputPassword.sendKeys(password);
    }

    public void clickLoginButton() {
        loginButton.click();
    }

    public boolean isSecureAreaVisible() {
        return successMessage.isDisplayed();
    }

    public boolean isNotSecureAreVisible() throws InterruptedException {
        Thread.sleep(2000);
        return errorMessage.isDisplayed();
    }
}
