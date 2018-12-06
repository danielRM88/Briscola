package com.rosato.polimi.cardgame;

import com.rosato.polimi.cardgame.models.Card;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CardTest {
    /**
     * Test that asserts that given a string representation of a suit
     * the method returns the proper ENUM associated with it.
     */
    @Test
    public void findsCorrectSuitByStringRepresentation() {
        String batons = "B";
        String swords = "S";
        String cups = "C";
        String golds = "G";

//        The suit is found from the string representation
        Card.SUIT foundSuit = Card.SUIT.fromString(batons);
//        check to make sure it is the one we were looking for
        assertThat(foundSuit, is(Card.SUIT.BATONS));

        foundSuit = Card.SUIT.fromString(swords);
        assertThat(foundSuit, is(Card.SUIT.SWORDS));

        foundSuit = Card.SUIT.fromString(cups);
        assertThat(foundSuit, is(Card.SUIT.CUPS));

        foundSuit = Card.SUIT.fromString(golds);
        assertThat(foundSuit, is(Card.SUIT.GOLDS));
    }

    /**
     * Test that asserts that given a string representation of a value
     * the method returns the proper ENUM associated with it.
     */
    @Test
    public void findsCorrectValueByStringRepresentation() {
        String ace = "1";
        String two = "2";
        String three = "3";
        String four = "4";
        String five = "5";
        String six = "6";
        String seven = "7";
        String jack = "J";
        String horse = "H";
        String king = "K";

//        The value is found from the string representation
        Card.VALUE foundValue = Card.VALUE.fromString(ace);
//        check to make sure it is the one we were looking for
        assertThat(foundValue, is(Card.VALUE.ACE));

        foundValue = Card.VALUE.fromString(two);
        assertThat(foundValue, is(Card.VALUE.TWO));

        foundValue = Card.VALUE.fromString(three);
        assertThat(foundValue, is(Card.VALUE.THREE));

        foundValue = Card.VALUE.fromString(four);
        assertThat(foundValue, is(Card.VALUE.FOUR));

        foundValue = Card.VALUE.fromString(five);
        assertThat(foundValue, is(Card.VALUE.FIVE));

        foundValue = Card.VALUE.fromString(six);
        assertThat(foundValue, is(Card.VALUE.SIX));

        foundValue = Card.VALUE.fromString(seven);
        assertThat(foundValue, is(Card.VALUE.SEVEN));

        foundValue = Card.VALUE.fromString(jack);
        assertThat(foundValue, is(Card.VALUE.JACK));

        foundValue = Card.VALUE.fromString(horse);
        assertThat(foundValue, is(Card.VALUE.HORSE));

        foundValue = Card.VALUE.fromString(king);
        assertThat(foundValue, is(Card.VALUE.KING));
    }

    /**
     * Test that asserts that the toString method of the Card class
     * returns the proper format "{VALUE}{SUIT}". E.g. "5B" - five of Batons
     */
    @Test
    public void correctToStringFormat() {
        Card card = new Card(Card.SUIT.BATONS, Card.VALUE.FIVE);
        assertThat(card.toString(), is("5B"));

        card = new Card(Card.SUIT.GOLDS, Card.VALUE.ACE);
        assertThat(card.toString(), is("1G"));

        card = new Card(Card.SUIT.GOLDS, Card.VALUE.ACE);
        boolean comparison = card.toString().equals("1B");
        assertThat(comparison, is(false));
    }
}
