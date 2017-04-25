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
 *      set the players (human or computer);
 *      load a game;
 *      continue a game;
 *      save the game;
 *      start a new game;
 *      exit the program.
 *
 * If providing a GUI then the same options need to be available through the GUI.
**/

public class Game implements GameInterface
{

    private HashMap<Colour, PlayerInterface> players;
    private Colour currentColour;
    private Board board;

    public Game()
    {
        this.players = new HashMap<Colour, PlayerInterface>();
        board = new Board();
    }

    public void setPlayer(Colour colour, PlayerInterface player)
    {
        players.put(colour, player);
    }

    public Colour getCurrentPlayer()
    {
        return currentColour;
    }

    public Colour play() throws PlayerNotDefinedException
    {
        if(players.size() == 0)
        {
            throw new PlayerNotDefinedException("No players have been defined.", 2);
        }
        else if(players.size() == 1)
        {
            throw new PlayerNotDefinedException("One player has not yet been defined.", 1);
        }
        else if(players.size() != 2)
        {
            // Shouldn't be able to get this far though
            throw new PlayerNotDefinedException("Two players need to be defined.");
        }

        Scanner scanner = new Scanner(System.in);
        String input = "";

        // Variable initialisation
        Board b = getBoard();
        Dice d = new Dice();

        PlayerInterface humanConsolePlayerOne = new HumanConsolePlayer();
        PlayerInterface humanConsolePlayerTwo = new HumanConsolePlayer();

        setPlayer(Colour.GREEN, humanConsolePlayerOne);
        setPlayer(Colour.BLUE, humanConsolePlayerTwo);

        boolean stillPlaying = true;
        TurnInterface t;
        // Do the game loop
        do
        {
            d.roll();
            try
            {
                t = humanConsolePlayerOne.getTurn(Colour.GREEN, b, d.getValues());
                for(MoveInterface move : t.getMoves())
                {
                    try
                    {
                        b.makeMove(Colour.GREEN, move);
                    }
                    catch (IllegalMoveException e)
                    {
                        System.out.println(e);
                    }
                }
            }
            catch (NotRolledYetException | PauseException e)
            {
                System.out.println(e);
            }
            stillPlaying = false;
        } while(stillPlaying);

        do {
            System.out.println("Choose from the following options:");
            System.out.println("(e)xit, print all (l)ocations, print (b)oard, (r)oll dice, print dice (v)alues, (c)lone and print new board");
            input = scanner.nextLine().toLowerCase();

            switch(input)
            {
                case "l": // print all locations
                {
                    System.out.println(b.getStartLocation());
                    for(int i=1;i<BoardInterface.NUMBER_OF_LOCATIONS;i++)
                    {
                        try
                        {
                            System.out.println(b.getBoardLocation(i));
                        }
                        catch (NoSuchLocationException e)
                        {
                            // Won't ever happen
                        }
                    }
                    System.out.println(b.getEndLocation());
                    System.out.println(b.getKnockedLocation());
                    break;
                }

                case "b": // print board
                {
                    System.out.println(b);
                    break;
                }

                case "r": // roll dice
                {
                    d.roll();
                    break;
                }

                case "e": // exit
                {
                    break;
                    // will finish do-while loop
                }

                case "v": // print dice values
                {
                    try
                    {
                        System.out.println(d.getValues());
                    }
                    catch (NotRolledYetException e)
                    {
                        System.out.println(e);
                    }
                    break;
                }

                case "c": // clone and print new board
                {
                    BoardInterface c = b.clone();
                    System.out.println(c);
                }
            }


        } while (!input.equals("e"));

        return null; // Should return the colour of the winner if there is one, or null if not (the game has been paused by a player)
    }

    public void saveGame(String filename) throws IOException
    {

    }

    public void loadGame(String filename) throws IOException
    {

    }

    private Board getBoard()
    {
        return board;
    }

    /**
    * The main method menu should allow users to:
    *      set the players (human or computer);
    *      load a game;
    *      continue a game;
    *      save the game;
    *      start a new game;
    *      exit the program.
    * Run with -c for a command line game or -g for a GUI game ??? (look into how GUI is created)
    */
    public static void main(String[] args)
    {
        // Initialise variables for scope
        Scanner scanner = new Scanner(System.in);
        String input = "";
        HumanConsolePlayer hcp1;
        HumanConsolePlayer hcp2;
        ComputerPlayer cp1;
        ComputerPlayer cp2;
        Game g = new Game();
        System.out.println("Welcome to Tabula North-East.");


        do
        {
            System.out.println();
            System.out.println("== MAIN MENU==");
            System.out.println("Please choose from the following options.");
            System.out.println(" 1) Load a game");
            System.out.println(" 2) Continue a paused game");
            System.out.println(" 3) Save the current game");
            System.out.println(" 4) Set the players");
            System.out.println(" 5) Start a new game");
            System.out.println(" 6) Exit the program");

            input = scanner.nextLine();

            switch(input)
            {
                case "1": // Load a game
                {

                    break;
                }
                case "2": // Continue a paused game
                {

                    break;
                }
                case "3": // Save the current game
                {

                    break;
                }
                case "4": // Set the players
                {

                    String[] colours = {Colour.values()[0].toString().toLowerCase(), Colour.values()[1].toString().toLowerCase()};

                    // Set first player
                    System.out.println("The colours available to you in this game are " + colours[0] + " and " + colours[1] + ", where " + colours[0] + " plays first.");
                    System.out.println("Would you like " + colours[0] + " to be a human or a computer player?");
                    boolean returnToMainMenu = false;
                    do
                    {
                        System.out.println(" 1) Human player");
                        System.out.println(" 2) Computer player");
                        System.out.println(" 3) Return to main menu");
                        input = scanner.nextLine();

                        if(input.equals("1"))
                        {
                            // make first colour a human
                            hcp1 = new HumanConsolePlayer();
                            g.setPlayer(Colour.values()[0], hcp1);
                            System.out.println("You have set " + colours[0] + " to be a human player.");
                        }
                        else if(input.equals("2"))
                        {
                            // make second colour computer
                            cp1 = new ComputerPlayer();
                            g.setPlayer(Colour.values()[0], cp1);
                            System.out.println("You have set " + colours[0] + " to be a computer player.");
                        }
                        else if(input.equals("3"))
                        {
                            returnToMainMenu = true;
                        }
                    } while(!input.equals("1") && !input.equals("2") && !input.equals("3"));
                    if(returnToMainMenu) continue;

                    // Set second player
                    System.out.println("Would you like " + colours[1] + " to be a human or computer player?");
                    do
                    {
                        System.out.println(" 1) Human player");
                        System.out.println(" 2) Computer player");
                        System.out.println(" 3) Return to main menu");
                        input = scanner.nextLine();

                        if(input.equals("1"))
                        {
                            // make first colour a human
                            hcp2 = new HumanConsolePlayer();
                            g.setPlayer(Colour.values()[1], hcp2);
                            System.out.println("You have set " + colours[1] + " to be a human player.");
                        }
                        else if(input.equals("2"))
                        {
                            // make second colour computer
                            cp2 = new ComputerPlayer();
                            g.setPlayer(Colour.values()[0], cp2);
                            System.out.println("You have set " + colours[1] + " to be a computer player.");
                        }
                        else if(input.equals("3"))
                        {
                            returnToMainMenu = true;
                        }
                    } while(!input.equals("1") && !input.equals("2") && !input.equals("3"));
                    if(returnToMainMenu) continue;

                    System.out.println("You have finished setting the players.");
                    break;
                }
                case "5": // Start a new game
                {
                    try
                    {
                        g.play();
                    }
                    catch(PlayerNotDefinedException e)
                    {
                        if(e.getNumUndefined() == 2)
                        {
                            System.out.println("You have not defined any players. Return to the main menu and try again.");
                        }
                        else if(e.getNumUndefined() == 1)
                        {
                            System.out.println("You have only defined one player. Return to the main menu to define the other player.");
                        }
                        else
                        {
                            System.out.println("Some players are undefined. Return to the main menu and try again.");
                        }
//                        System.out.println("You have not defined both players. Return to the main menu and try again.");
                    }
                    break;
                }
                case "6": // Exit the program
                {
                    break;
                }
                default:
                {
                    System.out.println("Your input was not valid. Try again.");
                }

            }

        } while(!input.equals("6"));



    } // end main()

} // end class
