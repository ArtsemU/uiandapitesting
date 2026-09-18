package sandbox;

/**
 * Two flags, one field each - to compare visibility across threads.
 * plainStop: no volatile, no synchronization - a write from one thread
 * is not guaranteed to become visible to another thread reading it.
 * volatileStop: same idea, but volatile guarantees the visibility.
 */
public class FlagWorker {
    private boolean plainStop = false;
    private volatile boolean volatileStop = false;

    public void spinUntilPlainStop() {
        while (!plainStop) {
            // busy-wait
        }
    }

    public void spinUntilVolatileStop() {
        while (!volatileStop) {
            // busy-wait
        }
    }

    public void stopPlain() {
        plainStop = true;
    }

    public void stopVolatile() {
        volatileStop = true;
    }
}
