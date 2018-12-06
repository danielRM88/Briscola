package com.rosato.polimi.cardgame;

import com.rosato.polimi.cardgame.exceptions.InconsistentNumberOfCardsException;
import com.rosato.polimi.cardgame.exceptions.InconsistentTrumpSuitException;
import com.rosato.polimi.cardgame.exceptions.InvalidHandException;
import com.rosato.polimi.cardgame.models.Card;
import com.rosato.polimi.cardgame.models.Game;
import com.rosato.polimi.cardgame.models.HumanPlayer;
import com.rosato.polimi.cardgame.models.interfaces.Player;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class GameTest {
    /**
     * Test that asserts that given a list of cards, it is consistent and forms a deck 40
     * with their respective suits and values.
     */
    @Test
    public void deckIsConsistent() {
        ArrayList<Card> deck = new ArrayList<>();
        for (Card.SUIT suit : Card.SUIT.values()) {
            for (Card.VALUE value : Card.VALUE.values()) {
                deck.add(new Card(suit, value));
            }
        }

//        The deck is consistent, it contains 40 cards with their respective suits and values
        assertEquals(true, Game.isDeckConsistent(deck));
        /******************/

        Card.SUIT[] suits = new Card.SUIT[2];
        suits[0] = Card.SUIT.BATONS;
        suits[1] = Card.SUIT.GOLDS;

        deck = new ArrayList<>();
        for (Card.SUIT suit : suits) {
            for (Card.VALUE value : Card.VALUE.values()) {
                deck.add(new Card(suit, value));
            }
        }

//        The deck does not contain all cards needed to play only suits (BATONS, GOLDS)
        assertEquals(false, Game.isDeckConsistent(deck));
        /******************/

        suits = new Card.SUIT[5];
        suits[0] = Card.SUIT.BATONS;
        suits[1] = Card.SUIT.GOLDS;
        suits[2] = Card.SUIT.CUPS;
        suits[3] = Card.SUIT.SWORDS;
        suits[4] = Card.SUIT.SWORDS;

        deck = new ArrayList<>();
        for (Card.SUIT suit : suits) {
            for (Card.VALUE value : Card.VALUE.values()) {
                deck.add(new Card(suit, value));
            }
        }

//        The deck contains more cards than needed to play
        assertEquals(false, Game.isDeckConsistent(deck));
        /******************/

        suits = new Card.SUIT[5];
        suits[0] = Card.SUIT.BATONS;
        suits[1] = Card.SUIT.GOLDS;
        suits[3] = Card.SUIT.SWORDS;
        suits[4] = Card.SUIT.SWORDS;

        deck = new ArrayList<>();
        for (Card.SUIT suit : suits) {
            for (Card.VALUE value : Card.VALUE.values()) {
                deck.add(new Card(suit, value));
            }
        }

//        The deck contains the same suit twice
        assertEquals(false, Game.isDeckConsistent(deck));
        /******************/

        Card.VALUE[] values = new Card.VALUE[1];
        values[0] = Card.VALUE.ACE;

        deck = new ArrayList<>();
        for (Card.SUIT suit : Card.SUIT.values()) {
            for (Card.VALUE value : values) {
                deck.add(new Card(suit, value));
            }
        }

//        The deck only contains aces
        assertEquals(false, Game.isDeckConsistent(deck));
        /******************/

        values = new Card.VALUE[10];
        values[0] = Card.VALUE.ACE;
        values[1] = Card.VALUE.TWO;
        values[2] = Card.VALUE.THREE;
        values[3] = Card.VALUE.FOUR;
        values[4] = Card.VALUE.FIVE;
        values[5] = Card.VALUE.SIX;
        values[6] = Card.VALUE.SEVEN;
        values[7] = Card.VALUE.JACK;
        values[8] = Card.VALUE.HORSE;
        values[9] = Card.VALUE.HORSE;

        deck = new ArrayList<>();
        for (Card.SUIT suit : Card.SUIT.values()) {
            for (Card.VALUE value : values) {
                deck.add(new Card(suit, value));
            }
        }

//        The deck only contains two horse for each suit
        assertEquals(false, Game.isDeckConsistent(deck));
    }

    /**
     * Test that asserts that all cards in game form a 40 card deck with all the
     * corresponding suits and values
     */
    @Test
    public void gameIsConsistent() {
        Game game = null;
        ArrayList<Card> deck = new ArrayList<>();

        for (Card.SUIT suit : Card.SUIT.values()) {
            for (Card.VALUE value : Card.VALUE.values()) {
                deck.add(new Card(suit, value));
            }
        }

        boolean result = false;
        try {
            game = new Game("0",
                            new HumanPlayer(),
                            new HumanPlayer(),
                            deck.get(deck.size()-1).getSuit(),
                            deck,
                            new ArrayList<Card>());
            result = game.isGameConsistent();
        } catch (InconsistentNumberOfCardsException | InconsistentTrumpSuitException e) {
            result = false;
        }

//        Check that all the cards in play form a proper deck. Game is consistent (40 cards in deck)
        assertEquals(true, result);
        /***************************/

        Player player1 = new HumanPlayer();
        result = false;
        try {
            player1.addCardToHand(new Card(Card.SUIT.CUPS, Card.VALUE.ACE));
            game = new Game("1",
                    player1,
                    new HumanPlayer(),
                    deck.get(deck.size()-1).getSuit(),
                    deck,
                    new ArrayList<Card>());
            result = game.isGameConsistent();
        } catch (InconsistentNumberOfCardsException | InconsistentTrumpSuitException | InvalidHandException e) {
            result = false;
        }

//        There are 41 cards in play now (40 deck + 1 in player1 hand). Game is not consistent
        assertEquals(false, result);
        /***************************/

        player1 = new HumanPlayer();
        result = false;
        try {
            player1.addCardToHand(new Card(Card.SUIT.CUPS, Card.VALUE.ACE));
//            Removed one cards from deck. "1B"
            deck.remove(0);
            game = new Game("0",
                    player1,
                    new HumanPlayer(),
                    deck.get(deck.size()-1).getSuit(),
                    deck,
                    new ArrayList<Card>());
            result = game.isGameConsistent();
        } catch (InconsistentNumberOfCardsException | InconsistentTrumpSuitException | InvalidHandException e) {
            result = false;
        }

//        There are 40 cards in play now (39 deck + 1 in player1 hand). Game is not consistent because does not
//        have all the corresponding suits and values. It's missing "1B"
        assertEquals(false, result);
    }

    /**
     * Test that asserts that the game is initialized correctly according
     * to the rules.
     */
    @Test
    public void correctlyInitializesTheGame() {
        Game game = new Game();
        try {
            game.initGame();
        } catch (InvalidHandException | InconsistentNumberOfCardsException e) {
            e.printStackTrace();
        }

        ArrayList<Card> deck = game.getDeck();
        ArrayList<Card> player1Hand = game.getPlayer1().getHand();
        ArrayList<Card> player2Hand = game.getPlayer2().getHand();
        ArrayList<Card> surfaceCards = game.getSurfaceCards();

//        The deck should contain 34 cards after the initial setup
//        it includes the briscola which is the last card of the deck and
//        is facing up
        assertEquals(true, deck.size() == 34);
//        Each player should have three cards
        assertEquals(true, player1Hand.size() == 3);
//        Each player should have three cards
        assertEquals(true, player2Hand.size() == 3);
//        There should not be any cards on the surface
        assertEquals(true, surfaceCards.size() == 0);

//        We make sure that the trump suit is equals to the last card of the deck (which is facing up).
        Card.SUIT trumpSuit = game.getTrumpSuit();
        assertThat(trumpSuit, is(deck.get(deck.size() - 1).getSuit()));

//        Check to see that the cards in play are consistent
        assertEquals(true, surfaceCards.size() == 0);

//        The amount of cards in play should form a proper deck
        assertEquals(true, game.isGameConsistent());
    }

    /**
     * Test that asserts which card would win taken into consideration
     * point value, trump card and number value.
     */
    @Test
    public void calculateWinner() {
        Card card1;
        Card card2;
        Card.SUIT suit;

//        Create two cards, one with the trump suit
        card1 = new Card(Card.SUIT.SWORDS, Card.VALUE.ACE);
        card2 = new Card(Card.SUIT.CUPS, Card.VALUE.TWO);
        suit = Card.SUIT.CUPS;
        int winner = Game.calculateWinner(card1, card2, suit);

//        card with the trump suit should win regardless of lower point value
        assertEquals(1, winner);

        card1 = new Card(Card.SUIT.SWORDS, Card.VALUE.ACE);
        card2 = new Card(Card.SUIT.SWORDS, Card.VALUE.TWO);
        suit = Card.SUIT.CUPS;
        winner = Game.calculateWinner(card1, card2, suit);

//        card with highest point value should win
        assertEquals(-1, winner);

        card1 = new Card(Card.SUIT.GOLDS, Card.VALUE.FOUR);
        card2 = new Card(Card.SUIT.SWORDS, Card.VALUE.TWO);
        suit = Card.SUIT.CUPS;
        winner = Game.calculateWinner(card1, card2, suit);

//        card with highest number should win
        assertEquals(-1, winner);

        card1 = new Card(Card.SUIT.CUPS, Card.VALUE.FOUR);
        card2 = new Card(Card.SUIT.CUPS, Card.VALUE.TWO);
        suit = Card.SUIT.CUPS;
        winner = Game.calculateWinner(card1, card2, suit);

//        card with higher number should win
        assertEquals(-1, winner);

        card1 = new Card(Card.SUIT.CUPS, Card.VALUE.KING);
        card2 = new Card(Card.SUIT.CUPS, Card.VALUE.ACE);
        suit = Card.SUIT.CUPS;
        winner = Game.calculateWinner(card1, card2, suit);

//        card with higher point value should win
        assertEquals(1, winner);

        card1 = new Card(Card.SUIT.CUPS, Card.VALUE.ACE);
        card2 = new Card(Card.SUIT.CUPS, Card.VALUE.ACE);
        suit = Card.SUIT.CUPS;
        winner = Game.calculateWinner(card1, card2, suit);

//        cards are the same
        assertEquals(0, winner);
    }

    /**
     * Test that asserts that the proper winner for a round is selected
     * after both players place cards on the surface
     */
    @Test
    public void checkForRoundWinner() {
        Game game = null;
        ArrayList<Card> deck = new ArrayList<>();

        for (Card.SUIT suit : Card.SUIT.values()) {
            for (Card.VALUE value : Card.VALUE.values()) {
                deck.add(new Card(suit, value));
            }
        }

        boolean result = false;
        ArrayList<Card> surfaceCards = new ArrayList<>();
        Player player1 = new HumanPlayer();
        Player player2 = new HumanPlayer();
        try {
            Card card = deck.remove(deck.indexOf(new Card(Card.SUIT.CUPS, Card.VALUE.ACE)));
            card.setPlayedBy(player2.getName());
            surfaceCards.add(card);

            card = deck.remove(deck.indexOf(new Card(Card.SUIT.CUPS, Card.VALUE.KING)));
            card.setPlayedBy(player1.getName());
            surfaceCards.add(card);

            game = new Game("0",
                            player1,
                            player2,
                            deck.get(deck.size()-1).getSuit(),
                            deck,
                            surfaceCards);
            result = game.isGameConsistent();
        } catch (InconsistentNumberOfCardsException | InconsistentTrumpSuitException e) {
            result = false;
        }

//        Each card has a reference to the player that played it in the surface.
//        E.g. player2 -> "1C"
//             player1 -> "KC"
//        Therefore it selects player2 as winner
        Player winner = game.checkForRoundWinner();
        assertThat(winner, is(player2));
    }

    /**
     * Test that asserts that the players draw cards from the deck accordingly to the
     * game's rules
     */
    @Test
    public void drawRoundCards() {
        Game game = null;
        ArrayList<Card> deck = new ArrayList<>();

        for (Card.SUIT suit : Card.SUIT.values()) {
            for (Card.VALUE value : Card.VALUE.values()) {
                deck.add(new Card(suit, value));
            }
        }

        boolean result = false;
        ArrayList<Card> surfaceCards = new ArrayList<>();
        Player player1 = new HumanPlayer();
        Player player2 = new HumanPlayer();
        try {
//            Two cards are added to each player
            Card card = deck.remove(deck.indexOf(new Card(Card.SUIT.CUPS, Card.VALUE.ACE)));
            player1.addCardToHand(card);
            card = deck.remove(deck.indexOf(new Card(Card.SUIT.GOLDS, Card.VALUE.KING)));
            player1.addCardToHand(card);

            card = deck.remove(deck.indexOf(new Card(Card.SUIT.SWORDS, Card.VALUE.ACE)));
            player2.addCardToHand(card);
            card = deck.remove(deck.indexOf(new Card(Card.SUIT.BATONS, Card.VALUE.KING)));
            player2.addCardToHand(card);

//            no cards are on the surface, meaning that it's a new round
            game = new Game("0",
                    player1,
                    player2,
                    deck.get(deck.size()-1).getSuit(),
                    deck,
                    surfaceCards);
            result = game.isGameConsistent();

//            each players should grab one card
            game.drawRoundCards();

//            each player should have three cards after drawing from the deck
            if(player1.getHand().size() == 3 && player2.getHand().size() == 3 && deck.size() == 34) {
                result = true;
            } else {
                result = false;
            }
        } catch (InconsistentNumberOfCardsException | InconsistentTrumpSuitException | InvalidHandException e) {
            result = false;
        }

        assertEquals(true, result);
    }

    /**
     * Test that asserts that the method move works appropriately and follows the flow
     * of the game.
     */
    @Test
    public void move() {
        Game game = null;
        boolean result = false;
        boolean success = false;
        try {
            game = new Game();
            game.initGame();

            game.move(1);
//            Player one should have played his first card into the surface cards
            if(game.getPlayer1().getHand().size() == 2 && game.getSurfaceCards().size() == 1) {
                result = true;
            }
            assertEquals(true, result);

            result = false;
            game.move(1);
//            Player two should have played his first card into the surface cards. A winner for the
//            round is calculated and the surface cards are removed and placed into the winners pile.
            if(game.getPlayer2().getHand().size() == 3 &&
               (game.getPlayer2().getPile().size() + game.getPlayer1().getPile().size()) == 2 &&
               game.getSurfaceCards().size() == 0) {
                result = true;
            }
            assertEquals(true, result);

            result = false;
            Player currentPlayer = game.getCurrentPlayer();
            game.move(0);
//            current player should have played his first card into the surface cards
            if(currentPlayer.getHand().size() == 2 && game.getSurfaceCards().size() == 1) {
                result = true;
            }
            assertEquals(true, result);

            result = false;
            currentPlayer = game.getCurrentPlayer();
//            getOtherPlayer() returns the player that is not the current player
            Player otherPlayer = game.getOtherPlayer();
            game.move(1);
//            current player should have played his card into the surface cards. A winner for the
//            round is calculated and the surface cards are removed and placed into the winners pile.
            if(currentPlayer .getHand().size() == 3 &&
               (currentPlayer.getPile().size() + otherPlayer.getPile().size()) == 4 &&
               game.getSurfaceCards().size() == 0) {
                result = true;
            }
            assertEquals(true, result);

            success = true;
        } catch (InconsistentNumberOfCardsException | InvalidHandException e) {
            success = false;
        }

        assertEquals(true, success);
    }
}
