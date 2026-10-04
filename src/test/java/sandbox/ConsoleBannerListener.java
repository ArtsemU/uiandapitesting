package sandbox;

import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Educational listener: prints a raw console banner per test result, to see how
 * TestNG listeners fire and how registration mechanisms stack. Not part of the
 * reporting feature (see the reporting package for that).
 *
 * Registered twice on purpose: in the root testng.xml {@code <listeners>} block
 * and with {@code @Listeners} on sandbox.tests.CowTest.
 */
public class ConsoleBannerListener implements ITestListener {

    @Override
    public void onTestSuccess(ITestResult result) {
        printBanner("test passed", result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        printBanner("test failed", result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        printBanner("test skipped", result);
    }

    private void printBanner(String text, ITestResult result) {
        String middle = "**** " + text + " ****";
        String border = "*".repeat(middle.length());
        String name = result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
        // One println so lines from parallel threads do not interleave inside a banner
        System.out.println(border + "\n" + middle + "\n" + border + "\n  -> " + name
                + " [listener@" + Integer.toHexString(System.identityHashCode(this)) + "]");
    }
}
