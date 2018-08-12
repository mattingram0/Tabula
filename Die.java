import java.util.Random;

/**
 * Created by matt on 17/03/2017.
 */
public class Die implements DieInterface{

    static final Random r = new Random();

    private boolean rolled;
    private int value;

    public Die(){
        this.rolled = false;
    }

    public boolean hasRolled(){
        return this.rolled;
    }

    public void roll(){
        this.value = r.nextInt(NUMBER_OF_SIDES_ON_DIE) + 1;
        this.rolled = true;
    }

    public int getValue() throws NotRolledYetException{
        if(hasRolled()){
            return this.value;
        }

        throw new NotRolledYetException("Dice Has Not Been Rolled Yet!");
    }

    public void setValue(int value){
        this.value = value;
    }

    public void setRolled(boolean rolled){
        this.rolled = rolled;
    }

    public void clear(){
        this.rolled = false;
        this.value = 0;
    }

    public void setSeed(long seed){
        r.setSeed(seed);
    }
}

