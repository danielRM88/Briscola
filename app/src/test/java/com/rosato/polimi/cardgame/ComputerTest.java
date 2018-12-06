package com.rosato.polimi.cardgame;

import com.rosato.polimi.cardgame.exceptions.InvalidHandException;
import com.rosato.polimi.cardgame.models.Card;
import com.rosato.polimi.cardgame.models.Computer;
import com.rosato.polimi.cardgame.models.interfaces.Player;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ComputerTest {
    /**
     * Test that asserts that given a computer player with difficulty level easy
     * it chooses next card to play randomly
     */
    @Test
    public void calculatesNextMoveRandomlyWithEasyDifficulty() {
        Player player = new Computer(Computer.EASY);
        ArrayList<Card> hand = new ArrayList<>();

        Card card1 = new Card(Card.SUIT.CUPS, Card.VALUE.ACE);
        Card card2 = new Card(Card.SUIT.GOLDS, Card.VALUE.ACE);
        Card card3 = new Card(Card.SUIT.CUPS, Card.VALUE.KING);
        hand.add(card1);
        hand.add(card2);
        hand.add(card3);

        try {
            player.setHand(hand);
        } catch (InvalidHandException e) {
            e.printStackTrace();
        }

        Card played = player.getCard(0);

        Boolean result = false;

        if (played.equals(card1) || played.equals(card2) || played.equals(card3)) {
            result = true;
        }

        assertEquals(true, result);
    }
}
