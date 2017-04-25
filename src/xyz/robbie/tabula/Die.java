package xyz.robbie.tabula;

import java.util.Random;

/**
 * Die represents a single die.
 * <p>
 * Implements DieInterface.
 * <p>
 * Requires a constructor with no parameters. Initially the die has no value until it is rolled.
 * <p>
 * A single static java.util.Random object should be the source of all randomisation.
 **/

public class Die implements DieInterface {
    private static Random randomiser;
    private Integer value; // null if unrolled

    public Die() {
        value = null;
        // Only bother initialising randomiser if it's not already been initialised!
        if (!(randomiser instanceof Random)) {
            randomiser = new Random();
        }
    }

    public boolean hasRolled() {
        return value != null;
    }

    public void roll() {
        value = randomiser.nextInt(NUMBER_OF_SIDES_ON_DIE) + 1; // nextInt(int bound) produces a random int between 0 (inc) and bound (exc), so add 1 to get a random int between 1 (inc) and bound (inc)
    }

    public int getValue() throws NotRolledYetException {
        if (hasRolled()) {
            return value;
        }
        else {
            throw new NotRolledYetException("Die has not yet been rolled.");
        }
    }

    public void setValue(int value) {
        if (1 <= value && value <= NUMBER_OF_SIDES_ON_DIE) {
            this.value = value;
        }
        else {
            this.value = null;
        }
    }

    public void clear() {
        value = null;
    }

    public void setSeed(long seed) {
        randomiser.setSeed(seed);
    }
}
