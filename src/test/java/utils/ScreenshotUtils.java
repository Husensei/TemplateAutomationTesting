package utils;

import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {

    private static final Logger logger = LoggerUtils.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = EnvironmentManager.get("screenshot.dir", "target/screenshots/");

    public static void captureScreenshot(WebDriver driver, String scenarioName) {
        if (!(driver instanceof TakesScreenshot)) {
            logger.warn("❌ Driver does not support taking screenshots.");
            return;
        }

        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
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
