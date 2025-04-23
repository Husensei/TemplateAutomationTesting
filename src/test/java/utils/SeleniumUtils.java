package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class SeleniumUtils {

    private static final Logger log = LoggerFactory.getLogger(SeleniumUtils.class);
    private static final int WAIT_TIME_SECONDS;

    static {
        String waitTimeStr = EnvironmentManager.get("wait.time.seconds", "10");
        int waitTime;
        try {
            waitTime = Integer.parseInt(waitTimeStr);
        } catch (NumberFormatException e) {
            log.warn("⚠️ Invalid wait.time.seconds '{}', falling back to 10 seconds", waitTimeStr);
            waitTime = 10;
        }
        WAIT_TIME_SECONDS = waitTime;
        log.info("⏱️ SeleniumUtils wait time set to {} seconds", WAIT_TIME_SECONDS);
    }

    private static WebDriverWait createWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME_SECONDS));
    }

    public static WebElement waitForElementToBeVisible(WebDriver driver, WebElement element) {
        try {
            return createWait(driver).until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            log.error("❌ Error waiting for element to be visible: {} on page: {}", element, driver.getCurrentUrl(), e);
            return null;
        }
    }

    public static WebElement waitForElementToBeClickable(WebDriver driver, WebElement element) {
        try {
            return createWait(driver).until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            log.error("❌ Error waiting for element to be clickable: {} on page: {}", element, driver.getCurrentUrl(), e);
            return null;
        }
    }

    public static Boolean waitForUrlToBe(WebDriver driver, String url) {
        try {
            return createWait(driver).until(ExpectedConditions.urlToBe(url));
        } catch (Exception e) {
            log.error("❌ Error waiting for URL to be '{}'. Current URL: {}", url, driver.getCurrentUrl(), e);
            return null;
        }
    }

    public static WebElement waitForElementPresence(WebDriver driver, By locator) {
        try {
            return createWait(driver).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception e) {
            log.error("❌ Error waiting for presence of element: {}", locator, e);
            return null;
        }
    }

    public static boolean waitForTextToBePresent(WebDriver driver, WebElement element, String text) {
        try {
            return createWait(driver).until(ExpectedConditions.textToBePresentInElement(element, text));
        } catch (Exception e) {
            log.error("❌ Error waiting for text '{}' to be present in element: {}", text, element, e);
            return false;
        }
    }

    public static boolean waitForTitleToContain(WebDriver driver, String title) {
        try {
            return createWait(driver).until(ExpectedConditions.titleContains(title));
        } catch (Exception e) {
            log.error("❌ Error waiting for title to contain '{}'", title, e);
            return false;
        }
    }
}
