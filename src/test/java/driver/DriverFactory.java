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

    protected URL startStandaloneGrid() {
        int port = PortProber.findFreePort();
        try {
            Main.main(
                    new String[] {
                            "standalone",
                            "--port",
                            String.valueOf(port),
                            "--selenium-manager",
                            "true",
                            "--enable-managed-downloads",
                            "true",
                            "--log-level",
                            "WARNING"
                    });
            return new URL("http://localhost:" + port);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
                    String remoteUrl = config.getProperty("remote.url");
                    switch (browser) {
                        case "chrome":
                            ChromeOptions chromeOptions = new ChromeOptions();
                            if (isHeadless) {
                                chromeOptions.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080");
                            }
                            driver = new RemoteWebDriver(new URL(URI.create(remoteUrl).toASCIIString()), chromeOptions);
                            break;
                        case "firefox":
                            FirefoxOptions firefoxOptions = new FirefoxOptions();
                            if (isHeadless) {
                                firefoxOptions.addArguments("--headless", "--width=1920", "--height=1080");
                            }
                            driver = new RemoteWebDriver(new URL(URI.create(remoteUrl).toASCIIString()), firefoxOptions);
                            break;
                        case "edge":
                            EdgeOptions edgeOptions = new EdgeOptions();
                            if (isHeadless) {
                                edgeOptions.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080");
                            }
                            driver = new RemoteWebDriver(new URL(URI.create(remoteUrl).toASCIIString()), edgeOptions);
                            break;
                        default:
                            throw new IllegalArgumentException("Unsupported remote browser: " + browser);
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException("Error creating RemoteWebDriver: " + e.getMessage());
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
                        throw new IllegalArgumentException("Unsupported local browser: " + browser);
                }
            }
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
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
