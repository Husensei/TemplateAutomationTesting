package runners.example;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/example") // ✅ relative to src/test/resources
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "steps, steps.example")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@heroku")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm")
public class HerokuLoginRunner {
}