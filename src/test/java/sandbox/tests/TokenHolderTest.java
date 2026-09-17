package sandbox.tests;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import sandbox.SharedTokenHolder;
import sandbox.ThreadLocalTokenHolder;

/**
 * Sandbox for observing TestNG parallel execution mechanics against a
 * shared field versus a ThreadLocal. Not part of the framework suites:
 * run it through sandbox-testng.xml.
 */
public class TokenHolderTest {

    private static final Logger log = LoggerFactory.getLogger(TokenHolderTest.class);

    private static final int INVOCATION_COUNT = 3;
    private static final int CHECKS_PER_INVOCATION = 10000;

    /** Shared on purpose: one instance for every invocation, so the writes race. */
    private final SharedTokenHolder sharedTokenHolder = new SharedTokenHolder();
    private final ThreadLocalTokenHolder threadLocalTokenHolder = new ThreadLocalTokenHolder();

    private final AtomicInteger sharedMismatches = new AtomicInteger(0);
    private final AtomicInteger threadLocalMismatches = new AtomicInteger(0);

    @Test(invocationCount = INVOCATION_COUNT, threadPoolSize = 3)
    public void readsBackSharedToken() {
        log.info("thread={} instance={}",
                Thread.currentThread().getName(), System.identityHashCode(this));

        String threadName = Thread.currentThread().getName();
        long start = System.nanoTime();
        for (int i = 0; i < CHECKS_PER_INVOCATION; i++) {
            String written = threadName + "-" + i;
            sharedTokenHolder.set(written);
            String readBack = sharedTokenHolder.get();
            if (!written.equals(readBack)) {
                sharedMismatches.incrementAndGet();
            }
        }
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        log.info("thread={} elapsed={}ms", Thread.currentThread().getName(), elapsedMs);
    }

    @Test(invocationCount = INVOCATION_COUNT, threadPoolSize = 3)
    public void readsBackThreadLocalToken() {
        log.info("thread={} instance={}",
                Thread.currentThread().getName(), System.identityHashCode(this));

        String threadName = Thread.currentThread().getName();
        long start = System.nanoTime();
        for (int i = 0; i < CHECKS_PER_INVOCATION; i++) {
            String written = threadName + "-" + i;
            threadLocalTokenHolder.set(written);
            String readBack = threadLocalTokenHolder.get();
            if (!written.equals(readBack)) {
                threadLocalMismatches.incrementAndGet();
            }
        }
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        log.info("thread={} elapsed={}ms", Thread.currentThread().getName(), elapsedMs);
    }

    @AfterClass
    public void logMismatchCounts() {
        log.info("shared token mismatches={} expected={}", sharedMismatches.get(), 0);
        log.info("thread-local token mismatches={} expected={}", threadLocalMismatches.get(), 0);
    }
}
