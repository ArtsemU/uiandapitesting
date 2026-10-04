package ui.steps;

import io.qameta.allure.Step;
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

    @Step("Open Web Tables page")
    public void openWebTablesPage() {
        openUrl(WEB_TABLES_PAGE);
    }

    @Step("Open add-record form")
    public void openAddForm() {
        log.info("Opening add-record form");
        webTablesPage.openAddForm();
    }

    @Step("Fill record form for {record.email}")
    public void fillRecordForm(WebTableRecord record) {
        log.info("Filling record form: {}", record);
        webTablesPage.fillRecordForm(record);
    }

    @Step("Submit record form")
    public void clickSubmit() {
        log.info("Submitting record form");
        webTablesPage.clickSubmit();
    }

    @Step("Close record form")
    public void clickClose() {
        log.info("Closing record form");
        webTablesPage.clickClose();
    }

    @Step("Add record {record.email}")
    public void addRecord(WebTableRecord record) {
        openAddForm();
        fillRecordForm(record);
        clickSubmit();
    }

    @Step("Open edit form for record {email}")
    public void openEditForm(String email) {
        log.info("Opening edit form for record with email: {}", email);
        webTablesPage.clickEditForRow(email);
    }

    @Step("Edit record {email}")
    public void editRecord(String email, WebTableRecord updated) {
        openEditForm(email);
        fillRecordForm(updated);
        clickSubmit();
    }

    @Step("Delete record {email}")
    public void deleteRecord(String email) {
        log.info("Deleting record with email: {}", email);
        webTablesPage.clickDeleteForRow(email);
    }

    @Step("Get record {email}")
    public WebTableRecord getRecordByEmail(String email) {
        WebTableRecord record = webTablesPage.getRecordByEmail(email);
        log.info("Got record: {}", record);
        return record;
    }

    @Step("Get records on current page")
    public List<WebTableRecord> getRecordsOnCurrentPage() {
        List<WebTableRecord> records = webTablesPage.getRecordsOnCurrentPage();
        log.info("Got {} records on current page", records.size());
        return records;
    }

    @Step("Get record count on current page")
    public int getRecordCountOnCurrentPage() {
        int count = webTablesPage.getRecordCountOnCurrentPage();
        log.info("Record count on current page: {}", count);
        return count;
    }

    @Step("Filter table by {value}")
    public void enterFilter(String value) {
        log.info("Filtering table by: {}", value);
        webTablesPage.enterFilter(value);
    }

    @Step("Clear filter")
    public void clearFilter() {
        log.info("Clearing filter");
        webTablesPage.clearFilter();
    }

    @Step("Get rows per page")
    public int getRowsPerPage() {
        int rowsPerPage = webTablesPage.getRowsPerPage();
        log.info("Rows per page: {}", rowsPerPage);
        return rowsPerPage;
    }

    @Step("Get current page number")
    public int getCurrentPageNumber() {
        int page = webTablesPage.getCurrentPageNumber();
        log.info("Current page: {}", page);
        return page;
    }

    @Step("Get total pages")
    public int getTotalPages() {
        int totalPages = webTablesPage.getTotalPages();
        log.info("Total pages: {}", totalPages);
        return totalPages;
    }

    @Step("Go to next page")
    public void goToNextPage() {
        log.info("Navigating to next page");
        webTablesPage.goToNextPage();
    }

    @Step("Go to previous page")
    public void goToPreviousPage() {
        log.info("Navigating to previous page");
        webTablesPage.goToPreviousPage();
    }

    @Step("Add {count} records")
    public void addRecords(int count) {
        log.info("Adding {} records", count);
        for (int i = 1; i <= count; i++) {
            addRecord(WebTableRecord.unique(i));
        }
    }
}
