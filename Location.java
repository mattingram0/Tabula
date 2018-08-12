import java.util.HashMap;

/**
 * Created by matt on 20/03/2017.
 */
public class Location implements LocationInterface {

    private int number;
    private String name;
    private boolean mixed;
    private HashMap<Colour, Integer> pieces;

    public Location(String name){
        this.name = name;
        this.pieces = new HashMap<Colour, Integer>();
    }

    public Location(String name, int number){
        this.number = number;
        this.name = name;
        this.pieces = new HashMap<Colour, Integer>();
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getNumber(){
        return this.number;
    }

    public void setNumber(int number){
        this.number = number;
    }

    /**
     * @return true if and only if the location allows pieces of both colours
     */
    public boolean isMixed(){
        return this.mixed;
    }

    /**
     * @param isMixed true if and only if the location allows pieces of both colours
     */
    public void setMixed(boolean isMixed){
        this.mixed = isMixed;
    }

    /**
     * @return true if and only if the location has no pieces in it
     */
    public boolean isEmpty(){
        int counter = 0;
        for(int i : pieces.values()){
            counter += i;
        }

        return counter == 0;
    }

    /**
     * @param colour the colour of pieces to count
     * @return the number of pieces of that colour
     **/
    public int numberOfPieces(Colour colour){
        if(pieces.containsKey(colour)){
            return pieces.get(colour);
        } else {
            return 0;
        }
    }

    public String stringNumberOfPieces(Colour colour){

        int number;

        if(pieces.containsKey(colour)){
            number = pieces.get(colour);
        } else {
            number = 0;
        }

        if(number < 10){
            return "0" + Integer.toString(number);
        } else {
            return Integer.toString(number);
        }
    }

    /**
     * @param colour the colour of the piece to add
     * @return true if and only if a piece of that colour can be added (i.e. no IllegalMoveException)
     **/
    public boolean canAddPiece(Colour colour){
        if(isMixed()) return true;

        for(Colour c : pieces.keySet()){
            if((c != colour) && numberOfPieces(c) > 1) {
                return false;
            }
        }

    return true;
    }

    /**
     * @param colour the colour of the piece to add
     *
     * @throws IllegalMoveException if the location is not mixed and already contains two or more pieces
     * of the other colour
     *
     * @return null if nothing has been knocked off, otherwise the colour of the piece that has been knocked off
     **/
    public Colour addPieceGetKnocked(Colour colour) throws IllegalMoveException{
        if(!canAddPiece(colour)){
            throw new IllegalMoveException("Location Already Contains More Than One Piece of The Other Colour!");
        }

        if(isMixed()) { //If it's mixed, add one to the location
            if (pieces.containsKey(colour)) {
                pieces.put(colour, pieces.get(colour) + 1);
            } else {
                pieces.put(colour, 1);
            }
            return null;
        } else { //If it's not mixed, add one to the location, and:
            if(pieces.containsKey(colour)) {
                pieces.put(colour, pieces.get(colour) + 1);
            } else {
                pieces.put(colour, 1);
            }

            try {
                removePiece(colour.otherColour()); //If a piece of the colour cannot be removed..
            } catch (IllegalMoveException e){
                return null; //There must only be this colour on the location, so return null
            }

            return colour.otherColour(); //Else there must have been a single piece of the other colour on this location, which is returned
        }
    }

    /**
     * @param colour the colour of the piece to remove
     * @return true if and only if a piece of that colour can be removed (i.e. no IllegalMoveException)
     **/
    public boolean canRemovePiece(Colour colour){ //canAddPiece() ensures that all the constraints are satisfied, so all we need to check is if there any pieces of that colour.
        if(pieces.containsKey(colour)){
            return pieces.get(colour) > 0;
        }
        return false;
    }

    /**
     * @param colour the colour of the piece to remove
     *
     * @throws IllegalMoveException if there are no pieces of that colour int the location
     *
     **/
    public void removePiece(Colour colour) throws IllegalMoveException{
        if(!canRemovePiece(colour)){
            throw new IllegalMoveException("No Counter of this Colour at this Location!");
        } else {
            pieces.put(colour, pieces.get(colour) - 1);
        }
    }

    /**
     * @return true if and only if the Location is in a valid state depending on the number of each colour and whether or not it is a mixed location
     */
    public boolean isValid(){
        if(isMixed()){
            return true;
        } else {
            int counter = 0;
            for(int i : pieces.values()){
                if(i > 0){
                    counter++;
                }
            }
            if(counter > 1){
                return false;
            } else {
                return true;
            }
        }
    }

    public void setPieces(HashMap<Colour, Integer> pieces){
        this.pieces.putAll(pieces);
    }

    public HashMap<Colour, Integer> getPieces(){
        return this.pieces;
    }

}

