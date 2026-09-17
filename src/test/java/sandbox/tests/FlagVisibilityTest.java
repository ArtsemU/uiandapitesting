package sandbox.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import sandbox.FlagWorker;

/**
 * Sandbox for observing visibility of a plain field write across threads,
 * versus a volatile field. Observational only - JIT behavior varies by
 * JVM/run, so a plain worker that happens to stop promptly on one run
 * does not disprove the visibility hazard.
 */
public class FlagVisibilityTest {

    private static final Logger log = LoggerFactory.getLogger(FlagVisibilityTest.class);

    @Test
    public void plainFlagMayNotStopPromptly() throws InterruptedException {
        FlagWorker worker = new FlagWorker();
        Thread workerThread = new Thread(worker::spinUntilPlainStop);
        workerThread.start();

        Thread.sleep(200);
        worker.stopPlain();
        workerThread.join(2000);

        log.info("plain flag: stopped={}", !workerThread.isAlive());
    }

    @Test
    public void volatileFlagStopsPromptly() throws InterruptedException {
        FlagWorker worker = new FlagWorker();
        Thread workerThread = new Thread(worker::spinUntilVolatileStop);
        workerThread.start();

        Thread.sleep(200);
        worker.stopVolatile();
        workerThread.join(2000);

        log.info("volatile flag: stopped={}", !workerThread.isAlive());
    }
}
