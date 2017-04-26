package xyz.robbie.tabula;

import java.util.*;

/**
 * Location represents a single location on the board, but not its position
 * Locations on the main part of the board may only contain a single colour of piece.
 * <p>
 * Off-board locations, i.e. the starting location, the finishing location and the knocked-off location
 * can contain pieces of both colours and are referred to as mixed.
 * <p>
 * Requires a constructor with one parameter (the name of the location), which creates a non-mixed location with no pieces.
 */

public class Location implements LocationInterface {

    private String name;
    private boolean mixed;
    private HashMap<Colour, Integer> pieces;

    public Location(String name) {
        setName(name);
        setMixed(false);
        pieces = new HashMap<Colour, Integer>();

        // Initialise the pieces HashMap
        for (Colour colour : Colour.values()) {
            pieces.put(colour, 0);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name != null) ? name : "";
    }

    public boolean isMixed() {
        return mixed;
    }

    public void setMixed(boolean isMixed) {
        mixed = isMixed;
    }

    public boolean isEmpty() {
        for (int count : pieces.values()) {
            if (count != 0) {
                return false;
            }
        }

        return true;
    }

    public int numberOfPieces(Colour colour) {
        return pieces.get(colour);
    }

    public boolean canAddPiece(Colour colour) {
        /* A piece can be added if:
           - The space is empty
           - The space only has counters of the same colour
           - The space has exactly one counter of the opposite colour
           - The space is mixed

           If a counter moves to a space with one counter of the opposite
           colour, the opposition counter is knocked off and is placed in a
           holding area
        */

        if (this.isEmpty()) {                                       // If the space is empty
            return true;
        }

        if(this.isMixed()) {                                        // If the space is mixed
            return true;
        } else {
            System.out.println("numberOfPieces(" + colour + ") = " + numberOfPieces(colour));
            System.out.println("numberOfPieces(" + colour.otherColour() + ") = " + numberOfPieces(colour.otherColour()));
            if(numberOfPieces(colour.otherColour()) > 1) {          // If the space has more than one counter of the other colour
                return false;
            } else {                                                // If the space only has one or zero counters of the other colour
                return true;
            }
//            if(this.numberOfPieces(colour) != 0) {                  // If the space has only counters of the same colour
//                return true;
//            } else if(numberOfPieces(colour.otherColour()) == 1) {  // If the space is not mixed and has exactly one counter of the opposite colour
//                return true;
//            }
        }

        /* If none of the above conditions are satisfied */
//        return false;
    }

    public Colour addPieceGetKnocked(Colour colour) throws IllegalMoveException {
        if(colour == null) {
            throw new IllegalMoveException("Null colour");
        }

        if(canAddPiece(colour)) {
            incrementColour(colour);
            if(numberOfPieces(colour.otherColour()) == 1) {
                return colour.otherColour();
            }
        } else {
            throw new IllegalMoveException("Cannot add piece");
        }

        return null;
    }

    private void incrementColour(Colour c) {
        pieces.put(c, numberOfPieces(c) + 1);
    }

    private void decrementColour(Colour c) {
        pieces.put(c, numberOfPieces(c) - 1);
    }

    public boolean canRemovePiece(Colour colour) {
        /* Can only remove a piece if there are >0 pieces of that colour in this Location */
        return numberOfPieces(colour) > 0;
    }

    public void removePiece(Colour colour) throws IllegalMoveException {
        if (canRemovePiece(colour)) {
            decrementColour(colour);
        } else {
            throw new IllegalMoveException("No pieces of that colour (" + colour + ") are in that location.");
        }
    }

    public boolean isValid() {

        if (isEmpty()) {
            return true;
        }

        // invalid if not mixed AND >0 of EACH colour
        boolean moreThanOneColour = false;
        Colour firstColourWithSomePieces = null;
        for (Colour c : pieces.keySet()) {
            if (numberOfPieces(c) > 0) {
                if (firstColourWithSomePieces != null) {
                    moreThanOneColour = true;
                } else {
                    firstColourWithSomePieces = c;
                }
            } else if (numberOfPieces(c) < 0) // No negative values allowed
            {
                return false;
            }
        }
        return !isMixed() && !moreThanOneColour;
    }

    // For debugging
    public String toString() {
        String output = "getName(): " + getName();
        output += "\nisValid(): " + isValid();
        output += "\nisMixed(): " + isMixed();
        output += "\nisEmpty(): " + isEmpty();
        for (Colour c : Colour.values()) {
            output += "\nnumberOfPieces(" + c + "): " + numberOfPieces(c);
        }
        return output + "\n";
    }

}
