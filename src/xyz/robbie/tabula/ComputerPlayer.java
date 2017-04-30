package xyz.robbie.tabula;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Player represents a player in the game of tabula
 *
 * Up to three different implementations of this interface can be provided: HumanConsolePlayer; ; ComputerPlayer; HumanGUIPlayer
 *
 * Each implementation requires a constructor with no parameters.
 **/

public class ComputerPlayer implements PlayerInterface {
    private final String typeOfPlayer = "computer";

    public TurnInterface getTurn(Colour colour, BoardInterface board, List<Integer> diceValues) throws PauseException {
        String c = colour.toString().toUpperCase();
        System.out.println();
        System.out.println(board);
        System.out.println("== PLAYER " + c + " (COMPUTER) ==");
        if (diceValues.size() == 4) {
            System.out.print("Computer rolled a double.");
        } else {
            System.out.print("Computer rolled the dice.");
        }
        System.out.println(" Die values available to computer are: " + PrettyStrings.prettifyList(diceValues));

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

    public String toString() {
        return "computer";
    }
}
