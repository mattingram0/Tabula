import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

//WHEN LOADING GAME, IF GAME PASUED MAKE SURE ISGAMEPAUSED IS THEN SET TO FALSE!

import static java.lang.System.out;

/**
 * Created by matt on 22/03/2017.
 */
public class Game implements GameInterface {
    private static boolean useGUI;
    private HashMap<Colour, PlayerInterface> players = new HashMap<Colour, PlayerInterface>();
    private Colour currentPlayer;
    private boolean isGamePaused;
    private boolean isGameLoaded;
    private Board board;
    private Dice dice;

    public Game(){
        isGamePaused = false;
        isGameLoaded = false;
        currentPlayer = Colour.values()[0]; //Set the starting player to be the first Colour enum value
    }

    public static void main(String[] args){
        if(args.length > 0){ //Probably change this - get them to select an option upon opening executing the jar file will be easier, easier to change though..
            switch(args[0]){
                case "GUI":
                    useGUI = true;
                    break;
                case "CLI":
                    useGUI = false;
                    break;
                default:
                    out.println("[-] Invalid Gamemode Type!");
                    out.println("Please Use Either:");
                    out.println("[a] 'java Tabula GUI' for a Graphical User Interface, or");
                    out.println("[b] 'java Tabula CLI' for a Command Line Interface.");
            }
        }

        if(useGUI){
            //... GUI Code to Execute
        } else {
            runCLI();
        }
    }

    public static void runCLI(){ //Main Program Loop
        Game g = new Game();
        g.displayWelcomeMessage();
        Colour winningColour = null;

        dance: while(true){ //Poor form I know, but it's easy
            switch(g.displayMainMenu()){
                case 1:
                    g.setPlayers();
                    out.println();
                    out.println("[+] Player Types Set Successfully");
                    out.println();
                    break;

                case 2:
                    if(g.isGamePaused){
                        System.out.println("[*] WARNING! Game Still Paused In Background, Do You Really Wish to Load another Game? (Y/N)");
                        Scanner s1 = new Scanner(System.in);
                        String choice = s1.nextLine();

                        while(!(choice.equals("Y") || choice.equals("y") || choice.equals("N") || choice.equals("n"))) {
                            System.out.println("[-] Invalid Choice! Please Choose Again");
                            choice = s1.nextLine();
                        }

                        if(choice.equals("Y") || choice.equals("y")){
                            System.out.println("[-]Paused Game Abandoned");
                            out.println();
                        } else if (choice.equals("N") || choice.equals("n")){
                            System.out.println("[+] Returning to Main Menu");
                            out.println();
                            break;
                        }
                    }

                    out.println("[*] Please Enter Filename of Game to Load:");
                    Scanner s2 = new Scanner(System.in);
                    String input = s2.nextLine();

                    while(!input.matches("\\d\\d_\\d\\d_\\d\\d_\\d\\d_\\d\\d_\\d\\d\\.txt")){
                        System.out.println("[-] Invalid Filename! Please Choose Again");
                        input = s2.nextLine();
                    }

                    out.println("[+] Valid Filename Syntax! Attempting to Open File");
                    try{
                        g.loadGame(input);
                    } catch (IOException e){
                        out.println(e.getMessage());
                        out.println("[-] Returning to Main Menu");
                        out.println();
                        g.isGameLoaded = false;
                    }

                    out.println("[+] Game Loaded Successfully!");
                    out.println();
                    g.isGameLoaded = true;

                    try {
                        g.isGamePaused = false;
                        winningColour = g.play();
                    } catch (PlayerNotDefinedException e){
                        out.println(e.getMessage());
                        out.println("[*] Please Use the Main Menu to Set the Player Types");
                        out.println();
                        break;
                    }
                    if(winningColour != null){
                        g.displayWinningScreen(winningColour);

                    }

                    break;

                case 3:
                    if(g.isGamePaused){
                        out.println("[+] Loading Paused Game");

                        try {
                            winningColour = g.play();
                        } catch (PlayerNotDefinedException e){
                            out.println(e.getMessage());
                            out.println("[*] Please Use the Main Menu to Set the Player Types");
                            out.println();
                            break;
                        }
                        if(winningColour != null){
                            g.displayWinningScreen(winningColour);

                        }

                    } else {
                        out.println("[-] No Paused Game Available to Load! Returning to Main Menu");
                    }

                    break;
                case 4:
                    if(g.isGamePaused){
                        String filename = new SimpleDateFormat("yy_MM_dd_HH_mm_ss").format(new Date()); //TEST THIS
                        filename = filename + ".txt";
                        try {
                            System.out.println("[*] Saving File As '" + filename + "'");
                            g.saveGame(filename);
                        } catch (IOException e){
                            out.println("[-] Unable to Save File! Please Try Again!");
                            out.println();
                            break;
                        }
                        out.println("[+] File Saved Successfully as '" + filename + "'");
                        out.println();
                    } else {
                        out.println("[-] No Paused Game Available for Saving. Returning to Main Menu");
                        out.println();
                    }
                    break;

                case 5:
                    if(g.isGamePaused){
                        System.out.println("[*] WARNING! Game Still Paused In Background, Do You Really Wish to Begin a new Game? (Y/N)");
                        Scanner s3 = new Scanner(System.in);
                        String choice = s3.nextLine();

                        while(!(choice.equals("Y") || choice.equals("y") || choice.equals("N") || choice.equals("n"))) {
                            System.out.println("[-] Invalid Choice! Please Choose Again");
                            choice = s3.nextLine();
                        }

                        if(choice.equals("Y") || choice.equals("y")){
                            System.out.println("[-]Paused Game Abandoned. Starting a New Game");
                            g.isGamePaused = false;
                            g.isGameLoaded = false;
                            out.println();
                        } else if (choice.equals("N") || choice.equals("n")){
                            System.out.println("[+] Returning to Main Menu");
                            out.println();
                            break;
                        }
                    }

                    try {
                        out.println();
                        winningColour = g.play();
                    } catch (PlayerNotDefinedException e){
                        out.println(e.getMessage());

                        /**List<Colour> notSet = new ArrayList<Colour>();
                        for(Colour c : Colour.values()){
                            if(!g.players.keySet().contains(c) || g.players.get(c) == null){
                                notSet.add(c);
                            }
                        }

                        out.println("[-] The Following Colours Do Not Have an Associated Player:");

                        for(Colour c : notSet){
                            out.println(c.toString());
                        } **/

                        out.println("[*] Please Use the Main Menu to Set the Player Types");
                        out.println();
                        break;
                    }
                    if(winningColour != null){
                        g.displayWinningScreen(winningColour);
                    }

                    break;

                case 6:
                    g.displayHelp();
                    break;

                case 7:
                    if(g.isGamePaused){
                        System.out.println("[*] WARNING! Game Still Paused In Background, Do You Really Wish to Exit? (Y/N)");
                        Scanner s4 = new Scanner(System.in);
                        String choice = s4.nextLine();

                        while(!(choice.equals("Y") || choice.equals("y") || choice.equals("N") || choice.equals("n"))) {
                            System.out.println("[-] Invalid Choice! Please Choose Again");
                            choice = s4.nextLine();
                        }

                        if(choice.equals("Y") || choice.equals("y")){
                            System.out.println("[-] Game Abandoned, Exiting.");
                            out.println();
                        } else if (choice.equals("N") || choice.equals("n")){
                            System.out.println("[+] Returning to Main Menu");
                            out.println();
                            break;
                        }
                    }

                    out.println("          * * * * * * * * * * * * * * * * *          ");
                    out.println("          ----- Thank You For Playing -----          ");
                    out.println("          * * * * * * * * * * * * * * * * *          ");
                    break dance; //lol

                default:
                    System.out.println("[-] Invalid Selection! Please Enter an Integer Value between 1 and 7, inclusive");
                    System.out.println();
            }
        }
    }

    public void displayWelcomeMessage(){
        out.println("          * * * * * * * * * * * * * * * * *          ");
        out.println("          ---------- Welcome To -----------          ");
        out.println("           _____     _           _                   ");
        out.println("          |_   _|___| |__  _   _| | __ _             ");
        out.println("            | |/ _` | '_ \\| | | | |/ _` |           ");
        out.println("            | | (_| | |_) | |_| | | (_| |            ");
        out.println("            |_|\\__,_|_.__/ \\__,_|_|\\__,_|         ");
        out.println("          * * * * * * * * * * * * * * * * *          ");
        out.println("                                                     ");
        out.println("          Welcome! I highly recommend that           ");
        out.println("           you give the help page a quick            ");
        out.println("          read to familiarise yourself with          ");
        out.println("         the user interface whilst the game          ");
        out.println("                     is running.                     ");
        out.println("                                                     ");
        out.println("           NOTE: My GUI only worked within           ");
        out.println("         the IntelliJ IDE I wrote this game          ");
        out.println("          in, so please test the Human and           ");
        out.println("          Computer players first! I'm afraid         ");
        out.println("          I did not have time to test the            ");
        out.println("                  GUI and bug fix.                   ");
        out.println("                                                     ");
    }

    public int displayMainMenu(){

        out.println("          * * * * * * * * * * * * * * * * *          ");
        out.println("          ----------- Main Menu -----------          ");
        out.println("                                                     ");
        out.println("          [1] Set Player(s)  [2] Load Game           ");
        out.println("          [3] Continue Game  [4] Save Game           ");
        out.println("          [5] Start New Game [6] Help                ");
        out.println("          [7] Exit                                   ");
        out.println("                                                     ");
        if(this.isGamePaused){
            out.println("             (GAME PAUSED IN BACKGROUND)             ");
        }
        out.println("          * * * * * * * * * * * * * * * * *          ");
        out.println("                                                     ");
        out.println("[*] Please Enter Selection:                          ");

        Scanner s = new Scanner(System.in);
        if (s.hasNextInt()) {
            return s.nextInt() ;
        } else {
            return 0;
        }
    }

    public void setPlayers(){
        if(isGamePaused){
            System.out.println("[*} There is a Game Paused in The Background, But Changing The Player Will Not Change The Board State");
        }

        int counter = 0;
        while(counter < 2){
            Colour colour = Colour.values()[counter];
            switch(displayPlayerSelectionMenu(colour.toString())){
                case 1:
                    HumanConsolePlayer humanPlayer = new HumanConsolePlayer();
                    this.setPlayer(colour, humanPlayer);
                    counter++;
                    break;

                case 2:
                    ComputerPlayer computerPlayer = new ComputerPlayer();
                    this.setPlayer(colour, computerPlayer);
                    counter++;
                    break;

                case 3:
                    HumanGUIPlayer guiPlayer = new HumanGUIPlayer();
                    this.setPlayer(colour, guiPlayer);
                    counter++;
                    break;

                default:
                    System.out.println("[-] Invalid Selection! Please Enter either 1, 2 or 3."); //TEST - CHANGE IF NOT USING GUI
                    System.out.println();
                    break;
            }
        }
    }

    public int displayPlayerSelectionMenu(String colour){
        out.println("          * * * * * * * * * * * * * * * * *          ");
        out.println("          --- Select " + colour + "'s Player Type ---          ");
        out.println("                                                     ");
        out.println("      [1] Human  [2] Computer  [3] GUI (BROKEN)      ");
        out.println("          * * * * * * * * * * * * * * * * *          ");
        out.println("                                                     ");
        out.println("[*] Please Enter Selection:                          ");

        Scanner s = new Scanner(System.in);
        if (s.hasNextInt()) {
            return s.nextInt() ;
        } else {
            return 0;
        }
    }

    public void setPlayer(Colour colour, PlayerInterface player){
        this.players.put(colour, player);
    }

    public Colour getCurrentPlayer(){
        return this.currentPlayer;
    }

    public void setCurrentPlayer(Colour c){
        if(players.keySet().contains(c)){
            this.currentPlayer = c;
        } else {
            System.out.println("[-] Error Setting Current Player, This Colour Is not Present in this Game!");
        }
    }

    public Colour play() throws PlayerNotDefinedException{
        if(!this.isGamePaused && !this.isGameLoaded){ //If it is a new game
            board = new Board(); //Create the board
            dice = new Dice(); //Create the dice
            currentPlayer = Colour.values()[0]; //Set first player to first enum, in the case that a game was abandoned midway through.

        } else if (this.isGamePaused) { //Game continued from background
            isGamePaused = false;
        } //Check for game loaded from file comes later

        Colour currentPlayerColour = getCurrentPlayer();; //Create current player colour
        PlayerInterface player; //Create the current player
        List<Integer> diceValues = new ArrayList<Integer>(); //Create dice values list

        for(Colour c : Colour.values()){ //Check if all the players have been set correctly
            if(!this.players.keySet().contains(c) || this.players.get(c) == null){ //CHECK == NULL TEST - may be set with something else other than a player from load game
                throw new PlayerNotDefinedException("[-] " + c.toString() + " does not Have a Valid Player Set!");
            }
        }

        TurnInterface activeTurn = new Turn();

        while(!board.isWinner(currentPlayerColour)){ //This may seem counterintuitive but it ensures the other person has not won before letting this person take their go

            currentPlayerColour = getCurrentPlayer();
            player = players.get(currentPlayerColour);

            if(!this.isGameLoaded){
                dice.roll();
            } else {
                this.isGameLoaded = false;
            }

            try{
                diceValues = dice.getValues();
            } catch (NotRolledYetException e){
                dice.roll();
            }

            try {
                activeTurn = player.getTurn(currentPlayerColour, board.clone(), diceValues);
            } catch(PauseException e){
                isGamePaused = true;
                break;
            }

            if(activeTurn == null){
                out.println("[-] No Turn Available!");
            } else {
                try{
                    board.takeTurn(currentPlayerColour, activeTurn, diceValues);
                    out.println("[+] Turn Executed Successfully!");
                } catch(IllegalTurnException e){
                    out.println(e.getMessage());
                    out.println("[-] The Given Turn Is Illegal! " + currentPlayerColour.toString() + " Forfeits Their Turn!");
                }
            }

            out.println("[*] " + currentPlayerColour.toString() + "'s Turn is Over!");
            out.println();

            //currentPlayer = green
            //currentPlayerColour = green

            this.setCurrentPlayer(currentPlayerColour.otherColour());

            //currentPlayer = blue
            //currentPlayerColour = green
        }

        if(!board.isWinner(currentPlayerColour) && isGamePaused){ //i.e if the player paused the game intentionally
            System.out.println("[+] Use Menu Option 3 to Resume.");
            return null;
        } else if(!board.isWinner(currentPlayerColour) && !isGamePaused){ //i.e if game was forfeited due to illegal move
            System.out.println("[-] An Illegal Turn Was Attempted, Game Forfeited!");
            isGamePaused = false;
            isGameLoaded = false;
            return currentPlayerColour.otherColour();
        } else { //One of the players won!
            isGamePaused = false;
            isGameLoaded = false;
            out.println(board.toString());
            return currentPlayerColour;
        }
    }

    public void displayWinningScreen(Colour c){
        out.println("          * * * * * * * * * * * * * * * * *          ");
        out.println("          ---------- " + c.toString() + " WINS -----------          ");
        out.println("     __        _____ _   _ _   _ _____ ____          ");
        out.println("     \\ \\      / /_ _| \\ | | \\ | | ____|  _ \\    ");
        out.println("      \\ \\ /\\ / / | ||  \\| |  \\| |  _| | |_) |   ");
        out.println("       \\ V  V /  | || |\\  | |\\  | |___|  _ <      ");
        out.println("        \\_/\\_/  |___|_| \\_|_| \\_|_____|_| \\_\\  ");
        out.println("                                                     ");
        out.println("          * * * * * * * * * * * * * * * * *          ");
        out.println();
    }

    public void displayHelp(){

        out.println("          * * * * * * * * * * * * * * * * *          ");
        out.println("          - The exact rules of Tabula can be         ");
        out.println("           found at: http://bit.ly/2ocNX5J           ");
        out.println("          - Upon the start of your turn, you         ");
        out.println("           will be given the opportunity to          ");
        out.println("           trial a 'turn', which will allow          ");
        out.println("           you to enter your 'turn', move by          ");
        out.println("           move, with each move being validated      ");
        out.println("           upon input.                               ");
        out.println("          - This functionality should prevent        ");
        out.println("           players forfeiting their turns as         ");
        out.println("           a consequence of inputting moves          ");
        out.println("           which are in fact illegal.                ");
        out.println("          - Once the sequence of moves has been      ");
        out.println("           inputted, the user will be given          ");
        out.println("           the choice to use this trialled           ");
        out.println("           'turn' as their actual 'turn', trial      ");
        out.println("           another turn, or input a different        ");
        out.println("           sequence of moves as their actual         ");
        out.println("           'turn'. ");
        out.println("          - Saved games are named automatically,     ");
        out.println("           in the format 'yy_MM_dd_HH_mm_ss.txt'     ");
        out.println("           . This provides an efficient way          ");
        out.println("           to detect and sanitise malicious          ");
        out.println("           input.");
        out.println("          - Finally, thank you for playing!          ");
        out.println("          * * * * * * * * * * * * * * * * *          ");
        out.println();

    }

    public void saveGame(String filename) throws IOException{
        String textToWrite;
        textToWrite = Boolean.toString(useGUI) + '\n';

        textToWrite = textToWrite + "||"; //DELIMITER

        for(Colour c : players.keySet()){
            textToWrite = textToWrite + c.toString() + '\n'; //TEST WHAT TO STRING GIVES YOU
            textToWrite = textToWrite + players.get(c).toString();
            textToWrite = textToWrite + "!!";
        }

        textToWrite = textToWrite + "||";

        textToWrite = textToWrite + currentPlayer.toString() + '\n';

        textToWrite = textToWrite + "||";

        textToWrite = textToWrite + Boolean.toString(isGamePaused) + '\n';

        textToWrite = textToWrite + "||";

        textToWrite = textToWrite + Boolean.toString(isGameLoaded) + '\n';

        textToWrite = textToWrite + "||";

        textToWrite = textToWrite + board.getName() + '\n';

        textToWrite = textToWrite + "||";

        for(Location l : board.getAllLocations()){
            textToWrite = textToWrite + Integer.toString(l.getNumber()) + '\n' + l.getName() + '\n' + Boolean.toString(l.isMixed()) + '\n' + Colour.values()[0].toString() + ":" + Integer.toString(l.numberOfPieces(Colour.values()[0])) + '\n'  + Colour.values()[1].toString() + ":" + Integer.toString(l.numberOfPieces(Colour.values()[1])); //DELIMIT ON /n HERE FOR fields, then on /: to split between colour and number of pieces
            textToWrite = textToWrite + "--"; //USE -- TO DELIMIT BETWEEN LOCATIONS
        }

        textToWrite = textToWrite + "||";

        for(DieInterface d : dice.getDice()){
            try {
                textToWrite = textToWrite + Boolean.toString(d.hasRolled()) + '\n' + Integer.toString(d.getValue());
            } catch (NotRolledYetException e){
                //FILL THIS IN, SHOULD NEVER EXECUTE THOUGH
            }
            textToWrite = textToWrite + "--"; //USE -- TO DELIMIT BETWEEN DICE
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, false), StandardCharsets.UTF_8))) {
            writer.write(textToWrite);
        }
        catch (IOException e) {
            throw new IOException("[-] File Could not be Written To or Created for Some Reason!");
        }
    }

    public void loadGame(String filename) throws IOException{
        List<String> objects = new ArrayList<String>();

        try {
            Scanner scan = new Scanner(new File(filename));
        } catch (FileNotFoundException e){
            throw new IOException("[-] File Not Found!");


        }
        Scanner scan = new Scanner(new File(filename)); //We know file exists
        scan.useDelimiter("\\|\\|"); //TEST IF WE NEED TO ESCAPE DELIMITRE
        while(scan.hasNext()){
            objects.add(scan.next());
        }

        //useGUI Field - 0
        String useGui = objects.get(0).replace("\n", "");
        this.setUseGUI(Boolean.parseBoolean(useGui)); //TECHNICALLY THIS DOESN'T WORK BECAUSE ANYTHING OTHER THAN 'TRUE' or 'true' will return false, may cause problems

        //players ArrayList + Individual Players - 1
        this.players.clear();
        String[] playerList = objects.get(1).split("!!"); //TEST this
        String[] pairs;

        for(String s : playerList){
            pairs = s.split("\\n");

            try {
                Colour c = Colour.valueOf(pairs[0]);
            } catch (IllegalArgumentException e){
                throw new IOException("[-] Incorrect Colour Token When Loading Players. Game Not Loaded");
            }
            Colour c = Colour.valueOf(pairs[0]);//We now know pairs[0] parsed correctly

            if(!(c == Colour.values()[0] || c == Colour.values()[1])){ //TEST - probably not needed as we have the check above
                throw new IOException("[-] Incorrect Colour Token When Loading Players. Game Not Loaded");
            }

            String playerType = pairs[1].split("@")[0];

            PlayerInterface player;
            if(playerType.equals("HumanConsolePlayer")){
                player = new HumanConsolePlayer();
            } else if(playerType.equals("ComputerPlayer")){
                player = new ComputerPlayer();
            } else if(playerType.equals("HumanGUIPlayer")){
                player = new HumanGUIPlayer();
            } else {
                throw new IOException("[-] Incorrect Player Type When Loading Players. Game Not Loaded");
            }

            this.players.put(c, player); //ADD FUNCTIONALITY TO SWITCH BACK TO GUI MODE IF GUI GAME LOADED
        }

        //currentPlayer Field - 2
        String currentColour = objects.get(2).replace("\n", "");
        try {
            Colour c = Colour.valueOf(currentColour);
        } catch (IllegalArgumentException e){
            throw new IOException("[-] Incorrect Colour Token When Loading Players. Game Not Loaded");
        }
        Colour c = Colour.valueOf(currentColour); //We now know objects.get(2) parsed correctly
        this.setCurrentPlayer(c);

        //isGamePaused Field - 3
        String isPaused = objects.get(3).replace("\n", "");
        this.isGamePaused = Boolean.parseBoolean(isPaused);


        //isGameLoaded Field - 4
        String isGameLoaded = objects.get(4).replace("\n", "");
        this.isGameLoaded = Boolean.parseBoolean(isGameLoaded);

        //Board name Field - 5

        String boardName = objects.get(5).replace("\n", "");
        if(boardName != null && !boardName.equals("")){
            this.board.setName(boardName);
        }

        //locations Array - 6
        String[] locations = objects.get(6).split("--"); //Parse Into Locations
        if(this.board != null){ //if 'board' is already pointing at a board object, clear the board object
            this.board.deleteAllLocations();
        } else {
            this.board = new Board(); //if it's not, create a new board object, but then we need to remove all of the empty locations created by the constructor
            this.board.deleteAllLocations();
        }

        Location l;
        HashMap<Colour, Integer> pieces = new HashMap<Colour, Integer>();

        for(String locationString : locations){ //ADD CHECKS TO ENSURE VALIDITY ON PARSING
            String[] fields = locationString.split("\\n"); //Split list of location fields into individual fields
            try{
                int locationNumber = Integer.parseInt(fields[0]);
            } catch(NumberFormatException e){
                throw new IOException("[-] Invalid locationNumber when Loading Locations. Game Not Loaded");
            }
            int locationNumber = Integer.parseInt(fields[0]); //We know int was parsed successfully

            String locationName = fields[1];

            l = new Location(locationName, locationNumber);
            boolean isMixed = Boolean.parseBoolean(fields[2]);
            l.setMixed(isMixed);

            for(int i = 0; i < 2; i++){
                String[] counterFields = fields[3 + i].split(":"); //MAY NEED ESCAPING

                try {
                    Colour colour = Colour.valueOf(counterFields[0]);
                } catch (IllegalArgumentException e){
                    throw new IOException("[-] Incorrect Colour Token When Loading Players. Game Not Loaded");
                }
                Colour colour = Colour.valueOf(counterFields[0]);

                try {
                    int numberOfCounters = Integer.parseInt(counterFields[1]);
                } catch (NumberFormatException e){
                    throw new IOException("[-] Invalid number of Counters when Loading Locations. Game Not Loaded");
                }
                int numberOfCounters = Integer.parseInt(counterFields[1]);

                pieces.put(colour, numberOfCounters);
            }

            l.setPieces(pieces);

            board.addLocation(l);
        }

        //dice and die - 7
        String[] dice = objects.get(7).split("--");
        Die d1 = new Die();
        Die d2 = new Die();

        String[] die1 = dice[0].split("\\n");
        String[] die2 = dice[1].split("\\n");

        int die1Value;
        boolean hasDie1Rolled = Boolean.parseBoolean(die1[0]);
        try{
            die1Value = Integer.parseInt(die1[1]);
        } catch (NumberFormatException e){
            throw new IOException("[-] Invalid Die 1 value when Loading Dice. Game Not Loaded");
        }
        die1Value = Integer.parseInt(die1[1]); //We know integer dice value parsed correctly

        int die2Value;
        boolean hasDie2Rolled = Boolean.parseBoolean(die2[0]);
        try{
            die2Value = Integer.parseInt(die2[1]);
        } catch (NumberFormatException e){
            throw new IOException("[-] Invalid Die 2 value when Loading Dice. Game Not Loaded");
        }
        die2Value = Integer.parseInt(die2[1]); //We know integer dice value parsed correctly

        d1.setValue(die1Value);
        d1.setRolled(hasDie1Rolled);
        d2.setValue(die2Value);
        d2.setRolled(hasDie2Rolled);

        this.dice = new Dice();
        this.dice.setDice(d1, d2);

        if(board.isValid()){
            this.isGameLoaded = true;
        } else {
            throw new IOException("[-] Game was loaded successfully, but Board State Invalid. Game Not Loaded.");
        }
    }

    public boolean getUseGUI(){
        return useGUI;
    }

    public void setUseGUI(boolean b) {
        useGUI = b;
    }
}
