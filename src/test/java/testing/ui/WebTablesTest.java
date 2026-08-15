package testing.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import ui.models.WebTableRecord;

import java.util.List;

public class WebTablesTest extends BaseUITest {
    private static final Logger log = LoggerFactory.getLogger(WebTablesTest.class);

    @Test(description = "WT-001: add record via form, verify full row in table")
    public void addRecordAppearsInTableWithAllFields() {
        WebTableRecord record = WebTableRecord.builder().build();

        wtSteps.openWebTablesPage();
        int initialCount = wtSteps.getRecordCount();
        log.info("Initial record count: {}", initialCount);

        wtSteps.addRecord(record);

        WebTableRecord actual = wtSteps.getRecordByEmail(record.getEmail());
        int updatedCount = wtSteps.getRecordCount();
        log.info("Updated record count: {}", updatedCount);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(actual, record, "Row in table does not match the submitted record");
        softAssert.assertEquals(updatedCount, initialCount + 1, "Record count did not increase by exactly one after adding a record");
        softAssert.assertAll();
    }

    @Test(description = "WT-002: editing a record updates its row")
    public void editRecordUpdatesTheRow() {
        WebTableRecord original = WebTableRecord.unique(1);
        WebTableRecord updated = original.toBuilder()
                .firstName("Edited")
                .department("Edited")
                .build();

        wtSteps.openWebTablesPage();
        wtSteps.addRecord(original);
        int countBeforeEdit = wtSteps.getRecordCount();
        log.info("Record count before edit: {}", countBeforeEdit);

        wtSteps.editRecord(original.getEmail(), updated);

        WebTableRecord actual = wtSteps.getRecordByEmail(original.getEmail());
        int updatedCount = wtSteps.getRecordCount();
        log.info("Updated record count: {}", updatedCount);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(actual, updated, "Edited row does not match the submitted updates");
        softAssert.assertEquals(updatedCount, countBeforeEdit, "Record count changed after editing a record");
        softAssert.assertAll();
    }

    @Test(description = "WT-003: filtering by email narrows the table to that record")
    public void filterByEmailNarrowsTable() {
        WebTableRecord record = WebTableRecord.unique(1);

        wtSteps.openWebTablesPage();
        wtSteps.addRecord(record);

        wtSteps.enterFilter(record.getEmail());

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(wtSteps.getRecordCount(), 1,
                "Filtering by an email should leave exactly one row");
        softAssert.assertEquals(wtSteps.getAllRecords(), List.of(record),
                "The remaining row does not match the record that was added");
        softAssert.assertAll();
    }
}
