import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.lang.System.out;

/**
 * Created by matt on 22/03/2017.
 */
public class ComputerPlayer implements PlayerInterface {

    /* Decision Making is Based on a Simple Priority Basis:

     * If a Move is in the Knocked-Off Position it is given 341 points
     * If a Move denies the other colour from getting into the end zone within one role of the dice, it is given 85 points (TEST may change to 9 away from finish, not 6, after calculating probabilities)
     * If a Move moves into the end zone it is given 21 points
     * If a Move knocks off any other piece, it is given 5 point
     * If a Move simply moves a piece forward, it is given 1 points
     * The total distance of the move is calculated throughout
     *
     * The points system has been thought out so that a turn containing one piece in the end zone will always be chosen over one not containing a piece in the endzone, and so on.
     *
     * These Priority Points can be modified, and I will extend them in due course
     */

    public static final int POINTS_FOR_IN_KNOCKED_OFF_ZONE = 341;
    public static final int POINTS_FOR_DENYING_FINISH = 85;
    public static final int POINTS_FOR_FINISHING = 21;
    public static final int POINTS_FOR_KNOCKING_PIECE_OFF = 5;
    public static final int POINTS_FOR_MOVING_FORWARD = 1;

    private List<Turn> listOfTurns;
    private boolean noTurnAvailable;


    public ComputerPlayer(){
        listOfTurns = new ArrayList<Turn>();
        noTurnAvailable = false;
    }

    /**
     * Get from the player the turn that they wish to take
     *
     * @param colour the Colour they are playing as
     *
     * @param board a clone of the current board state, so that the player can try different moves
     *
     * @param diceValues a list of the dice values the player can use.
     *
     * @return the turn the player wishes to take. It is the Player's responsibility to ensure that the turn is legal, matches the provided diceValues and uses as may of the diceValues as possible.
     *
     * @throws PauseException is only used by human players if they are in the middle of a game and wish to pause the game instead of taking a turn.
     **/
    public TurnInterface getTurn(Colour colour, BoardInterface board, List<Integer> diceValues) throws PauseException {

        listOfTurns.clear(); //forgetting this caused so much grief
        this.noTurnAvailable = false;

        out.println("[*] " + colour.toString() + " Player's Turn");
        out.println();
        out.println(board.toString());

        ((Board) board).displayDice(diceValues);

        out.println();
        out.println("[*] Computer-Generating a Turn");
        out.println();

        Turn t = new Turn();
        generateTurns(colour, t, (Board) board, diceValues);

        if(noTurnAvailable){
            return null;
        }

        t = Collections.max(listOfTurns, new TurnComparator());

        out.println("          * * * * * * * * * * * * * * * * *          ");
        out.println("          --------- Computer Turn ---------          ");
        out.println();

        int counter = 1;
        for(MoveInterface m : t.getMoves()){
            int source = m.getSourceLocation();
            String stringSource;

            if(source < 10){
                stringSource = "0" + Integer.toString(source);
            } else {
                stringSource = Integer.toString(source);
            }

            if(source == Board.KNOCKED_LOCATION){
                source = 0;
            }

            int destination = source + m.getDiceValue();

            if(destination >= Board.END_LOCATION){ //don't worry about the knocked location as no move has the knocked location as its destination
                destination = Board.END_LOCATION;
            }
            String stringDestination;
            if(destination < 10){
                stringDestination = "0" + Integer.toString(destination);
            } else {
                stringDestination = Integer.toString(destination);
            }

            out.println("              Move " + Integer.toString(counter) + ": [ " + stringSource + " ] -> [ " + stringDestination + " ]              ");
            counter ++;
        }

        out.println("                                                     ");
        out.println("          * * * * * * * * * * * * * * * * *          ");
        out.println();

        return t;
    }

//TEST IF WE NEED TO CHANGE TO ENSURE ALL DIE ARE USED
    public void generateTurns(Colour colour, Turn turn, Board board, List<Integer> diceValues){ //TEST IF LOTS OF TURNS OR JUST ONE IS ADDED ALSO
        Set<MoveInterface> possibleMoves = board.possibleMoves(colour, diceValues);

        if(turn.getMoves().size() == 0 && possibleMoves.isEmpty()){ //THIS SHOULD NOW WORK BUT TEST - (THIS WILL NOT WORK EITHER - if only one move is available then turn is still allowed)
            noTurnAvailable = true;
        }

        if(diceValues.isEmpty() || possibleMoves.isEmpty()){ //THIS SHOULD NOW WORK BUT TEST - THIS IS WRONG TEST - we may get the situation where we don't have any possible moves but haven't used all the dice and still have a legal move
            Turn t = new Turn();
            for(MoveInterface m : turn.getMoves()){
                try {
                    t.addMove(m);
                } catch (IllegalTurnException e) {
                    //This will never execute as we are simply copying the moves from one turn to another to prevent the turn within the ListOfTurns being modified when the turn is updated in the recursion
                }
            }

            t.setTotalDistance(turn.getTotalDistance());
            t.setTotalPoints(turn.getTotalPoints());

            listOfTurns.add(t);
            return;
        }

        int points;
        Board boardCopy;

        for (MoveInterface m : possibleMoves) {
            points = 0;

            boardCopy = (Board) board.clone();

            try {
                turn.addMove(m);
            } catch (IllegalTurnException e) {
                out.println("This one executed");
                break; //Never Execute As Only Working With Possible Moves
            }

            try {
                boardCopy.makeMove(colour, m);
            } catch (IllegalMoveException e) {
                out.println("This one executed [2]");
                break;
            }

            if( m.getSourceLocation() == Board.KNOCKED_LOCATION){
                switch(turn.getMoves().size()){ //Add extra points to ensure if we have two or more turns with the same moves in different orders, the one with the move out of the knocked off location first will get executed
                    case 1:
                        points += POINTS_FOR_IN_KNOCKED_OFF_ZONE + 3;
                        break;
                    case 2:
                        points += POINTS_FOR_IN_KNOCKED_OFF_ZONE + 2;
                        break;
                    case 3:
                        points += POINTS_FOR_IN_KNOCKED_OFF_ZONE + 1;
                        break;
                    default:
                        points += POINTS_FOR_IN_KNOCKED_OFF_ZONE;
                }
            }

            if (((Move) m).getDenied()) {
                points += POINTS_FOR_DENYING_FINISH;
            }
            if (((Move) m).getFinished()) {
                points += POINTS_FOR_FINISHING;
            }
            if (((Move) m).getKnockedOffOther()) {
                points += POINTS_FOR_KNOCKING_PIECE_OFF;
            }

            points += POINTS_FOR_MOVING_FORWARD;
            turn.addPoints(points);

            turn.addTotalDistance(m.getDiceValue());
            diceValues.remove((Integer)m.getDiceValue());

            generateTurns(colour, turn, boardCopy, diceValues);

            diceValues.add(m.getDiceValue());
            turn.removeLastMove();
            turn.removePoints(points);
            turn.removeDistance(m.getDiceValue());
        }
    }
}

