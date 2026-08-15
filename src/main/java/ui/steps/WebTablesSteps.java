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

    public List<WebTableRecord> getAllRecords() {
        List<WebTableRecord> records = webTablesPage.getAllRecords();
        log.info("Got {} records", records.size());
        return records;
    }

    public int getRecordCount() {
        int count = webTablesPage.getRecordCount();
        log.info("Record count: {}", count);
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
}
