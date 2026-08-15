package ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.models.WebTableRecord;

import java.util.ArrayList;
import java.util.List;

public class WebTablesPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(WebTablesPage.class);

    public WebTablesPage(WebDriver driver) {
        super(driver);
    }

    // containers - rows and the modal are dynamic and carry no stable ids,
    // so lookups are always scoped inside one of these two
    private final By tableContainer = By.xpath("//*[contains(concat(' ', normalize-space(@class), ' '), ' web-tables-wrapper ')]");
    private final By modalContainer = By.xpath("//*[contains(concat(' ', normalize-space(@class), ' '), ' modal-content ')]");

    private final By addButton = By.xpath(".//button[@id='addNewRecordButton']");
    private final By rows = By.xpath(".//table/tbody/tr");

    // modal form fields
    private final By firstNameField = By.xpath(".//input[@id='firstName']");
    private final By lastNameField  = By.xpath(".//input[@id='lastName']");
    private final By emailField     = By.xpath(".//input[@id='userEmail']");
    private final By ageField       = By.xpath(".//input[@id='age']");
    private final By salaryField    = By.xpath(".//input[@id='salary']");
    private final By departmentField = By.xpath(".//input[@id='department']");
    private final By submitButton   = By.xpath(".//button[@id='submit']");

    // single source of truth for table column positions (1-based, matches td[n])
    private enum Column {
        FIRST_NAME(1, "First Name"),
        LAST_NAME(2, "Last Name"),
        AGE(3, "Age"),
        EMAIL(4, "Email"),
        SALARY(5, "Salary"),
        DEPARTMENT(6, "Department");

        private final int index;
        private final String label;

        Column(int index, String label) {
            this.index = index;
            this.label = label;
        }

        private By cellLocator() {
            return By.xpath("./td[" + index + "]");
        }
    }

    private By rowByEmail(String email) {
        return By.xpath(".//table/tbody/tr[td[" + Column.EMAIL.index + "][normalize-space(text())='" + email + "']]");
    }

    private String getCell(WebElement row, Column column) {
        return getText(row.findElement(column.cellLocator()));
    }

    private int getIntCell(WebElement row, Column column) {
        String text = getCell(row, column);
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    "Column '" + column.label + "' could not be parsed as a number, actual text: '" + text + "'", e);
        }
    }

    private WebTableRecord readRecord(WebElement row) {
        return WebTableRecord.builder()
                .firstName(getCell(row, Column.FIRST_NAME))
                .lastName(getCell(row, Column.LAST_NAME))
                .email(getCell(row, Column.EMAIL))
                .age(getIntCell(row, Column.AGE))
                .salary(getIntCell(row, Column.SALARY))
                .department(getCell(row, Column.DEPARTMENT))
                .build();
    }

    public void clickAddButton() {
        WebElement table = driver.findElement(tableContainer);
        click(table.findElement(addButton));
    }

    public void submitRecordForm(WebTableRecord record) {
        WebElement modal = driver.findElement(modalContainer);
        sendKeys(modal.findElement(firstNameField), record.getFirstName());
        sendKeys(modal.findElement(lastNameField), record.getLastName());
        sendKeys(modal.findElement(emailField), record.getEmail());
        sendKeys(modal.findElement(ageField), String.valueOf(record.getAge()));
        sendKeys(modal.findElement(salaryField), String.valueOf(record.getSalary()));
        sendKeys(modal.findElement(departmentField), record.getDepartment());
        click(modal.findElement(submitButton));
    }

    public WebTableRecord getRecordByEmail(String email) {
        WebElement table = driver.findElement(tableContainer);
        WebElement row = table.findElement(rowByEmail(email));
        return readRecord(row);
    }

    public List<WebTableRecord> getAllRecords() {
        WebElement table = driver.findElement(tableContainer);
        List<WebElement> rowElements = table.findElements(rows);
        List<WebTableRecord> records = new ArrayList<>();
        for (WebElement row : rowElements) {
            records.add(readRecord(row));
        }
        return records;
    }
}
