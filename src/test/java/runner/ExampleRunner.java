package runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber-report.html"},
        glue = {"steps", "steps.example"},
        features = {"src/test/resources/features/example"},
        tags = "@example"
)

public class TemplateRunner {
}
