package com.rosato.polimi.cardgame.models;

import com.rosato.polimi.cardgame.exceptions.InvalidHandException;
import com.rosato.polimi.cardgame.models.interfaces.Player;

import java.util.ArrayList;

public class HumanPlayer implements Player {
    private String name;
    private ArrayList<Card> hand;
    private ArrayList<Card> pile;

    public HumanPlayer() {
        this.hand = new ArrayList<>();
        this.pile = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Card getCard(int index) {
        Card card = null;
        if (!hand.isEmpty()) {
            if (hand.size() <= index) {
                index = hand.size() - 1;
            }
            card = hand.remove(index);
        }

        return card;
    }

    public void addCardToHand(Card card) throws InvalidHandException {
        if (hand.size() == 3) {
            throw new InvalidHandException("Player cannot have more than 3 cards on hand");
        }
        hand.add(card);
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) throws InvalidHandException {
        if (hand.size() > 3) {
            throw new InvalidHandException("Player cannot have more than 3 cards on hand");
        }
        this.hand = hand;
    }

    public ArrayList<Card> getPile() {
        return pile;
    }

    public void setPile(ArrayList<Card> pile) {
        this.pile = pile;
    }

    /**
     * Method that calculates how many points are in the pile of the
     * player.
     *
     * @return total points accumulated by player
     */
    public int calculatePoints() {
        int points = 0;

        if (pile != null) {
            for(Card c : pile) {
                points += c.getValue().getPointValue();
            }
        }

        return points;
    }

    @Override
    public boolean isComputer() {
        return false;
    }

    @Override
    public void addToPlayedByOpponent(Card card) {
//        used by computer
    }

    @Override
    public void addToPlayedBySelf(Card card) {
//       used by computer
    }

    @Override
    public void setTrumpCard(Card card) {
//        used by computer
    }

    @Override
    public void setTrumpSuit(Card.SUIT suit) {
//        used by computer
    }
}
