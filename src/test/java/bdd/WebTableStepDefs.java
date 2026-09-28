package bdd;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.asserts.SoftAssert;
import ui.models.WebTableRecord;
import ui.steps.WebTablesSteps;

public class WebTableStepDefs {

    private final ScenarioContext context;
    private final UiSession session;

    public WebTableStepDefs(ScenarioContext context, UiSession session) {
        this.context = context;
        this.session = session;
    }

    @When("the user adds a new record through the registration form")
    public void theUserAddsANewRecord() {
        WebTablesSteps steps = session.webTablesSteps();
        WebTableRecord record = WebTableRecord.builder().build();
        context.setWebTableRecord(record);
        // Baseline for the row-count check in the Then step.
        context.setInitialRecordCount(steps.getRecordCountOnCurrentPage());
        steps.addRecord(record);
    }

    @Then("the table gains exactly one row holding all the values of the new record")
    public void theTableGainsOneRowWithTheNewRecord() {
        WebTablesSteps steps = session.webTablesSteps();
        WebTableRecord record = context.getWebTableRecord();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(steps.getRecordByEmail(record.getEmail()), record,
                "Row in table does not match the submitted record");
        softAssert.assertEquals(steps.getRecordCountOnCurrentPage(), context.getInitialRecordCount() + 1,
                "Record count did not increase by exactly one after adding a record");
        softAssert.assertAll();
    }
}
