package com.rosato.polimi.cardgame.models.interfaces;

import com.rosato.polimi.cardgame.exceptions.InvalidHandException;
import com.rosato.polimi.cardgame.models.Card;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Interface that abstract whether a player is a human or an
 * AI to the main Game object.
 */
public interface Player extends Serializable {
    String getName();
    void setName(String name);
    Card getCard(int index);
    void addCardToHand(Card card) throws InvalidHandException;
    ArrayList<Card> getHand();
    void setHand(ArrayList<Card> hand) throws InvalidHandException;
    ArrayList<Card> getPile();
    void setPile(ArrayList<Card> pile);
    int calculatePoints();

    boolean isComputer();
    void addToPlayedByOpponent(Card card);
    void addToPlayedBySelf(Card card);

    void setTrumpCard(Card card);
    void setTrumpSuit(Card.SUIT suit);
}
