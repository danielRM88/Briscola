package com.rosato.polimi.cardgame.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rosato.polimi.cardgame.activities.SettingsActivity;
import com.rosato.polimi.cardgame.exceptions.InconsistentNumberOfCardsException;
import com.rosato.polimi.cardgame.exceptions.InconsistentTrumpSuitException;
import com.rosato.polimi.cardgame.exceptions.InvalidHandException;
import com.rosato.polimi.cardgame.models.interfaces.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game implements Serializable {
    private static final int MAX_NUMBER = 40;
    public static final String GAME_KEY = "GAME";

    private Settings settings;

    private Player player1;
    private Player player2;

    private Card.SUIT trumpSuit;

    private Player currentPlayer;

    private ArrayList<Card> deck;

    private ArrayList<Card> surfaceCards;

    /**
     * Empty constructor. Should be used if initDeck() is going to be called afterwards.
     */
    public Game() {
        player1 = new HumanPlayer();
        player1.setName("Player 1");
        player2 = new HumanPlayer();
        player2.setName("Player 2");
        currentPlayer = player1;
        deck = new ArrayList<>();
        surfaceCards = new ArrayList<>();
        settings = new Settings();
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setTrumpSuit(Card.SUIT trumpSuit) {
        this.trumpSuit = trumpSuit;
    }

    /**
     * Constructor for the game, useful for restoring a point in time, makes sure that the game state is consistent.
     *
     * @param currentPlayer player that should play next. Can be either "0" or "1"
     * @param player1 player 1
     * @param player2 player 2
     * @param trumpSuit trump suit
     * @param deck list of cards
     * @param surfaceCards cards on the surface played by the players
     * @throws InconsistentNumberOfCardsException exception thrown when there are not 40 cards in total within the game
     * @throws InconsistentTrumpSuitException exception thrown when the trump suit does not match the suit of the trump card (last card of deck)
     */
    public Game(String currentPlayer,
                Player player1,
                Player player2,
                Card.SUIT trumpSuit,
                ArrayList<Card> deck,
                ArrayList<Card> surfaceCards) throws InconsistentNumberOfCardsException, InconsistentTrumpSuitException {

        int player1Hand = 0;
        int player1Pile = 0;
        if (player1 != null) {
            player1Hand = player1.getHand().size();
            player1Pile = player1.getPile().size();
        }

        int player2Hand = 0;
        int player2Pile = 0;
        if (player2 != null) {
            player2Hand = player2.getHand().size();
            player2Pile = player2.getPile().size();
        }

        int deckSize = 0;
        if (deck != null) {
            deckSize = deck.size();
        }

        int surfaceSize = 0;
        if (surfaceCards != null) {
            surfaceSize = surfaceCards.size();
        }

        if (player1Hand + player1Pile + player2Hand + player2Pile + deckSize + surfaceSize != MAX_NUMBER) {
            throw new InconsistentNumberOfCardsException("There is an inconsistent number of cards in play");
        }

        this.player1 = player1;
        this.player2 = player2;
        this.player1.setName("Player 1");
        this.player2.setName("Player 2");
        this.settings = new Settings();

        if (currentPlayer.equals("0")) {
            this.currentPlayer = this.player1;
        } else {
            this.currentPlayer = this.player2;
        }

        this.deck = deck;
        this.surfaceCards = surfaceCards;

        if (!this.surfaceCards.isEmpty()) {
            this.surfaceCards.get(0).setPlayedBy(getOtherPlayer().getName());
        }

        this.trumpSuit = trumpSuit;

//        Card trumpCard;
////        If deck is empty it means that all cards have been drawn
////        we need to do a bit of logic to find the trump card.
////        It should be the last card drawn. We find the player that played
////        last based on how many surface cards are in place.
////        The last card in the hand of that player should be the trump card.
//        if(this.deck.isEmpty()) {
//
//            if (surfaceCards.isEmpty()) {
//                trumpCard = getOtherPlayer().getHand().get(getOtherPlayer().getHand().size() - 1);
//            } else {
//                trumpCard = this.currentPlayer.getHand().get(this.currentPlayer.getHand().size() - 1);
//            }
//        } else {
////            If the deck is not empty, the last card of the deck should be the trump card.
//            trumpCard = this.deck.get(this.deck.size() - 1);
//        }
//
//        if (trumpCard.getSuit() != this.trumpSuit) {
//            throw new InconsistentTrumpSuitException("Trump suit does not match the trump card in the deck");
//        }
//
//        trumpCard.setTrumpCard(true);
//        this.player1.setTrumpCard(trumpCard);
//        this.player2.setTrumpCard(trumpCard);
        this.player1.setTrumpSuit(trumpSuit);
        this.player2.setTrumpSuit(trumpSuit);

        boolean result = this.isGameConsistent();

        if (!result) {
            throw new InconsistentNumberOfCardsException("The cards in game do not form a proper 40 cards deck.");
        }
    }

    /* GETTERS */

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Card.SUIT getTrumpSuit() {
        return trumpSuit;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public ArrayList<Card> getSurfaceCards() {
        return surfaceCards;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    /******/

    /**
     * Method that shuffles the deck of cards
     */
    private void shuffleDeck() {
        Collections.shuffle(deck);
    }

    /**
     * Method that initializes the deck of cards with the proper suits and values
     *
     * @throws InconsistentNumberOfCardsException exception thrown when there are not 40 cards in total within the deck
     */
    private void initDeck() throws InconsistentNumberOfCardsException {
        deck = new ArrayList<>();
        for (Card.SUIT suit : Card.SUIT.values()) {
            for (Card.VALUE value : Card.VALUE.values()) {
                deck.add(new Card(suit, value));
            }
        }

        if (!this.isGameConsistent()) {
            throw new InconsistentNumberOfCardsException("There is an inconsistent number of cards in play");
        }
    }

    /**
     * Method that checks that all the cards in play are consistent and form a deck of 40
     * needed to play the game
     *
     * @return true or false depending on whether all cards are there or not
     */
    public boolean isGameConsistent() {
        ArrayList<Card> cards = new ArrayList<>();
        cards.addAll(deck);
        if (player1 != null) {
            cards.addAll(player1.getHand());
            cards.addAll(player1.getPile());
        }
        if (player2 != null) {
            cards.addAll(player2.getHand());
            cards.addAll(player2.getPile());
        }
        if (surfaceCards != null) {
            cards.addAll(surfaceCards);
        }

        return Game.isDeckConsistent(cards);
    }

    /**
     * Method that checks whether a game deck of 40 cards is consistent, that is,
     * it contains all the cards needed to play the game.
     *
     * @param cards list of cards in the deck (should be a 40 card deck)
     * @return true or false depending on whether all cards are there or not
     */
    public static boolean isDeckConsistent(ArrayList<Card> cards) {
        boolean result = true;
        loop:
        for (Card.SUIT suit : Card.SUIT.values()) {
            for (Card.VALUE value : Card.VALUE.values()) {
                result = cards.contains(new Card(suit, value));
                if (!result) {
                    break loop;
                }
            }
        }

        if (cards.size() != MAX_NUMBER) {
            result = false;
        }

        return result;
    }

    /**
     * Method that initializes the game.
     *
     * @throws InvalidHandException exception thrown if a player gets a hold of more than three cards on hand
     * @throws InconsistentNumberOfCardsException exception thrown if the number of cards in game do not form a proper deck
     */
    public void initGame() throws InvalidHandException, InconsistentNumberOfCardsException {
        initDeck();
        shuffleDeck();

//        if settings are not passed, default one is created
        if (settings == null) {
            settings = new Settings();
        }

//        is player one an AI
        if (settings.getPlayer1Computer()) {
            player1 = new Computer(settings.getDifficulty());
            player1.setName("AI 1");
        } else {
            player1 = new HumanPlayer();
            player1.setName("Player 1");
        }

//        is player two an AI
        if (settings.getPlayer2Computer()) {
            player2 = new Computer(settings.getDifficulty());
            player2.setName("AI 2");
        } else {
            player2 = new HumanPlayer();
            player2.setName("Player 2");
        }

        player1.addCardToHand(deck.remove(0));
        player2.addCardToHand(deck.remove(0));

        player1.addCardToHand(deck.remove(0));
        player2.addCardToHand(deck.remove(0));

        player1.addCardToHand(deck.remove(0));
        player2.addCardToHand(deck.remove(0));

        Card trumpCard = deck.remove(0);
        trumpCard.setTrumpCard(true);

        trumpSuit = trumpCard.getSuit();

        deck.add(trumpCard);

        player1.setTrumpCard(trumpCard);
        player2.setTrumpCard(trumpCard);
        player1.setTrumpSuit(trumpCard.getSuit());
        player2.setTrumpSuit(trumpCard.getSuit());

        surfaceCards = new ArrayList<>();

        if (settings.getWhoStarts() == Settings.PLAYER_1_STARTS) {
            currentPlayer = player1;
        } else if (settings.getWhoStarts() == Settings.PLAYER_2_STARTS) {
            currentPlayer = player2;
        } else {
            Random r = new Random();
            int i1 = r.nextInt(2);
            if (i1 == 0) {
                currentPlayer = player1;
            } else {
                currentPlayer = player2;
            }
        }
    }

    /**
     * Method that returns the winner of a round
     * based on the rank and trump suit of the cards
     *
     * @return winner player or null if there is no winner.
     */
    public Player checkForRoundWinner() {
        Player roundWinner = null;
        if (surfaceCards.size() == 2) {
            Card card1 = surfaceCards.get(0);
            Card card2 = surfaceCards.get(1);
            int winner = Game.calculateWinner(card1, card2, trumpSuit);

            if (winner == -1 || winner == 0) {
//                Reference to the player that played the card
                String winnerName = card1.getPlayedBy();

                if (winnerName.equals(getPlayer1().getName())) {
                    roundWinner = getPlayer1();
                } else {
                    roundWinner = getPlayer2();
                }
            } else {
//                Reference to the player that played the card
                String winnerName = card2.getPlayedBy();

                if (winnerName.equals(getPlayer1().getName())) {
                    roundWinner = getPlayer1();
                } else {
                    roundWinner = getPlayer2();
                }
//                roundWinner = card2.getPlayedBy();
            }
        }

        return roundWinner;
    }

    /**
     * Method that makes players draw cards accordingly to the rules of the game
     *
     * @throws InvalidHandException exception thrown when a player gets more than 3 cards on his hand
     */
    public void drawRoundCards() throws InvalidHandException {
        if(surfaceCards.size() == 0) {
            if (currentPlayer.getHand().size() < 3 && deck.size() > 0) {
                currentPlayer.addCardToHand(deck.remove(0));
            }

            Player otherPlayer = getOtherPlayer();

            if(otherPlayer.getHand().size() < 3 && deck.size() > 0) {
                otherPlayer.addCardToHand(deck.remove(0));
            }
        }
    }

    public void play(int cardInHand) throws InconsistentNumberOfCardsException {
        if (!isGameOver()) {
            if (surfaceCards.size() < 2) {
                Card card = currentPlayer.getCard(cardInHand);
//                By storing a reference to the player that played the card,
//                makes it easier to determine round winner.
                card.setPlayedBy(currentPlayer.getName());

//                methods to be used by the AI
                currentPlayer.addToPlayedBySelf(card);
//                methods to be used by the AI
                getOtherPlayer().addToPlayedByOpponent(card);
                surfaceCards.add(card);
            }
        }

        if(!isGameConsistent()) {
            throw new InconsistentNumberOfCardsException("Number of cards in play do not form a proper 40 cards deck");
        }
    }

    public boolean roundWinner() {
        boolean roundWon = false;
        Player winner = checkForRoundWinner();

        if(winner != null) {
//            winner.getPile().addAll(surfaceCards);
//            surfaceCards.clear();
            currentPlayer = winner;
            roundWon = true;
        } else {
            currentPlayer = getOtherPlayer();
        }

        return roundWon;
    }

    public void collectSurfaceCards() {
        Player winner = checkForRoundWinner();

        if(winner != null) {
            winner.getPile().addAll(surfaceCards);
            surfaceCards.clear();
//            currentPlayer = winner;
        }
    }

    /**
     * Method that simulates a move within the card game
     *
     * @param cardInHand index of the card that the player in turn is going to play
     * @throws InvalidHandException exception thrown when a player gets more than 3 cards on his hand
     */
    public void move(int cardInHand) throws InvalidHandException, InconsistentNumberOfCardsException {

        if (!isGameOver()) {
            Card card = currentPlayer.getCard(cardInHand);
            if (surfaceCards.size() < 2) {
//                By storing a reference to the player that played the card,
//                makes it easier to determine round winner.
                card.setPlayedBy(currentPlayer.getName());

//                methods to be used by the AI
                currentPlayer.addToPlayedBySelf(card);
//                methods to be used by the AI
                getOtherPlayer().addToPlayedByOpponent(card);
                surfaceCards.add(card);
            }

            Player winner = checkForRoundWinner();

            if(winner != null) {
                winner.getPile().addAll(surfaceCards);
                surfaceCards.clear();
                currentPlayer = winner;
            } else {
                currentPlayer = getOtherPlayer();
            }
        }

        drawRoundCards();

        if(!isGameConsistent()) {
            throw new InconsistentNumberOfCardsException("Number of cards in play do not form a proper 40 cards deck");
        }
    }

    /**
     * Method that returns the player that is not the current player
     *
     * @return player that is not the current player
     */
    public Player getOtherPlayer() {
        Player otherPlayer;
        if (currentPlayer.equals(player1)) {
            otherPlayer = player2;
        } else {
            otherPlayer = player1;
        }

        return otherPlayer;
    }

    /**
     * Method that calculates which card wins based on trump card,
     * point value and number value.
     *
     * @param card1 first card to be compared
     * @param card2 second card to be compared
     * @param trump trump suit
     * @return -1 if card1 wins, 1 if card2 wins, 0 if it's a tie
     */
    public static int calculateWinner(Card card1, Card card2, Card.SUIT trump) {
        int winner;
        if (card1.getSuit() == trump && card2.getSuit() != trump) {
            winner = -1;
        } else if (card1.getSuit() != trump && card2.getSuit() == trump) {
            winner = 1;
        } else if (card1.getSuit() != trump && card2.getSuit() != trump && card1.getSuit() != card2.getSuit()) {
            winner = -1;
        } else {
//            if trump suit does not come into play we use the natural comparison for the cards.
            winner = card1.compareTo(card2);
        }

        return winner;
    }

    /**
     * Method that returns the string representation of the current player
     *
     * @return "0" if current player is player1 or "1" if current player is player2
     */
    public String getCurrentPlayerString() {
        String current;

        if (currentPlayer.getName().equals(player1.getName())) {
            current = "0";
        } else {
            current = "1";
        }

        return current;
    }

    /**
     * Method that checks if the game is over.
     * That is, all cards haven played.
     *
     * @return true or false depending if game is over or not
     */
    public boolean isGameOver() {
        boolean result = false;

        if (deck.isEmpty() &&
            player1.getHand().isEmpty() &&
            player2.getHand().isEmpty() &&
            surfaceCards.isEmpty() &&
            isGameConsistent()) {
            result = true;
        }

        return result;
    }

    /**
     * Method that returns the winner of the game
     *
     * @return player1 or player2 depending on who won the game. Will return null if there is a draw or if game is not over yet
     */
    public Player getWinner() {
        Player winner = null;

        if (isGameOver()) {
            int player1Points = player1.calculatePoints();
            int player2Points = player2.calculatePoints();

            if (player1Points > player2Points) {
                winner = player1;
            } else if (player1Points < player2Points) {
                winner = player2;
            }
        }

        return winner;
    }

    /**
     * Method that returns a score object with the current
     * scores for the game.
     *
     * @return score object with the points and names of each player
     */
    public Score getGameScore() {
        Score sb = new Score();
        sb.setPlayer1Name(player1.getName());
        sb.setPlayer1Score(player1.calculatePoints());
        sb.setPlayer2Name(player2.getName());
        sb.setPlayer2Score(player2.calculatePoints());

        return sb;
    }

    public static Game getGame(Context context) {
        Game game = null;

        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SettingsActivity.PREFS_NAME, 0);
            String serializedGame = sharedPreferences.getString(Game.GAME_KEY, "");

            if (!serializedGame.isEmpty()) {
                final RuntimeTypeAdapterFactory<Player> typeFactory = RuntimeTypeAdapterFactory
                        .of(Player.class, "type")
                        .registerSubtype(HumanPlayer.class)
                        .registerSubtype(Computer.class);
                final Gson gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();
                game = gson.fromJson(serializedGame, Game.class);
                if (game.getCurrentPlayer().getName().equals(game.getPlayer1().getName())) {
                    game.currentPlayer = game.getPlayer1();
                } else {
                    game.currentPlayer = game.getPlayer2();
                }
            }
        }

        return game;
    }
}
