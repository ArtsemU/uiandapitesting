package lessonslearned.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;

public class Hooks {

    @BeforeAll
    public static void beforeAll() {
        System.out.println(">>> BeforeAll: once before all scenarios");
    }

    @AfterAll
    public static void afterAll() {
        System.out.println(">>> AfterAll: once after all scenarios");
    }

    @Before
    public void before(Scenario scenario) {
        System.out.println(">>> Before scenario: " + scenario.getName());
    }

    @After
    public void after(Scenario scenario) {
        System.out.println(">>> After scenario: " + scenario.getName()
                + " — status: " + scenario.getStatus());
    }

    @BeforeStep
    public void beforeStep() {
        System.out.println(">>> BeforeStep");
    }

    @AfterStep
    public void afterStep() {
        System.out.println(">>> AfterStep");
    }
}
