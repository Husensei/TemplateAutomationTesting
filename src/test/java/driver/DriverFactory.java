package driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.EnvironmentManager;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * DriverFactory is a utility class that provides thread-safe management of WebDriver instances.
 * It supports both local and remote driver execution, configurable via environment variables.
 * <p>
 * Example environment variables:
 * <ul>
 *     <li>{@code browser} – the browser type (e.g., "chrome", "firefox")</li>
 *     <li>{@code headless} – true to run browser in headless mode</li>
 *     <li>{@code remote} – true to run tests on Selenium Grid</li>
 *     <li>{@code grid.url} – URL of the Selenium Grid</li>
 *     <li>{@code implicit.wait} – implicit wait time in seconds</li>
 * </ul>
 */
public class DriverFactory {

    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);

    /**
     * ThreadLocal driver that automatically initializes using createDriver on first access.
     */
    private static final ThreadLocal<WebDriver> driverThreadLocal = ThreadLocal.withInitial(() -> {
        WebDriver driver = createDriver();
        logger.info("✅ WebDriver initialized automatically for thread: {}", Thread.currentThread().getName());
        return driver;
    });

    /**
     * Returns the WebDriver for the current thread.
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Builds a new WebDriver instance based on environment settings.
     */
    public static WebDriver createDriver() {
        final String browserName = EnvironmentManager.get("browser", "chrome");
        final boolean isHeadless = Boolean.parseBoolean(EnvironmentManager.get("headless", "false"));
        final boolean isRemote = Boolean.parseBoolean(EnvironmentManager.get("remote", "false"));
        final String gridUrl = EnvironmentManager.get("grid.url", "http://localhost:4444");
        final long implicitWaitSeconds = Long.parseLong(EnvironmentManager.get("implicit.wait", "10"));

        Map<String, Object> additionalCapabilities = new HashMap<>();
        // additionalCapabilities.put("acceptInsecureCerts", true);

        try {
            BrowserType browser = BrowserType.fromString(browserName);
            WebDriver driver = browser.createDriver(isHeadless, isRemote, gridUrl, additionalCapabilities);
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds((implicitWaitSeconds)));
            logger.info("✅ WebDriver started successfully.");
            return driver;
        } catch (MalformedURLException e) {
            logger.error("⚠️ Invalid grid URL: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid Selenium Grid URL", e);
        } catch (WebDriverException e) {
            logger.error("⚠️ WebDriver error occurred: {}", e.getMessage(), e);
            throw new RuntimeException("WebDriver error", e);
        } catch (Exception e) {
            logger.error("❌ Unexpected error while starting WebDriver: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    /**
     * Explicitly start a new driver and override the current ThreadLocal.
     * Useful if you want to restart in the same thread.
     */
    public static void startDriver() {
        WebDriver driver = createDriver();
        driverThreadLocal.set(driver);
        logger.info("✅ WebDriver manually started for thread: {}", Thread.currentThread().getName());
    }

    /**
     * Quits the current WebDriver instance and removes it from ThreadLocal.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                if (driver instanceof RemoteWebDriver && ((RemoteWebDriver) driver).getSessionId() == null) {
                    logger.warn("Session already closed.");
                } else {
                    driver.quit();
                    logger.info("🛑 WebDriver quit successfully for thread: {}", Thread.currentThread().getName());
                }
            } catch (Exception e) {
                logger.error("⚠️ Error quitting WebDriver: {}", e.getMessage(), e);
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    /**
     * Resets the WebDriver by quitting current and starting a new one.
     */
    public static void resetDriver() {
        quitDriver();
        startDriver();
    }
}
