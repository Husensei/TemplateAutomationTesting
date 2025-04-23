package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import utils.EnvironmentManager;
import utils.LoggerUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static driver.DriverFactory.*;
import static utils.ScreenshotUtils.captureScreenshot;

public class Hooks {

    private static final Logger logger = LoggerUtils.getLogger(Hooks.class);

    @Before
    public void setUp() {
        startDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        if (getDriver() != null) {
            try {
                if (scenario.isFailed()) {
                    captureScreenshot(getDriver(), scenario.getName());
                }
            } finally {
                quitDriver();
            }
        }
    }
}
