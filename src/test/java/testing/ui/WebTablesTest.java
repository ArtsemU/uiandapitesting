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
        int initialCount = wtSteps.getRecordCountOnCurrentPage();
        log.info("Initial record count: {}", initialCount);

        wtSteps.addRecord(record);

        WebTableRecord actual = wtSteps.getRecordByEmail(record.getEmail());
        int updatedCount = wtSteps.getRecordCountOnCurrentPage();
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
        int countBeforeEdit = wtSteps.getRecordCountOnCurrentPage();
        log.info("Record count before edit: {}", countBeforeEdit);

        wtSteps.editRecord(original.getEmail(), updated);

        WebTableRecord actual = wtSteps.getRecordByEmail(original.getEmail());
        int updatedCount = wtSteps.getRecordCountOnCurrentPage();
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
        softAssert.assertEquals(wtSteps.getRecordCountOnCurrentPage(), 1,
                "Filtering by an email should leave exactly one row");
        softAssert.assertEquals(wtSteps.getRecordsOnCurrentPage(), List.of(record),
                "The remaining row does not match the record that was added");
        softAssert.assertAll();
    }

    @Test(description = "WT-004: table paginates once it exceeds one page")
    public void tablePaginatesOnceItExceedsOnePage() {
        wtSteps.openWebTablesPage();
        int initialCount = wtSteps.getRecordCountOnCurrentPage();
        log.info("Initial record count: {}", initialCount);

        // 11 exceeds the default page size on its own, so two pages are guaranteed
        // regardless of how many rows the site ships with by default
        int recordsToAdd = 11;
        wtSteps.addRecords(recordsToAdd);

        int rowsPerPage = wtSteps.getRowsPerPage();
        int expectedTotal = initialCount + recordsToAdd;
        int expectedPage2Count = expectedTotal - rowsPerPage;
        log.info("Expected total: {}, rows per page: {}, expected page 2 count: {}",
                expectedTotal, rowsPerPage, expectedPage2Count);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(wtSteps.getTotalPages(), 2,
                "Table should span exactly two pages after adding enough records");
        softAssert.assertEquals(wtSteps.getCurrentPageNumber(), 1,
                "Table should start on page 1");
        softAssert.assertEquals(wtSteps.getRecordCountOnCurrentPage(), rowsPerPage,
                "Page 1 should be full, holding exactly rowsPerPage records");

        List<WebTableRecord> page1Records = wtSteps.getRecordsOnCurrentPage();

        wtSteps.goToNextPage();
        softAssert.assertEquals(wtSteps.getCurrentPageNumber(), 2,
                "Next should move the table to page 2");
        softAssert.assertEquals(wtSteps.getRecordCountOnCurrentPage(), expectedPage2Count,
                "Page 2 should hold exactly the remaining records");
        softAssert.assertNotEquals(wtSteps.getRecordsOnCurrentPage(), page1Records,
                "Page 2 should show different records than page 1");

        wtSteps.goToPreviousPage();
        softAssert.assertEquals(wtSteps.getCurrentPageNumber(), 1,
                "Previous should move the table back to page 1");
        softAssert.assertEquals(wtSteps.getRecordsOnCurrentPage(), page1Records,
                "Page 1 should show the same records as before navigating away");
        softAssert.assertAll();
    }

    @Test(description = "WT-005: deleting a record removes it from the table")
    public void deleteRecordRemovesItFromTable() {
        WebTableRecord record = WebTableRecord.builder().build();

        wtSteps.openWebTablesPage();
        int initialCount = wtSteps.getRecordCountOnCurrentPage();
        log.info("Initial record count: {}", initialCount);

        wtSteps.addRecord(record);
        wtSteps.deleteRecord(record.getEmail());

        List<WebTableRecord> remainingRecords = wtSteps.getRecordsOnCurrentPage();
        int updatedCount = wtSteps.getRecordCountOnCurrentPage();
        log.info("Updated record count: {}", updatedCount);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertFalse(remainingRecords.contains(record), "Deleted record should no longer appear in the table");
        softAssert.assertEquals(updatedCount, initialCount, "Row count should match the count recorded before the record was added");
        softAssert.assertAll();
    }
}
