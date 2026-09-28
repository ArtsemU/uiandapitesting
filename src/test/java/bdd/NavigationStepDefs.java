package bdd;

import io.cucumber.java.en.Given;

public class NavigationStepDefs {

    private final UiSession session;

    public NavigationStepDefs(UiSession session) {
        this.session = session;
    }

    @Given("the user is on the {string} page")
    public void theUserIsOnThePage(String page) {
        switch (page) {
            case "Text Box" -> session.textBoxSteps().openTBPage();
            case "Web Tables" -> session.webTablesSteps().openWebTablesPage();
            default -> throw new IllegalArgumentException("Unknown page: " + page);
        }
    }
}
