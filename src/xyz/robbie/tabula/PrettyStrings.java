package xyz.robbie.tabula;

import java.util.List;

/**
 * Created by Robbie Jakob-Whitworth on 29/04/2017.
 * Used to prettify inputs for pretty printing
 */
public class PrettyStrings {

    public static String ordinalNumber(int n) {
        switch(n) {
            case 1: {
                return "first";
            }
            case 2: {
                return "second";
            }
            case 3: {
                return "third";
            }
            case 4: {
                return "fourth";
            }
            default: {
                return "";
            }
        }
    }

    public static String prettifyList(List<Integer> diceValues) {
        String output = "";
        if (diceValues.size() == 1) {
            return "" + diceValues.get(0);
        }
        for (int i = 0; i < diceValues.size() - 2; i++) {
            output += diceValues.get(i) + ", ";
        }
        if (diceValues.size() >= 2) {
            output += diceValues.get(diceValues.size() - 2) + " and " + diceValues.get(diceValues.size() - 1);
        }

        return output;
    }
}
