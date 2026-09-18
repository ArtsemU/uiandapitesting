package sandbox;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapWriter {

    private final Map<String, String> data = new ConcurrentHashMap<>();

    public void put(String key, String value) {
        data.put(key, value);
    }

    public int size() {
        return data.size();
    }
}
