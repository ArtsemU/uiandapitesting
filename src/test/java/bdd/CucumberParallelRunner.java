package bdd;

import org.testng.annotations.DataProvider;

/**
 * Runs the same scenarios as CucumberRunner, one scenario per TestNG data-provider
 * invocation, in parallel. @CucumberOptions is read from CucumberRunner: Cucumber
 * walks the class hierarchy when it looks for the annotation. The pool size comes
 * from data-provider-thread-count in the suite XML.
 */
public class CucumberParallelRunner extends CucumberRunner {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
