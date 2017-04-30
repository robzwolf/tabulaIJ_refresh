package xyz.robbie.tabula;

import java.util.*;

/**
 * Dice represents a pair of dice as used in tabula.
 *
 * It should use the Die class so that all randomness comes from there
 *
 * Requires a constructor with no parameters. Initially the dice have no value until they are rolled
 **/

public class Dice implements DiceInterface {
    private Die d1;
    private Die d2;

    public Dice() {
        d1 = new Die();
        d2 = new Die();
    }

    /**
     * @return true if and only if both of the dice have been rolled
     **/
    public boolean haveRolled() {
        return d1.hasRolled() && d2.hasRolled();
    }

    /**
     * Roll both of the dice
     */
    public void roll() {
        d1.roll();
        d2.roll();
    }

    /**
     * @return four numbers if there is a double, otherwise two
     *
     * @throws NotRolledYetException if either of the dice have not been rolled yet
     **/
    public List<Integer> getValues() throws NotRolledYetException {
        List<Integer> values = new ArrayList<Integer>();
        if (d1.getValue() == d2.getValue()) // Rolled a double
        {
            /* The same value four times */
            values.add(d1.getValue());
            values.add(d1.getValue());
            values.add(d1.getValue());
            values.add(d1.getValue());
        } else {
            values.add(d1.getValue());
            values.add(d2.getValue());
        }
        return values;
    }

    /**
     * clear both of the dice so they have no value until they are rolled again
     **/
    public void clear() {
        d1.clear();
        d2.clear();
    }

    /**
     * Get the individual dice in a list.
     *
     * @return the Die objects in a list, which will have length 2
     */
    public List<DieInterface> getDice() {
        List<DieInterface> dice = new ArrayList<DieInterface>();
        dice.add(d1);
        dice.add(d2);
        return dice;
    }
}
