package driver;

import dev.husensei.Main;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.net.PortProber;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static Properties config;

    static {
        config = new Properties();
        try (InputStream inputStream = DriverFactory.class.getClassLoader().getResourceAsStream("config/config.properties")) {
            if (inputStream != null) {
                config.load(inputStream);
            } else {
                throw new RuntimeException("config.properties file not found.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            startDriver();
        }
        return driverThreadLocal.get();
    }

    public static void startDriver() {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        boolean isRemote = Boolean.parseBoolean(System.getProperty("remote", "false"));
        WebDriver driver = null;

        try {
            if (isRemote) {
                try {
                    URL gridUrl = URI.create(System.getProperty("grid.url", "http://localhost:4444")).toURL();

                    switch (browser) {
                        case "chrome":
                            System.out.println(">>> TESTS");
                            ChromeOptions remoteChromeOptions = new ChromeOptions();
                            if (isHeadless) {
                                remoteChromeOptions.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
                            }
                            driver = new RemoteWebDriver(gridUrl, remoteChromeOptions);
                            break;

                        case "firefox":
                            FirefoxOptions remoteFirefoxOptions = new FirefoxOptions();
                            if (isHeadless) {
                                remoteFirefoxOptions.addArguments("--headless", "--width=1920", "--height=1080");
                            }
                            driver = new RemoteWebDriver(gridUrl, remoteFirefoxOptions);
                            break;

                        case "edge":
                            EdgeOptions remoteEdgeOptions = new EdgeOptions();
                            if (isHeadless) {
                                remoteEdgeOptions.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
                            }
                            driver = new RemoteWebDriver(gridUrl, remoteEdgeOptions);
                            break;

                        default:
                            throw new IllegalArgumentException("Unsupported remote browser: " + browser);
                    }

                } catch (MalformedURLException e) {
                    System.out.println("Invalid Grid URL: " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Failed to initialize remote WebDriver: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                switch (browser) {
                    case "chrome":
                        WebDriverManager.chromedriver().setup();
                        ChromeOptions chromeOptions = new ChromeOptions();
                        chromeOptions.addArguments(
                                "--disable-extensions",
                                "--disable-popup-blocking",
                                "--disable-notifications",
                                "--remote-allow-origins=*"
                        );
                        if (isHeadless) {
                            chromeOptions.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080");
                        }
                        driver = new ChromeDriver(chromeOptions);
                        break;
                    case "firefox":
                        WebDriverManager.firefoxdriver().setup();
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        if (isHeadless) {
                            firefoxOptions.addArguments("--headless", "--width=1920", "--height=1080");
                        }
                        driver = new FirefoxDriver(firefoxOptions);
                        break;
                    case "edge":
                        WebDriverManager.edgedriver().setup();
                        EdgeOptions edgeOptions = new EdgeOptions();
                        edgeOptions.addArguments(
                                "--disable-extensions",
                                "--disable-popup-blocking",
                                "--disable-notifications"
                        );
                        if (isHeadless) {
                            edgeOptions.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080");
                        }
                        driver = new EdgeDriver(edgeOptions);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported local browser: " + browser);
                }
            }
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            driverThreadLocal.set(driver);
        } catch (Exception e) {
            System.out.println("Error starting WebDriver: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                driverThreadLocal.remove();
            } catch (Exception e) {
                System.out.println("Error quitting WebDriver: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
