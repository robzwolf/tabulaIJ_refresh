package xyz.robbie.tabula;

import java.util.List;

/**
 * Player represents a player in the game of tabula
 * <p>
 * Up to three different implementations of this interface can be provided: HumanConsolePlayer; ; ComputerPlayer; HumanGUIPlayer
 * <p>
 * Each implementation requires a constructor with no parameters.
 **/

public class HumanGUIPlayer implements PlayerInterface {
    public TurnInterface getTurn(Colour colour, BoardInterface board, List<Integer> diceValues) throws PauseException {
        return null;
    }
}
