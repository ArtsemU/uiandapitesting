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

    public void addRecord(WebTableRecord record) {
        log.info("Adding record: {}", record);
        webTablesPage.clickAddButton();
        webTablesPage.submitRecordForm(record);
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
}
