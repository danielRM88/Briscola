package com.rosato.polimi.cardgame.models;

import com.rosato.polimi.cardgame.exceptions.InconsistentNumberOfCardsException;
import com.rosato.polimi.cardgame.exceptions.InconsistentTrumpSuitException;
import com.rosato.polimi.cardgame.exceptions.InvalidHandException;
import com.rosato.polimi.cardgame.models.interfaces.Player;

import java.util.ArrayList;

/**
 * Class in charge of restoring a Game from a configuration String
 * Also in charge of serializing a Game state to a String
 */
public class StateManager {
    /**
     * Method that transforms a string configuration of the game into
     * a full game object
     *
     * @param config string with the data for restoring the game
     * @return game object with all the configuration
     */
    public static Game restoreConfiguration(String config) throws InconsistentNumberOfCardsException, InconsistentTrumpSuitException, InvalidHandException {
        ArrayList<Card> cards;

        String deck = "";
        String surface = "";
        String cardsPlayer1 = "";
        String cardsPlayer2 = "";
        String pile1 = "";
        String pile2 = "";

//        We split on '.'
        String[] states = config.split("\\.");
//        first character is current player
        String player = states[0].substring(0, 1);
//        second character is trump suit
        String trump = states[0].substring(1, 2);
        Card.SUIT trumpSUIT = Card.SUIT.fromString(trump);
//        from second character onwards is the deck
        deck = states[0].substring(2);

//        if there are any surface cards
        if(states.length > 1) {
            surface = states[1];
        }

//        if there are any cards on the players hand
        if(states.length > 2) {
            cardsPlayer1 = states[2];
        }

//        if there are any cards on the players hand
        if(states.length > 3) {
            cardsPlayer2 = states[3];
        }

//        if there are any cards on the players pile
        if(states.length > 4) {
            pile1 = states[4];
        }

//        if there are any cards on the players pile
        if(states.length > 5) {
            pile2 = states[5];
        }

        Game g = null;
        ArrayList<Card> surfaceCards;

        Player player1 = new HumanPlayer();
//        create list of cards from string
        cards = StateManager.deserializeCards(cardsPlayer1);
//        set player1 hand
        player1.setHand(cards);
//        create list of cards from string
        cards = StateManager.deserializeCards(pile1);
//        set player1 pile
        player1.setPile(cards);

        Player player2 = new HumanPlayer();
//        create list of cards from string
        cards = StateManager.deserializeCards(cardsPlayer2);
//        set player2 hand
        player2.setHand(cards);
//        create list of cards from string
        cards = StateManager.deserializeCards(pile2);
//        set player2 pile
        player2.setPile(cards);

//        create list of cards from string, this will be the deck
        cards = StateManager.deserializeCards(deck);

//        deserialize the surface cards
        surfaceCards = StateManager.deserializeCards(surface);

//        Card trumpCard;
//        if(deck.isEmpty()) {
//            Player currentPlayer;
//            Player otherPlayer;
//            if(player.equals("0")) {
//                currentPlayer = player1;
//                otherPlayer = player2;
//            } else {
//                currentPlayer = player2;
//                otherPlayer = player1;
//            }
//
//            if (surfaceCards.isEmpty()) {
//                trumpCard = otherPlayer.getHand().get(otherPlayer.getHand().size() - 1);
//            } else {
//                trumpCard = currentPlayer.getHand().get(otherPlayer.getHand().size() - 1);
//            }
//        } else {
////           last card is the trump card
//            trumpCard = cards.get(cards.size() - 1);
//        }
//        trumpCard.setTrumpCard(true);


//        create the game object with all the configuration
        g = new Game(player,
                player1,
                player2,
                trumpSUIT,
                cards,
                surfaceCards);

        return g;
    }

    /**
     * Method that takes a string configuration of cards
     * and returns a list
     *
     * @param stringOfCards string configuration of cards
     * @return array list of cards
     */
    public static ArrayList<Card> deserializeCards(String stringOfCards) {
        ArrayList<Card> cards = new ArrayList<>();

        if(!stringOfCards.isEmpty()) {
            Card.SUIT SUIT;
            Card.VALUE value;
            String[] sCard = StateManager.splitByNumber(stringOfCards, 2);//stringOfCards.split("(?<=\\G.{2})");
            for (String c : sCard) {
                SUIT = Card.SUIT.fromString(c.substring(1, 2));
                value = Card.VALUE.fromString(c.substring(0, 1));
                cards.add(new Card(SUIT, value));
            }
        }

        return cards;
    }

    public static String[] splitByNumber(String text, int size) {
        if (text == null || size == 0) {
            return null;
        }

        int chunks = text.length() / size + (text.length() % size > 0 ? 1 : 0);

        String[] arr = new String[chunks];

        for(int i=0, j=0, l=text.length(); i<l; i+=size, j++) {
            arr[j] = text.substring(i, Math.min(l, i+size));
        }

        return arr;
    }

    /**
     * Method that serializes a game object into the configuration string
     *
     * @param game game object
     * @return configuration string
     */
    public static String serialize(Game game) {
        StringBuilder builder = new StringBuilder();

        builder.append(game.getCurrentPlayerString());
        String trumpSuit;

        if (game.getTrumpSuit() != null) {
            trumpSuit = game.getTrumpSuit().getId();
        } else {
            trumpSuit = "";
        }

        builder.append(trumpSuit);

        ArrayList<Card> deck = game.getDeck();
        for (Card c : deck) {
            builder.append(c.toString());
        }
        builder.append(".");

//        Surface cards
        for (Card c : game.getSurfaceCards()) {
            builder.append(c.toString());
        }
        builder.append(".");

//        player 1 hand
        ArrayList<Card> player1Hand = game.getPlayer1().getHand();
        for (Card c : player1Hand) {
            builder.append(c.toString());
        }
        builder.append(".");

//        player 2 hand
        ArrayList<Card> player2Hand = game.getPlayer2().getHand();
        for (Card c : player2Hand) {
            builder.append(c.toString());
        }
        builder.append(".");

//        player 1 pile
        ArrayList<Card> player1Pile = game.getPlayer1().getPile();
        for (Card c : player1Pile) {
            builder.append(c.toString());
        }
        builder.append(".");

//        player 1 pile
        ArrayList<Card> player2Pile = game.getPlayer2().getPile();
        for (Card c : player2Pile) {
            builder.append(c.toString());
        }

        return builder.toString();
    }

    /**
     * Method that restores a Game from a configuration String and
     * performs a series of moves in the game
     *
     * @param config string representing state of the game
     * @param moves moves to perform
     * @return string with result - new configuration, winner, draw or error
     */
    public static String moveTest(String config, String moves) {
        String result = "";

        try {
            Game g = StateManager.restoreConfiguration(config);
            for (Character c : moves.toCharArray()) {
                g.move(Integer.parseInt(c.toString()));
            }

            if (g.isGameOver()) {
                Player winner = g.getWinner();
                if (winner != null) {
                    String winnerString = "";
                    if (g.getPlayer1().equals(winner)) {
                        winnerString = "0";
                    } else {
                        winnerString = "1";
                    }
                    result = "WINNER"+winnerString+""+winner.calculatePoints();
                } else {
                    result = "DRAW";
                }
            } else {
                result = StateManager.serialize(g);
            }
        } catch (InconsistentNumberOfCardsException | InconsistentTrumpSuitException | InvalidHandException ex) {
            result = "ERROR: " + ex.getMessage();
        }

        return result;
    }
}
