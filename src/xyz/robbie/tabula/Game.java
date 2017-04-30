package xyz.robbie.tabula;

import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;

/**
 * Game represents the game state including the board, the dice and the players
 *
 * Requires a constructor with no parameters.
 *
 * Also requires a main method which allows the user to choose the player types and start the game.
 * The main method menu should allow users to:
 * set the players (human or computer);
 * load a game;
 * continue a game;
 * save the game;
 * start a new game;
 * exit the program.
 *
 * If providing a GUI then the same options need to be available through the GUI.
 **/

public class Game implements GameInterface {

    private HashMap<Colour, PlayerInterface> players;
    private Colour currentColour;
    private BoardInterface board;
    private DiceInterface d;

    public Game() {
        this.players = new HashMap<Colour, PlayerInterface>();
        resetGame();
    }

    /**
     * Assigns each colour to a player.
     * @return the player who has the next turn. Green goes first.
     **/
    public void setPlayer(Colour colour, PlayerInterface player) {
        players.put(colour, player);
    }

    /**
     * Gets the current player.
     * @return the current player
     */
    public Colour getCurrentPlayer() {
        return currentColour;
    }

    /**
     * Play the game until completion or pause. Should work either for a new game or the continuation of a paused game. This method should roll the dice and pass the dice values to the players. The players should be asked one after another for their choice of turn via their getTurn method. The board that is passed to the players should be a clone of the game board so that they can try out moves without affecting the state of the game.
     *
     * @return the colour of the winner if there is one, or null if not (the game has been paused by a player). If a player tries to take an illegal turn then they forfeit the game and the other player immediately wins.
     *
     * @throws PlayerNotDefinedException if one or both of the players is undefined
     **/
    public Colour play() throws PlayerNotDefinedException {

        if(currentColour == null) {
            currentColour = Colour.values()[0];
        }

        if (players.size() == 0) {
            throw new PlayerNotDefinedException("No players have been defined.", 2);
        } else if (players.size() == 1) {
            throw new PlayerNotDefinedException("One player has not yet been defined.", 1);
        } else if (players.size() != 2) {

            /* Shouldn't be able to get this far though */
            throw new PlayerNotDefinedException("Two players need to be defined.");
        }

        boolean stillPlaying = true;
        TurnInterface t;

        /* Do the game loop */
        while (stillPlaying) {
            if(!d.haveRolled()) {
                d.roll();
            }
            try {
                t = players.get(currentColour).getTurn(currentColour, board.clone(), d.getValues());
                d.clear();
                for (MoveInterface move : t.getMoves()) {
                    try {
                        board.makeMove(currentColour, move);
                    } catch (IllegalMoveException e) {
                        System.out.println(e);
                        stillPlaying = false;
                    }
                }
            } catch (PauseException e) {
                return null;
            } catch (NotRolledYetException e) {

                /* Should never happen */
                e.printStackTrace();
                stillPlaying = false;
            }
            currentColour = currentColour.otherColour();
            if(board.winner() != null) {
                stillPlaying = false;
            }
        }

        currentColour = null;
        return board.winner(); // Returns the colour of the winner
    }

    /**
     * Save the current state of the game (including the board, dice and player types) into a file so it can be re-loaded and game play continued. You choose what the format of the file is.
     *
     * @param filename the name of the file in which to save the game state
     *
     * @throws IOException when an I/O problem occurs while saving
     **/
    public void saveGame(String filename) throws IOException {

    }

    /**
     * Load the game state from the given file
     *
     * @param filename  the name of the file from which to load the game state
     *
     * @throws IOException when an I/O problem occurs or the file is not in the correct format (as used by saveGame())
     **/
    public void loadGame(String filename) throws IOException {
        BoardInterface loadedBoard = new Board();

    }

    private BoardInterface getBoard() {
        return board;
    }

    private void setBoard(BoardInterface board) {
        this.board = board;
    }

    /**
     * The main method menu should allow users to:
     * set the players (human or computer);
     * load a game;
     * continue a game;
     * save the game;
     * start a new game;
     * exit the program.
     */
    public static void main(String[] args) {

        /* Initialise variables for scope */
        Scanner scanner = new Scanner(System.in);
        String input = "";
        Game g = new Game();
        System.out.println("\nWelcome to Tabula North-East.");

        do {
            System.out.println();
            System.out.println("== MAIN MENU ==");
            System.out.println("Please choose from the following options.");
            System.out.println(" 1) Load a game");
            System.out.println(" 2) Continue a paused game");
            System.out.println(" 3) Save the current game");
            System.out.println(" 4) Set the players");
            System.out.println(" 5) Start a new game");
            System.out.println(" 6) Exit the program");

            input = scanner.nextLine();

            switch (input) {
                case "1": { // Load a game

                    break;
                }
                case "2": { // Continue a paused game
                    if(g.getCurrentPlayer() == null) {
                        System.out.println("No game is currently being played.");
                        continue;
                    }
                    try {
                        Colour winner = g.play();
                        if(winner == null) {
                            /* Player paused the game */
                            System.out.println("The game has been paused. Returning to main menu.");
                            continue;
                        } else {
                            /* A player won the game */
                            handleGameFinish(g, winner);
                            continue;
                        }
                    } catch (PlayerNotDefinedException e) {
                        complainAboutNotDefinedPlayers(e);
                    }
                    break;
                }
                case "3": { // Save the current game
                    break;
                }
                case "4": { // Set the players
                    String[] colours = {Colour.values()[0].toString().toLowerCase(), Colour.values()[1].toString().toLowerCase()};

                    /* Set first player */
                    System.out.println("The colours available to you in this game are " + colours[0] + " and " + colours[1] + ", where " + colours[0] + " plays first.");
                    System.out.println("Would you like " + colours[0] + " to be a human or a computer player?");
                    boolean returnToMainMenu = false;
                    do {
                        System.out.println(" 1) Human player");
                        System.out.println(" 2) Computer player");
                        System.out.println(" 3) Return to main menu");
                        input = scanner.nextLine();

                        switch (input) {
                            case "1": {
                                /* Make first colour a human */
                                PlayerInterface hcp = new HumanConsolePlayer();
                                g.setPlayer(Colour.values()[0], hcp);
                                System.out.println("You have set " + colours[0] + " to be a human player.");
                                break;
                            }
                            case "2": {
                                /* Make second colour computer */
                                PlayerInterface cp = new ComputerPlayer();
                                g.setPlayer(Colour.values()[0], cp);
                                System.out.println("You have set " + colours[0] + " to be a computer player.");
                                break;
                            }
                            case "3": {
                                returnToMainMenu = true;
                                break;
                            }
                        }
                    } while (!input.equals("1") && !input.equals("2") && !input.equals("3"));
                    if (returnToMainMenu) {
                        continue;
                    }

                    /* Set second player */
                    System.out.println("Would you like " + colours[1] + " to be a human or computer player?");
                    do {
                        System.out.println(" 1) Human player");
                        System.out.println(" 2) Computer player");
                        System.out.println(" 3) Return to main menu");
                        input = scanner.nextLine();

                        switch (input) {
                            case "1": {
                                /* Make second colour a human */
                                PlayerInterface hcp = new HumanConsolePlayer();
                                g.setPlayer(Colour.values()[1], hcp);
                                System.out.println("You have set " + colours[1] + " to be a human player.");
                                break;
                            }
                            case "2": {
                                /* Make second colour computer */
                                PlayerInterface cp = new ComputerPlayer();
                                g.setPlayer(Colour.values()[1], cp);
                                System.out.println("You have set " + colours[1] + " to be a computer player.");
                                break;
                            }
                            case "3": {
                                returnToMainMenu = true;
                                break;
                            }
                        }

                    } while (!input.equals("1") && !input.equals("2") && !input.equals("3"));
                    if (returnToMainMenu) {
                        continue;
                    }

                    System.out.println("You have finished setting the players.");
                    break;
                }
                case "5": { // Start a new game
                    g.resetGame();

                    try {
                        Colour winner = g.play();
                        if(winner == null) {
                            /* Player paused the game */
                            System.out.println("The game has been paused. Returning to main menu.");
                            continue;
                        } else {
                            /* A player won the game */
                            handleGameFinish(g, winner);
                            continue;
                        }
                    } catch (PlayerNotDefinedException e) {
                        complainAboutNotDefinedPlayers(e);
                    }
                    break;
                }
                case "6": { // Exit the program
                    break;
                }
                case "dev": { // Dev options
                    System.out.println();
                    System.out.println("== DEVELOPER OPTIONS ==");
                    do {
                        System.out.println(" 1) Return to main menu");
                        System.out.println(" 2) Print current players");
                        input = scanner.nextLine();
                        switch (input) {
                            case "1": { // Return to main menu
                                break;
                            }
                            case "2": { // Print current players
                                System.out.println("No. of defined players = " + g.players.keySet().size());
                                for (Colour c : g.players.keySet()) {
                                    System.out.println(c + " = " + g.players.get(c));
                                }
                                break;
                            }
                            default: {
                                System.out.println("Your input was not valid. Try again.");
                                break;
                            }
                        }
                    } while (!input.equals("1"));
                }
                default: {
                    System.out.println("Your input was not valid. Try again.");
                }
            } // end switch(input) for main menu
        } while (!input.equals("6"));

    } // end main()

    private static void complainAboutNotDefinedPlayers(PlayerNotDefinedException e) {
        if (e.getNumUndefined() == 2) {
            System.out.println("You have not defined any players. Return to the main menu and try again.");
        } else if (e.getNumUndefined() == 1) {
            System.out.println("You have only defined one player. Return to the main menu to define the other player.");
        } else {
            System.out.println("Some players are undefined. Return to the main menu and try again.");
        }
    }

    private void resetGame() {
        currentColour = null;
        board = new Board();
        d = new Dice();
    }

    /**
     * Handles the end of a game by printing the final board and congratulating the winner.
     * @param game The game whose board to print
     * @param winner The winner of the game
     */
    private static void handleGameFinish(Game game, Colour winner) {
        System.out.println(game.getBoard());
        System.out.println("Congratulations, " + winner.toString().toLowerCase() + " is the winner!");
    }

} // end class
