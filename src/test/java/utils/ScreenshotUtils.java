package utils;

import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for capturing and saving screenshots during test execution.
 * <p>
 * Screenshots are saved to a configurable directory and also attached to the Allure report.
 */
public class ScreenshotUtils {

    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = EnvironmentManager.get("screenshot.dir", "target/screenshots/");

    /**
     * Captures a screenshot of the current browser window.
     * <p>
     * The screenshot file is saved in a directory configured via the {@code screenshot.dir} environment property
     * (defaults to {@code target/screenshots/} if not set). The filename includes the sanitized scenario name,
     * timestamp, and current thread ID to avoid collisions.
     * <p>
     * The screenshot is also attached to the Allure report for enhanced test reporting.
     *
     * @param driver       the WebDriver instance to take the screenshot from; must implement {@link TakesScreenshot}
     * @param scenarioName the name of the current test scenario, used in the screenshot filename and Allure attachment
     */
    public static void captureScreenshot(WebDriver driver, String scenarioName) {
        if (!(driver instanceof TakesScreenshot ts)) {
            logger.warn("❌ Driver does not support taking screenshots.");
            return;
        }

        try {
            File source = ts.getScreenshotAs(OutputType.FILE);

            String threadId = String.valueOf(Thread.currentThread().threadId());
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String safeScenarioName = scenarioName.replaceAll("[^a-zA-Z0-9]", "_");
            String fileName = String.format("screenshot_%s_%s_%s.png", safeScenarioName, timestamp, threadId);

            File destination = new File(SCREENSHOT_DIR, fileName);
            FileUtils.copyFile(source, destination);

            Allure.addAttachment("Screenshot - " + scenarioName, new ByteArrayInputStream(ts.getScreenshotAs(OutputType.BYTES)));

            logger.info("📸 Screenshot saved: {}", destination.getAbsolutePath());
        } catch (IOException e) {
            logger.error("❌ Error saving screenshot: {}", e.getMessage(), e);
        }
    }
}
