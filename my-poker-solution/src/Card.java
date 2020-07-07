
class Card {

    public Card(String suit, int value) {
        this.suit = suit;
        this.value = value;
    }

    private String suit;
    private int value = -1;

    public String getSuit() {
        return suit;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "" + this.value + this.suit;
    }

}
