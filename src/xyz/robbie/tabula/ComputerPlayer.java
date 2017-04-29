package xyz.robbie.tabula;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Player represents a player in the game of tabula
 * <p>
 * Up to three different implementations of this interface can be provided: HumanConsolePlayer; ; ComputerPlayer; HumanGUIPlayer
 * <p>
 * Each implementation requires a constructor with no parameters.
 **/

public class ComputerPlayer implements PlayerInterface {

//    private static final String[] ordinalNumbers = {"first", "second", "third", "fourth"};
//
//    private String getPrettyNumbersList(List<Integer> diceValues) {
//        String output = "";
//        if (diceValues.size() == 1) {
//            return "" + diceValues.get(0);
//        }
//        for (int i = 0; i < diceValues.size() - 2; i++) {
//            output += diceValues.get(i) + ", ";
//        }
//        if (diceValues.size() >= 2) {
//            output += diceValues.get(diceValues.size() - 2) + " and " + diceValues.get(diceValues.size() - 1);
//        }
//
//        return output;
//    }

    public TurnInterface getTurn(Colour colour, BoardInterface board, List<Integer> diceValues) throws PauseException {
        String c = colour.toString().toUpperCase();
//
        System.out.println();
        System.out.println(board);
        System.out.println("== PLAYER " + c + " (COMPUTER) ==");
        if (diceValues.size() == 4) {
            System.out.print("Computer rolled a double.");
        } else {
            System.out.print("Computer rolled the dice.");
        }
        System.out.println(" Die values available to computer are: " + PrettyStrings.prettifyList(diceValues));
//
//        List<MoveInterface> chosenMoves = new ArrayList<MoveInterface>();
//
//        // Loop through until diceValues() is empty
//        while (diceValues.size() > 0 && board.possibleMoves(colour, diceValues).size() > 0) {
//
//            System.out.println("board.possibleMoves(colour, diceValues).size() = " + board.possibleMoves(colour, diceValues).size());
//
//            // Ask user for their preferred dice value
//            System.out.println(c + ", The die values available to you are: " + getPrettyNumbersList(diceValues));
//            System.out.println(c + ", Enter which die value you wish to use " + ordinalNumbers[chosenMoves.size()] + ":"); // when chosenMoves is empty, get ordinalNumbers[0] and so on
////            int chosenDie = askUserForNum(diceValues, "%s is not one of the values you rolled. Try again:");
//            int chosenDie = askUserForNum(diceValues,"error choosing dice value %s");
////
////            // Ask user for move source location
////            // First, check if they have any knocked pieces
////            int numKnocked = board.getKnockedLocation().numberOfPieces(colour);
////            int chosenSourceLocation;
////            if(numKnocked > 0) {
////                System.out.println(c + ", You currently have " + numKnocked + " knocked piece" + (numKnocked == 1 ? "" : "s") + ". The die value " + chosenDie + " will be used to move a piece from the knocked location.");
////                chosenSourceLocation = 0; // The knocked location
////            } else {
////                System.out.println(c + ", Enter from which location you wish to move a counter " + chosenDie + " space" + (chosenDie == 1 ? "" : "s") + " (for the start location, enter 0):");
////                List<Integer> availableLocationNums = new ArrayList<Integer>();
////                if(board.getStartLocation().canRemovePiece(colour)) {
////                    availableLocationNums.add(0);
////                    boolean canAddToTarget;
////                    try {
////                        canAddToTarget = board.getBoardLocation(chosenDie).canAddPiece(colour);
////                    } catch (NoSuchLocationException e) {
////                        /* Should never happen unless number of sides on die is greater than number of locations, in which case we just don't add the start location to availableLocationNums */
////                    }
////                }
////                for (int j = 1; j < BoardInterface.NUMBER_OF_LOCATIONS; j++) {
////                    try {
////                        boolean canRemoveFromSource = board.getBoardLocation(j).canRemovePiece(colour);
////                        boolean canAddToTarget = (j+chosenDie > BoardInterface.NUMBER_OF_LOCATIONS ? board.getEndLocation() : board.getBoardLocation(j+chosenDie)).canAddPiece(colour);
////                        System.out.println("canRemoveFromSource = " + canRemoveFromSource);
////                        System.out.println("canAddToTarget = " + canAddToTarget);
////                        System.out.println("j = " + j);
////                        System.out.println("j+chosenDie = " + (j+chosenDie));
////                        if(canRemoveFromSource && canAddToTarget) {
////                            availableLocationNums.add(j);
////                            System.out.println("Added location " + j + " to availableLocationNums");
////                        }
////                    } catch (NoSuchLocationException e) {
////
////                        /* Might get called if j+chosenDie > BoardInterface.NUMBER_OF_LOCATIONS, in which case, just ignore this j and go to the next iteration */
////                        continue;
////                    }
////                }
////                for(MoveInterface move : board.possibleMoves(colour, diceValues)) {
////                    availableLocationNums.add(move.getDiceValue());
////                }
////                chosenSourceLocation = askUserForNum(availableLocationNums, "%s is not a valid location. Try again:");
////                System.out.println("chosenSourceLocation = " + chosenSourceLocation);
//////                if(chosenSourceLocation = )
////            }
//
////            MoveInterface calculatedMove = new Move();
////            try {
////                calculatedMove.setDiceValue(chosenDie);
////                calculatedMove.setSourceLocation(chosenSourceLocation);
////                if (board.canMakeMove(colour, calculatedMove)) {
////                    try {
////                        board.makeMove(colour, calculatedMove);
////                    } catch (IllegalMoveException e) {
////                        System.out.println(c + ", That move is not valid.");
////                        e.printStackTrace();
////                    }
////                    diceValues.remove(Integer.valueOf(chosenDie));
////                    chosenMoves.add(calculatedMove);
////                    if(numKnocked == 0) { // Don't print this if we forced the player to move their knocked piece first
////                        System.out.println(c + ", You chose to move a piece " + chosenDie + " space" + (chosenDie == 1 ? "" : "s") + " from location " + chosenSourceLocation + ".");
////                    }
////                } else {
////                    System.out.println(c + ", That move is not valid. Try again.");
////                    System.out.println("move.getSourceLocation() = " + calculatedMove.getSourceLocation() + " | move.getDiceValue() = " + calculatedMove.getDiceValue());
////                }
////            } catch (IllegalMoveException e) {
////                System.out.println(c + ", Something went wrong. That die value is not valid.");
////                e.printStackTrace();
////            } catch (NoSuchLocationException e) {
////                System.out.println(c + ", Something went wrong. That location number is not valid.");
////                e.printStackTrace();
////            } catch (NullPointerException e) {
////                System.out.println(c + ", Something went catastrophically wrong!");
////                e.printStackTrace();
////            }
////        }
//
//        TurnInterface turn = new Turn();
//        Set<MoveInterface> possibleMoves = board.possibleMoves(colour, diceValues);
//        if(possibleMoves.size() == 0) {
//            break;
//        }
//        for (MoveInterface move : possibleMoves) {
//            try {
//                turn.addMove(possibleMoves.iterator().next()); // i.e. the 'first' element in the set
//            } catch (IllegalTurnException e) {
//                // Will never be called
//                e.printStackTrace();
//            }
//        }
//
//        return turn;


        TurnInterface turn = new Turn();

        while(diceValues.size() != 0) {
            ArrayList<Integer> wrappedFirstDieValue = new ArrayList<Integer>();
            wrappedFirstDieValue.add(diceValues.get(0)); // Wrap it because possibleMoves() requires a List of die values
            Set<MoveInterface> possibleMoves = board.possibleMoves(colour, wrappedFirstDieValue);
            if(possibleMoves.size() == 0) {
                if(turn.getMoves().size() == 0) {
                    System.out.println("Computer had no possible moves.");
                } else {
                    System.out.println("Computer had no more possible moves.");
                }
                break;
            }
            MoveInterface chosenMove = possibleMoves.iterator().next(); // i.e. the 'first' element in possibleMoves
            try {
                board.makeMove(colour, chosenMove);
                turn.addMove(chosenMove); // i.e. the 'first' element in possibleMoves
                System.out.println("Computer moved " + wrappedFirstDieValue.get(0) + " space" + (wrappedFirstDieValue.get(0) > 1 ? "s" : "") + " from location " + chosenMove.getSourceLocation());
            } catch (IllegalTurnException | IllegalMoveException e) {
                // Should never happen
                e.printStackTrace();
            }
            diceValues.remove(0);
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
    private int askUserForNum(List<Integer> allowableValues, String errMessage) {
        System.out.println("Arrays.toString(allowableValues.toArray()) = " + Arrays.toString(allowableValues.toArray()));
        Integer chosenNum = null;
//        do {
//
////            input = scanner.nextLine().toLowerCase();
//
//            // Check if the user entered a number or something else
//            try {
//                chosenNum = Integer.parseInt(input);
//                // Check user entered a value that is contained in allowableValue
//                if (!allowableValues.contains(Integer.parseInt(input))) // user hasn't entered a valid value
//                {
//                    System.out.println(String.format(errMessage, input));
//                    chosenNum = null;
//                }
//            } catch (NumberFormatException e) {
//                System.out.println("Your input, '" + input + "', is not a valid number. Try again:");
//            }
//        } while (chosenNum == null);

        /* Always return the first item in the list */
//        chosenNum = allowableValues.get(0);

        /* Always return the last item in the list */
        chosenNum = allowableValues.size() > 0 ? allowableValues.get(allowableValues.size() - 1) : -1;

        return chosenNum;
    }

    public String toString() {
        return "computer";
    }
}
