package testing.ui;

import org.testng.Assert;
import org.testng.annotations.Test;
import ui.models.WebTableRecord;

public class WebTablesTest extends BaseUITest {

    @Test(description = "WT-001: add record via form, verify full row in table")
    public void addRecordAppearsInTableWithAllFields() {
        WebTableRecord record = WebTableRecord.builder().build();

        wtSteps.openWebTablesPage();
        wtSteps.addRecord(record);

        WebTableRecord actual = wtSteps.getRecordByEmail(record.getEmail());
        Assert.assertEquals(actual, record, "Row in table does not match the submitted record");
    }
}
