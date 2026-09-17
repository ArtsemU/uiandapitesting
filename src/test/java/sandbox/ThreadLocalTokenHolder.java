package sandbox;

public class ThreadLocalTokenHolder {

    private final ThreadLocal<String> token = new ThreadLocal<>();

    public void set(String value) {
        token.set(value);
    }

    public String get() {
        return token.get();
    }
}
