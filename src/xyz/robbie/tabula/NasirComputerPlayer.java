/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xyz.robbie.tabula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nasir
 */
public class NasirComputerPlayer implements PlayerInterface{

    private int highestPos;
    private TurnInterface turn;
    private Set<MoveInterface> possSet;
    private Map<Integer, List<MoveInterface>> ranksf;
    private boolean moveAdded;

    public NasirComputerPlayer() {

    }

    public TurnInterface getTurn(Colour colour, BoardInterface board, List<Integer> diceValues) throws PauseException {
        System.out.println("==========TABULA - COMPUTER " + colour.toString() + "==========");
        System.out.println("The dice values are : " + diceValues);
        turn = new Turn();
        List<Integer> userDiceValues = new ArrayList<Integer>(diceValues);
        BoardInterface redo = board.clone();
        while (!userDiceValues.isEmpty()) {
            if (redo.possibleMoves(colour, userDiceValues).size() == 0) {
                return turn;
            }
            MoveInterface move = new Move();
            if (redo.getKnockedLocation().numberOfPieces(colour) > 0) {
                System.out.println("enter knocked");
                //System.err.println(userDiceValues);
                List<MoveInterface> possKnock = getKnockedMoves(redo, colour, userDiceValues);
                try {
                    MoveInterface m = possKnock.get(0);
                    turn.addMove(m);
                    redo.makeMove(colour, m);
                    System.out.println("made the move confirmed " + m.getSourceLocation());
                    userDiceValues.remove(userDiceValues.indexOf(m.getDiceValue()));
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                    System.out.println("arhghg");
                }

            } else {
                try {
                    ranksf = rankMoves(colour, redo, userDiceValues);
                    System.out.println("what" + userDiceValues);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                }
                System.out.println(ranksf);
                moveAdded = false;
                for (List<MoveInterface> moves: ranksf.values()) {
                    //List<MoveInterface> moves = new ArrayList<>(entry.getValue());
                    if (moves.isEmpty()) {
                        System.out.println("skipping " + ranksf.values() );
                        continue;
                    }
                    System.out.println("trying to add now");
                    System.out.println("the first move is from" + moves.get(0).getSourceLocation());
                    try {
                        for (MoveInterface m: moves) {
                            System.out.println("is this even working? " + m.getSourceLocation());
                            try {
                                moveAdded = true;
                                turn.addMove(m);
                                redo.makeMove(colour, m);
                                System.out.println("made the move confirmed " + m.getSourceLocation() + "die: " + m.getDiceValue());
                                userDiceValues.remove(userDiceValues.indexOf(m.getDiceValue()));
                                break;
                            } catch (Exception e) {
                                e.printStackTrace(System.out);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("nomove");
                    }
                    if (moveAdded) {
                        break;
                    }
                }
            }
        }
        return turn;
    }
    //players responsiblity to be valid


    public Map<Integer, List<MoveInterface>> rankMoves(Colour colour, BoardInterface board, List<Integer> diceValues) throws IllegalMoveException, NoSuchLocationException {
        BoardInterface redo = board.clone();
        Set<MoveInterface> possSet = new HashSet<>(redo.possibleMoves(colour, diceValues));
        System.out.println("number of possible moves: " + possSet.size());
        Map<Integer, List<MoveInterface>> ranks = new TreeMap<>();
        int knocked = redo.getKnockedLocation().numberOfPieces(colour.otherColour());
        highestPos = 0;
        for (MoveInterface m: possSet) {
            System.out.println("posset had location: " + m.getSourceLocation() + " dice val: " + m.getDiceValue());
            redo = board.clone();
            List<MoveInterface> moves = new ArrayList<>();
            redo.makeMove(colour, m);
            int newknocked = redo.getKnockedLocation().numberOfPieces(colour.otherColour());
            int newPos = m.getSourceLocation() + m.getDiceValue();
            if (newPos > redo.NUMBER_OF_LOCATIONS) {
                newPos = redo.NUMBER_OF_LOCATIONS + 1;
            }
            if (knocked != newknocked) {
                moves.add(m);
                ranks.put(0, moves);
                System.out.println("added to 0");
            } else if(newPos == redo.NUMBER_OF_LOCATIONS + 1) {
                moves.add(m);
                ranks.put(1, moves);
                System.out.println("added to 1");
            } else if (newPos > highestPos && newPos < redo.NUMBER_OF_LOCATIONS + 1) {
                moves.add(m);
                ranks.remove(2, moves);
                ranks.put(2, moves);
                highestPos = newPos;
                System.out.println("added to 2");
            } else if (redo.getBoardLocation(newPos).numberOfPieces(colour) == 2 && !redo.getBoardLocation(newPos).isMixed()) {
                moves.add(m);
                ranks.put(3, moves);
                System.out.println("added to 3");
            } else if (redo.getKnockedLocation().numberOfPieces(colour) > 0) {
                moves.add(m);
                ranks.put(5, moves);
                System.out.println("added to 5");
            } else {
                moves.add(m);
                ranks.put(4, moves);
                System.out.println("added to 4");
            }
        }
        System.out.println(ranks);
        return ranks;
    }

    public List<MoveInterface> getKnockedMoves(BoardInterface board, Colour colour, List<Integer> diceValues) {
        System.out.println("enter getknocked moves");
        Set<MoveInterface> all = board.possibleMoves(colour, diceValues);
        List<MoveInterface> posZero = new ArrayList<MoveInterface>();
        for (MoveInterface m: all) {
            if (m.getSourceLocation() == 0) {
                posZero.add(m);
                System.out.println("poszero contains location: " + m.getSourceLocation() + "and dice val: " + m.getDiceValue());
            }
        }
        return posZero;
    }

}