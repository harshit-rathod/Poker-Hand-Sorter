
import java.util.Comparator;

class CardComparator implements Comparator<Card> {

    @Override
    public int compare(Card o1, Card o2) {
        if(o1.getValue() > o2.getValue())
            return 1 ;
        else if(o1.getValue() < o2.getValue())
            return -1;
        else 
            return 0;
    }

}
