package testing.testdata;

import java.util.List;

public final class CheckBoxTestData {

    private CheckBoxTestData() {
    }

    public static final List<String> EXPECTED_OUTPUT_DESKTOP = List.of("desktop", "notes", "commands");
    public static final List<String> EXPECTED_OUTPUT_OFFICE_DOWNLOADS = List.of(
            "office", "public", "private", "classified", "general",
            "downloads", "wordFile", "excelFile");
    public static final List<String> EXPECTED_OUTPUT_NOTES = List.of("notes");
}
