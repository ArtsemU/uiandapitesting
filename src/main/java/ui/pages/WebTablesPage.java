package ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
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
    private final By editIcon = By.xpath(".//span[@title='Edit']");
    private final By deleteIcon = By.xpath(".//span[@title='Delete']");
    private final By searchBoxField = By.xpath(".//input[@id='searchBox']");

    // pagination bar, scoped like modalContainer above; controls below are scoped inside it
    private final By paginationContainer = By.xpath(".//div[contains(concat(' ', normalize-space(@class), ' '), ' pagination ')]");
    private final By rowsPerPageSelect = By.xpath(".//select");
    private final By pageInfo = By.xpath(".//strong");
    private final By nextPageButton = By.xpath(".//button[normalize-space(text())='Next']");
    private final By previousPageButton = By.xpath(".//button[normalize-space(text())='Previous']");

    // modal form fields
    private final By firstNameField = By.xpath(".//input[@id='firstName']");
    private final By lastNameField  = By.xpath(".//input[@id='lastName']");
    private final By emailField     = By.xpath(".//input[@id='userEmail']");
    private final By ageField       = By.xpath(".//input[@id='age']");
    private final By salaryField    = By.xpath(".//input[@id='salary']");
    private final By departmentField = By.xpath(".//input[@id='department']");
    private final By submitButton   = By.xpath(".//button[@id='submit']");
    private final By closeButton    = By.xpath(".//button[contains(concat(' ', normalize-space(@class), ' '), ' btn-close ')]");

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

    public void openAddForm() {
        WebElement table = driver.findElement(tableContainer);
        click(table.findElement(addButton));
    }

    public void clickEditForRow(String email) {
        WebElement table = driver.findElement(tableContainer);
        List<WebElement> matches = table.findElements(rowByEmail(email));
        if (matches.size() != 1) {
            throw new IllegalStateException(
                    "Expected exactly one record with email '" + email + "' to edit, found " + matches.size());
        }
        click(matches.get(0).findElement(editIcon));
    }

    public void clickDeleteForRow(String email) {
        WebElement table = driver.findElement(tableContainer);
        List<WebElement> matches = table.findElements(rowByEmail(email));
        if (matches.size() != 1) {
            throw new IllegalStateException(
                    "Expected exactly one record with email '" + email + "' to delete, found " + matches.size());
        }
        click(matches.get(0).findElement(deleteIcon));
    }

    // the edit modal opens pre-filled; clearing via keyboard rather than
    // WebElement.clear() so React's input state tracker actually registers
    // the field as empty before sendKeys types the new value
    private void clearField(WebElement element) {
        click(element);
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.DELETE);
    }

    public void fillRecordForm(WebTableRecord record) {
        WebElement modal = driver.findElement(modalContainer);

        WebElement firstName = modal.findElement(firstNameField);
        clearField(firstName);
        sendKeys(firstName, record.getFirstName());

        WebElement lastName = modal.findElement(lastNameField);
        clearField(lastName);
        sendKeys(lastName, record.getLastName());

        WebElement email = modal.findElement(emailField);
        clearField(email);
        sendKeys(email, record.getEmail());

        WebElement age = modal.findElement(ageField);
        clearField(age);
        sendKeys(age, String.valueOf(record.getAge()));

        WebElement salary = modal.findElement(salaryField);
        clearField(salary);
        sendKeys(salary, String.valueOf(record.getSalary()));

        WebElement department = modal.findElement(departmentField);
        clearField(department);
        sendKeys(department, record.getDepartment());
    }

    public void clickSubmit() {
        WebElement modal = driver.findElement(modalContainer);
        click(modal.findElement(submitButton));
    }

    public void clickClose() {
        WebElement modal = driver.findElement(modalContainer);
        click(modal.findElement(closeButton));
    }

    public WebTableRecord getRecordByEmail(String email) {
        WebElement table = driver.findElement(tableContainer);
        List<WebElement> matches = table.findElements(rowByEmail(email));
        if (matches.size() != 1) {
            throw new IllegalStateException(
                    "Expected exactly one record with email '" + email + "', found " + matches.size());
        }
        return readRecord(matches.get(0));
    }

    public List<WebTableRecord> getRecordsOnCurrentPage() {
        WebElement table = driver.findElement(tableContainer);
        List<WebElement> rowElements = table.findElements(rows);
        List<WebTableRecord> records = new ArrayList<>();
        for (WebElement row : rowElements) {
            records.add(readRecord(row));
        }
        return records;
    }

    public int getRecordCountOnCurrentPage() {
        WebElement table = driver.findElement(tableContainer);
        return table.findElements(rows).size();
    }

    public void enterFilter(String value) {
        WebElement table = driver.findElement(tableContainer);
        WebElement search = table.findElement(searchBoxField);
        clearField(search);
        sendKeys(search, value);
    }

    public void clearFilter() {
        WebElement table = driver.findElement(tableContainer);
        clearField(table.findElement(searchBoxField));
    }

    public int getRowsPerPage() {
        WebElement table = driver.findElement(tableContainer);
        WebElement pagination = table.findElement(paginationContainer);
        Select select = new Select(pagination.findElement(rowsPerPageSelect));
        return Integer.parseInt(select.getFirstSelectedOption().getAttribute("value"));
    }

    // page info is rendered as a single "<current> of <total>" text node, e.g. "1 of 2"
    private int[] readPageInfo() {
        WebElement table = driver.findElement(tableContainer);
        WebElement pagination = table.findElement(paginationContainer);
        String text = getText(pagination.findElement(pageInfo));
        String[] parts = text.split(" of ");
        if (parts.length != 2) {
            throw new IllegalStateException("Could not parse page info text: '" + text + "'");
        }
        try {
            return new int[]{Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())};
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Page info text was not numeric: '" + text + "'", e);
        }
    }

    public int getCurrentPageNumber() {
        return readPageInfo()[0];
    }

    public int getTotalPages() {
        return readPageInfo()[1];
    }

    public void goToNextPage() {
        WebElement table = driver.findElement(tableContainer);
        WebElement pagination = table.findElement(paginationContainer);
        click(pagination.findElement(nextPageButton));
    }

    public void goToPreviousPage() {
        WebElement table = driver.findElement(tableContainer);
        WebElement pagination = table.findElement(paginationContainer);
        click(pagination.findElement(previousPageButton));
    }
}
