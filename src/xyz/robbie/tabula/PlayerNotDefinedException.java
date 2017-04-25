package xyz.robbie.tabula;

public class PlayerNotDefinedException extends Exception {
    private int numUndefined;

    public PlayerNotDefinedException(String message) {
        super(message);
        numUndefined = 0;
    }

    public PlayerNotDefinedException(String message, int numUndefined) {
        super(message);
        this.numUndefined = numUndefined;
    }

    public int getNumUndefined() {
        return numUndefined;
    }

}
