package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.BrowserLogUtils;
import utils.EnvironmentManager;
import utils.FileManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static driver.DriverFactory.*;
import static org.apache.commons.compress.utils.ArchiveUtils.sanitize;
import static utils.ScreenshotUtils.captureScreenshot;

/**
 * Cucumber Hooks for setting up and tearing down WebDriver sessions and browser logging.
 * <p>
 * This class initializes the driver and logging tools (CDP or BrowserMob Proxy) before each scenario,
 * and handles log capturing, screenshot collection, and Allure report attachment after each scenario.
 */
public class Hooks {

    private static final Logger logger = LoggerFactory.getLogger(Hooks.class);
    private String sanitizedScenarioName;
    private long startTime;
    private static final String LOG_DIR = "target/logs";

    /**
     * Cucumber {@code @Before} hook.
     * <p>
     * Initializes WebDriver, sanitizes the scenario name for log filenames, and enables logging
     * based on the browser type. Also deletes any existing logs for the same scenario name.
     *
     * @param scenario the current Cucumber scenario
     */
    @Before
    public void setUp(Scenario scenario) {
        sanitizedScenarioName = sanitize(scenario.getName()) + "_" + Thread.currentThread().threadId();
        cleanOldLogs(sanitizedScenarioName);
        startTime = System.currentTimeMillis();
        startDriver();

        RemoteWebDriver driver = (RemoteWebDriver) getDriver();
        String browserName = driver.getCapabilities().getBrowserName();

        if (browserName.equalsIgnoreCase("chrome") || browserName.equalsIgnoreCase("edge")) {
            BrowserLogUtils.enableCDPLogging(driver, sanitizedScenarioName);
        } else if (browserName.equalsIgnoreCase("firefox") || browserName.equalsIgnoreCase("safari")) {
            BrowserLogUtils.startProxy();
        }
    }

    /**
     * Cucumber {@code @After} hook.
     * <p>
     * Captures browser logs, screenshots (if scenario failed), and attaches them to the Allure report.
     * Ends the WebDriver session and logs the scenario duration.
     *
     * @param scenario the current Cucumber scenario
     */
    @After
    public void tearDown(Scenario scenario) {
        long duration = System.currentTimeMillis() - startTime;
        logger.info("🕒 Scenario '{}' finished in {} ms", sanitizedScenarioName, duration);

        try {
            if (getDriver() != null) {
                RemoteWebDriver driver = (RemoteWebDriver) getDriver();
                String browserName = driver.getCapabilities().getBrowserName();

                if (scenario.isFailed()) {
                    captureScreenshot(driver, scenario.getName());
                }

                if (browserName.equalsIgnoreCase("firefox") || browserName.equalsIgnoreCase("safari")) {
                    BrowserLogUtils.saveProxyHar(sanitizedScenarioName);
                }

                // Attach logs if present
                attachLogIfExists("console", sanitizedScenarioName + ".log", "Console Logs");
                attachLogIfExists("network", sanitizedScenarioName + ".log", "Network Logs");
                attachLogIfExists("network", sanitizedScenarioName + ".har", "HAR File");
            }
        } finally {
            quitDriver();
        }
    }

    /**
     * Deletes old log files (if any) for the given scenario name to avoid mixing logs from past runs.
     *
     * @param scenarioName the sanitized scenario name
     */
    private void cleanOldLogs(String scenarioName) {
        String[] types = {"console", "network"};
        for (String type : types) {
            deleteIfExists(Paths.get(LOG_DIR, type, scenarioName + ".log").toFile());
            deleteIfExists(Paths.get(LOG_DIR, type, scenarioName + ".har").toFile());
        }
    }

    /**
     * Deletes a file if it exists.
     *
     * @param file the file to be deleted
     */
    private void deleteIfExists(File file) {
        if (file.exists() && file.delete()) {
            logger.debug("🧹 Deleted old log: {}", file.getAbsolutePath());
        }
    }

    /**
     * Attaches a log file to the Allure report if it exists.
     *
     * @param type     the type of log (e.g., "console", "network")
     * @param filename the log file name
     * @param label    the label to display in the Allure report
     */
    private void attachLogIfExists(String type, String filename, String label) {
        String path = Paths.get(LOG_DIR, type, filename).toString();
        File file = new File(path);
        if (file.exists()) {
            FileManager.attachFileToAllure(path, label);
        } else {
            logger.info("📁 No {} found for scenario: {}", label, sanitizedScenarioName);
        }
    }
}
