import java.util.*;

import static java.lang.System.out;

/**
LOCATIONS:  0: Start
            1 to NUMBER_OF_LOCATIONS: Board Locations
            NUMBER_OF_LOCATIONS + 1: End Location
            NUMBER_OF_LOCATIONS + 2: Knocked Location
**/

public class Board implements BoardInterface{

    static final int START_LOCATION = 0;
    static final int END_LOCATION = NUMBER_OF_LOCATIONS + 1;
    static final int KNOCKED_LOCATION = NUMBER_OF_LOCATIONS + 2;
    static final int TOTAL_LOCATIONS = NUMBER_OF_LOCATIONS + 2;

    private List<Location> locations = new ArrayList<Location>();
    private String name;

    public Board() { //Normal Constructor
        Location l;
        name = ""; //Avoid null exceptions when loading and saving the name

        int counter = 0;
        while (counter <= TOTAL_LOCATIONS) {
            l = new Location("Board Location " + Integer.toString(counter), counter);

            if(counter == START_LOCATION){
                l.setMixed(true);
                l.setName("Start Location");
            } else if(counter == END_LOCATION){
                l.setMixed(true);
                l.setName("End Location");
            } else if(counter == KNOCKED_LOCATION){
                l.setMixed(true);
                l.setName("Knocked Location");
            } else {
                l.setMixed(false);
            }

            locations.add(l);
            counter++;
        }

        //Add Counters to Start Position

        int pieceCounter = 0;
        int colourCounter = 0;
        Colour[] colours = Colour.values();
        int numberOfColours = colours.length;
        Colour c;

        while (pieceCounter < PIECES_PER_PLAYER){ //Repeat for each number of counters
            colourCounter = 0;
            while(colourCounter < numberOfColours){ //Repeat for each colour of counter
                c = colours[colourCounter];

                try {
                    getStartLocation().addPieceGetKnocked(c);
                } catch(IllegalMoveException e){
                    System.out.println("[-] Error Whilst Adding Start Pieces!");
                }
                colourCounter++;
            }
            pieceCounter++;
        }
    }

    /**
     * Copy Constructor - i.e creates an exact copy of the current board
     **/
    public Board(BoardInterface b){
        name = ((Board) b).getName();
        int counter = 0;
        Location l;

        while (counter <= TOTAL_LOCATIONS){
            l = new Location("Board Location " + Integer.toString(counter), counter);

            try{
                if(counter == START_LOCATION){
                    l.setPieces(((Location) b.getStartLocation()).getPieces());
                    l.setName(new String(b.getStartLocation().getName()));
                    l.setMixed(b.getStartLocation().isMixed());
                } else if(counter == END_LOCATION){
                    l.setPieces(((Location) b.getEndLocation()).getPieces());
                    l.setName(new String(b.getEndLocation().getName()));
                    l.setMixed(b.getEndLocation().isMixed());
                } else if(counter == KNOCKED_LOCATION){
                    l.setPieces(((Location) b.getKnockedLocation()).getPieces());
                    l.setName(new String(b.getKnockedLocation().getName()));
                    l.setMixed(b.getKnockedLocation().isMixed());
                } else {
                    l.setPieces(((Location) b.getBoardLocation(counter)).getPieces());
                    l.setName(new String(b.getBoardLocation(counter).getName()));
                    l.setMixed(b.getBoardLocation(counter).isMixed());
                }

            } catch (NoSuchLocationException e){
                System.out.println("[-] Error Occurred Whilst Cloning Location Number: " + Integer.toString(counter));
                continue;
            }

            this.locations.add(l);
            counter++;
        }
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationInterface getStartLocation() {
        return locations.get(START_LOCATION);
    }

    public LocationInterface getEndLocation() {
        return locations.get(END_LOCATION);
    }

    public LocationInterface getKnockedLocation() {
        return locations.get(KNOCKED_LOCATION);
    }

    public LocationInterface getBoardLocation(int locationNumber) throws NoSuchLocationException {
        if ((locationNumber < 1) || (locationNumber > (NUMBER_OF_LOCATIONS))) {
            throw new NoSuchLocationException("[-] Location Number Provided Out of Range!");
        }
        return locations.get(locationNumber);
    }

    public boolean canMakeMove(Colour colour, MoveInterface move) {
        int source = move.getSourceLocation();
        int destination = source + move.getDiceValue();

        if(source == KNOCKED_LOCATION){ //Handle the special case where the source is the knocked/end location - caused so much grief
            destination = START_LOCATION + move.getDiceValue(); //If we are moving from the knocked off location back onto the board, set the destination equal to the source location + the dice value so the game does not thing we are moving into the end location
            return (locations.get(destination).canAddPiece(colour)) && (getKnockedLocation().canRemovePiece(colour));
        }
        else if(destination > NUMBER_OF_LOCATIONS){
            return (getEndLocation().canAddPiece(colour)) && (locations.get(source).canRemovePiece(colour));
        }
        else if (destination > 0 && destination <= NUMBER_OF_LOCATIONS) {
            return (locations.get(destination).canAddPiece(colour)) && (locations.get(source).canRemovePiece(colour));
        }
        else {
            return false; //Should never execute
        }
    }

    public void makeMove(Colour colour, MoveInterface move) throws IllegalMoveException {

        int source = move.getSourceLocation();
        boolean isSourceKnockedOff = false;

        if(source == KNOCKED_LOCATION){
            source = START_LOCATION; //If we are moving from the knocked off location back onto the board, set the source back to 0 so the game does not think we're moving to an end location
            isSourceKnockedOff = true; //Used again when removing a piece
        }

        int destination = source + move.getDiceValue();
        Colour otherColour;


        if (canMakeMove(colour, move)) {                                               //If we can make a move
            if (destination > NUMBER_OF_LOCATIONS) {                                      //If we are moving into the end zone
                getEndLocation().addPieceGetKnocked(colour);                           //Get the end location, add the counter to it, null will be returned
                ((Move) move).setFinished(true);//This may not work with computer player!!!!!
            } else {                                                                   //If we are moving to a board location
                otherColour = locations.get(destination).addPieceGetKnocked(colour);   //Get the destination location, add the counter to it, and get the colour of the counter removed
                if (otherColour != null) {                                               //If a piece was knocked off
                    getKnockedLocation().addPieceGetKnocked(otherColour);               //Add the counter to the knocked location
                    ((Move) move).setKnockedOffOther(true);                             //This may not work with computer player!!!!!
                    if (destination >= NUMBER_OF_LOCATIONS - DieInterface.NUMBER_OF_SIDES_ON_DIE) {
                        ((Move) move).setDenied(true);
                    }
                }
            }

            if (isSourceKnockedOff) {
                locations.get(KNOCKED_LOCATION).removePiece(colour);                    //Remove piece from starting position
            } else {
                locations.get(source).removePiece(colour);
            }
        } else {
            throw new IllegalMoveException("[-] Attempted Move Is Illegal!");
        }
    }


    public void takeTurn(Colour colour, TurnInterface turn, List<Integer> diceValues) throws IllegalTurnException {

        //Check each move is in the possible moves set, and the total move values are equal
        List<Integer> diceCopy = new ArrayList<Integer>();
        diceCopy.addAll(diceValues);

        Board boardCopy = new Board(this);
        Set<MoveInterface> possibleMoves = possibleMoves(colour, diceValues);

        for (MoveInterface m : turn.getMoves()) { //For every move in the turn

            if(boardCopy.getKnockedLocation().canRemovePiece(colour) && m.getSourceLocation() != KNOCKED_LOCATION){ //If there are pieces in the knocked off location of the given colour, they have to be moved first
                throw new IllegalTurnException("[-] All Knocked Off Pieces Must be Moved onto the Board First!");
            }

            if (possibleMoves.contains(m)){ //If the move is possible

                try {
                    boardCopy.makeMove(colour, m); //Make the move on the copy
                } catch (IllegalMoveException e) {
                    throw new IllegalTurnException("[-] One of the Attempted Moves Within Turn is Illegal!"); //This should never execute
                }

                if(!diceValues.remove((Integer) m.getDiceValue())){ //Remove the dice value just used in the move from the available dice values
                    throw new IllegalTurnException("[-] A Move Was Attempted Using Incorrect Dice Values!"); //If the move used a dice value not in diceValues, then throw an exception - should never execute?
                }

                possibleMoves = boardCopy.possibleMoves(colour, diceValues); //Generate the new set of possible moves
            } else {
                throw new IllegalTurnException("[-] One of the Attempted Moves Within Turn is Illegal!"); //If the move is not legal, throw an exception.
            }
        }

        if(!diceValues.isEmpty()){ //If there are any dice left over, see if any further moves could have been made
            for(Integer i : diceValues){
                for(MoveInterface m : possibleMoves){
                    if(m.getDiceValue() == i){
                        throw new IllegalTurnException("[-] Not All of the Dice Were Used!");
                    }
                }
            }
        }

        boardCopy = new Board(this);
        int counter = 0;

        if(!(checkMoreMoves(boardCopy, colour, diceCopy, counter) == turn.getMoves().size())){ //also need to check if different order of moves was possible to use all the die, up to four levels of recursion
            throw new IllegalTurnException("[-] Not All of the Dice Were Used!");
        }

        //Execute the moves in the turn, one by one
        for (MoveInterface m : turn.getMoves()){
            try {
                makeMove(colour, m);
            } catch (IllegalMoveException e) {
                throw new IllegalTurnException("[-] One of the Attempted Moves Within Turn is Illegal!"); //This should never execute
            }
        }
    }

    public int checkMoreMoves(Board b, Colour c, List<Integer> diceValues, int counter){ //THIS IS PROBABLY BROKEN

        if(diceValues.isEmpty()){
            return counter;
        }

        Board boardCopy = new Board(b);
        List<Integer> diceCopy = diceValues;

        for (MoveInterface m : boardCopy.possibleMoves(c, diceCopy)) {

            try {
                b.makeMove(c, m);
            } catch (IllegalMoveException e) {
                continue;
            }

            diceValues.remove((Integer) m.getDiceValue());
            return checkMoreMoves(b, c, diceValues, counter + 1);
        }
        return counter;
    }

    public boolean isWinner(Colour colour){
        return locations.get(END_LOCATION).numberOfPieces(colour) == PIECES_PER_PLAYER;
    }

    public Colour winner() {
        for (Colour c : Colour.values()) {
            if (isWinner(c)) {
                return c;
            }
        }
        return null;
    }

    public boolean isValid(){
        int total1 = 0;
        int total2 = 0;
        for(Location l : locations){
            total1 += l.numberOfPieces(Colour.values()[0]);
            total2 += l.numberOfPieces(Colour.values()[1]);

            if(l.isEmpty() || l.isMixed()){ //If the location is empty, everything should be fine
                continue;
            } else if(l.getPieces().get(Colour.values()[0]) > 0 && l.getPieces().get(Colour.values()[1]) > 0){ //If the location contains two pieces of different colour, something has gone wrong
                return false;
            }
        }
        if (total1 != PIECES_PER_PLAYER || total2 != PIECES_PER_PLAYER){ //If for some reason pieces have been lost/added, return false
            return false;
        }
        return true;
    }

    public Set<MoveInterface> possibleMoves(Colour colour, List<Integer> diceValues){
        Set<MoveInterface> moves = new HashSet<MoveInterface>();

        Move m;

        if(getKnockedLocation().canRemovePiece(colour)){
            for(Integer i : diceValues) { //We don't need to loop through each location as we have to remove the pieces from the knocked off location first before any other location, so just loop through dice values
                m = new Move();

                try {
                    m.setSourceLocation(KNOCKED_LOCATION);
                    m.setDiceValue(i);
                } catch (NoSuchLocationException | IllegalMoveException e) {

                }

                if(canMakeMove(colour, m)) {
                    moves.add(m);
                }
            }
        } else {
            for (Location l : locations) { //Loop through each location
                if (l.isEmpty()) {
                    continue;
                }

                for (Integer i : diceValues) { //Check if there is a legal move for a piece at that location with each of the dice values
                    m = new Move();

                    try {
                        m.setSourceLocation(l.getNumber());
                        m.setDiceValue(i);
                    } catch (NoSuchLocationException | IllegalMoveException e) {
                        //System.out.println("[-] Invalid Move Created When Generating Possible Moves!");
                        continue;
                    }

                    if (canMakeMove(colour, m)) {
                        moves.add(m);
                    }
                }
            }
        }

        return moves;
    }

    public BoardInterface clone(){
        return new Board(this); //Calls the secondary constructor
    }

    public String toString(){
        int counter = 1;
        ArrayList<String> colour1 = new ArrayList<>();
        ArrayList<String> colour2 = new ArrayList<>();
        String output = "";

        colour1.add("00"); //BUG FIX - Padding out the colour arrays so that they align with the locations - i.e colour1.get(1) returns location1's pieces, not location2.
        colour2.add("00");

        while (counter <= NUMBER_OF_LOCATIONS) {
            try {
                colour1.add(((Location) getBoardLocation(counter)).stringNumberOfPieces(Colour.values()[0]));
                colour2.add(((Location) getBoardLocation(counter)).stringNumberOfPieces(Colour.values()[1]));
            } catch (NoSuchLocationException e) {
                out.println("[-] Error Counting Tokens on Each Location, Location Not Found!");
            }
            counter++;
        }

        output = output +

                "          * * * * * * * * * * * * * * * * *          \n" +
                "          ------ Current Board State ------          \n" +
                "                                                     \n" +
                "                Start Location: [" + ((Location) getStartLocation()).stringNumberOfPieces(Colour.values()[0]) + "|" + ((Location) getStartLocation()).stringNumberOfPieces(Colour.values()[1]) + "]              \n" +
                "                                                     \n";

        if(NUMBER_OF_LOCATIONS == 24) {

            output = output +
                    "      1       2       3       4       5       6      \n" +
                    "   [" + colour1.get(1) + "|" + colour2.get(1) + "] [" + colour1.get(2) + "|" + colour2.get(2) + "] [" + colour1.get(3) + "|" + colour2.get(3) + "] [" + colour1.get(4) + "|" + colour2.get(4) + "] [" + colour1.get(5) + "|" + colour2.get(5) + "] [" + colour1.get(6) + "|" + colour2.get(6) + "]   \n" +
                    "24 [" + colour1.get(24) + "|" + colour2.get(24) + "]                                 [" + colour1.get(7) + "|" + colour2.get(7) + "] 7 \n" +
                    "23 [" + colour1.get(23) + "|" + colour2.get(23) + "]               ____              [" + colour1.get(8) + "|" + colour2.get(8) + "] 8 \n" +
                    "22 [" + colour1.get(22) + "|" + colour2.get(22) + "]      Knocked [ " + ((Location) getKnockedLocation()).stringNumberOfPieces(Colour.values()[0]) + " ] " + Colour.values()[0] + "       [" + colour1.get(9) + "|" + colour2.get(9) + "] 9 \n" +
                    "21 [" + colour1.get(21) + "|" + colour2.get(21) + "]        Off   [ " + ((Location) getKnockedLocation()).stringNumberOfPieces(Colour.values()[1]) + " ] " + Colour.values()[1] + "        [" + colour1.get(10) + "|" + colour2.get(10) + "] 10\n" +
                    "20 [" + colour1.get(20) + "|" + colour2.get(20) + "]               ‾‾‾‾              [" + colour1.get(11) + "|" + colour2.get(11) + "] 11\n" +
                    "19 [" + colour1.get(19) + "|" + colour2.get(19) + "]                                 [" + colour1.get(12) + "|" + colour2.get(12) + "] 12\n" +
                    "   [" + colour1.get(18) + "|" + colour2.get(18) + "] [" + colour1.get(17) + "|" + colour2.get(17) + "] [" + colour1.get(16) + "|" + colour2.get(16) + "] [" + colour1.get(15) + "|" + colour2.get(15) + "] [" + colour1.get(14) + "|" + colour2.get(14) + "] [" + colour1.get(13) + "|" + colour2.get(13) + "]   \n" +
                    "      18      17      16      15      14      13     \n";

        } else {

            output = output + "                 Knocked [ " + ((Location) getKnockedLocation()).stringNumberOfPieces(Colour.values()[0]) + " ] " + Colour.values()[0] + "       \n";
            output = output + "                  Off    [ " + ((Location) getKnockedLocation()).stringNumberOfPieces(Colour.values()[1]) + " ] " + Colour.values()[1] + "        \n\n" ;

            counter = 1;

            while(counter <= NUMBER_OF_LOCATIONS){
                if(counter < 10){
                    output = output + "                 Location 0" + Integer.toString(counter) + ": [" + colour1.get(counter) + "|" + colour2.get(counter) + "]                \n";
                } else {
                    output = output + "                 Location " + Integer.toString(counter) + ": [" + colour1.get(counter) + "|" + colour2.get(counter) + "]                \n";
                }
                counter++;
            }
        }

        output = output +
                "                                                     \n" +
                "                End Location: [" + ((Location) getEndLocation()).stringNumberOfPieces(Colour.values()[0]) + "|" + ((Location) getEndLocation()).stringNumberOfPieces(Colour.values()[1]) + "]                \n" +
                "                                                     \n" +
                "                Key: [ " + Colour.values()[0] + " | " + Colour.values()[1] + " ]                \n";

        return output;
    }

    public void displayDice(List<Integer> diceValues){
        switch(diceValues.size()){
            case 1:
                out.println("                      Dice: [ " + diceValues.get(0).toString() + " ]                     ");
                out.println("          * * * * * * * * * * * * * * * * *          ");
                break;
            case 2:
                out.println("                  Dice: [ " + diceValues.get(0).toString() + " ], [ " + diceValues.get(1).toString() + " ]                 ");
                out.println("          * * * * * * * * * * * * * * * * *          ");
                break;
            case 3:
                out.println("              Dice: [ " + diceValues.get(0).toString() + " ], [ " + diceValues.get(1).toString() + " ], [ " + diceValues.get(2).toString() + " ]              ");
                out.println("          * * * * * * * * * * * * * * * * *          ");
                break;
            case 4:
                out.println("           Dice: [ " + diceValues.get(0).toString() + " ], [ " + diceValues.get(1).toString() + " ], [ " + diceValues.get(2).toString() + " ], [ " + diceValues.get(3).toString() + " ]          ");
                out.println("          * * * * * * * * * * * * * * * * *          ");
                break;

        }
    }

    public List<Location> getAllLocations(){
       return this.locations;
    }

    public void deleteAllLocations(){
        this.locations.clear();
    }

    public void addLocation(Location l){
        this.locations.add(l);
    }
}