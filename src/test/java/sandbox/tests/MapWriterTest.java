package sandbox.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import sandbox.MapWriter;
import sandbox.UnsafeMapWriter;

/**
 * Sandbox for observing TestNG parallel execution mechanics against a shared
 * collection rather than a scalar counter - closer to the createdUsers case
 * in BookApiTests. Not part of the framework suites: run it through
 * sandbox-testng.xml.
 */
public class MapWriterTest {

    private static final Logger log = LoggerFactory.getLogger(MapWriterTest.class);

    private static final int INVOCATION_COUNT = 3;
    private static final int INCREMENTS_PER_INVOCATION = 10000;


    /** Shared on purpose: one instance for every invocation, so the writes race. */
    private final UnsafeMapWriter unsafeMapWriter = new UnsafeMapWriter();
    private final MapWriter concurrentMapWriter = new MapWriter();

    @Test(invocationCount = INVOCATION_COUNT, threadPoolSize = 3)
    public void writesUnsafeMap() {
        log.info("thread={} instance={}",
                Thread.currentThread().getName(), System.identityHashCode(this));

        long start = System.nanoTime();
        for (int i = 0; i < INCREMENTS_PER_INVOCATION; i++) {
            String key = "key-" + Thread.currentThread().getName() + "-" + System.nanoTime();
            unsafeMapWriter.put(key, "value");
        }
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        log.info("thread={} elapsed={}ms", Thread.currentThread().getName(), elapsedMs);
    }

    @Test(invocationCount = INVOCATION_COUNT, threadPoolSize = 3)
    public void writesConcurrentMap() {
        log.info("thread={} instance={}",
                Thread.currentThread().getName(), System.identityHashCode(this));

        long start = System.nanoTime();
        for (int i = 0; i < INCREMENTS_PER_INVOCATION; i++) {
            String key = "key-" + Thread.currentThread().getName() + "-" + System.nanoTime();
            concurrentMapWriter.put(key, "value");
        }
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        log.info("thread={} elapsed={}ms", Thread.currentThread().getName(), elapsedMs);
    }

    @AfterClass
    public void logFinalSizes() {
        log.info("final unsafe map size={} expected={}",
                unsafeMapWriter.size(), INVOCATION_COUNT);
        log.info("final concurrent map size={} expected={}",
                concurrentMapWriter.size(), INVOCATION_COUNT);
    }
}
