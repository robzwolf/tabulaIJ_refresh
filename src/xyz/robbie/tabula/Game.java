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
    private boolean playing;

    public Game() {
        this.players = new HashMap<Colour, PlayerInterface>();
        resetGame();
    }

    public void setPlayer(Colour colour, PlayerInterface player) {
        players.put(colour, player);
    }

    public Colour getCurrentPlayer() {
        return currentColour;
    }

    public Colour play() throws PlayerNotDefinedException {

        if(!playing) {
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

        /* Variable initialisation */
        board = getBoard();
        Dice d = new Dice();

        boolean stillPlaying = true;
        TurnInterface t;

        /* Do the game loop */
        while (stillPlaying) {
            d.roll();
            try {
                t = players.get(currentColour).getTurn(currentColour, board.clone(), d.getValues());
                for (MoveInterface move : t.getMoves()) {
                    try {
                        board.makeMove(currentColour, move);
                    } catch (IllegalMoveException e) {
                        System.out.println(e);
                        stillPlaying = false;
                    }
                }
            } catch (PauseException e) {
//                System.out.println(e);
                return null;
            } catch (NotRolledYetException e) {
                /* Should never happen */
                e.printStackTrace();
            }
            currentColour = currentColour.otherColour();
            if(board.winner() != null) {
                stillPlaying = false;
            }
        }

        currentColour = null;
        return board.winner(); // Should return the colour of the winner if there is one, or null if not (the game has been paused by a player)
    }

    public void saveGame(String filename) throws IOException {

    }

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
                case "1": // Load a game
                {

                    break;
                }
                case "2": // Continue a paused game
                {
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
                case "3": // Save the current game
                {
                    break;
                }
                case "4": // Set the players
                {
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
//                    System.out.println("Would you like " + colours[1] + " to be a human or computer player?");
//                    do {
//                        System.out.println(" 1) Human player");
//                        System.out.println(" 2) Computer player");
//                        System.out.println(" 3) Return to main menu");
//                        input = scanner.nextLine();
//
//                        switch (input) {
//                            case "1": {
//                                /* Make second colour a human */
//                                PlayerInterface hcp = new HumanConsolePlayer();
//                                g.setPlayer(Colour.values()[1], hcp);
//                                System.out.println("You have set " + colours[1] + " to be a human player.");
//                                break;
//                            }
//                            case "2": {
//                                /* Make second colour computer */
//                                PlayerInterface cp = new ComputerPlayer();
//                                g.setPlayer(Colour.values()[1], cp);
//                                System.out.println("You have set " + colours[1] + " to be a computer player.");
//                                break;
//                            }
//                            case "3": {
//                                returnToMainMenu = true;
//                                break;
//                            }
//                        }

//                    } while (!input.equals("1") && !input.equals("2") && !input.equals("3"));
                    if (returnToMainMenu) {
                        continue;
                    }

                    NasirComputerPlayer ncp = new NasirComputerPlayer();
                    g.setPlayer(Colour.values()[1], ncp);

                    System.out.println("You have finished setting the players.");
                    break;
                }
                case "5": // Start a new game
                {
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
                case "6": // Exit the program
                {
                    break;
                }
                case "dev": // Dev options
                {
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
    }

    private static void handleGameFinish(Game game, Colour winner) {
        System.out.println(game.getBoard());
        System.out.println("Congratulations, " + winner.toString().toLowerCase() + " is the winner!");
    }

} // end class
