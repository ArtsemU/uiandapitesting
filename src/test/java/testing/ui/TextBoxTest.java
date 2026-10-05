package testing.ui;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import testing.testdata.TextBoxTestData;

public class TextBoxTest extends BaseUITest{

    @Test(priority = 1, testName = "TB-001: setting only full name displays it in output", groups = {"smoke"})
    public void setNameOnlyTest() {
        tbSteps().openTBPage();
        tbSteps().fillFullName(TextBoxTestData.FULL_NAME);
        tbSteps().submitForm();
        String result = tbSteps().getOutputName();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(result, TextBoxTestData.FULL_NAME,
                "Submitting only the full name does not display it in the output block");
        softAssert.assertAll();
    }

    @Test(priority = 2, testName = "TB-002: submitted data is displayed in the output block", groups = {"regression"})
    public void submittedDataIsDisplayedInOutput() {
        tbSteps().openTBPage();
        tbSteps().fillFullName(TextBoxTestData.FULL_NAME);
        tbSteps().fillEmail(TextBoxTestData.EMAIL);
        tbSteps().fillCurrAddress(TextBoxTestData.CURRENT_ADDRESS);
        tbSteps().fillPerAddress(TextBoxTestData.PERMANENT_ADDRESS);

        tbSteps().submitForm();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(tbSteps().getOutputName(), TextBoxTestData.FULL_NAME, "Submitted full name is not displayed in the output block");
        softAssert.assertEquals(tbSteps().getOutputEmail(), TextBoxTestData.EMAIL, "Submitted email is not displayed in the output block");
        softAssert.assertEquals(tbSteps().getOutputCurrAddress(), TextBoxTestData.CURRENT_ADDRESS, "Submitted current address is not displayed in the output block");
        softAssert.assertEquals(tbSteps().getOutputPerAddress(), TextBoxTestData.PERMANENT_ADDRESS, "Submitted permanent address is not displayed in the output block");
        softAssert.assertAll();
    }

}
