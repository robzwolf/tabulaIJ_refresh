package xyz.robbie.tabula;

/**
 * Move represents the use of a single Die to move a single piece.
 *
 * Requires a constructor with no parameters.
 **/

public class Move implements MoveInterface {
    private Integer sourceLocation;
    private int dieValue;

    public Move() {
        sourceLocation = null;
        dieValue = 0;
    }

    /**
     * @param locationNumber represents the board position to move a piece from
     * in the range 0-24. 0 represents off the board (the knocked location if there are pieces there, otherwise the off-board start location). A locationNumber of 1-24 refers to locations on the board with 1 being the first and 24 being the last.
     * @throws NoSuchLocationException if locationNumber is not in the range 0-24
     **/
    public void setSourceLocation(int locationNumber) throws NoSuchLocationException {
        if (locationNumber < 0 || locationNumber > BoardInterface.NUMBER_OF_LOCATIONS) {
            throw new NoSuchLocationException("That is not a valid location. Locations must lie in the range 0 to " + BoardInterface.NUMBER_OF_LOCATIONS);
        } else {
            this.sourceLocation = locationNumber;
        }

    }

    public int getSourceLocation() {
        return sourceLocation;
    }

    /* interface is wrong (says range 0-6, should be range 1-6) - see https://duo.dur.ac.uk/bbcswebdav/pid-3988457-dt-content-rid-16369722_2/courses/COMP1011_2016/Tabula%20FAQs%281%29.pdf, retrieved 03/04/2017
       setDiceValue() should really be named setDieValue() */
    /**
     * A
     * @param diceValue represents the value of the dice to be used in the move
     *
     * @throws IllegalMoveException if diceValue is not in the range 0-6
     **/
    public void setDiceValue(int diceValue) throws IllegalMoveException {
        if (1 <= diceValue && diceValue <= DieInterface.NUMBER_OF_SIDES_ON_DIE) {
            this.dieValue = diceValue;
        } else {
            throw new IllegalMoveException("Dice value must be in the range 1-" + DieInterface.NUMBER_OF_SIDES_ON_DIE);
        }
    }

    /* name getDiceValue should really be name getDieValue */
    public int getDiceValue() {
        return dieValue;
    }

    /* For debugging */
    public String toString() {
        return "Move " + getDiceValue() + " spaces from location no. " + getSourceLocation();
    }

}
