package driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import utils.EnvironmentManager;
import utils.LoggerUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DriverFactory {

    private static final Logger logger = LoggerUtils.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            startDriver();
        }
        return driverThreadLocal.get();
    }

    public static void startDriver() {
        String browserName = EnvironmentManager.get("browser", "chrome");
        boolean isHeadless = Boolean.parseBoolean(EnvironmentManager.get("headless", "false"));
        boolean isRemote = Boolean.parseBoolean(EnvironmentManager.get("remote", "false"));
        String gridUrl = EnvironmentManager.get("grid.url", "http://localhost:4444");

        Map<String, Object> additionalCapabilities = new HashMap<>();
//         ===== EXAMPLE ADDITIONAL CAPABILITIES =====
//         if (Boolean.parseBoolean(EnvironmentManager.get("acceptInsecureCerts", "true"))) {
//            additionalCapabilities.put("acceptInsecureCerts", true);
//        }

        try {
            BrowserType browser = BrowserType.fromString(browserName);
            WebDriver driver = browser.createDriver(isHeadless, isRemote, gridUrl, additionalCapabilities);
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Long.parseLong(EnvironmentManager.get("implicit.wait", "10"))));
            logger.info("✅ WebDriver started successfully");
            driverThreadLocal.set(driver);
        } catch (MalformedURLException e) {
            logger.error("⚠️ Invalid grid URL: {}", e.getMessage(), e);
        } catch (WebDriverException e) {
            logger.error("⚠️ WebDriver error occurred: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("❌ Unexpected error while starting WebDriver: {}", e.getMessage(), e);
        }
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("🛑 WebDriver quit successfully.");
                driverThreadLocal.remove();
            } catch (Exception e) {
                logger.error("⚠️ Error quitting WebDriver: {}", e.getMessage(), e);
            }
        }
    }
}
