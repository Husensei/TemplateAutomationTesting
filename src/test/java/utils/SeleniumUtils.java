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
 * Utility class providing common Selenium WebDriver wait operations.
 * <p>
 * The default wait timeout can be configured via the environment property
 * {@code wait.time.seconds}. If not set or invalid, it defaults to 10 seconds.
 * <p>
 * This class provides explicit wait methods for various common conditions such as
 * element visibility, clickability, presence, URL, text presence, and title checks.
 */
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

    /**
     * Creates a {@link WebDriverWait} instance with the configured timeout.
     *
     * @param driver the WebDriver instance
     * @return a new WebDriverWait with configured timeout
     */
    private static WebDriverWait createWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME_SECONDS));
    }

    /**
     * Waits until the specified WebElement is visible on the page.
     *
     * @param driver  the WebDriver instance
     * @param element the WebElement to wait for visibility
     * @return the visible WebElement, or {@code null} if timeout or error occurs
     */
    public static WebElement waitForElementToBeVisible(WebDriver driver, WebElement element) {
        try {
            return createWait(driver).until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            log.error("❌ Error waiting for element to be visible: {} on page: {}", element, driver.getCurrentUrl(), e);
            return null;
        }
    }

    /**
     * Waits until the specified WebElement is clickable.
     *
     * @param driver  the WebDriver instance
     * @param element the WebElement to wait for clickability
     * @return the clickable WebElement, or {@code null} if timeout or error occurs
     */
    public static WebElement waitForElementToBeClickable(WebDriver driver, WebElement element) {
        try {
            return createWait(driver).until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            log.error("❌ Error waiting for element to be clickable: {} on page: {}", element, driver.getCurrentUrl(), e);
            return null;
        }
    }

    /**
     * Waits until the current URL matches the specified URL.
     *
     * @param driver the WebDriver instance
     * @param url    the URL to wait for
     * @return {@code true} if URL matches within timeout, {@code null} if error occurs
     */
    public static Boolean waitForUrlToBe(WebDriver driver, String url) {
        try {
            return createWait(driver).until(ExpectedConditions.urlToBe(url));
        } catch (Exception e) {
            log.error("❌ Error waiting for URL to be '{}'. Current URL: {}", url, driver.getCurrentUrl(), e);
            return null;
        }
    }

    /**
     * Waits until an element located by the specified locator is present in the DOM.
     *
     * @param driver  the WebDriver instance
     * @param locator the locator to find the element
     * @return the found WebElement, or {@code null} if timeout or error occurs
     */
    public static WebElement waitForElementPresence(WebDriver driver, By locator) {
        try {
            return createWait(driver).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception e) {
            log.error("❌ Error waiting for presence of element: {}", locator, e);
            return null;
        }
    }

    /**
     * Waits until the specified text is present in the given WebElement.
     *
     * @param driver  the WebDriver instance
     * @param element the WebElement to check text presence
     * @param text    the text to wait for
     * @return {@code true} if text is present within timeout, {@code false} otherwise
     */
    public static boolean waitForTextToBePresent(WebDriver driver, WebElement element, String text) {
        try {
            return createWait(driver).until(ExpectedConditions.textToBePresentInElement(element, text));
        } catch (Exception e) {
            log.error("❌ Error waiting for text '{}' to be present in element: {}", text, element, e);
            return false;
        }
    }

    /**
     * Waits until the page title contains the specified text.
     *
     * @param driver the WebDriver instance
     * @param title  the text expected to be contained in the page title
     * @return {@code true} if title contains the text within timeout, {@code false} otherwise
     */
    public static boolean waitForTitleToContain(WebDriver driver, String title) {
        try {
            return createWait(driver).until(ExpectedConditions.titleContains(title));
        } catch (Exception e) {
            log.error("❌ Error waiting for title to contain '{}'", title, e);
            return false;
        }
    }
}
