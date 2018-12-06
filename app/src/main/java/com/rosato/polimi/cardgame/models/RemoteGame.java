package com.rosato.polimi.cardgame.models;

import com.rosato.polimi.cardgame.exceptions.InvalidHandException;
import com.rosato.polimi.cardgame.models.interfaces.Player;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by danielrosato on 12/30/17.
 */

public class RemoteGame extends Game implements Serializable {
    private String gameId;
    private Card trumpCard;
    private Boolean yourTurn;
    private String gameUrl;

    private Integer round = 1;

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Card getTrumpCard() {
        return trumpCard;
    }

    public void setTrumpCard(Card trumpCard) {
        this.trumpCard = trumpCard;
    }

    public Player getPlayer() {
        return getPlayer1();
    }

    public void setPlayer(Player player) {
        this.setPlayer1(player);
    }

    public void setupRemotePlayer() {
        this.setPlayer2(new HumanPlayer());
        Player player2 = this.getPlayer2();
        player2.setName("Remote Player");
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card(Card.SUIT.BATONS, Card.VALUE.ACE));
        cards.add(new Card(Card.SUIT.BATONS, Card.VALUE.ACE));
        cards.add(new Card(Card.SUIT.BATONS, Card.VALUE.ACE));
        try {
            player2.setHand(cards);
        } catch (InvalidHandException e) {
            e.printStackTrace();
        }
    }

    public Boolean getYourTurn() {
        return yourTurn;
    }

    public void setYourTurn(Boolean yourTurn) {
        this.yourTurn = yourTurn;
        if (yourTurn) {
            this.setCurrentPlayer(getPlayer1());
        } else {
            this.setCurrentPlayer(getPlayer2());
        }
    }

    public String getGameUrl() {
        return gameUrl;
    }

    public void setGameUrl(String gameUrl) {
        this.gameUrl = gameUrl;
    }

    public void setTrumpSuit(Card.SUIT suit) {
        super.setTrumpSuit(suit);
    }

    public void play(Card card) {
        getCurrentPlayer().addToPlayedBySelf(card);
        getOtherPlayer().addToPlayedByOpponent(card);
        getSurfaceCards().add(card);
    }

    public void drawCards(Card card) {
        getPlayer2().getHand().add(new Card(Card.SUIT.BATONS, Card.VALUE.ACE));
        try {
            getPlayer().addCardToHand(card);
        } catch (InvalidHandException e) {
            e.printStackTrace();
        }
    }
}
