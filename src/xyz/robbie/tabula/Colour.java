package xyz.robbie.tabula;

public enum Colour {
    // First colour here is the one that will start first in the game, as Colour.values()[0] is used to determine who goes first
    GREEN, BLUE;

    /**
     * @return the other colour from this colour
     **/
    public Colour otherColour() {
        switch (this) {
            case GREEN:
                return BLUE;

            case BLUE:
                return GREEN;

            default:
                // will not happen
                return null;
        }
    }
}
