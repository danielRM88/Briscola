package com.rosato.polimi.cardgame.models;

import android.support.annotation.NonNull;

import com.rosato.polimi.cardgame.models.interfaces.Player;

import java.io.Serializable;

public class Card implements Comparable<Card>, Serializable {
    public enum SUIT {
        BATONS("B"),
        SWORDS("S"),
        CUPS("C"),
        GOLDS("G");

        private String id;

        SUIT(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public static SUIT fromString(String text) {
            for (SUIT b : SUIT.values()) {
                if (b.id.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    public enum VALUE {
        ACE("1", 11, 1, "ace"),
        TWO("2", 0, 10, "two"),
        THREE("3", 10, 2, "three"),
        FOUR("4", 0, 9, "four"),
        FIVE("5", 0, 8, "five"),
        SIX("6", 0, 7, "six"),
        SEVEN("7", 0, 6, "seven"),
        JACK("J", 2, 5, "jack"),
        HORSE("H", 3, 4, "horse"),
        KING("K", 4, 3, "king");

        private String id;
        private Integer pointValue;
        private Integer rank;
        private String assetName;

        VALUE (String id, Integer pointValue, Integer rank, String assetName) {
            this.id = id;
            this.pointValue = pointValue;
            this.rank = rank;
            this.assetName = assetName;
        }

        public String getId() {
            return this.id;
        }

        public Integer getPointValue() {
            return pointValue;
        }

        public Integer getRank() {
            return rank;
        }

        public static VALUE fromString(String text) {
            for (VALUE b : VALUE.values()) {
                if (b.id.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    private SUIT suit;
    private VALUE value;
    private Boolean trumpCard;

    private String playedBy;

    public Card (SUIT suit, VALUE value) {
        this.suit = suit;
        this.value = value;
        this.trumpCard = false;
    }

    public Boolean getTrumpCard() {
        return trumpCard;
    }

    public void setTrumpCard(Boolean trumpCard) {
        this.trumpCard = trumpCard;
    }

    public SUIT getSuit() {
        return suit;
    }

    public void setSuit(SUIT suit) {
        this.suit = suit;
    }

    public VALUE getValue() {
        return value;
    }

    public void setValue(VALUE value) {
        this.value = value;
    }

    public String getPlayedBy() {
        return playedBy;
    }

    public void setPlayedBy(String playedBy) {
        this.playedBy = playedBy;
    }

    public Integer getRank() {
        return this.getValue().getRank();
    }

    @Override
    public String toString() {
        return this.value.getId()+this.suit.getId();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof Card) {
            Card card = (Card) obj;
            result = (card.getSuit() == this.getSuit() && card.getValue() == this.getValue());
        }
        return result;
    }

    public String getImageName() {
        String name = "";

        name = "n_" + getValue().assetName + "_" + getSuit().getId().toLowerCase();

        return name;
    }

    /**
     * Natural comparison method for Card objects
     *
     * @param card card to be compered to
     * @return -1 if this card is bigger, 1 if card parameter is bigger, 0 if they are equals
     */
    @Override
    public int compareTo(@NonNull Card card) {
        int result;
        if (this.getValue().getRank() < card.getValue().getRank()) {
            result = -1;
        } else if (this.getValue().getRank() > card.getValue().getRank()) {
            result = 1;
        } else {
            result = 0;
        }

        return result;
    }
}
