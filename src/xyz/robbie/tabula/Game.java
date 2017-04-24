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
        if(getCurrentPlayer() == null){
            throw new PlayerNotDefinedException("No player has been defined!");
        }


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
        String mode = "";

        if(args.length <= 0){
            System.out.println("You did not specify whether to start in command line or GUI mode.\nDefaulting to command line mode.");
            mode = "-c";
        }
        else
        {
            mode = args[0];
        }

        Scanner scanner = new Scanner(System.in);
        String input = "";

		Game g = new Game();
		Board b = g.getBoard();

		switch(mode){
            case "-g":
            {
                // GUI play
                break;
            }

            case "-dev":
            {
                System.out.println("Welcome to Tabula North-East.");

                Dice d = new Dice();

                PlayerInterface humanConsolePlayerOne = new HumanConsolePlayer();
                PlayerInterface humanConsolePlayerTwo = new HumanConsolePlayer();

                g.setPlayer(Colour.GREEN, humanConsolePlayerOne);
                g.setPlayer(Colour.BLUE, humanConsolePlayerTwo);

                boolean stillPlaying = true;
                TurnInterface t;
                // Do the game loop
                do
                {
                    d.roll();
                    try
                    {
                        t = humanConsolePlayerOne.getTurn(Colour.GREEN,b,d.getValues());
                        for(MoveInterface move : t.getMoves())
                        {
                            try
                            {
                                b.makeMove(Colour.GREEN,move);
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
                break;
            }

            case "-c":
            {
                System.out.println("Welcome to Tabula North-East.");
                break;
            }

            default:
            {
                System.out.println("Your command line parameter was not recognised. Use -c to start in command line mode or -g to start in GUI mode.");
            }
        } // end switch
    } // end main()

} // end class
