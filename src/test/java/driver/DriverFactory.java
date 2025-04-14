package driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver == null) {
            startDriver();
        }
        return driver;
    }

    public static void startDriver() {
        try {
            // Set up Chrome Options
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--no-sandbox");
            options.addArguments("--start-maximized");
            options.addArguments("--disable-gpu");
            // options.addArguments("--headless"); // Run in headless mode
            // options.addArguments("window-size=1920x1080"); // Ensures headless tests have a reasonable window size
            // options.addArguments("window-size=360x800"); // Responsive test for Android phones
            // options.addArguments("window-size=800x1280"); // Responsive test for Tablets

            // Set up ChromeDiver with the configured options
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        } catch (Exception e) {
            System.out.println("Error starting WebDriver: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void quitDriver() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("Error quitting WebDriver: " + e.getMessage());
                e.printStackTrace();
            } finally {
                driver = null;
            }
        }
    }
}
