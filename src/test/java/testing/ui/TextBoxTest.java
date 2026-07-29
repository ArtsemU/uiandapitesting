package testing.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import ui.steps.TextBoxSteps;

public class TextBoxTest extends BaseUITest{
    private static final Logger log = LoggerFactory.getLogger(TextBoxTest.class);

    @Test
    public void test() {
        tbSteps.openTBPage();
        tbSteps.fillFullName("John Doue");
        tbSteps.submitForm();
        String result = tbSteps.getFullName();
        Assert.assertEquals(result, "John Doue", "Full name mismatch");
    }

}
