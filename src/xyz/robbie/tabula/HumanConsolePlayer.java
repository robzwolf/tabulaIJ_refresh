package xyz.robbie.tabula;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Player represents a player in the game of tabula
 *
 * Up to three different implementations of this interface can be provided: HumanConsolePlayer; ; ComputerPlayer; HumanGUIPlayer
 *
 * Each implementation requires a constructor with no parameters.
**/

public class HumanConsolePlayer implements PlayerInterface
{
    private static final String[] ordinalNumbers = { "first", "second", "third", "fourth" };

    private Scanner scanner;
    private String input;

    public HumanConsolePlayer()
    {
        scanner = new Scanner(System.in);
        input = "";
    }

    private String getPrettyNumbersList(List<Integer> diceValues)
    {
        String output = "";
        if(diceValues.size() == 1)
        {
            return "" + diceValues.get(0);
        }
        for (int i=0; i<diceValues.size()-2; i++)
        {
            output += diceValues.get(i) + ", ";
        }
        if(diceValues.size() >= 2)
        {
            output += diceValues.get(diceValues.size()-2) + " and " + diceValues.get(diceValues.size()-1);
        }

        return output;
    }

    /** Takes a string and returns it in Title Case
     * @param str The string to convert
     * @return The Title Case form of the string
     */
    private static String strToTitleCase(String str)
    {
        if(str.length() == 0)
        {
            return "";
        }
        String[] strSplit = str.split("");
        String output = "";
        output += strSplit[0].toUpperCase();
        for(int i=1;i<strSplit.length;i++){
            output += strSplit[i].toLowerCase();
        }
        return output;
    }

    public TurnInterface getTurn(Colour colour, BoardInterface board, List<Integer> diceValues) throws PauseException
    {
        System.out.println(board);
        System.out.println("Player " + strToTitleCase(colour + ", it's your turn."));
        if(diceValues.size() == 4)
        {
            System.out.print("You're lucky - you rolled a double! ");
        }
        else
        {
            System.out.print("The dice have been rolled. ");
        }

        List<MoveInterface> moves = new ArrayList<MoveInterface>();

        // Loop through until diceValues() is empty
        do
        {
            // Ask user for their preferred dice value
            System.out.println("The die values available to you are: " + getPrettyNumbersList(diceValues));
            System.out.println("Enter which die value you wish to use " + ordinalNumbers[moves.size()] + ":"); // when moves is empty, get ordinalNumbers[0] and so on
            int chosenDie = askUserForNum(diceValues, "%s is not one of the values you rolled. Try again:");

            // Ask user for move source location
            System.out.println("Enter from which location you wish to move a counter " + chosenDie + " space" + (chosenDie == 1 ? "" : "s") + " (for the start location, enter 0):");
            List<Integer> locationNums = new ArrayList<Integer>();
            for(int j=0; j<BoardInterface.NUMBER_OF_LOCATIONS; j++)
            {
                locationNums.add(j);
            }
            int chosenSourceLocation = askUserForNum(locationNums, "%s is not a valid location. Try again:");

            MoveInterface calculatedMove = new Move();
            try
            {
                calculatedMove.setDiceValue(chosenDie);
                calculatedMove.setSourceLocation(chosenSourceLocation);
                if(board.canMakeMove(colour, calculatedMove))
                {
                    diceValues.remove(Integer.valueOf(chosenDie));
                    moves.add(calculatedMove);
                    System.out.println("You chose to move a piece " + chosenDie + " space" + (chosenDie == 1 ? "" : "s") + " from location " + chosenSourceLocation + ".");
                }
                else
                {
                    System.out.println("That move is not valid. Try again.");
                }
            }
            catch (IllegalMoveException e)
            {
                System.out.println("Something went wrong. That die value is not valid.");
            }
            catch (NoSuchLocationException e)
            {
                System.out.println("Something went wrong. That location number is not valid.");
            }
            catch (NullPointerException e)
            {
                System.out.println("Something went catastrophically wrong!");
                e.printStackTrace();
            }
        } while(diceValues.size() > 0);

        TurnInterface turn = new Turn();
        for(MoveInterface move : moves)
        {
            try
            {
                turn.addMove(move);
            }
            catch(IllegalTurnException e)
            {
                // Will never be called
                e.printStackTrace();
            }
        }

        return turn;
    }

    /**
     * Repeatedly ask the user for a specific number out of a list until they give a valid response.
     * @param allowableValues List of values from which the user should choose
     * @param errMessage Message to print if the user does not enter an acceptable value. Use %s to refer to user's input.
     * @return The (valid) number which was chosen by the user.
     */
    private int askUserForNum(List<Integer> allowableValues, String errMessage)
    {
        Integer chosenNum = null;
        do
        {

            input = scanner.nextLine().toLowerCase();

            // Check if the user entered a number or something else
            try
            {
                chosenNum = Integer.parseInt(input);
                // Check user entered a value that is contained in allowableValue
                if(!allowableValues.contains(Integer.parseInt(input))) // user hasn't entered a valid value
                {
                    System.out.println(String.format(errMessage,input));
                    chosenNum = null;
                }
            }
            catch (NumberFormatException e)
            {
                System.out.println("Your input, '" + input + "', is not a valid number. Try again:");
            }
        } while(chosenNum == null);

        return chosenNum;
    }

    public String toString()
    {
        return "human";
    }

}
