package xyz.robbie.tabula;

import java.util.*;

/**
 * A Turn represents a series of moves which constitute a turn by a player.
 * <p>
 * Requires a constructor with no parameters.
 **/

public class Turn implements TurnInterface {
    List<MoveInterface> moves;

    public Turn() {
        moves = new ArrayList<MoveInterface>();
    }

    public void addMove(MoveInterface move) throws IllegalTurnException {
        if (moves.size() == 4) {
            throw new IllegalTurnException("Only four moves are allowed per turn.");
        }
        else {
            moves.add(move);
        }
    }

    public List<MoveInterface> getMoves() {
        return moves;
    }
}
