import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by matt on 23/03/2017.
 */
public class TurnComparator implements Comparator<Turn>, Serializable { //Just in case, implement serializable
    @Override
    public int compare(Turn t1, Turn t2){
        if(t1.getTotalPoints() == t2.getTotalPoints()){ //I've defined one turn to be better than the other if it has more points than the other, if they have the same points
            if(t1.getTotalDistance() == t2.getTotalDistance()){ //Check their distances, if they have the same distance too
                return t1.getTotalPoints(); //Simply return the first turn
            } else { //If they have the same points but different distances
                return t1.getTotalDistance() - t2.getTotalDistance();  //Return the turn that moves the most distance forward
            }
        } else { //If they have different points
            return t1.getTotalPoints() - t2.getTotalPoints(); //Return the turn with the greatest number of points
        }
    }
}
