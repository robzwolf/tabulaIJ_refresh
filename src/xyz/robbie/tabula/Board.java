package xyz.robbie.tabula;

import java.util.*;

/**
 * Board represents the board state in the game of tabula (not including dice and players).
 *
 * Requires a constructor with no parameters which creates and initialises all of the locations for the start of the game.
 **/

public class Board implements BoardInterface {

    private String name;

    /*
    List of all the locations in game
    Location 0 ("Start") is the start location, off the board
    Location 1 ("Newcastle") is the first location on the board
    Location 24 ("Durham") is the last location on the board
    Location 25 ("Finish") is the finish location, off the board
    Location 26 ("Stockton") is the 'knocked' location
    Hence locations list should look like:
    START (0, OFF), 1 (ON), 2 (ON), ..., NUMBER_OF_LOCATIONS-1 (23, ON), NUMBER_OF_LOCATIONS (24, ON), END (25, OFF), KNOCKED (26, OFF)
    */
    private List<LocationInterface> locations;

    private static final String[] locationNames = {
            "Newcastle",        //  1
            "Gateshead",        //  2
            "Sunderland",       //  3
            "Peterlee",         //  4
            "Hartlepool",       //  5
            "Redcar",           //  6
            "Saltburn",         //  7
            "Staithes",         //  8
            "Guisborough",      //  9
            "Middlesbrough",    // 10
            "Thornaby",         // 11
            "Darlington",       // 12
            "Barnard Castle",   // 13
            "Middleton",        // 14
            "Stanhope",         // 15
            "Alston",           // 16
            "Haltwhistle",      // 17
            "Haydon Bridge",    // 18
            "Hexham",           // 19
            "Consett",          // 20
            "Bishop Auckland",  // 21
            "Newton Aycliffe",  // 22
            "Spennymoor",       // 23
            "Durham"            // 24
            /* initialiseBoard() accounts for location names higher than 24 */
    };

    private static final String START_NAME    = "START";
    private static final String FINISH_NAME   = "FINISH";
    private static final String KNOCKED_NAME  = "KNOCKED";
    public static final int    START_INDEX   = 0;                           // Public because we may need to access this from Game for loadGame()
    public static final int    FINISH_INDEX  = NUMBER_OF_LOCATIONS + 1;     // Public because we may need to access this from Game for loadGame()
    public static final int    KNOCKED_INDEX = NUMBER_OF_LOCATIONS + 2;     // Public because we may need to access this from Game for loadGame()

    public Board() {
        initialiseBoard();
        prePopulateStart();
    }

    public Board(boolean prePopulateStart) {
        if (prePopulateStart) {
            prePopulateStart();
        }
        initialiseBoard();
    }

    private void initialiseBoard() {
        locations = new ArrayList<LocationInterface>();

        /*
        Create the list of Locations
        Location 0 ("START") is the start location, off the board
        Location 1 ("Newcastle") is the first location on the board
        Location 24 ("Durham") is the last location on the board
        Location 25 ("FINISH") is the finish location, off the board
        Location 26 ("KNOCKED") is the 'knocked' location
        Hence locations list should look like:
        START (0, OFF), 1 (ON), 2 (ON), ..., NUMBER_OF_LOCATIONS-1 (23, ON), NUMBER_OF_LOCATIONS (24, ON), END (25, OFF), KNOCKED (26, OFF)
        */
        for (int i = 0; i <= KNOCKED_INDEX; i++) {
            String locName;
            if (i == START_INDEX) {
                locName = START_NAME;
            } else if (i == FINISH_INDEX) {
                locName = FINISH_NAME;
            } else if (i == KNOCKED_INDEX) {
                locName = KNOCKED_NAME;
            } else if (1 <= i && i <= locationNames.length) {
                locName = locationNames[i - 1];
            } else {
                locName = "Town #" + i;
            }

            Location l = new Location(locName);

            if (i == START_INDEX || i == FINISH_INDEX || i == KNOCKED_INDEX) // if start, end or 'knocked' location (all off the board), make location mixed
            {
                l.setMixed(true);
            }

            locations.add(l);

        }

        /* Set the board name */
        setName("North-East Board");
    }

    private void prePopulateStart() {

        /* Pre-populate the START location */
        for (Colour c : Colour.values()) {
            for (int i = 1; i <= BoardInterface.PIECES_PER_PLAYER; i++) {
                try {
                    getStartLocation().addPieceGetKnocked(c);
                } catch (IllegalMoveException e) {
                    // This will never be called
                    System.out.println(e);
                }
            }
        }
    }

    public void setName(String name) {
        this.name = (name != null) ? name : "";
    }

    public String getName() {
        return name;
    }

    /**
     * @return the Location off the board where all pieces start the game. This will be a mixed location.
     **/
    public LocationInterface getStartLocation() {
        return locations.get(0);
    }

    /**
     * @return the Location off the board where pieces get to when they have gone all the way round the board. This will be a mixed location.
     **/
    public LocationInterface getEndLocation() {
        return locations.get(FINISH_INDEX);
    }

    /**
     * @return the Location where pieces go to when they are knocked off the board by an opposing piece. This will be a mixed location.
     **/
    public LocationInterface getKnockedLocation() {
        return locations.get(KNOCKED_INDEX);
    }

    /**
     * @return the Location corresponding to a numbered position on the board. This will not be a mixed location.
     *
     * @param locationNumber the number of the location going from 1-24
     *
     * @throws NoSuchLocationException when position is not in the range 1-24
     **/
    public LocationInterface getBoardLocation(int locationNumber) throws NoSuchLocationException {
        if (locationNumber < 1 || locationNumber > BoardInterface.NUMBER_OF_LOCATIONS) {
            throw new NoSuchLocationException("Requested location number was out of the given range (1 to " + NUMBER_OF_LOCATIONS + ").");
        } else {
            return locations.get(locationNumber);
        }
    }

    /**
     * @param colour the colour to move
     *
     * @param move the move to make
     *
     * @return true if and only if, from the current board state it would be legal for the given colour to make the given move.
     **/
    public boolean canMakeMove(Colour colour, MoveInterface move) {

        /* Move can be made if:
         - player has no pieces on knockedLocation AND any of the following apply:
            - current space has available pieces of that colour
            - new space is empty
            - new space has counters of the same colour
            - new space has one counter of the opposite colour */

        if(getKnockedLocation().numberOfPieces(colour) > 0 & move.getSourceLocation() != 0) {
            return false;
        }

        /* Check current space has at least one of this colour */
        try {
            LocationInterface sourceLocation;
            if (move.getSourceLocation() == 0) {
                sourceLocation = getStartLocation();
            } else {
                sourceLocation = getBoardLocation(move.getSourceLocation());
            }
            if (!sourceLocation.canRemovePiece(colour)) {
                return false;
            }
        } catch (NoSuchLocationException e) {
            System.out.println("Something went wrong.");
            e.printStackTrace();
            return false;
        }

        /* Find the new space */
        LocationInterface targetLocation;
        int targetLocIndex = move.getSourceLocation() + move.getDiceValue();
        if (targetLocIndex > NUMBER_OF_LOCATIONS) {     // if the move would take us off the board
            targetLocIndex = FINISH_INDEX;   // set the target location index to the finish location
        }
        targetLocation = locations.get(targetLocIndex);
        return targetLocation.canAddPiece(colour);
    }

    /**
     * Update the Board state by making the given move for the given colour, including any knocking off.
     *
     * @param colour the colour to move
     *
     * @param move the move to make
     *
     * @throws IllegalMoveException if and only if the move is not legal.
     **/
    public void makeMove(Colour colour, MoveInterface move) throws IllegalMoveException {

        /* Move a knocked piece to the start location, if we have to */
        if(getKnockedLocation().numberOfPieces(colour) > 0) {
            getStartLocation().addPieceGetKnocked(colour);
            getKnockedLocation().removePiece(colour);
        }

        LocationInterface sourceLocation = locations.get(move.getSourceLocation());
        if (canMakeMove(colour, move)) {

            if(!sourceLocation.canRemovePiece(colour)) {
                throw new IllegalMoveException("Cannot remove a piece from location " + sourceLocation.getName() + ".");
            }

            try {

                /* Find the new space */
                LocationInterface targetLocation;
                int targetLocIndex = move.getSourceLocation() + move.getDiceValue();
                if (targetLocIndex > NUMBER_OF_LOCATIONS) // if the move would take us off the end of board
                {
                    targetLocIndex = FINISH_INDEX; // set the target location index to the finish location
                }
                targetLocation = locations.get(targetLocIndex);

                if (targetLocation.canAddPiece(colour)) {
                    Colour knockedColour = targetLocation.addPieceGetKnocked(colour);
                    if(knockedColour != null) {
                        getKnockedLocation().addPieceGetKnocked(knockedColour);
                        targetLocation.removePiece(knockedColour);
                    }
                } else {
                    throw new IllegalMoveException("That move is not allowed. Player forfeits.");
                }

                sourceLocation.removePiece(colour);

            } catch (IllegalMoveException e) {
                throw new IllegalMoveException("Cannot remove a " + colour + " piece from location " + sourceLocation.getName());
            }
        } else { // Can't make move
            throw new IllegalMoveException("That move is not allowed. Player forfeits.");
        }
    }

    /**
     * Update the Board state by making the all of the moves in the given turn in order, including any knocking off, based on the given diceValues.
     *
     * @param colour the colour to move
     *
     * @param turn the turn to take
     *
     * @param diceValues the values of the dice available in no particular order. There will be repeated values in the list if a double is thrown
     *
     * @throws IllegalTurnException if and only if the turns in the move are not legal for the diceValues given. Each of the moves has to be legal, and the diceValues in the moves of the turn must match the diceValues parameter. The number of moves in the turn must be no less than the maximum possible number of legal moves: all available dice must be used. If IllegalTurnException is thrown then the board state remains unchanged.
     **/
    public void takeTurn(Colour colour, TurnInterface turn, List<Integer> diceValues) throws IllegalTurnException {

        if (turn.getMoves().size() > diceValues.size()) {
            throw new IllegalTurnException("Player submitted wrong number of moves in one turn. You forfeit.");
        } else if(turn.getMoves().size() == 0) {
            /* There were no moves, so the player must've been unable to move or they cheated */
        }

        int index = 0;
        for (MoveInterface move : turn.getMoves()) {
            if (!diceValues.contains(move.getDiceValue())) {
                throw new IllegalTurnException("Die value (" + move.getDiceValue() + ") of move #" + (index + 1) + " does not match the given dice value (" + diceValues.get(index) + "). Player forfeits.");
            } else {
                try {
                    makeMove(colour, move);
                } catch (IllegalMoveException e) {
                    /* Player submitted an illegal move */
                    e.printStackTrace();
                    throw new IllegalTurnException("One of your moves was invalid. You forfeit.");
                }
            }
            index++;
        }
    }

    /**
     * @param colour the colour to check
     *
     * @return true if and only if the given colour has won
     **/
    public boolean isWinner(Colour colour) {
        /*
        Colour has won iff all their pieces are on the finish location AND not all the other colour's pieces are on the finish location
        Colour has also won if no possible moves for colour.otherColour()
        */
        if(getEndLocation().numberOfPieces(colour) == PIECES_PER_PLAYER && getEndLocation().numberOfPieces(colour.otherColour()) != PIECES_PER_PLAYER) {
            return true;
        }

        /* If none of the above conditions are satisfied */
        return false;
    }

    /**
     * @return the colour of the winner if there is one, otherwise null
     **/
    public Colour winner() {
        for(Colour c : Colour.values())
        {
            if(isWinner(c)) {
                return c;
            }
        }
        return null;
    }

    // ??
    /**
     * @return true if and only if the Board is in a valid state (do not need to check whether or not it could be reached by a valid sequence of moves)
     **/
    public boolean isValid() {
        HashMap<Colour,Integer> totalPiecesCount = new HashMap<Colour,Integer>();
        totalPiecesCount.put(Colour.values()[0],0);
        totalPiecesCount.put(Colour.values()[1],0);

        for(int i=0; i<=KNOCKED_INDEX; i++) {
            boolean locValid = locations.get(i).isValid();
            if(!locValid) {
                return false;
            }
            for(Colour c : Colour.values()){
                totalPiecesCount.put(c,locations.get(i).numberOfPieces(c));
            }
        }

        if((totalPiecesCount.get(Colour.values()[0]) > PIECES_PER_PLAYER) || totalPiecesCount.get(Colour.values()[1]) > PIECES_PER_PLAYER) {
            return false;
        }

        if((totalPiecesCount.get(Colour.values()[0]) + totalPiecesCount.get(Colour.values()[1])) > (2 * PIECES_PER_PLAYER)) {
            return false;
        }

        /* If none of the above fail conditions were met */
        return true;
    }

    /**
     * @param colour the colour to move next
     *
     * @param diceValues the dice values available to use
     *
     * @return a set of moves that the given colour can make from the current board state with (any one of) the given diceValues
     **/
    public Set<MoveInterface> possibleMoves(Colour colour, List<Integer> diceValues) {
        Set<MoveInterface> moves = new HashSet<MoveInterface>();
        if(diceValues.size() == 4) {
            moves.addAll(calculatePossibleMoves(colour, diceValues.get(0)));
        } else {
            for(int dieValue : diceValues) {
                moves.addAll(calculatePossibleMoves(colour, dieValue));
            } // end for each die value
        }
        return moves;
    }

    private Set<MoveInterface> calculatePossibleMoves(Colour colour, int dieValue) {
        Set<MoveInterface> output = new HashSet<MoveInterface>();
        for(int sourceLocationIndex=0; sourceLocationIndex<=NUMBER_OF_LOCATIONS; sourceLocationIndex++) {
            MoveInterface testMove = new Move();
            try {
                testMove.setSourceLocation(sourceLocationIndex);
                testMove.setDiceValue(dieValue);
            } catch (NoSuchLocationException | IllegalMoveException e) {
                /* Should never be called */
                e.printStackTrace();
            }
            if(canMakeMove(colour, testMove)) {
                output.add(testMove);
            }
        }

        return output;
    }

    /**
     * @return a copy of the board that can be passed to players to work with
     */
    public BoardInterface clone() {

        Board cloneBoard = new Board(false);

        for (int i = 0; i <= KNOCKED_INDEX; i++) {
            Location tl = null;

            if (i == START_INDEX) {                     // Start location
                tl = (Location) this.getStartLocation();
            } else if (i == FINISH_INDEX) {             // Finish location
                tl = (Location) this.getEndLocation();
            } else if (i == KNOCKED_INDEX) {            // Knocked location
                tl = (Location) this.getKnockedLocation();
            } else {
                try {
                    tl = (Location) this.getBoardLocation(i);
                } catch (NoSuchLocationException e) {   // Something went wrong, but we this should never happen
                    e.printStackTrace();
                    continue;
                }
            }

            Location cl = tl.clone();

            cloneBoard.replaceLocation(i, cl);

        } // end for each location index

        return cloneBoard;
    }

    /**
     * Replaces a specific element in the locations ArrayList
     * @param locationIndex The index of the location to replace
     * @param newLocation The new location instance to replace the old one
     */
    public void replaceLocation(int locationIndex, LocationInterface newLocation) {
        this.locations.set(locationIndex, newLocation);
    }

    private int getLengthOfNumber(int num) {
        if (num == 0) {
            return 1;
        }
        return (int) Math.floor(Math.log10(num)) + 1;
        /* Note also that num.toString().length() would also work */
    }

    private String getNOf(String str, int n) {
        String output = "";
        if (n > 0) {
            for (int i = 1; i <= n; i++) {
                output += str;
            }
            return output;
        } else {
            return "";
        }
    }

    /**
     * Overrides toString() from Object with a suitable String representation of the board state for displaying via the console to a human
     **/
    public String toString() {
        List<String> lines = new ArrayList<String>();
        /*
        VERTICAL LAYOUT
        [ 0]
        [ 1]
        ...
        [ 9]
        [10]
        ...
        [24]
        [ F]
        [ K]
        */

        /* Calculate the maximum length of colour string */
        int maxColourLength = 0;
        for (Colour c : Colour.values()) {
            if (c.toString().length() > maxColourLength) {
                maxColourLength = c.toString().length();
            }
        }

        /* Calculate the biggest number length
         * Biggest number will be number of pieces per colour
         * Length will be floor (log_10 (NUMBER_OF_PIECES)) + 1
         */
        int maxNumberLength = getLengthOfNumber(BoardInterface.PIECES_PER_PLAYER);

        /* <space> + maxNumberLength + <space> + maxColourLength + <space> */
        int boxInnerWidth = maxNumberLength + maxColourLength + 3;

        String dashLine = getNOf("-", boxInnerWidth);   // Store this for use in dashLine (efficiency)
        String paddedDashLine = "  " + dashLine;            // Use this one for efficiency

        String thisLine = "";

        /* Print start + main (1, ..., NUMBER_OF_LOCATIONS) + finish + knocked locations */
        for (int i = 0; i <= KNOCKED_INDEX; i++) {

            /* Special locations are START (index 0), FINISH (index FINISH_INDEX), KNOCKED (KNOCKED_INDEX) */
            boolean isSpecialLocation = (i == START_INDEX || i == FINISH_INDEX || i == KNOCKED_INDEX);

            /* Top line */
            lines.add(paddedDashLine);

            /* For each colour, loop through and print the number of each piece */
            boolean firstColour = true;
            for (Colour c : Colour.values()) {
                int numPieces = this.locations.get(i).numberOfPieces(c); // use locations.get() rather than getBoardLocation() because need to access off-board locations
                thisLine = (isSpecialLocation ? "|" : " ") + "| " + getNOf(" ", maxNumberLength - getLengthOfNumber(numPieces)) + numPieces + " " + c + getNOf(" ", maxColourLength - c.toString().length()) + " |" + (isSpecialLocation ? "|" : " ") + " ";
                if (firstColour) {
                    thisLine += getNOf(" ", maxNumberLength - getLengthOfNumber(i)) + (isSpecialLocation ? getNOf(" ", getLengthOfNumber(i)) : i) + " " + locations.get(i).getName(); // where 1 is the length of the number 0
                    firstColour = false;
                }
                lines.add(thisLine);
            }

        } // End for each Location

        lines.add(paddedDashLine);

        String output = "";
        for (String line : lines) {
            output += line + "\n";
        }

        return output;

    }
}
