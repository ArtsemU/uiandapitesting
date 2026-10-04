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
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Regenerates the Allure HTML report ({@code mvn allure:report}) when a suite
 * finishes, pass or fail, so IDE runs that bypass Maven still get a fresh report.
 *
 * Registered through META-INF/services/org.testng.ITestNGListener, the same
 * ServiceLoader mechanism that registers Allure's own listener, so it fires under
 * any TestNG launcher. Fires once per suite; on Maven and CI runs it duplicates the
 * explicit allure:report step, which is harmless.
 *
 * A failure to generate the report is logged at ERROR and never fails the run.
 */
public class AllureReportListener implements ISuiteListener {
    private static final Logger log = LoggerFactory.getLogger(AllureReportListener.class);

    private static final long TIMEOUT_MINUTES = 5;
    private static final int OUTPUT_TAIL_LINES = 30;

    @Override
    public void onFinish(ISuite suite) {
        File projectDir = new File(System.getProperty("user.dir"));
        if (!new File(projectDir, "pom.xml").isFile()) {
            log.error("Allure report not generated: no pom.xml in working directory {}", projectDir);
            return;
        }

        Path output = projectDir.toPath().resolve("target").resolve("allure-report-listener.log");
        log.info("Suite '{}' finished, generating Allure report (output: {})", suite.getName(), output);
        try {
            Files.createDirectories(output.getParent());
            Process process = new ProcessBuilder(command())
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
                log.info("Allure report: {}", projectDir.toPath().resolve("allure-report").resolve("index.html"));
            }
        } catch (IOException e) {
            log.error("Allure report not generated: could not run mvn allure:report", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Allure report not generated: interrupted while waiting for mvn allure:report", e);
        }
    }

    // Windows: mvn is mvn.cmd, a batch file. cmd /c resolves it through PATH/PATHEXT like a
    // terminal does; starting a .cmd directly from ProcessBuilder depends on JDK batch-file
    // handling (jdk.lang.Process.allowAmbiguousCommands) and quotes arguments differently.
    private static List<String> command() {
        if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            return List.of("cmd", "/c", "mvn", "-B", "allure:report");
        }
        return List.of("mvn", "-B", "allure:report");
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
