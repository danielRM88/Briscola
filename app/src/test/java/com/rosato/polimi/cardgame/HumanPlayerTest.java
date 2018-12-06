package com.rosato.polimi.cardgame;

import com.rosato.polimi.cardgame.exceptions.InvalidHandException;
import com.rosato.polimi.cardgame.models.Card;
import com.rosato.polimi.cardgame.models.HumanPlayer;
import com.rosato.polimi.cardgame.models.interfaces.Player;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class HumanPlayerTest {
    /**
     * Test that asserts that a player cannot have more than three cards at a given moment
     * in his hand.
     */
    @Test
    public void handWithNoMoreThanThreeCards() {
        Player player = new HumanPlayer();

        boolean exceptionRaised = false;

        try {
//            We add three cards to the hand of the player
            player.addCardToHand(new Card(Card.SUIT.BATONS, Card.VALUE.ACE));
            player.addCardToHand(new Card(Card.SUIT.GOLDS, Card.VALUE.ACE));
            player.addCardToHand(new Card(Card.SUIT.CUPS, Card.VALUE.ACE));
        } catch (InvalidHandException e) {
            exceptionRaised = true;
        }

//        there should not have been any issues
        assertEquals(exceptionRaised, false);
        exceptionRaised = false;

        try {
//            we add the fourth card to his hand
            player.addCardToHand(new Card(Card.SUIT.BATONS, Card.VALUE.THREE));
        } catch (InvalidHandException e) {
            exceptionRaised = true;
        }

//        an exception must have been raised
        assertEquals(exceptionRaised, true);
        exceptionRaised = false;

        player = new HumanPlayer();
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(Card.SUIT.GOLDS, Card.VALUE.ACE));
        cards.add(new Card(Card.SUIT.BATONS, Card.VALUE.TWO));
        cards.add(new Card(Card.SUIT.SWORDS, Card.VALUE.THREE));

        try {
//            three cards are added again to the hand of the new player but through
//            a different method
            player.setHand(cards);
        } catch (InvalidHandException e) {
            exceptionRaised = true;
        }

//        there should not have been any issues
        assertEquals(exceptionRaised, false);
        exceptionRaised = false;

//        add the fourth card
        cards.add(new Card(Card.SUIT.SWORDS, Card.VALUE.ACE));

        try {
            player.setHand(cards);
        } catch (InvalidHandException e) {
            exceptionRaised = true;
        }

//        an exception should have been raised
        assertEquals(exceptionRaised, true);
    }

    /**
     * Test that asserts that the amount of points won by the user
     * is correctly calculated from the pile of the user based on the
     * point value of the cards in his pile.
     */
    @Test
    public void calculatesPoints() {
        Player player = new HumanPlayer();
        ArrayList<Card> pile = new ArrayList<>();
//        given a pile of cards won by the player
//        ACE -> 11, KING -> 4
        pile.add(new Card(Card.SUIT.SWORDS, Card.VALUE.ACE));
        pile.add(new Card(Card.SUIT.GOLDS, Card.VALUE.ACE));
        pile.add(new Card(Card.SUIT.SWORDS, Card.VALUE.KING));
        player.setPile(pile);
//        The amount of points won should match
        assertEquals(26, player.calculatePoints());

        pile = new ArrayList<>();
//        given a pile of cards won by the player
//        SEVEN -> 0, THREE -> 10
        pile.add(new Card(Card.SUIT.BATONS, Card.VALUE.THREE));
        pile.add(new Card(Card.SUIT.CUPS, Card.VALUE.SEVEN));
        player.setPile(pile);
//        The amount of points won should match
        assertEquals(10, player.calculatePoints());
    }
}
