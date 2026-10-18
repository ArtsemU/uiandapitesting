package lessonslearned.cucumber.orangehrm;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

public class OrangeParallelRunner extends OrangeRunner {

    private static final int THREADS = 3;

    @Override
    @BeforeClass(alwaysRun = true)
    public void setUpClass(ITestContext context) {
        context.getCurrentXmlTest().getSuite().setDataProviderThreadCount(THREADS);
        super.setUpClass(context);
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
