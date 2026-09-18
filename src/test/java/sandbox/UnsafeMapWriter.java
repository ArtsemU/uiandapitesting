package sandbox;

import java.util.HashMap;
import java.util.Map;

/**
 * Deliberately not thread-safe: a plain HashMap, no synchronization.
 * Concurrent writes to distinct keys can still lose entries or corrupt
 * internal state during a resize - the lost updates under concurrent
 * access are the point of the sandbox.
 */
public class UnsafeMapWriter {

    private final Map<String, String> data = new HashMap<>();

    public void put(String key, String value) {
        data.put(key, value);
    }

    public int size() {
        return data.size();
    }
}
