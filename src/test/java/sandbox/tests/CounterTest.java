package sandbox.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import sandbox.Counter;
import sandbox.SynchronizedCounter;

/**
 * Sandbox for observing TestNG parallel execution mechanics.
 * Not part of the framework suites: run it through sandbox-testng.xml.
 */
public class CounterTest {

    private static final Logger log = LoggerFactory.getLogger(CounterTest.class);

    private static final int INVOCATION_COUNT = 3;
    private static final int INCREMENTS_PER_INVOCATION = 10000000;

    /** Shared on purpose: one instance for every invocation, so the increments race. */
    private final Counter counter = new Counter();
    private final SynchronizedCounter synchronizedCounter = new SynchronizedCounter();

    @Test(invocationCount = INVOCATION_COUNT, threadPoolSize = 3)
    public void incrementsSharedCounter() {
        log.info("thread={} instance={}",
                Thread.currentThread().getName(), System.identityHashCode(this));

        for (int i = 0; i < INCREMENTS_PER_INVOCATION; i++) {
            counter.increment();
        }
    }

    //
    @Test(invocationCount = INVOCATION_COUNT, threadPoolSize = 3)
    public void incrementsSynchronizedCounter() {
        log.info("thread={} instance={}",
                Thread.currentThread().getName(), System.identityHashCode(this));

        for (int i = 0; i < INCREMENTS_PER_INVOCATION; i++) {
            synchronizedCounter.increment();
        }
    }

    @AfterClass
    public void logFinalCount() {
        log.info("final count={} expected={}",
                counter.getCount(), INVOCATION_COUNT * INCREMENTS_PER_INVOCATION);
        log.info("final synchronized count={} expected={}",
                synchronizedCounter.getCount(), INVOCATION_COUNT * INCREMENTS_PER_INVOCATION);
    }
}
