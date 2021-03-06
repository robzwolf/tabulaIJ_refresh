package xyz.robbie.tabula;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Player represents a player in the game of tabula
 *
 * Up to three different implementations of this interface can be provided: HumanConsolePlayer; ; ComputerPlayer; HumanGUIPlayer
 *
 * Each implementation requires a constructor with no parameters.
 **/

public class HumanConsolePlayer implements PlayerInterface {
    private final String typeOfPlayer = "human"; // This is used for Gson so that we have a string representation of what type of player this is

    private transient Scanner scanner;
    private transient String input;

    public HumanConsolePlayer() {
        scanner = new Scanner(System.in);
        input = "";
    }

    public TurnInterface getTurn(Colour colour, BoardInterface board, List<Integer> diceValues) throws PauseException {
        System.out.println();
        System.out.println(board);
        System.out.println("== PLAYER " + colour.toString().toUpperCase() + " ==");
        if (diceValues.size() == 4) {
            System.out.println("You're lucky - you rolled a double! You rolled: " + PrettyStrings.prettifyList(diceValues));
        } else {
            System.out.println("The dice have been rolled. You rolled: " + PrettyStrings.prettifyList(diceValues));
        }

        if(board.possibleMoves(colour,diceValues).size() == 0) {
            System.out.println("No moves are available to you on this turn.");
        }

        List<MoveInterface> chosenMoves = new ArrayList<MoveInterface>();

        /* Loop through until diceValues() is empty */
        while (diceValues.size() > 0 && board.possibleMoves(colour, diceValues).size() > 0) {

            /* Ask user for their preferred dice value */
            if(chosenMoves.size() != 0) { // Don't re-print die values if we've just done it above
                System.out.println("\nThe remaining die values available to you are: " + PrettyStrings.prettifyList(diceValues));
            }
            System.out.println("Enter which die value you wish to use " + PrettyStrings.ordinalNumber(chosenMoves.size() + 1) + (chosenMoves.size() == 0 ? ", or press P to pause" : "") + ":"); // when chosenMoves is empty, get ordinalNumbers[0] and so on
            int chosenDie = askUserForNum(diceValues, "%s is not one of the values you rolled. Try again:");

            /* Ask user for move source location
             * First, check if they have any knocked pieces */
            int numKnocked = board.getKnockedLocation().numberOfPieces(colour);
            int chosenSourceLocation;
            if(numKnocked > 0) {
                System.out.println("You currently have " + numKnocked + " knocked piece" + (numKnocked == 1 ? "" : "s") + ". The die value " + chosenDie + " will be used to move a piece from the knocked location.");
                chosenSourceLocation = 0;
            } else {
                System.out.println("Enter from which location you wish to move a counter " + chosenDie + " space" + (chosenDie == 1 ? "" : "s") + " (for the start location, enter 0):");
                List<Integer> availableLocationNums = new ArrayList<Integer>();
                if(board.getStartLocation().canRemovePiece(colour)) {
                    availableLocationNums.add(0);
                }
                for (int j = 1; j < BoardInterface.NUMBER_OF_LOCATIONS; j++) {
                    try {
                        if(board.getBoardLocation(j).canRemovePiece(colour)) {
                            availableLocationNums.add(j);
                        }
                    } catch (NoSuchLocationException e) {

                        /* Should never be called */
                        System.out.println("Something went terribly wrong.");
                        e.printStackTrace();
                    }
                }
                chosenSourceLocation = askUserForNum(availableLocationNums, "%s is not a valid location. Try again:");
            }

            MoveInterface calculatedMove = new Move();
            try {
                calculatedMove.setDiceValue(chosenDie);
                calculatedMove.setSourceLocation(chosenSourceLocation);
                if (board.canMakeMove(colour, calculatedMove)) {
                    try {
                        board.makeMove(colour, calculatedMove);
                        diceValues.remove(Integer.valueOf(chosenDie));
                        chosenMoves.add(calculatedMove);
                        if(numKnocked == 0) { // Don't print this if we forced the player to move their knocked piece first
                            System.out.println("You chose to move a piece " + chosenDie + " space" + (chosenDie == 1 ? "" : "s") + " from location " + chosenSourceLocation + ".");
                        }
                    } catch (IllegalMoveException e) {
                        System.out.println("That move is not valid. Try again.");
                    }
                } else {
                    System.out.println("That move is not valid. Try again.");
                }
            } catch (IllegalMoveException e) {
                System.out.println("Something went wrong. That die value is not valid.");
                e.printStackTrace();
            } catch (NoSuchLocationException e) {
                System.out.println("Something went wrong. That location number is not valid.");
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.out.println("Something went catastrophically wrong!");
                e.printStackTrace();
            }
        }

        TurnInterface turn = new Turn();
        for (MoveInterface move : chosenMoves) {
            try {
                turn.addMove(move);
            } catch (IllegalTurnException e) {

                // Will never be called
                e.printStackTrace();
            }
        }

        return turn;
    }

    /**
     * Repeatedly ask the user for a specific number out of a list until they give a valid response.
     *
     * @param allowableValues List of values from which the user should choose
     * @param errMessage      Message to print if the user does not enter an acceptable value. Use %s to refer to user's input.
     * @return The (valid) number which was chosen by the user.
     */
    private int askUserForNum(List<Integer> allowableValues, String errMessage) throws PauseException {
        Integer chosenNum = null;
        do {

            input = scanner.nextLine().toLowerCase();

            if(input.equals("p")) {
                throw new PauseException("Game paused");
            }

            /* Check if the user entered a number or something else */
            try {
                chosenNum = Integer.parseInt(input);

                /* Check user entered a value that is contained in allowableValue */
                if (!allowableValues.contains(Integer.parseInt(input))) // user hasn't entered a valid value
                {
                    System.out.println(String.format(errMessage, input));
                    chosenNum = null;
                }
            } catch (NumberFormatException e) {
                System.out.println("Your input, '" + input + "', is not a valid number. Try again:");
            }
        } while (chosenNum == null);

        return chosenNum;
    }

    public String toString() {
        return "human";
    }

}
