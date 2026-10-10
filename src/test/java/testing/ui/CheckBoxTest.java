package testing.ui;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import testing.testdata.CheckBoxTestData;

import java.util.List;

public class CheckBoxTest extends BaseUITest {

    @Test(priority = 1, testName = "CB-001: check Desktop selects Desktop, Notes, Commands", groups = {"smoke"})
    public void checkDesktopSelectsChildren() {
        cbSteps().openCheckBoxPage();
        cbSteps().expandNode(CheckBoxTestData.NODE_HOME);
        cbSteps().expandNode(CheckBoxTestData.NODE_DESKTOP);
        cbSteps().selectCheckbox(CheckBoxTestData.NODE_DESKTOP);

        List<String> selectedItems = cbSteps().getSelectedItems();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(selectedItems, CheckBoxTestData.EXPECTED_OUTPUT_DESKTOP,
                "Selecting Desktop does not select exactly Desktop and its children");
        softAssert.assertAll();
    }

    @Test(priority = 2, testName = "CB-002: check Office+Downloads selects both subtrees", groups = {"regression"})
    public void checkOfficeAndDownloadsSelectsSubtrees() {
        cbSteps().openCheckBoxPage();
        cbSteps().expandNode(CheckBoxTestData.NODE_HOME);
        cbSteps().expandNode(CheckBoxTestData.NODE_DOCUMENTS);
        cbSteps().expandNode(CheckBoxTestData.NODE_OFFICE);
        cbSteps().selectCheckbox(CheckBoxTestData.NODE_OFFICE);
        cbSteps().selectCheckbox(CheckBoxTestData.NODE_DOWNLOADS);

        List<String> selectedItems = cbSteps().getSelectedItems();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(selectedItems, CheckBoxTestData.EXPECTED_OUTPUT_OFFICE_DOWNLOADS,
                "Selecting Office and Downloads does not select exactly both subtrees");
        softAssert.assertAll();
    }

    @Test(priority = 3, testName = "CB-003: check Notes only selects just Notes", groups = {"regression"})
    public void checkNotesOnlySelectsNotes() {
        cbSteps().openCheckBoxPage();
        cbSteps().expandNode(CheckBoxTestData.NODE_HOME);
        cbSteps().expandNode(CheckBoxTestData.NODE_DESKTOP);
        cbSteps().selectCheckbox(CheckBoxTestData.NODE_NOTES);

        List<String> selectedItems = cbSteps().getSelectedItems();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(selectedItems, CheckBoxTestData.EXPECTED_OUTPUT_NOTES,
                "Selecting Notes does not select only Notes");
        softAssert.assertAll();
    }
}
