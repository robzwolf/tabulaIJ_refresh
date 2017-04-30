package xyz.robbie.tabula;

import java.util.*;

/**
 * A Turn represents a series of moves which constitute a turn by a player.
 *
 * Requires a constructor with no parameters.
 **/

public class Turn implements TurnInterface {
    private List<MoveInterface> moves;

    public Turn() {
        moves = new ArrayList<MoveInterface>();
    }

    /**
     * @param move to be added after the moves already defined in the current turn
     *
     * @throws IllegalTurnException if there are already four or more moves in the turn
     */
    public void addMove(MoveInterface move) throws IllegalTurnException {
        if (moves.size() == 4) {
            throw new IllegalTurnException("Only four moves are allowed per turn.");
        } else {
            moves.add(move);
        }
    }

    public List<MoveInterface> getMoves() {
        return moves;
    }
}
