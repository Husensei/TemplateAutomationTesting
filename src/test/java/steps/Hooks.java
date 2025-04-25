package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import utils.BrowserLogUtils;
import utils.EnvironmentManager;
import utils.FileManager;
import utils.LoggerUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static driver.DriverFactory.*;
import static utils.ScreenshotUtils.captureScreenshot;

public class Hooks {

    private static final Logger logger = LoggerUtils.getLogger(Hooks.class);
    private String sanitizedScenarioName;

    @Before
    public void setUp(Scenario scenario) {
        sanitizedScenarioName = scenario.getName().replaceAll("[^a-zA-Z0-9]", "_") + "_" + Thread.currentThread().threadId();
        startDriver();

        RemoteWebDriver driver = (RemoteWebDriver) getDriver();
        String browserName = driver.getCapabilities().getBrowserName();

        if (browserName.equalsIgnoreCase("chrome") || browserName.equalsIgnoreCase("edge")) {
            BrowserLogUtils.enableCDPLogging(driver, sanitizedScenarioName);
        } else if (browserName.equalsIgnoreCase("firefox") || browserName.equalsIgnoreCase("safari")) {
            BrowserLogUtils.startProxy();
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (getDriver() != null) {
                if (scenario.isFailed()) {
                    captureScreenshot(getDriver(), scenario.getName());
                }

                RemoteWebDriver driver = (RemoteWebDriver) getDriver();
                String browserName = driver.getCapabilities().getBrowserName();

                if (browserName.equalsIgnoreCase("firefox") || browserName.equalsIgnoreCase("safari")) {
                    BrowserLogUtils.saveProxyHar(sanitizedScenarioName);
                }

                String consoleLogPath = Paths.get("target", "logs", "console", sanitizedScenarioName + ".log").toString();
                String networkLogPath = Paths.get("target", "logs", "network", sanitizedScenarioName + ".log").toString();
                String harPath = Paths.get("target", "logs", "network", sanitizedScenarioName + ".har").toString();

                FileManager.attachFileToAllure(consoleLogPath, "Console Logs");
                FileManager.attachFileToAllure(networkLogPath, "Network Logs");

                if (new File(harPath).exists()) {
                    FileManager.attachFileToAllure(harPath, "HAR File");
                } else {
                    logger.info("HAR file not found for scenario: " + sanitizedScenarioName);
                }
            }
        } finally {
            quitDriver();
        }
    }
}
