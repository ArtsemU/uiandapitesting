package reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Regenerates the Allure HTML report ({@code mvn allure:report}) when a suite
 * finishes, pass or fail, on mvn test and IDE runs alike.
 * Each run gets its own folder, allure-report/report-yyyyMMdd-HHmmss.
 *
 * Registered through META-INF/services/org.testng.ITestNGListener, the same
 * ServiceLoader mechanism that registers Allure's own listener, so it fires under
 * any TestNG launcher. Fires once per suite.
 *
 * Does nothing when the CI environment variable is "true" (set by GitHub Actions):
 * the workflow generates and uploads the report once per job itself.
 *
 * After a report is generated, only the newest report folders are kept (default 10,
 * override with -Dallure.report.keep=<n>); older report-yyyyMMdd-HHmmss folders are
 * deleted. Other content of allure-report/ is never touched.
 *
 * A failure to generate a report is logged at ERROR, a failure to prune old ones at WARN;
 * neither ever fails the run.
 */
public class AllureReportListener implements ISuiteListener {
    private static final Logger log = LoggerFactory.getLogger(AllureReportListener.class);

    private static final long TIMEOUT_MINUTES = 5;
    private static final int OUTPUT_TAIL_LINES = 30;
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final Pattern REPORT_FOLDER = Pattern.compile("report-\\d{8}-\\d{6}");
    private static final String KEEP_PROPERTY = "allure.report.keep";
    private static final int DEFAULT_KEEP = 10;

    @Override
    public void onFinish(ISuite suite) {
        if ("true".equals(System.getenv("CI"))) {
            log.info("CI=true, Allure report left to the workflow");
            return;
        }

        File projectDir = new File(System.getProperty("user.dir"));
        if (!new File(projectDir, "pom.xml").isFile()) {
            log.error("Allure report not generated: no pom.xml in working directory {}", projectDir);
            return;
        }

        Path output = projectDir.toPath().resolve("target").resolve("allure-report-listener.log");
        Path reportDir = projectDir.toPath().resolve("allure-report")
                .resolve("report-" + LocalDateTime.now().format(TIMESTAMP));
        log.info("Suite '{}' finished, generating Allure report (output: {})", suite.getName(), output);
        try {
            Files.createDirectories(output.getParent());
            Process process = new ProcessBuilder(command(reportDir))
                    .directory(projectDir)
                    .redirectErrorStream(true)
                    .redirectOutput(output.toFile())
                    .start();

            if (!process.waitFor(TIMEOUT_MINUTES, TimeUnit.MINUTES)) {
                process.destroyForcibly();
                log.error("Allure report not generated: mvn allure:report did not finish within {} min, see {}",
                        TIMEOUT_MINUTES, output);
            } else if (process.exitValue() != 0) {
                log.error("Allure report not generated: mvn allure:report exited with {}, last output:\n{}",
                        process.exitValue(), tail(output));
            } else {
                log.info("Allure report: {}", reportDir.resolve("index.html"));
                pruneOldReports(reportDir.getParent());
            }
        } catch (IOException e) {
            log.error("Allure report not generated: could not run mvn allure:report", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Allure report not generated: interrupted while waiting for mvn allure:report", e);
        }
    }

    // Folder names sort chronologically, so name order is age order
    private static void pruneOldReports(Path reportsRoot) {
        Integer keep = keepCount();
        if (keep == null) {
            return;
        }
        List<Path> reports;
        try (Stream<Path> children = Files.list(reportsRoot)) {
            reports = children
                    .filter(Files::isDirectory)
                    .filter(dir -> REPORT_FOLDER.matcher(dir.getFileName().toString()).matches())
                    .sorted(Comparator.comparing((Path dir) -> dir.getFileName().toString()).reversed())
                    .toList();
        } catch (IOException e) {
            log.warn("Old Allure reports not pruned: could not list {}", reportsRoot, e);
            return;
        }
        for (Path old : reports.subList(Math.min(keep, reports.size()), reports.size())) {
            try {
                deleteRecursively(old);
                log.info("Deleted old Allure report {}", old);
            } catch (IOException e) {
                log.warn("Old Allure report not deleted: {}", old, e);
            }
        }
    }

    // A bad value never deletes anything: null means skip pruning
    private static Integer keepCount() {
        String value = System.getProperty(KEEP_PROPERTY);
        if (value == null || value.isBlank()) {
            return DEFAULT_KEEP;
        }
        try {
            int keep = Integer.parseInt(value.trim());
            if (keep >= 1) {
                return keep;
            }
        } catch (NumberFormatException ignored) {
            // reported below
        }
        log.warn("Old Allure reports not pruned: -D{}={} is not a positive integer", KEEP_PROPERTY, value);
        return null;
    }

    private static void deleteRecursively(Path dir) throws IOException {
        try (Stream<Path> paths = Files.walk(dir)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    // Windows: mvn is mvn.cmd, a batch file. cmd /c resolves it through PATH/PATHEXT like a
    // terminal does; starting a .cmd directly from ProcessBuilder depends on JDK batch-file
    // handling (jdk.lang.Process.allowAmbiguousCommands) and quotes arguments differently.
    private static List<String> command(Path reportDir) {
        String reportDirProperty = "-Dallure.report.directory=" + reportDir;
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            return List.of("cmd", "/c", "mvn", "-B", "allure:report", reportDirProperty);
        }
        return List.of("mvn", "-B", "allure:report", reportDirProperty);
    }

    private static String tail(Path output) {
        try {
            // Decoded leniently: console output of mvn may not be valid in the default charset
            List<String> lines = new String(Files.readAllBytes(output), Charset.defaultCharset()).lines().toList();
            return String.join("\n", lines.subList(Math.max(0, lines.size() - OUTPUT_TAIL_LINES), lines.size()));
        } catch (IOException e) {
            return "<could not read " + output + ": " + e.getMessage() + ">";
        }
    }
}
