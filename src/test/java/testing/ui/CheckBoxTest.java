package testing.ui;

import org.testng.Assert;
import org.testng.annotations.Test;
import testing.testdata.CheckBoxTestData;

import java.util.List;

public class CheckBoxTest extends BaseUITest {

    @Test(description = "CB-001: check Desktop selects Desktop, Notes, Commands")
    public void checkDesktopSelectsChildren() {
        cbSteps.openCheckBoxPage();
        cbSteps.expandNode("Home");
        cbSteps.expandNode("Desktop");
        cbSteps.selectCheckbox("Desktop");

        List<String> selectedItems = cbSteps.getSelectedItems();
        Assert.assertEquals(selectedItems, CheckBoxTestData.EXPECTED_OUTPUT_DESKTOP, "Selected items mismatch");
    }

    @Test(description = "CB-002: check Office+Downloads selects both subtrees")
    public void checkOfficeAndDownloadsSelectsSubtrees() {
        cbSteps.openCheckBoxPage();
        cbSteps.expandNode("Home");
        cbSteps.expandNode("Documents");
        cbSteps.expandNode("Office");
        cbSteps.selectCheckbox("Office");
        cbSteps.selectCheckbox("Downloads");

        List<String> selectedItems = cbSteps.getSelectedItems();
        Assert.assertEquals(selectedItems, CheckBoxTestData.EXPECTED_OUTPUT_OFFICE_DOWNLOADS, "Selected items mismatch");
    }

    @Test(description = "CB-003: check Notes only selects just Notes")
    public void checkNotesOnlySelectsNotes() {
        cbSteps.openCheckBoxPage();
        cbSteps.expandNode("Home");
        cbSteps.expandNode("Desktop");
        cbSteps.selectCheckbox("Notes");

        List<String> selectedItems = cbSteps.getSelectedItems();
        Assert.assertEquals(selectedItems, CheckBoxTestData.EXPECTED_OUTPUT_NOTES, "Selected items mismatch");
    }
}
