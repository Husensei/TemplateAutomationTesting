package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static driver.DriverFactory.*;

public class Hooks {
    @Before
    public void setUp() {
        System.out.println("Starting WebDriver...");
        startDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        if (getDriver() != null) {
            try {
                if (scenario.isFailed()) {
                    TakesScreenshot ts = (TakesScreenshot) getDriver();
                    File source = ts.getScreenshotAs(OutputType.FILE);

                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
                    String screenshotName = "screenshot_" + timestamp + ".png";
                    String screenshotPath = "src/test/resources/screenshots/" + screenshotName;

                    File destination = new File(screenshotPath);
                    FileUtils.copyFile(source, destination);

                    System.out.println("Screenshot saved: " + screenshotPath);
                }
            } catch (IOException e) {
                System.out.println("Error capturing screenshot: " + e.getMessage());
            } finally {
                System.out.println("Name   : " + scenario.getName());
                System.out.println("Status : " + scenario.getStatus());
                System.out.println("Quitting WebDriver...");
                quitDriver();
            }
        }
    }
}
