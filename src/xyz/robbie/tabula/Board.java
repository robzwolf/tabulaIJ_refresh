package xyz.robbie.tabula;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

/**
 * Board represents the board state in the game of tabula (not including dice and players).
 *
 * Requires a constructor with no parameters which creates and initialises all of the locations for the start of the game.
 *
 **/

public class Board implements BoardInterface
{
    // Determines whether to print all locations in a vertical list (as opposed to S-shape) in toString()
    private boolean verticalToString;

    private String name;

    // List of all the locations in game
    // Location 0 ("Start") is the start location, off the board
    // Location 1 ("Newcastle") is the first location on the board
    // Location 24 ("Durham") is the last location on the board
    // Location 25 ("Finish") is the finish location, off the board
    // Location 26 ("Stockton") is the 'knocked' location
    // Hence locations list should look like:
    //     START (0, OFF), 1 (ON), 2 (ON), ..., NUMBER_OF_LOCATIONS-1 (23, ON), NUMBER_OF_LOCATIONS (24, ON), END (25, OFF), KNOCKED (26, OFF)
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
    };

    private static final String START_NAME = "START";
    private static final String FINISH_NAME = "FINISH";
    private static final String KNOCKED_NAME = "KNOCKED";

    public Board()
    {

        locations = new ArrayList<LocationInterface>();

        // Create the list of Locations
        // Location 0 ("START") is the start location, off the board
        // Location 1 ("Newcastle") is the first location on the board
        // Location 24 ("Durham") is the last location on the board
        // Location 25 ("FINISH") is the finish location, off the board
        // Location 26 ("KNOCKED") is the 'knocked' location
        // Hence locations list should look like:
        //     START (0, OFF), 1 (ON), 2 (ON), ..., NUMBER_OF_LOCATIONS-1 (23, ON), NUMBER_OF_LOCATIONS (24, ON), END (25, OFF), KNOCKED (26, OFF)
        for(int i=0; i<NUMBER_OF_LOCATIONS+3; i++)
        {
            String locName;
            if(i == 0)
            {
                locName = START_NAME;
            }
            else if(i == NUMBER_OF_LOCATIONS + 1)
            {
                locName = FINISH_NAME;
            }
            else if(i == NUMBER_OF_LOCATIONS + 2)
            {
                locName = KNOCKED_NAME;
            }
            else if(1 <= i && i <= locationNames.length)
            {
                locName = locationNames[i-1];
            }
            else
            {
                locName = "Town #" + i;
            }
            //String locName = i>=locationNames.length ? "Town "+i : locationNames[i];
//            System.out.println("Location #" + i + ": " + locName);

            Location l = new Location(locName);

            if(i == 0 || i == 25 || i == 26) // if start, end or 'knocked' location (all off the board), make location mixed
            {
                l.setMixed(true);
            }

            locations.add(l);

        }

        // Set the board name
        setName("North-East Board");

        // Pre-populate the START location
        for(Colour c : Colour.values())
        {
            for(int i=1;i<=BoardInterface.PIECES_PER_PLAYER;i++)
            {
                try
                {
                    getStartLocation().addPieceGetKnocked(c);
                }
                catch (IllegalMoveException e)
                {
                    // This will never be called
                    System.out.println(e);
                }
            }
        }

    }

    public void setVerticalToString(boolean v)
    {
        verticalToString = v;
    }

    public boolean getVerticalToString()
    {
        return verticalToString;
    }

    public void setName(String name)
    {
        this.name = (name != null) ? name : "";
    }

    public LocationInterface getStartLocation()
    {
        return locations.get(0);
    }

    public LocationInterface getEndLocation()
    {
        return locations.get(NUMBER_OF_LOCATIONS + 1);
    }

    public LocationInterface getKnockedLocation()
    {
        return locations.get(NUMBER_OF_LOCATIONS + 2);
    }

    public LocationInterface getBoardLocation(int locationNumber) throws NoSuchLocationException
    {
        if(locationNumber < 1 || locationNumber > BoardInterface.NUMBER_OF_LOCATIONS)
        {
            throw new NoSuchLocationException("Requested location number was out of the given range (1 to " + NUMBER_OF_LOCATIONS + ").");
        }
        else
        {
            return locations.get(locationNumber);
        }
    }


    public boolean canMakeMove(Colour colour, MoveInterface move)
    {
        // Move can be made if:
        // - current space has available pieces of that colour
        // - new space is empty
        // - new space has counters of the same colour
        // - new space has one counter of the opposite colour

        // Check current space has at least one of this colour
        try
        {
            LocationInterface sourceLocation;
            if(move.getSourceLocation() == 0)
            {
                sourceLocation = getStartLocation();
            }
            else
            {
                sourceLocation = getBoardLocation(move.getSourceLocation());
            }
            if(sourceLocation.numberOfPieces(colour) == 0)
            {
                return false;
            }
        }
        catch(NoSuchLocationException e)
        {
            System.out.println("Something went wrong.");
            e.printStackTrace();
            return false;
        }

        // Find the new space
        LocationInterface targetLocation;
        try
        {
            int targetLocIndex = move.getSourceLocation() + move.getDiceValue();
            if(targetLocIndex > NUMBER_OF_LOCATIONS) // if the move would take us off the board
            {
                targetLocIndex = NUMBER_OF_LOCATIONS + 1; // set the target location index to the finish location
            }
            targetLocation = getBoardLocation(targetLocIndex);
            if(targetLocation.canAddPiece(colour))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (NoSuchLocationException e)
        {
            // Should never be called
            e.printStackTrace();
            return false;
        }
    }

    public void makeMove(Colour colour, MoveInterface move) throws IllegalMoveException
    {
        if(canMakeMove(colour, move))
        {
            LocationInterface sourceLocation = locations.get(move.getSourceLocation());
            if(sourceLocation.canRemovePiece(colour)){
                try
                {
                    sourceLocation.removePiece(colour);
                }
                catch(IllegalMoveException e)
                {
                    throw new IllegalMoveException("Cannot remove a " + colour + " piece from location " + sourceLocation.getName());
                }
            }
            else
            {
                throw new IllegalMoveException("Cannot remove a " + colour + " piece from location " + sourceLocation.getName());
            }
        }
        else
        {
            // try-catch on moveThing() instead?
            // throw IllegalMoveException
        }
    }

    public void takeTurn(Colour colour, TurnInterface turn, List<Integer> diceValues) throws IllegalTurnException
    {
        int index = 0;
        for(MoveInterface move : turn.getMoves())
        {
            if(move.getDiceValue() != diceValues.get(index))
            {
                throw new IllegalTurnException("Die value (" + move.getDiceValue() + ") of move #" + (index+1) + " does not match the given dice value (" + diceValues.get(index) + ")");
            }
            else
            {

            }
            index++;
        }
    }

    public boolean isWinner(Colour colour)
    {
        return false;
    }

    public Colour winner()
    {
        return null;
    }

    public boolean isValid()
    {
        return false;
    }

    public Set<MoveInterface> possibleMoves(Colour colour, List<Integer> diceValues)
    {
        return null;
    }

    public BoardInterface clone()
    {

        //     make new Board()
        //         (this will create new locations and set them mixed as required)
        //     transfer number of each colour counters to each location in the new board

        BoardInterface cloneBoard = new Board();

        for(int i=0; i<=NUMBER_OF_LOCATIONS+2; i++)
        {
            LocationInterface tl = null;
            LocationInterface cl = null;
            System.out.println("i = " + i);

            if(i == 0)
            {
                cl = cloneBoard.getStartLocation();
                tl = this.getStartLocation();
            }
            else if(i == NUMBER_OF_LOCATIONS+1)
            {
                cl = cloneBoard.getEndLocation();
                tl = this.getEndLocation();
            }
            else if(i == NUMBER_OF_LOCATIONS+2)
            {
                cl = cloneBoard.getKnockedLocation();
                tl = this.getKnockedLocation();
            }
            else
            {
                try
                {

                    cl = cloneBoard.getBoardLocation(i);
                    tl = this.getBoardLocation(i);
                }
                catch(NoSuchLocationException e)
                {
                    // Something went wrong, but we this should never happen
                    System.out.println(e);
//                  continue;
                }
            }

            // Transfer over the Location name
            cl.setName(tl.getName());

            // Transfer over whether Location is mixed (probably not necessary by default, unless this property has been manually changed for any Location)
            cl.setMixed(tl.isMixed());

            // Transfer number of pieces of each colour
            for(Colour c : Colour.values())
            {
                // Add the piece c the correct number of times
                for(int j=1; j<=tl.numberOfPieces(c); j++)
                {
                    try
                    {
                        cl.addPieceGetKnocked(c);
                    }
                    catch(IllegalMoveException e)
                    {
                        // Should never happen as tl will be valid
                        System.out.println("Error adding " + c + " on j-iteration #" + j + " to location #" + i);
                    }
                }
            }

            System.out.println("Copied location " + i);
        }
        return cloneBoard;
    }

    private int getLengthOfNumber(int num)
    {
        if(num == 0)
        {
            return 1;
        }
        return (int) Math.floor(Math.log10(num)) + 1;
        // Note also that num.toString().length() would also work
    }
    
    private String getNOf(String str, int n)
    {
        String output = "";
        if(n > 0)
        {
            for(int i=1;i<=n;i++)
            {
                output += str;
            }
            return output;
        }
        else
        {
            return "";
        }
    }

    public String toString()
    {
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

        // Calculate the maximum length of colour string
        int maxColourLength = 0;
        for(Colour c : Colour.values())
        {
            if(c.toString().length() > maxColourLength)
            {
                maxColourLength = c.toString().length();
            }
        }
//            System.out.println("Max colour length is " + maxColourLength);


        // Calculate the biggest number length
        // Biggest number will be number of pieces per colour
        // Length will be floor (log_10 (NUMBER_OF_PIECES)) + 1
        int maxNumberLength = getLengthOfNumber(BoardInterface.PIECES_PER_PLAYER);
//            System.out.println("Max number length is " + maxNumberLength);

        // space + maxNumberLength + space + maxColourLength + space
        int boxInnerWidth = maxNumberLength + maxColourLength + 3;
//            System.out.println("Inner box width is " + boxInnerWidth);

        String dashLine = getNOf("-",boxInnerWidth); // Store this for use in dashLine (efficiency)
        String paddedDashLine = "  " + dashLine; // Use this one for efficiency

        String thisLine = "";

        // Print start + main (1, ..., NUMBER_OF_LOCATIONS) + finish + knocked locations
        for(int i=0;i<=NUMBER_OF_LOCATIONS+2;i++){
            // Special locations are START (index 0), FINISH (index NUMBER_OF_LOCATIONS+1), KNOCKED (NUMBER_OF_LOCATIONS+2)
            boolean isSpecialLocation = i==0 || i==NUMBER_OF_LOCATIONS+1 || i== NUMBER_OF_LOCATIONS+2;


            // Top line
            lines.add(paddedDashLine);

            // For each colour, loop through and print the number of each piece
            boolean firstColour = true;
            for(Colour c : Colour.values())
            {
                int numPieces = this.locations.get(i).numberOfPieces(c); // use locations.get() rather than getBoardLocation() because need to access off-board locations
                thisLine = (isSpecialLocation ? "|" : " ") + "| " + getNOf(" ",maxNumberLength - getLengthOfNumber(numPieces)) + numPieces + " " + c + getNOf(" ",maxColourLength - c.toString().length()) + " |" + (isSpecialLocation ? "|" : " ") + " ";
                if(firstColour){
                    thisLine += getNOf(" ",maxNumberLength - getLengthOfNumber(i)) + (isSpecialLocation ? getNOf(" ", getLengthOfNumber(i)) : i) + " " + locations.get(i).getName(); // where 1 is the length of the number 0
                    firstColour = false;
                }
                lines.add(thisLine);
            }

        } // End for each location

        lines.add(paddedDashLine);

        String output = "";
        for(String line : lines)
        {
            output += line + "\n";
        }
        return output;

    }
}
