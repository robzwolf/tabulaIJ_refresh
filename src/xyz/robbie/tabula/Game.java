package xyz.robbie.tabula;

import com.google.gson.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

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

    private static final String DEFAULT_SAVE_LOCATION = "save.json";

    private static Game g;

    private HashMap<Colour, PlayerInterface> players;
    private Colour currentColour;
    private BoardInterface board;
    private DiceInterface d;

    public Game() {
        this.players = new HashMap<Colour, PlayerInterface>();
        resetGame();
    }

    /**
     * @param colour of the player to set
     *
     * @param player the player to use
     **/
    public void setPlayer(Colour colour, PlayerInterface player) {
        players.put(colour, player);
    }

    /**
     * @return the player who has the next turn. Green goes first.
     **/
    public Colour getCurrentPlayer() {
        return currentColour;
    }

    private void setCurrentPlayer(Colour c) {
        this.currentColour = c;
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
            setCurrentPlayer(Colour.values()[0]);
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
            setCurrentPlayer(currentColour.otherColour());
            if(board.winner() != null) {
                stillPlaying = false;
            }
        }

        setCurrentPlayer(null);
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

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.setPrettyPrinting().create();

        Files.write(Paths.get(filename), gson.toJson(this).getBytes());

        System.out.println("Saved game state to " + Paths.get(filename).toAbsolutePath().toString());

    }

    /**
     * Load the game state from the given file
     *
     * @param filename  the name of the file from which to load the game state
     *
     * @throws IOException when an I/O problem occurs or the file is not in the correct format (as used by saveGame())
     **/
    public void loadGame(String filename) throws IOException {

        String wholeFile = new String(Files.readAllBytes(Paths.get(filename)));
        JsonObject jsonObject = new JsonParser().parse(wholeFile).getAsJsonObject();

        resetGame();

        Board newBoard = new Board(false);
        JsonObject jsonBoard = jsonObject.get("board").getAsJsonObject();
        JsonArray jsonLocations = jsonBoard.get("locations").getAsJsonArray();

        /* Transfer player/colour assignment */
        /* Adapted from http://stackoverflow.com/a/27964552/2176546, retrieved 30/04/2017 */
        Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.get("players").getAsJsonObject().entrySet();
        for(Map.Entry<String,JsonElement> entry : entrySet) {
            JsonObject jsonColour = entry.getValue().getAsJsonObject();
            for(Colour c : Colour.values()) {
                if(entry.getKey().toUpperCase().equals(c.toString().toUpperCase())) {
                    if(jsonColour.get("typeOfPlayer").getAsString().toLowerCase().equals("human")) {
                        g.setPlayer(c, new HumanConsolePlayer());
                    } else if(jsonColour.get("typeOfPlayer").getAsString().toLowerCase().equals("computer")) {
                        g.setPlayer(c, new ComputerPlayer());
                    } else {
                        g.setPlayer(c, null);
                    }
                }
            }
        }

        /* Set newBoard name */
        newBoard.setName(jsonBoard.get("name").getAsString());

        /* Check there are the right number of locations in the save file */
        if(jsonLocations.size() != BoardInterface.NUMBER_OF_LOCATIONS+3) {
            throw new IOException("The saved game state has an invalid number of locations (need " + (BoardInterface.NUMBER_OF_LOCATIONS+3) + " but only found " + jsonLocations.size() + ")");
        }

        /* Transfer over details about each location */
        for(int i=0; i<=Board.KNOCKED_INDEX; i++) {
            JsonObject jsonLocation = jsonLocations.get(i).getAsJsonObject();

            /* Transfer over location name */
            Location newLocation = new Location(jsonLocation.get("name").getAsString());

            /* Transfer over location mixed property */
            newLocation.setMixed(jsonLocation.get("mixed").getAsBoolean());

            /* Transfer number of each piece */
            for(Colour c : Colour.values()) {
                if(jsonLocation.get("pieces").getAsJsonObject().has(c.toString())) {
                    for(int j=1; j<=jsonLocation.get("pieces").getAsJsonObject().get(c.toString()).getAsInt(); j++) {
                        try {
                            newLocation.addPieceGetKnocked(c);
                        } catch (IllegalMoveException e) {
                            throw new IOException("The saved game state is not valid");
                        }
                    }
                }
            }

            newBoard.replaceLocation(i, newLocation);

        }

        /* Transfer current player */
        for(Colour c : Colour.values()) {
            if(c.toString().equals(jsonObject.get("currentColour").getAsString())) {
                g.setCurrentPlayer(c);
            }
        }

        /* Transfer die values */
        JsonObject jsonDice = jsonObject.get("d").getAsJsonObject();
        d.getDice().get(0).setValue(jsonDice.get("d1").getAsJsonObject().get("value").getAsInt());
        d.getDice().get(1).setValue(jsonDice.get("d2").getAsJsonObject().get("value").getAsInt());

        setBoard(newBoard);

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
        g = new Game();
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
                    // ??
                    System.out.println("Enter the name of the file to load, or press Enter to load from the default location:");
                    input = scanner.nextLine();
                    String filename = DEFAULT_SAVE_LOCATION;
                    if(!input.equals("")) {
                        filename = input;
                    }
                    try {
                        g.loadGame(filename);
                        handleGamePlay();
                    } catch (IOException e) {
                        System.out.println("There was an error loading the game: " + e);
                    }
                    break;
                }
                case "2": { // Continue a paused game
                    // ??
                    if(g.getCurrentPlayer() == null) {
                        System.out.println("No game is currently being played.");
                        continue;
                    }
                    handleGamePlay();
                    break;
                }
                case "3": { // Save the current game
                    try {
                        if(g.getCurrentPlayer() == null) { // No game is currently being played
                            System.out.println("No game is being played.");
                        } else {
                            System.out.println("Enter name of save file, or press Enter to use default:");
                            input = scanner.nextLine();
                            String filename = DEFAULT_SAVE_LOCATION;
                            if(!input.equals("")) {
                                filename = input;
                            }
                            g.saveGame(filename);
                        }
                    } catch (IOException e) {
                        System.out.println("Something went wrong saving the file: " + e);
                    }
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
                    handleGamePlay();
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
        setCurrentPlayer(null);
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

    private static void handleGamePlay() {

        try {
            Colour winner = g.play();
            if(winner == null) {

                /* Player paused the game */
                System.out.println("The game has been paused. Returning to main menu.");
            } else {

                /* A player won the game */
                handleGameFinish(g, winner);
            }
        } catch (PlayerNotDefinedException e) {
            complainAboutNotDefinedPlayers(e);
        }
    }

} // end class
