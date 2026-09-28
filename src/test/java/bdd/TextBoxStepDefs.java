package bdd;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.asserts.SoftAssert;
import ui.steps.TextBoxSteps;

import static testing.testdata.TextBoxTestData.CURRENT_ADDRESS;
import static testing.testdata.TextBoxTestData.EMAIL;
import static testing.testdata.TextBoxTestData.FULL_NAME;
import static testing.testdata.TextBoxTestData.PERMANENT_ADDRESS;

public class TextBoxStepDefs {

    private final UiSession session;

    public TextBoxStepDefs(UiSession session) {
        this.session = session;
    }

    @When("the user submits their full name, email, current address and permanent address")
    public void theUserSubmitsAllFields() {
        TextBoxSteps steps = session.textBoxSteps();
        steps.fillFullName(FULL_NAME);
        steps.fillEmail(EMAIL);
        steps.fillCurrAddress(CURRENT_ADDRESS);
        steps.fillPerAddress(PERMANENT_ADDRESS);
        steps.submitForm();
    }

    @Then("the output shows the submitted full name, email, current address and permanent address")
    public void theOutputShowsAllSubmittedFields() {
        TextBoxSteps steps = session.textBoxSteps();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(steps.getOutputName(), FULL_NAME, "Output does not show the submitted full name");
        softAssert.assertEquals(steps.getOutputEmail(), EMAIL, "Output does not show the submitted email");
        softAssert.assertEquals(steps.getOutputCurrAddress(), CURRENT_ADDRESS,
                "Output does not show the submitted current address");
        softAssert.assertEquals(steps.getOutputPerAddress(), PERMANENT_ADDRESS,
                "Output does not show the submitted permanent address");
        softAssert.assertAll();
    }
}
