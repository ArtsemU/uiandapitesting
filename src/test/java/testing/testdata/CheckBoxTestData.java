package testing.testdata;

import java.util.List;

public final class CheckBoxTestData {

    private CheckBoxTestData() {
    }

    // Tree node labels as shown on the page.
    public static final String NODE_HOME = "Home";
    public static final String NODE_DESKTOP = "Desktop";
    public static final String NODE_DOCUMENTS = "Documents";
    public static final String NODE_OFFICE = "Office";
    public static final String NODE_DOWNLOADS = "Downloads";
    public static final String NODE_NOTES = "Notes";

    public static final List<String> EXPECTED_OUTPUT_DESKTOP = List.of("desktop", "notes", "commands");
    public static final List<String> EXPECTED_OUTPUT_OFFICE_DOWNLOADS = List.of(
            "office", "public", "private", "classified", "general",
            "downloads", "wordFile", "excelFile");
    public static final List<String> EXPECTED_OUTPUT_NOTES = List.of("notes");
}
