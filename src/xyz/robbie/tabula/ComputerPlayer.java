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

        TurnInterface turn = new Turn();

        while(diceValues.size() != 0) {
            ArrayList<Integer> wrappedFirstDieValue = new ArrayList<Integer>();
            wrappedFirstDieValue.add(diceValues.get(0)); // Wrap it because possibleMoves() requires a List of die values
            Set<MoveInterface> possibleMoves = board.possibleMoves(colour, wrappedFirstDieValue);
            if(possibleMoves.size() == 0) {
                break;
            }
            MoveInterface chosenMove = possibleMoves.iterator().next(); // i.e. the 'first' element in possibleMoves
            try {
                board.makeMove(colour, chosenMove);
                turn.addMove(chosenMove); // i.e. the 'first' element in possibleMoves
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
