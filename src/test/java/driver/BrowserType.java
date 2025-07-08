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
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static utils.ConfigUtils.parseWindowSize;

/**
 * Enum representing supported browser types in the automation framework.
 * <p>
 * Each enum constant implements logic for:
 * <ul>
 *     <li>Generating browser-specific {@link Capabilities}</li>
 *     <li>Creating a local {@link WebDriver} instance using those capabilities</li>
 * </ul>
 * <p>
 * Remote execution using Selenium Grid is supported for all browsers except Safari.
 */
public enum BrowserType {

    /**
     * Google Chrome browser.
     * Supports headless mode and additional Chrome-specific options.
     */
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

    /**
     * Mozilla Firefox browser.
     * Supports headless mode and standard Firefox options.
     */
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

    /**
     * Microsoft Edge browser.
     * Supports headless mode and basic startup options.
     */
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

    /**
     * Apple Safari browser.
     * Does not support headless mode. Only works on macOS with Safari's WebDriver enabled.
     */
    SAFARI {
        @Override
        public Capabilities getOptions(boolean headless, Map<String, Object> additionalCapabilities) {
            SafariOptions options = new SafariOptions();

            if (headless) {
                throw new UnsupportedOperationException("⚠️  Safari does not support headless mode");
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

    private static final Logger logger = LoggerFactory.getLogger(BrowserType.class);

    /**
     * Returns the browser-specific capabilities with optional headless and custom capabilities.
     *
     * @param headless                true if headless mode should be enabled
     * @param additionalCapabilities additional custom capabilities to include
     * @return browser-specific {@link Capabilities}
     */
    public abstract Capabilities getOptions(boolean headless, Map<String, Object> additionalCapabilities);

    /**
     * Creates a local WebDriver instance using the provided capabilities.
     *
     * @param options the capabilities to use when creating the driver
     * @return a local {@link WebDriver} instance
     */
    public abstract WebDriver createDriverFromOptions(Capabilities options);

    /**
     * Creates a WebDriver instance for the current browser type.
     * Supports both local and remote (Selenium Grid) execution.
     *
     * @param headless                whether the browser should run in headless mode
     * @param remote                  whether to use Selenium Grid (remote) execution
     * @param gridUrl                 the Selenium Grid URL
     * @param additionalCapabilities additional capabilities for customization
     * @return a configured {@link WebDriver} instance
     * @throws MalformedURLException if the grid URL is invalid
     */
    public WebDriver createDriver(boolean headless, boolean remote, String gridUrl, Map<String, Object> additionalCapabilities) throws MalformedURLException {
        logger.info("🚀 Starting {} browser | headless: {} | remote: {} | gridUrl: {}", this.name(), headless, remote, gridUrl);
        Capabilities options = getOptions(headless, additionalCapabilities);
        if (remote) {
            return new RemoteWebDriver(URI.create(gridUrl).toURL(), options);
        } else {
            return createDriverFromOptions(options);
        }
    }

    /**
     * Returns a comma-separated list of all supported browser names.
     *
     * @return supported browser names (e.g., "CHROME, FIREFOX, EDGE, SAFARI")
     */
    public static String supportedBrowsers() {
        return Arrays.stream(values()).map(Enum::name).collect(Collectors.joining(", "));
    }

    /**
     * Returns the corresponding {@link BrowserType} enum constant for the given string.
     *
     * @param browser the browser name (case-insensitive)
     * @return the matching {@link BrowserType}
     * @throws RuntimeException if the browser is not supported
     */
    public static BrowserType fromString(String browser) {
        try {
            return valueOf(browser.toUpperCase());
        } catch (IllegalArgumentException e) {
            String message = "❌ Unsupported browser: " + browser + ". Supported browsers: " + supportedBrowsers();
            logger.error(message);
            throw new RuntimeException(message, e);
        }
    }
}
