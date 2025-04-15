package driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

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
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        boolean isHeadless = System.getProperty("headless", "false").equalsIgnoreCase("true");

        try {
            switch (browser) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments(
                            "--start-maximized",
                            "--disable-infobars",
                            "--disable-extensions",
                            "--disable-popup-blocking",
                            "--disable-notifications",
                            "--remote-allow-origins=*"
                    );
                    if (isHeadless) {
                        System.out.println(">>> Running Chrome in headless mode");
                        chromeOptions.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080");
                    }
                    driver = new ChromeDriver(chromeOptions);
                    break;

                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    if (isHeadless) {
                        System.out.println(">>> Running Firefox in headless mode");
                        firefoxOptions.addArguments("--headless", "--width=1920", "--height=1080");
                    }
                    driver = new FirefoxDriver(firefoxOptions);
                    break;

                case "edge":
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.addArguments(
                            "--start-maximized",
                            "--disable-infobars",
                            "--disable-extensions",
                            "--disable-popup-blocking",
                            "--disable-notifications"
                    );
                    if (isHeadless) {
                        System.out.println(">>> Running Edge in headless mode");
                        edgeOptions.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080");
                    }
                    driver = new EdgeDriver(edgeOptions);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browser);
            }

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
