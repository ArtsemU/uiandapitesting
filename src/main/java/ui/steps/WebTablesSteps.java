package ui.steps;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.models.WebTableRecord;
import ui.pages.WebTablesPage;

import java.util.List;

public class WebTablesSteps extends BaseSteps {
    private static final Logger log = LoggerFactory.getLogger(WebTablesSteps.class);
    private static final String WEB_TABLES_PAGE = "https://demoqa.com/webtables";

    private final WebTablesPage webTablesPage;

    public WebTablesSteps(WebDriver driver) {
        super(driver);
        this.webTablesPage = new WebTablesPage(driver);
    }

    public void openWebTablesPage() {
        openUrl(WEB_TABLES_PAGE);
    }

    public void openAddForm() {
        log.info("Opening add-record form");
        webTablesPage.openAddForm();
    }

    public void fillRecordForm(WebTableRecord record) {
        log.info("Filling record form: {}", record);
        webTablesPage.fillRecordForm(record);
    }

    public void clickSubmit() {
        log.info("Submitting record form");
        webTablesPage.clickSubmit();
    }

    public void clickClose() {
        log.info("Closing record form");
        webTablesPage.clickClose();
    }

    public void addRecord(WebTableRecord record) {
        openAddForm();
        fillRecordForm(record);
        clickSubmit();
    }

    public void openEditForm(String email) {
        log.info("Opening edit form for record with email: {}", email);
        webTablesPage.clickEditForRow(email);
    }

    public void editRecord(String email, WebTableRecord updated) {
        openEditForm(email);
        fillRecordForm(updated);
        clickSubmit();
    }

    public WebTableRecord getRecordByEmail(String email) {
        WebTableRecord record = webTablesPage.getRecordByEmail(email);
        log.info("Got record: {}", record);
        return record;
    }

    public List<WebTableRecord> getRecordsOnCurrentPage() {
        List<WebTableRecord> records = webTablesPage.getRecordsOnCurrentPage();
        log.info("Got {} records on current page", records.size());
        return records;
    }

    public int getRecordCountOnCurrentPage() {
        int count = webTablesPage.getRecordCountOnCurrentPage();
        log.info("Record count on current page: {}", count);
        return count;
    }

    public void enterFilter(String value) {
        log.info("Filtering table by: {}", value);
        webTablesPage.enterFilter(value);
    }

    public void clearFilter() {
        log.info("Clearing filter");
        webTablesPage.clearFilter();
    }

    public int getRowsPerPage() {
        int rowsPerPage = webTablesPage.getRowsPerPage();
        log.info("Rows per page: {}", rowsPerPage);
        return rowsPerPage;
    }

    public int getCurrentPageNumber() {
        int page = webTablesPage.getCurrentPageNumber();
        log.info("Current page: {}", page);
        return page;
    }

    public int getTotalPages() {
        int totalPages = webTablesPage.getTotalPages();
        log.info("Total pages: {}", totalPages);
        return totalPages;
    }

    public void goToNextPage() {
        log.info("Navigating to next page");
        webTablesPage.goToNextPage();
    }

    public void goToPreviousPage() {
        log.info("Navigating to previous page");
        webTablesPage.goToPreviousPage();
    }

    public void addRecords(int count) {
        log.info("Adding {} records", count);
        for (int i = 1; i <= count; i++) {
            addRecord(WebTableRecord.unique(i));
        }
    }
}
