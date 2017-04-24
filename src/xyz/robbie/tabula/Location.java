package xyz.robbie.tabula;

import java.util.*;

/**
 * Location represents a single location on the board, but not its position
 * Locations on the main part of the board may only contain a single colour of piece.
 *
 * Off-board locations, i.e. the starting location, the finishing location and the knocked-off location
 * can contain pieces of both colours and are referred to as mixed.
 *
 * Requires a constructor with one parameter (the name of the location), which creates a non-mixed location with no pieces.
 *
*/

public class Location implements LocationInterface
{

    private String name;
    private boolean mixed;
    private HashMap<Colour,Integer> pieces;

    public Location(String name)
    {
        setName(name);
        setMixed(false);
        pieces = new HashMap<Colour,Integer>();

        // Populate the pieces HashMap
        for(Colour colour : Colour.values())
        {
            pieces.put(colour,0);
        }
    }

    public String getName(){
        return name;
    }

    public void setName(String name)
    {
        this.name = (name != null) ? name : "";
    }

    public boolean isMixed()
    {
        return mixed;
    }

    public void setMixed(boolean isMixed)
    {
        mixed = isMixed;
    }

    public boolean isEmpty()
    {
        for(Integer count : pieces.values())
        {
            if(count != 0)
            {
                return false;
            }
        }

        return true;
    }

    public int numberOfPieces(Colour colour)
    {
        return pieces.get(colour);
    }

    public boolean canAddPiece(Colour colour)
    {
        /* A piece can be added if:
           - The space is empty
           - The space has counters of the same colour
           - The space has one counter of the opposite colour

           If a counter moves to a space with one counter of the opposite
           colour, the opposition counter is knocked off and is placed in a
           holding area
        */

        // If the space is empty
        if(this.isEmpty())
        {
            return true;
        }

        // If the space has counters of the same colour
        if(!this.isMixed() && numberOfPieces(colour) != 0)
        {
            return true;
        }

        // If the space is not mixed and has exactly one counter of the opposite colour
        if(!this.isMixed())
        {
            if(numberOfPieces(colour.otherColour()/*Board.getOtherColour(colour)*/) == 1)
            {
                return true;
            }

            // boolean onlyThisColour = true;
            // // If any other colours have a non-zero number of pieces on this location
            // for(Colour c : pieces.keySet())
            // {
            //     if(numberOfPieces(c) != 0 & c != colour)
            //     {
            //         onlyThisColour = false;
            //     }
            // }
            // if(onlyThisColour){
            //     return true;
            // }
        }

        // If none of the above conditions are satisfied
        return false;
    }

    public Colour addPieceGetKnocked(Colour colour) throws IllegalMoveException
    {
        // Do we need to knock a piece?
        // First, check if the location is mixed

        if(!isMixed())
        {
            Colour otherColour = colour.otherColour();//Board.getOtherColour(colour);
            if(numberOfPieces(otherColour) == 0) // Simply add the piece
            {
//                pieces.put(colour,numberOfPieces(colour)+1);
                incrementColour(colour);
            }
            else if(numberOfPieces(otherColour) == 1) // There is one piece of the other colour, so knock it
            {
                return otherColour;
            }
            else
            {
                throw new IllegalMoveException("Too many pieces of other colour in this location to knock.");
            }
            return null;
        }
        else
        {
//            pieces.put(colour,numberOfPieces(colour)+1);
            incrementColour(colour);
            return null;
        }
    }

    private void incrementColour(Colour c)
    {
        pieces.put(c,numberOfPieces(c)+1);
    }

    ///// return true if and only if a piece of that colour can be removed (i.e. no IllegalMoveException)
    public boolean canRemovePiece(Colour colour)
    {
        // Can remove a piece if there are >0 pieces in this Location
        if(numberOfPieces(colour) > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void removePiece(Colour colour) throws IllegalMoveException
    {
        if(canRemovePiece(colour))
        {
            pieces.put(colour,numberOfPieces(colour)-1);
        }
        else
        {
            throw new IllegalMoveException("No pieces of that colour (" + colour + ") are in that location.");
        }
    }

    public boolean isValid()
    {
		
		if(isEmpty())
		{
			return true;
		}
		
        // invalid if not mixed AND >0 of EACH colour
        boolean moreThanOneColour = false;
        Colour firstColourWithSomePieces = null;
        for(Colour c : pieces.keySet())
        {
            if(numberOfPieces(c) > 0)
            {
                if(firstColourWithSomePieces instanceof Colour)
                {
                    moreThanOneColour = true;
                }
                else
                {
                    firstColourWithSomePieces = c;
                }
            }
            else if(numberOfPieces(c) < 0) // No negative values allowed
            {
                return false;
            }
        }
        return !isMixed() && !moreThanOneColour;
    }
	
	// For debugging
	public String toString()
	{
		String output = "getName(): " + getName();
		output += "\nisValid(): " + isValid();
		output += "\nisMixed(): " + isMixed();
		output += "\nisEmpty(): " + isEmpty();
		for(Colour c : Colour.values())
		{
			output += "\nnumberOfPieces(" + c + "): " + numberOfPieces(c);
		}
		return output + "\n";
	}

}
