package xyz.robbie.tabula;

import java.util.Random;

/**
 * Die represents a single die.
 *
 * Implements DieInterface.
 *
 * Requires a constructor with no parameters. Initially the die has no value until it is rolled.
 *
 * A single static java.util.Random object should be the source of all randomisation.
 **/

public class Die implements DieInterface {
    private static Random randomiser;
    private Integer value; // null if unrolled

    public Die() {
        value = null;
        // Only bother initialising randomiser if it's not already been initialised!
        if (randomiser == null) {
            randomiser = new Random();
        }
    }

    /**
     * @return false when first constructed or cleared, then true once rolled (unless it is then cleared)
     **/
    public boolean hasRolled() {
        return value != null;
    }

    /**
     * rolls the die to give a it a value in the range 1-NUMBER_OF_SIDES_ON_DIE (inclusive)
     */
    public void roll() {
        value = randomiser.nextInt(NUMBER_OF_SIDES_ON_DIE) + 1; // nextInt(int bound) produces a random int between 0 (inc) and bound (exc), so add 1 to get a random int between 1 (inc) and bound (inc)
    }

    /**
     *
     * @return the visible face of the die, a value in the range 1-6
     *
     * @throws NotRolledYetException if the die has not been rolled or has been cleared since the last roll
     **/
    public int getValue() throws NotRolledYetException {
        if (hasRolled()) {
            return value;
        } else {
            throw new NotRolledYetException("Die has not yet been rolled.");
        }
    }

    /**
     * set the face value of the die: only needed when recreating a game state
     *
     * @param value the new value of the die. If it is not in an acceptable range then afterwards hasRolled() should return false.
     **/
    public void setValue(int value) {
        if (1 <= value && value <= NUMBER_OF_SIDES_ON_DIE) {
            this.value = value;
        } else {
            this.value = null;
        }
    }

    /**
     * clears the die so it has no value until it is rolled again
     **/
    public void clear() {
        value = null;
    }

    /**
     * sets the seed for the random number generator used by all dice
     *
     * @param seed the seed value to use for randomisation
     **/
    public void setSeed(long seed) {
        randomiser.setSeed(seed);
    }
}
