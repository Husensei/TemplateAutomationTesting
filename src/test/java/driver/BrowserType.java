package driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.slf4j.Logger;
import utils.LoggerUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;

import static utils.ConfigUtils.parseWindowSize;

public enum BrowserType {
    CHROME {
        @Override
        public Capabilities getOptions(boolean headless, Map<String, Object> additionalCapabilities) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments(
                    "--disable-extensions",
                    "--disable-popup-blocking",
                    "--disable-notifications",
                    "--remote-allow-origins=*"
            );

            int[] size = parseWindowSize(System.getProperty("window.size", "1920,1080"));
            options.addArguments("--width=" + size[0], "--height=" + size[1]);

            if (headless) {
                options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
            }

            if (additionalCapabilities != null) {
                additionalCapabilities.forEach(options::setCapability);
            }
            return options;
        }

        @Override
        public WebDriver createDriverFromOptions(Capabilities options) {
            WebDriverManager.chromedriver().setup();
            return new ChromeDriver((ChromeOptions) options);
        }
    },

    FIREFOX {
        @Override
        public Capabilities getOptions(boolean headless, Map<String, Object> additionalCapabilities) {
            FirefoxOptions options = new FirefoxOptions();

            int[] size = parseWindowSize(System.getProperty("window.size", "1920,1080"));

            if (headless) {
                options.addArguments("--headless", "--width=" + size[0], "--height=" + size[1]);
            }

            if (additionalCapabilities != null) {
                additionalCapabilities.forEach(options::setCapability);
            }

            return options;
        }

        @Override
        public WebDriver createDriverFromOptions(Capabilities options) {
            WebDriverManager.firefoxdriver().setup();
            return new FirefoxDriver((FirefoxOptions) options);
        }
    },

    EDGE {
        @Override
        public Capabilities getOptions(boolean headless, Map<String, Object> additionalCapabilities) {
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--disable-extensions", "--disable-popup-blocking", "--disable-notifications");

            int[] size = parseWindowSize(System.getProperty("window.size", "1920,1080"));
            options.addArguments("--width=" + size[0], "--height=" + size[1]);

            if (headless) {
                options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
            }

            if (additionalCapabilities != null) {
                additionalCapabilities.forEach(options::setCapability);
            }

            return options;
        }

        @Override
        public WebDriver createDriverFromOptions(Capabilities options) {
            WebDriverManager.edgedriver().setup();
            return new EdgeDriver((EdgeOptions) options);
        }
    },

    SAFARI {
        @Override
        public Capabilities getOptions(boolean headless, Map<String, Object> additionalCapabilities) {
            SafariOptions options = new SafariOptions();

            if (headless) {
                throw new UnsupportedOperationException("⚠️  Safari does not support headless mode.");
            }

            if (additionalCapabilities != null) {
                additionalCapabilities.forEach(options::setCapability);
            }

            return options;
        }

        @Override
        public WebDriver createDriverFromOptions(Capabilities options) {
            return new SafariDriver((SafariOptions) options);
        }
    };

    private static final Logger logger = LoggerUtils.getLogger(BrowserType.class);

    public abstract Capabilities getOptions(boolean headless, Map<String, Object> additionalCapabilities);
    public abstract WebDriver createDriverFromOptions(Capabilities options);

    public WebDriver createDriver(boolean headless, boolean remote, String gridUrl, Map<String, Object> additionalCapabilities) throws MalformedURLException {
        logger.info("🚀 Starting {} browser | headless: {} | remote: {} | gridUrl: {}", this.name(), headless, remote, gridUrl);
        Capabilities options = getOptions(headless, additionalCapabilities);
        if (remote) {
            return new RemoteWebDriver(URI.create(gridUrl).toURL(), options);
        } else {
            return createDriverFromOptions(options);
        }
    }

    public static BrowserType fromString(String browser) {
        try {
            return valueOf(browser.toUpperCase());
        } catch (IllegalArgumentException e) {
            String message = "❌ Unsupported browser: " + browser + ". Supported browsers: CHROME, FIREFOX, EDGE, SAFARI.";
            logger.error(message);
            throw new RuntimeException(message, e);
        }
    }
}
