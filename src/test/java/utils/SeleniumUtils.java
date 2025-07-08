package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Utility class providing common Selenium WebDriver explicit waits.
 * <p>
 * Configurable default wait timeout via "wait.time.seconds" environment property.
 * Falls back to 10 seconds if not set.
 */
public class SeleniumUtils {

    private static final Logger logger = LoggerFactory.getLogger(SeleniumUtils.class);
    private static final int WAIT_TIME_SECONDS;

    static {
        String waitTimeStr = EnvironmentManager.get("wait.time.seconds", "10");
        int waitTime;
        try {
            waitTime = Integer.parseInt(waitTimeStr);
        } catch (NumberFormatException e) {
            logger.warn("⚠️ Invalid wait.time.seconds '{}', falling back to 10 seconds", waitTimeStr);
            waitTime = 10;
        }
        WAIT_TIME_SECONDS = waitTime;
        logger.info("⏱️ SeleniumUtils wait time set to {} seconds", WAIT_TIME_SECONDS);
    }

    /**
     * Creates a {@link WebDriverWait} instance with the configured timeout.
     *
     * @param driver the WebDriver instance
     * @return a new WebDriverWait with configured timeout
     */
    private static WebDriverWait createWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME_SECONDS));
    }

    private static WebDriverWait createWait(WebDriver driver, int timeoutInSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
    }

    /**
     * Waits until element located by the locator is visible.
     */
    public static WebElement waitForElementToBeVisible(WebDriver driver, By locator) {
        return waitForElementToBeVisible(driver, locator, WAIT_TIME_SECONDS);
    }

    public static WebElement waitForElementToBeVisible(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            return createWait(driver, timeoutInSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            logger.error("❌ Timeout waiting for element to be visible: {} on page: {}", locator, driver.getCurrentUrl(), e);
            return null;
        }
    }

    /**
     * Waits until element located by the locator is clickable.
     */
    public static WebElement waitForElementToBeClickable(WebDriver driver, By locator) {
        return waitForElementToBeClickable(driver, locator, WAIT_TIME_SECONDS);
    }

    public static WebElement waitForElementToBeClickable(WebDriver driver, By locator, int timeoutInSeconds) {
        try {
            return createWait(driver, timeoutInSeconds).until(ExpectedConditions.elementToBeClickable(locator));
        } catch (Exception e) {
            logger.error("❌ Timeout waiting for element to be clickable: {} on page: {}", locator, driver.getCurrentUrl(), e);
            return null;
        }
    }

    /**
     * Waits until the current URL matches expected.
     */
    public static boolean waitForUrlToBe(WebDriver driver, String url) {
        try {
            return createWait(driver).until(ExpectedConditions.urlToBe(url));
        } catch (Exception e) {
            logger.error("❌ Timeout waiting for URL to be '{}'. Current URL: {}", url, driver.getCurrentUrl(), e);
            return false;
        }
    }

    /**
     * Waits until presence in the DOM.
     */
    public static WebElement waitForElementPresence(WebDriver driver, By locator) {
        try {
            return createWait(driver).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception e) {
            logger.error("❌ Timeout waiting for presence of element: {}", locator, e);
            return null;
        }
    }

    /**
     * Waits until text is present in the specified element.
     */
    public static boolean waitForTextToBePresent(WebDriver driver, WebElement element, String text) {
        if (element == null) {
            logger.warn("⚠️ Provided element is null. Cannot wait for text '{}'", text);
            return false;
        }
        try {
            return createWait(driver).until(ExpectedConditions.textToBePresentInElement(element, text));
        } catch (Exception e) {
            logger.error("❌ Timeout waiting for text '{}' to be present in element: {}", text, element, e);
            return false;
        }
    }

    /**
     * Waits until the title contains expected text.
     */
    public static boolean waitForTitleToContain(WebDriver driver, String title) {
        try {
            return createWait(driver).until(ExpectedConditions.titleContains(title));
        } catch (Exception e) {
            logger.error("❌ Timeout waiting for title to contain '{}'", title, e);
            return false;
        }
    }
}
