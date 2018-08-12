import java.util.ArrayList;
import java.util.List;

/**
 * Created by matt on 20/03/2017.
 */
public class Turn implements TurnInterface{

    private List<MoveInterface> moves;

    private int totalDistance;
    private int totalPoints;

    /*
    private int numberOfStartingMoves;
    private int totalKnockedOff;
    private int totalFinished;
    private int totalDenied;
     */

    public Turn(){
        moves = new ArrayList<MoveInterface>();
        totalDistance = 0;
        totalPoints = 0;

        /*
        totalKnockedOff = 0;
        totalFinished = 0;
        totalDenied = 0;
         */
    }

    /**
     * @param move to be added after the moves already defined in the current turn
     *
     * @throws IllegalTurnException if there are already four or more moves in the turn
     */
    public void addMove(MoveInterface move) throws IllegalTurnException{
        if(moves.size() > 3){
            throw new IllegalTurnException("[-] Maximum Number Of Turns Reached!");
        }

        moves.add(move);
    }

    public List<MoveInterface> getMoves(){
        return this.moves;
    }

    public void addTotalDistance(int distance){
        this.totalDistance += distance;
    }

    public int getTotalDistance(){
        return this.totalDistance;
    }

    public void setTotalDistance(int distance) {
        this.totalDistance = distance;
    }

    /*
    public void addTotalDenied(){
        this.totalDistance ++;
    }

    public void addTotalFinished(){
        this.totalDistance ++;
    }

    public void addTotalKnockedOff(){
        this.totalDistance ++;
    }

    public void addNumberOfStartingMoves(){
        this.numberOfStartingMoves++;
    }

    public int getTotalKnockedOff(){
        return this.totalKnockedOff;
    }

    public int getTotalFinished(){
        return this.totalFinished;
    }

    public int getTotalDenied(){
        return this.totalDenied;
    }

    public int getNumberOfStartingMoves(){
        return numberOfStartingMoves;
    } */

    public void addPoints(int points){
        this.totalPoints += points;
    }

    public int getTotalPoints(){
        return this.totalPoints;
    }

    public void setTotalPoints(int points) {
        this.totalPoints = points;
    }

    public void removeLastMove(){
        moves.remove(moves.size()-1);
    }

    public void removePoints(int points){
        totalPoints -= points;
    }

    public void removeDistance(int distance){
        totalDistance -= distance;
    }
}
