import java.util.List;
import java.util.ArrayList;

/**
 * Created by matt on 17/03/2017.
 */
public class Dice implements DiceInterface {
    private Die die1;
    private Die die2;

    public Dice() {
        die1 = new Die();
        die2 = new Die();
    }


    public boolean haveRolled() {
        return die1.hasRolled() & die2.hasRolled();
    }

    public void roll() {
        die1.roll();
        die2.roll();
    }

    public List<Integer> getValues() throws NotRolledYetException {
        int value1 = die1.getValue();
        int value2 = die2.getValue();
        List<Integer> values = new ArrayList<Integer>();

        if (value1 != value2) {
            values.add(value1);
            values.add(value2);
        } else {
            for (int i = 0; i < 4; i++) {
                values.add(value1);
            }
        }

        return values;
    }

    public void clear(){
        die1.setValue(0);
        die2.setValue(0);
    }

    public List<DieInterface> getDice(){
        List<DieInterface> dice = new ArrayList<DieInterface>();
        dice.add(die1);
        dice.add(die2);
        return dice;
    }

    public void setDice(Die d1, Die d2){
        this.die1 = d1;
        this.die2 = d2;
    }
}
