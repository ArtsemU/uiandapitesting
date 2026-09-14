package sandbox;

/**
 * Deliberately not thread-safe: no synchronization, no atomics.
 * The lost updates under concurrent access are the point of the sandbox.
 */
public class Counter {

    private int count = 0;

    public void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}
