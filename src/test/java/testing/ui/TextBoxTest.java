package testing.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import ui.steps.TextBoxSteps;

public class TextBoxTest extends BaseUITest{
    private static final Logger log = LoggerFactory.getLogger(TextBoxTest.class);

    @Test(priority = 1, description = "TB-001: setting only full name displays it in output")
    public void setNameOnlyTest() {
        tbSteps.openTBPage();
        tbSteps.fillFullName("John Doue");
        tbSteps.submitForm();
        String result = tbSteps.getOutputName();
        Assert.assertEquals(result, "John Doue", "Full name mismatch");
    }

    @Test(description = "TB-002: submitted data is displayed in the output block")
    public void submittedDataIsDisplayedInOutput() {
        String fullName = "John Doue";
        String email = "johndoue@myemail.net";
        String currAddress = "Poland, Wroclaw, 50-422, Szybka 1A - 20";
        String perAddress = "Belarus, Minsk, 255125, Gor 7 - 55";

        tbSteps.openTBPage();
        tbSteps.fillFullName(fullName);
        tbSteps.fillEmail(email);
        tbSteps.fillCurrAddress(currAddress);
        tbSteps.fillPerAddress(perAddress);

        tbSteps.submitForm();

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(tbSteps.getOutputName(), fullName, "Full name mismatched");
        softAssert.assertEquals(tbSteps.getOutputEmail(), email, "Email mismatched");
        softAssert.assertEquals(tbSteps.getOutputCurrAddress(), currAddress, "Address mismatched");
        softAssert.assertEquals(tbSteps.getOutputPerAddress(), perAddress, "Address mismatched");
        softAssert.assertAll();
    }

}
