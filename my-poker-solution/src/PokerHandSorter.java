
import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class PokerHandSorter {

    private Scanner input;
    private ArrayList<Card> hand1;
    private ArrayList<Card> hand2;
    
    // to simplify the conversion process between characters and integers
    private Map<String, Integer> cardMap = new HashMap<String, Integer>();

    public PokerHandSorter() throws FileNotFoundException {

        input = new Scanner(PokerHandSorter.class.getResourceAsStream("poker-hands.txt"));

        for (int i = 1; i < 10; i++) {
            cardMap.put("" + i, i);
        }

        cardMap.put("T", 10);
        cardMap.put("J", 11);
        cardMap.put("Q", 12);
        cardMap.put("K", 13);
        cardMap.put("A", 14);
    }

    public void readFileAndFindWinner() {
        int player1 = 0;
        int player2 = 0;

        try {
            while (input.hasNextLine()) {
                int i = 0;

                hand1 = new ArrayList<Card>();
                hand2 = new ArrayList<Card>();

                while (i < 10) {
                    String card = input.next();
                    if (i < 5) {
                        //reading input and creating card object
                        hand1.add(new Card(card.substring(card.length() - 1), cardMap.get(card.substring(0, card.length() - 1))));
                    } else {
                        hand2.add(new Card(card.substring(card.length() - 1), cardMap.get(card.substring(0, card.length() - 1))));
                    }
                    i++;
                }

                Result h1 = dynamicLogicFinder(hand1);

                Result h2 = dynamicLogicFinder(hand2);

                // if player one has higher rank
                if (h1.getPriority() > h2.getPriority()) {
                    player1++;
                } else if (h1.getPriority() < h2.getPriority()) { // if player two has higher rank
                    player2++;
                } else {
                    // if both player have same rank then compare based on card value 
                    if (h1.getCard() > h2.getCard()) {
                        player1++;
                    } else if (h1.getCard() < h2.getCard()) {
                        player2++;
                    } else {
                         // if both player have same rank and card value then compare based on highest value card
                        int temp = returnHighestCard(hand1, hand2);
                        if (temp == 1) {
                            player1++;
                        } else if (temp == 2) {
                            player2++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        
        File outFile = new File("output.txt");
        PrintWriter out = null;
        try {
            out = new PrintWriter(outFile);
            out.println("Player 1: " + player1 + " hands \nPlayer 2: " + player2 +" hands");
        } catch (FileNotFoundException ex) {
            System.out.println("Fine not found");
        } finally {
            out.close();
            input.close();
        }

        System.out.println("Player 1: " + player1 + " hands \nPlayer 2: " + player2 +" hands");
    }

    public int returnHighestCard(ArrayList<Card> list1, ArrayList<Card> list2) {

        //sorting array and reversing the order highest to lowest
        Collections.sort(list1, new CardComparator());
        Collections.reverse(list1);
        
        //sorting array and reversing the order highest to lowest
        Collections.sort(list2, new CardComparator());
        Collections.reverse(list2);

        //comparing element if found greater then returning player
        for (int i = 0; i <= 4; i++) {
            if (list1.get(i).getValue() > list2.get(i).getValue()) {
                return 1;
            } else if (list1.get(i).getValue() < list2.get(i).getValue()) {
                return 2;
            }
        }
        
        // if both player have same card then 
        return 0;
    }

    public Result dynamicLogicFinder(ArrayList<Card> cards) {
        Result obj = new Result();

        // checking from highest to lowest priority 
        
        if (isRoyalFlush(cards)) {
            obj.setPriority(10);
            return obj;
        } else if (isStraightFlush(cards)) {
            obj.setPriority(9);
            return obj;
        } else if (obj.setCard(findFourOfAKind(cards)) != -1) {
            obj.setPriority(8);
            return obj;
        } else if (obj.setCard(findFullHouse(cards)) != -1) {
            obj.setPriority(7);
            return obj;
        } else if (isFlush(cards)) {
            obj.setPriority(6);
            return obj;
        } else if (isStraight(cards)) {
            obj.setPriority(5);
            return obj;
        } else if (obj.setCard(findThreeOfAKind(cards)) != -1) {
            obj.setPriority(4);
            return obj;
        } else if (obj.setCard(findTwoPairs(cards)) != -1) {
            obj.setPriority(3);
            return obj;
        } else if (obj.setCard(findPairs(cards)) != -1) {
            obj.setPriority(2);
            return obj;
        } else if (obj.setCard(findHighCard(cards)) != -1) {
            obj.setPriority(1);
            return obj;
        }

        // returning result object
        return obj;
    }

    public int findFourOfAKind(ArrayList<Card> cards) {

        Map<Integer, Integer> hm = new HashMap<Integer, Integer>();

         //generate map for each card frequency 
        for (int i = 0; i < cards.size(); i++) {
            if (hm.containsKey(cards.get(i).getValue())) {
                hm.put(cards.get(i).getValue(), hm.get(cards.get(i).getValue()) + 1);
            } else {
                hm.put(cards.get(i).getValue(), 1);
            }
        }

        // retuning 4 frequency card
        for (Map.Entry<Integer, Integer> entry : hm.entrySet()) {
            if (entry.getValue() == 4) {
                return entry.getKey();
            }
        }

        return -1;
    }

    public int findThreeOfAKind(ArrayList<Card> cards) {

        Map<Integer, Integer> hm = new HashMap<Integer, Integer>();

        //generate map for each card frequency 
        for (int i = 0; i < cards.size(); i++) {
            if (hm.containsKey(cards.get(i).getValue())) {
                hm.put(cards.get(i).getValue(), hm.get(cards.get(i).getValue()) + 1);
            } else {
                hm.put(cards.get(i).getValue(), 1);
            }
        }

        // retuning 3 frequency card
        for (Map.Entry<Integer, Integer> entry : hm.entrySet()) {
            if (entry.getValue() == 3) {
                return entry.getKey();
            }
        }

        return -1;
    }

    public int findHighCard(ArrayList<Card> cards) {
        // returning highest card value
        Collections.sort(cards, new CardComparator());
        return cards.get(4).getValue();
    }

    public int findPairs(ArrayList<Card> cards) {

        Map<Integer, Integer> hm = new HashMap<Integer, Integer>();

        //generate map for each card frequency 
        for (int i = 0; i < cards.size(); i++) {
            if (hm.containsKey(cards.get(i).getValue())) {
                hm.put(cards.get(i).getValue(), hm.get(cards.get(i).getValue()) + 1);
            } else {
                hm.put(cards.get(i).getValue(), 1);
            }
        }

        //retuning pair card value
        for (Map.Entry<Integer, Integer> entry : hm.entrySet()) {
            if (entry.getValue() == 2) {
                return entry.getKey();
            }
        }

        return -1;
    }

    public int findTwoPairs(ArrayList<Card> cards) {

        Map<Integer, Integer> hm = new HashMap<Integer, Integer>();

        //generate map for each card frequency 
        for (int i = 0; i < cards.size(); i++) {
            if (hm.containsKey(cards.get(i).getValue())) {
                hm.put(cards.get(i).getValue(), hm.get(cards.get(i).getValue()) + 1);
            } else {
                hm.put(cards.get(i).getValue(), 1);
            }
        }

        // we have two card with 2 frequency and another card with one so size must be 3
        if (hm.size() != 3) {
            return -1;
        }

        int higestPair = -999;

        // returning higest frequency card
        for (Map.Entry<Integer, Integer> entry : hm.entrySet()) {
            if (entry.getValue() == 2) {
                if (higestPair < entry.getKey()) {
                    higestPair = entry.getKey();
                }
            }
        }

        return higestPair;
    }

    public boolean isStraight(ArrayList<Card> cards) {
        int number = 0;

        Collections.sort(cards, new CardComparator());

        // check if all cards belong to same suit and Ten, Jack, Queen, King and Ace
        for (int i = 0; i < cards.size(); i++) {

            if (i == 0) {
                number = cards.get(i).getValue();
                continue;
            }

            // check if difference is 1
            if (((Math.abs(cards.get(i).getValue() - number)) != 1)) {
                return false;
            }

            number += 1;
        }

        return true;
    }

    public int findFullHouse(ArrayList<Card> cards) {

        Map<Integer, Integer> hm = new HashMap<Integer, Integer>();

        //generate map for each card frequency 
        for (int i = 0; i < cards.size(); i++) {
            if (hm.containsKey(cards.get(i).getValue())) {
                hm.put(cards.get(i).getValue(), hm.get(cards.get(i).getValue()) + 1);
            } else {
                hm.put(cards.get(i).getValue(), 1);
            }
        }

        // we have two differnt frequency so size must be 2
        if (hm.size() != 2) {
            return -1;
        }

        // returning higest frequency card
        if (hm.get(hm.keySet().toArray()[0]) == 2 && hm.get(hm.keySet().toArray()[1]) == 3) {
            return (int) hm.keySet().toArray()[1];
        } else {
            return (int) hm.keySet().toArray()[0];
        }
    }

    public boolean isRoyalFlush(ArrayList<Card> cards) {
        String suit = "";

        // check if all cards belong to same suit and Ten, Jack, Queen, King and Ace
        for (int i = 0; i < cards.size(); i++) {

            if (i == 0) {
                suit = cards.get(i).getSuit();
                continue;
            }

            // check if suit is same and values are Ten, Jack, Queen, King and Ace
            if (cards.get(i).getSuit().indexOf(suit) == -1 || ((cards.get(i).getValue() - 10) < 0)) {
                return false;
            }

        }

        return true;
    }

    public boolean isStraightFlush(ArrayList<Card> cards) {
        String suit = "";
        int number = 0;

        Collections.sort(cards, new CardComparator());

        // check if all cards belong to same suit and Ten, Jack, Queen, King and Ace
        for (int i = 0; i < cards.size(); i++) {

            if (i == 0) {
                suit = cards.get(i).getSuit();
                number = cards.get(i).getValue();
                continue;
            }

            // check if suit is same and difference is 1
            if (cards.get(i).getSuit().indexOf(suit) == -1 || ((Math.abs(cards.get(i).getValue() - number)) != 1)) {
                return false;
            }

            number += 1;
        }

        return true;
    }

    public boolean isFlush(ArrayList<Card> cards) {
        String suit = "";

        // check if all cards belong to same suit
        for (int i = 0; i < cards.size(); i++) {

            if (i == 0) {
                suit = cards.get(i).getSuit();
                continue;
            }

            if (cards.get(i).getSuit().indexOf(suit) == -1) {
                return false;
            }

        }

        return true;
    }

    public static void main(String[] args) {
        try {
            PokerHandSorter phs = new PokerHandSorter();
            phs.readFileAndFindWinner();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
