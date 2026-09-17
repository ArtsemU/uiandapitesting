package sandbox;

/**
 * Deliberately not thread-safe: a single shared field, no synchronization.
 * One thread's write can be read back by another thread before it writes
 * its own value - the leaked value is the point of the sandbox.
 */
public class SharedTokenHolder {

    private String token;

    public void set(String value) {
        token = value;
    }

    public String get() {
        return token;
    }
}
