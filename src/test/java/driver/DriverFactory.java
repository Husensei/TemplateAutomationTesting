package driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import utils.EnvironmentManager;
import utils.LoggerUtils;

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

    private static final Logger logger = LoggerUtils.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    /**
     * Returns the current thread's WebDriver instance.
     * If no instance exists, it initializes a new one using {@link #startDriver()}.
     *
     * @return the WebDriver instance for the current thread
     */
    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            startDriver();
        }
        return driverThreadLocal.get();
    }

    /**
     * Initializes and starts a WebDriver instance based on environment configurations.
     * Supports local or remote execution, headless mode, and custom capabilities.
     * The created WebDriver is stored in a ThreadLocal for thread-safe access.
     *
     * @throws RuntimeException if WebDriver fails to initialize
     */
    public static void startDriver() {
        final String browserName = EnvironmentManager.get("browser", "chrome");
        final boolean isHeadless = Boolean.parseBoolean(EnvironmentManager.get("headless", "false"));
        final boolean isRemote = Boolean.parseBoolean(EnvironmentManager.get("remote", "false"));
        final String gridUrl = EnvironmentManager.get("grid.url", "http://localhost:4444");
        final long implicitWaitSeconds = Long.parseLong(EnvironmentManager.get("implicit.wait", "10"));

        Map<String, Object> additionalCapabilities = new HashMap<>();
//         ===== EXAMPLE ADDITIONAL CAPABILITIES =====
//         if (Boolean.parseBoolean(EnvironmentManager.get("acceptInsecureCerts", "true"))) {
//            additionalCapabilities.put("acceptInsecureCerts", true);
//        }

        try {
            BrowserType browser = BrowserType.fromString(browserName);
            WebDriver driver = browser.createDriver(isHeadless, isRemote, gridUrl, additionalCapabilities);
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds((implicitWaitSeconds)));
            logger.info("✅ WebDriver started successfully.");
            driverThreadLocal.set(driver);
        } catch (MalformedURLException e) {
            logger.error("⚠️ Invalid grid URL: {}", e.getMessage(), e);
        } catch (WebDriverException e) {
            logger.error("⚠️ WebDriver error occurred: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("❌ Unexpected error while starting WebDriver: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    /**
     * Quits the current thread's WebDriver instance and removes it from the ThreadLocal store.
     * Ensures that browser sessions are properly closed and resources are freed.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("🛑 WebDriver quit successfully");
            } catch (Exception e) {
                logger.error("⚠️ Error quitting WebDriver: {}", e.getMessage(), e);
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
}
