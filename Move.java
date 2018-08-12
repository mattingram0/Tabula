import java.util.Objects;

/**
 * Created by matt on 20/03/2017.
 */
public class Move implements MoveInterface {

    private int sourceLocation;
    private int diceValue;

    private boolean knockedOffOther;
    private boolean finished;
    private boolean denied;


    public Move(){

        this.finished = false;
        this.denied = false;
        this.knockedOffOther = false;


    }

    //ALTERED: position NUMBER_OF_LOCATIONS + 2 is the KNOCKED_LOCATION and is also valid
    /**
     * @param locationNumber represents the board position to move a piece from
     * in the range 0-24. 0 reresents off the board (the knocked location if there are pieces there, otherwise the off-board star location). A locationNumber of 1-24 refers to locations on the board with 1 being the first and 24 being the last.
     * @throws NoSuchLocationException if locationNumer is not in the range 0-24
     **/
    public void setSourceLocation(int locationNumber) throws NoSuchLocationException{
        if (locationNumber < Board.START_LOCATION || locationNumber == Board.END_LOCATION || locationNumber > Board.KNOCKED_LOCATION) {
            throw new NoSuchLocationException("No Such Location Exists, or Impossible For Location To be Source !");
        }

        this.sourceLocation = locationNumber;
    }

    public int getSourceLocation(){
        return this.sourceLocation;
    }

    /**
     *
     * @param diceValue represents the value of the dice to be used in the move
     *
     * @throws IllegalMoveException if diceValue is not in the range 0-6
     **/


    public void setDiceValue(int diceValue) throws IllegalMoveException{
        if(diceValue < 0 || diceValue > DieInterface.NUMBER_OF_SIDES_ON_DIE){
            throw new IllegalMoveException("Invalid Dice Value!");
        }

        this.diceValue = diceValue;
    }

    public int getDiceValue(){
        return this.diceValue;
    }

    @Override
    public int hashCode(){
        return (int)(0.5*(sourceLocation + diceValue)*(sourceLocation + diceValue + 1)) + diceValue; //Cantor pairing, nice..
    }

    @Override
    public boolean equals(Object o) {

        // self check
        if (this == o)
            return true;
        // null check
        if (o == null)
            return false;
        // type check and cast
        if (getClass() != o.getClass())
            return false;

        Move m = (Move) o;
        // field comparison
        return Objects.equals(sourceLocation, m.sourceLocation)
                && Objects.equals(diceValue, m.diceValue);
    }


    public void setFinished(boolean finished){
        this.finished = finished;
    }

    public void setKnockedOffOther(boolean knockedOffOther){
        this.knockedOffOther = knockedOffOther;
    }

    public void setDenied(boolean denied){
        this.denied = denied;
    }

    public boolean getFinished(){
        return this.finished;
    }

    public boolean getKnockedOffOther(){
        return this.knockedOffOther;
    }

    public boolean getDenied(){
        return this.denied;
    }
}
