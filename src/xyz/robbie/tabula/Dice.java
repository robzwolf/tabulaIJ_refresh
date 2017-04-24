package xyz.robbie.tabula;

import java.util.*;

/**
 * Dice represents a pair of dice as used in tabula.
 *
 * It should use the Die class so that all randomness comes from there
 *
 * Requires a constructor with no parameters. Initially the dice have no value until they are rolled
**/

public class Dice implements DiceInterface
{
    private Die d1;
    private Die d2;

    public Dice()
    {
        d1 = new Die();
        d2 = new Die();
    }

    public boolean haveRolled()
    {
        return d1.hasRolled() && d2.hasRolled();
    }

    public void roll()
    {
        d1.roll();
        d2.roll();
    }
    
    public List<Integer> getValues() throws NotRolledYetException
    {
        List<Integer> values = new ArrayList<Integer>();
        if(d1.getValue() == d2.getValue()) // Rolled a double
        {
            // The same value four times
            values.add(d1.getValue());
            values.add(d1.getValue());
            values.add(d1.getValue());
            values.add(d1.getValue());
        }
        else
        {
            values.add(d1.getValue());
            values.add(d2.getValue());
        }
        return values;
    }

    public void clear()
    {
        d1.clear();
        d2.clear();
    }

    public List<DieInterface> getDice()
    {
        List<DieInterface> dice = new ArrayList<DieInterface>();
        dice.add(d1);
        dice.add(d2);
        return dice;
    }
}
