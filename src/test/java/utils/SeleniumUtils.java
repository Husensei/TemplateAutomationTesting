package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class SeleniumUtils {

    private static final int DEFAULT_WAIT_TIME = 10;
    private static final Logger log = LoggerFactory.getLogger(SeleniumUtils.class);

    public static WebElement waitForElementToBeVisible(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIME));

        try {
            return  wait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            log.error("e: ", e);
            return null;
        }
    }

    public static WebElement waitForElementToBeClickable(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIME));
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            log.error("e: ", e);
            return null;
        }
    }

    public static Boolean waitForUrlToBe(WebDriver driver, String url) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIME));
        try {
            return wait.until(ExpectedConditions.urlToBe(url));
        } catch (Exception e) {
            System.out.println("e: " + e);
            return null;
        }
    }
}
