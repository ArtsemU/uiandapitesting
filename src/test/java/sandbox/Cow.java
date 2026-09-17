package sandbox;

/**
 * Sandbox for practicing TestNG suite/thread configuration. Deliberately not
 * about concurrency: no shared state, each test builds its own instance.
 */
public class Cow {

    private String name;
    private double availableMilkLiters;
    private boolean hungry;
    private int age;

    public Cow(String name) {
        this.name = name;
        this.availableMilkLiters = 100.0;
        this.hungry = false;
        this.age = 0;
    }

    public String speak() {
        return "Muu";
    }

    public double milk(double liters) {
        double milked = Math.min(liters, availableMilkLiters);
        availableMilkLiters -= milked;
        return milked;
    }

    public void feed() {
        hungry = false;
    }

    public void haveBirthday() {
        age++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAvailableMilkLiters() {
        return availableMilkLiters;
    }

    public void setAvailableMilkLiters(double availableMilkLiters) {
        this.availableMilkLiters = availableMilkLiters;
    }

    public boolean isHungry() {
        return hungry;
    }

    public void setHungry(boolean hungry) {
        this.hungry = hungry;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
