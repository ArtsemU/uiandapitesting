package sandbox.tests;

import api.tests.BookApiTests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import sandbox.Cow;

/**
 * Sandbox for practicing TestNG suite/thread configuration (not for
 * demonstrating a race condition). Each test builds its own Cow - no shared
 * state, nothing thread-related. Not part of the framework suites: run it
 * through sandbox-testng.xml.
 */
public class CowTest {
    private static final Logger log = LoggerFactory.getLogger(CowTest.class);

    @Test
    public void speak_returnsMuu() {
        log.info("thread={}", Thread.currentThread().getName());
        Cow cow = new Cow("Bessie");

        Assert.assertEquals(cow.speak(), "Muu", "Cow should always speak Muu");
    }

    @Test
    public void speak_returnsSameValueOnRepeatedCalls() {
        log.info("thread={}", Thread.currentThread().getName());
        Cow cow = new Cow("Bessie");

        Assert.assertEquals(cow.speak(), cow.speak(), "Repeated calls to speak should return the same value");
    }

    @Test
    public void newCow_startsWithHundredLiters() {
        log.info("thread={}", Thread.currentThread().getName());
        Cow cow = new Cow("Bessie");

        Assert.assertEquals(cow.getAvailableMilkLiters(), 100.0, "New cow should start with 100.0 liters of milk");
    }

    @Test
    public void newCow_startsNotHungry() {
        log.info("thread={}", Thread.currentThread().getName());
        Cow cow = new Cow("Bessie");

        Assert.assertFalse(cow.isHungry(), "New cow should not start hungry");
    }

    @Test
    public void newCow_startsAtAgeZero() {
        log.info("thread={}", Thread.currentThread().getName());
        Cow cow = new Cow("Bessie");

        Assert.assertEquals(cow.getAge(), 0, "New cow should start at age 0");
    }

    @Test
    public void constructor_setsNameCorrectly() {
        log.info("thread={}", Thread.currentThread().getName());
        Cow cow = new Cow("Daisy");

        Assert.assertEquals(cow.getName(), "Daisy", "Constructor should set the cow's name");
    }

    @Test
    public void milkingLessThanAvailable_reducesByExactAmount() {
        Cow cow = new Cow("Bessie");

        cow.milk(30.0);

        Assert.assertEquals(cow.getAvailableMilkLiters(), 70.0, "Balance should be reduced by exactly the milked amount");
    }

    @Test
    public void milkingLessThanAvailable_returnsRequestedAmount() {
        Cow cow = new Cow("Bessie");

        double milked = cow.milk(30.0);

        Assert.assertEquals(milked, 30.0, "Milking less than available should return the requested amount");
    }

    @Test
    public void milkingExactAvailable_leavesZeroBalance() {
        Cow cow = new Cow("Bessie");

        cow.milk(100.0);

        Assert.assertEquals(cow.getAvailableMilkLiters(), 0.0, "Milking the exact available amount should leave zero balance");
    }

    @Test
    public void milkingExactAvailable_returnsFullAmount() {
        Cow cow = new Cow("Bessie");

        double milked = cow.milk(100.0);

        Assert.assertEquals(milked, 100.0, "Milking the exact available amount should return the full amount");
    }

    @Test
    public void milkingMoreThanAvailable_capsBalanceAtZero() {
        Cow cow = new Cow("Bessie");

        cow.milk(150.0);

        Assert.assertEquals(cow.getAvailableMilkLiters(), 0.0, "Balance should not go negative when over-milking");
    }

    @Test
    public void milkingMoreThanAvailable_returnsOnlyAvailableAmount() {
        Cow cow = new Cow("Bessie");

        double milked = cow.milk(150.0);

        Assert.assertEquals(milked, 100.0, "Over-milking should return only what was available");
    }

    @Test
    public void milkingZero_leavesBalanceUnchanged() {
        Cow cow = new Cow("Bessie");

        cow.milk(0.0);

        Assert.assertEquals(cow.getAvailableMilkLiters(), 100.0, "Milking zero liters should leave the balance unchanged");
    }

    @Test
    public void milkingZero_returnsZero() {
        Cow cow = new Cow("Bessie");

        double milked = cow.milk(0.0);

        Assert.assertEquals(milked, 0.0, "Milking zero liters should return zero");
    }

    @Test
    public void milkingTwiceInSuccession_reducesCumulatively() {
        Cow cow = new Cow("Bessie");

        cow.milk(40.0);
        cow.milk(25.0);

        Assert.assertEquals(cow.getAvailableMilkLiters(), 35.0, "Successive milkings should reduce the balance cumulatively");
    }

    @Test
    public void milkingAfterDepleted_returnsZero() {
        Cow cow = new Cow("Bessie");

        cow.milk(100.0);
        double milked = cow.milk(10.0);

        Assert.assertEquals(milked, 0.0, "Milking a depleted cow should return zero");
    }

    @Test
    public void milkingDoesNotAffectHungryFlag() {
        Cow cow = new Cow("Bessie");

        cow.milk(50.0);

        Assert.assertFalse(cow.isHungry(), "Milking should not change the hungry flag");
    }

    @Test
    public void milkingDoesNotAffectAge() {
        Cow cow = new Cow("Bessie");

        cow.milk(50.0);

        Assert.assertEquals(cow.getAge(), 0, "Milking should not change the age");
    }

    @Test
    public void feed_setsHungryFalse() {
        Cow cow = new Cow("Bessie");
        cow.setHungry(true);

        cow.feed();

        Assert.assertFalse(cow.isHungry(), "Feed should set hungry to false");
    }

    @Test
    public void feed_whenAlreadyNotHungry_remainsFalse() {
        Cow cow = new Cow("Bessie");

        cow.feed();

        Assert.assertFalse(cow.isHungry(), "Feed should leave an already fed cow not hungry");
    }

    @Test
    public void feed_doesNotAffectMilkBalance() {
        Cow cow = new Cow("Bessie");

        cow.feed();

        Assert.assertEquals(cow.getAvailableMilkLiters(), 100.0, "Feed should not change the milk balance");
    }

    @Test
    public void feed_doesNotAffectAge() {
        Cow cow = new Cow("Bessie");

        cow.feed();

        Assert.assertEquals(cow.getAge(), 0, "Feed should not change the age");
    }

    @Test
    public void haveBirthday_incrementsAgeByOne() {
        Cow cow = new Cow("Bessie");

        cow.haveBirthday();

        Assert.assertEquals(cow.getAge(), 1, "Birthday should increment age by exactly one");
    }

    @Test
    public void haveBirthday_calledMultipleTimes_incrementsEachTime() {
        Cow cow = new Cow("Bessie");

        cow.haveBirthday();
        cow.haveBirthday();
        cow.haveBirthday();

        Assert.assertEquals(cow.getAge(), 3, "Three birthdays should increment age by three total");
    }

    @Test
    public void haveBirthday_doesNotAffectMilkBalance() {
        Cow cow = new Cow("Bessie");

        cow.haveBirthday();

        Assert.assertEquals(cow.getAvailableMilkLiters(), 100.0, "Birthday should not change the milk balance");
    }

    @Test
    public void haveBirthday_doesNotAffectHungryFlag() {
        Cow cow = new Cow("Bessie");

        cow.haveBirthday();

        Assert.assertFalse(cow.isHungry(), "Birthday should not change the hungry flag");
    }

    @Test
    public void setName_getNameRoundTrip() {
        Cow cow = new Cow("Bessie");

        cow.setName("Clarabelle");

        Assert.assertEquals(cow.getName(), "Clarabelle", "Name getter should return the value passed to the setter");
    }

    @Test
    public void setAvailableMilkLiters_getAvailableMilkLitersRoundTrip() {
        Cow cow = new Cow("Bessie");

        cow.setAvailableMilkLiters(42.5);

        Assert.assertEquals(cow.getAvailableMilkLiters(), 42.5, "Milk liters getter should return the value passed to the setter");
    }

    @Test
    public void setHungry_getHungryRoundTrip() {
        Cow cow = new Cow("Bessie");

        cow.setHungry(true);

        Assert.assertTrue(cow.isHungry(), "Hungry getter should return the value passed to the setter");
    }

    @Test
    public void setAge_getAgeRoundTrip() {
        Cow cow = new Cow("Bessie");

        cow.setAge(7);

        Assert.assertEquals(cow.getAge(), 7, "Age getter should return the value passed to the setter");
    }

    @Test
    public void twoIndependentCows_milkingOneDoesNotAffectOther() {
        Cow first = new Cow("Bessie");
        Cow second = new Cow("Daisy");

        first.milk(60.0);

        Assert.assertEquals(second.getAvailableMilkLiters(), 100.0, "Milking one cow should not affect a different cow instance");
    }

    @DataProvider(name = "milkAmounts")
    public Object[][] milkAmounts() {
        return new Object[][] {
                { 30.0, 30.0 },
                { 100.0, 100.0 },
                { 150.0, 100.0 },
                { 0.0, 0.0 },
                { 99.9, 99.9 },
        };
    }

    @Test(dataProvider = "milkAmounts")
    public void milk_returnsExpectedAmountForRequestedLiters(double requestedLiters, double expectedMilked) {
        Cow cow = new Cow("Bessie");

        double milked = cow.milk(requestedLiters);

        Assert.assertEquals(milked, expectedMilked, "Milked amount did not match expectation for requested liters " + requestedLiters);
    }

    @DataProvider(name = "cowNames")
    public Object[][] cowNames() {
        return new Object[][] {
                { "Bessie" },
                { "Daisy" },
                { "Clarabelle" },
                { "Moobert" },
                { "Ferdinand" },
        };
    }

    @Test(dataProvider = "cowNames")
    public void cow_getNameReturnsConstructorValue(String name) {
        Cow cow = new Cow(name);

        Assert.assertEquals(cow.getName(), name, "getName should return the name passed to the constructor");
    }
}
