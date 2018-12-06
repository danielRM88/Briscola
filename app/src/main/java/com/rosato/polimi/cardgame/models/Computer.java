package com.rosato.polimi.cardgame.models;

import java.util.ArrayList;
import java.util.Random;

/**
 * AI class
 */
public class Computer extends HumanPlayer {
    public static final Integer EASY = 0;
    public static final Integer MEDIUM = 1;
    public static final Integer HARD = 2;

    private Integer difficulty;
    private ArrayList<Card> playedByOpponent;
    private ArrayList<Card> playedBySelf;

    private Card.SUIT trumpSuit;
    private Card trumpCard;

    public Computer(Integer difficulty) {
        this.difficulty = difficulty;
        this.playedByOpponent = new ArrayList<>();
        this.playedBySelf = new ArrayList<>();
    }

    /**
     * Method that returns the card to be played by the player
     * given the player is an AI, it ignores the parameter passed (card selected)
     * and instead calculates the next move.
     *
     * @param index not used
     * @return card selected
     */
    @Override
    public Card getCard(int index) {
        Card card = null;

        if (difficulty.compareTo(EASY) == 0) {
            Random r = new Random();
            int i1 = r.nextInt(3);
            card = super.getCard(i1);
        } else if (difficulty.compareTo(MEDIUM) == 0) {
            card = getHighestRankedCardOfSuit(trumpSuit);
            if (card == null) {
                card = getHighestRankedCard();
            }
            getHand().remove(card);
        } else {
            card = executeHardDifficultyPlay();
        }

        return card;
    }

    private Card executeHardDifficultyPlay() {
        Card card = null;
        if(playedByOpponent.size() > playedBySelf.size()) {
            // opponent played first
            Card opponentsCard = playedByOpponent.get(playedByOpponent.size()-1);

            if (opponentsCard.getValue() == Card.VALUE.ACE ||
                    opponentsCard.getValue() == Card.VALUE.THREE) {
//                    want to win
                card = getLowestMinimumCardToBeat(opponentsCard);
                if (card == null && opponentsCard.getSuit() != trumpSuit) {
                    card = getLowestRankedCardOfSuit(trumpSuit);
                }

                if (card == null) {
                    card = getLowestRankedCard(true);
                }
            } else {
//                    do not want to win
                if (opponentsCard.getSuit() != trumpSuit) {
                    card = getLowestMinimumCardToBeat(opponentsCard);
                    Card lowestRankedCard = getLowestRankedCard(true);

                    if (card == null || (opponentsCard.getValue().getPointValue() == 0 &&
                            lowestRankedCard.getValue().getPointValue() < card.getValue().getPointValue())) {
                        if (card != null) {
                            if (lowestRankedCard.getValue() == Card.VALUE.ACE ||
                                    lowestRankedCard.getValue() == Card.VALUE.THREE) {

                            } else {
                                card = lowestRankedCard;
                            }
                        } else {
                            card = lowestRankedCard;
                        }
                    }

                } else {
                    card = getLowestRankedCard(false);
                }
            }
        } else {
            // ai plays first
//                card = getHighestRankedCardOfSuit(trumpSuit);
            card = getLowestRankedCard(true);
        }

        if (card == null) {
            card = getLowestRankedCard(true);
        }

        getHand().remove(card);

        return card;
    }

    @Override
    public boolean isComputer() {
        return true;
    }

    @Override
    public void addToPlayedByOpponent(Card card) {
        this.playedByOpponent.add(card);
    }

    @Override
    public void addToPlayedBySelf(Card card) {
        this.playedBySelf.add(card);
    }

    @Override
    public void setTrumpCard(Card card) {
        this.trumpCard = card;
    }

    @Override
    public void setTrumpSuit(Card.SUIT suit) {
        this.trumpSuit = suit;
    }

    private Card getLowestMinimumCardToBeat(Card opponentsCard) {
        Card card = null;

        for(Card myCard : getHand()) {
            if(myCard.getSuit() == opponentsCard.getSuit()) {
                if (myCard.getRank() < opponentsCard.getRank()) {
                    if (card == null) {
                        card = myCard;
                    } else if (myCard.getRank() > card.getRank()) {
                        card = myCard;
                    }
                }
            }
        }

        return card;
    }

    private Card getHighestRankedCardOfSuit(Card.SUIT suit) {
        Card card = null;

        for(Card myCard : getHand()) {
            if (myCard.getSuit() == suit && (card == null || myCard.getRank() < card.getRank())) {
                card = myCard;
            }
        }

        return card;
    }

    private Card getLowestRankedCardOfSuit(Card.SUIT suit) {
        Card card = null;

        for(Card myCard : getHand()) {
            if (myCard.getSuit() == suit && (card == null || myCard.getRank() > card.getRank())) {
                card = myCard;
            }
        }

        return card;
    }

    private Card getHighestRankedCard() {
        Card card = null;

        for(Card myCard : getHand()) {
            if (card == null || myCard.getRank() < card.getRank()) {
                card = myCard;
            }
        }

        return card;
    }

    private Card getLowestRankedCard(boolean notTrumpSuit) {
        Card card = null;

        for(Card myCard : getHand()) {
            if (card == null) {
                card = myCard;
            } else if (myCard.getRank() > card.getRank()) {
                if (notTrumpSuit && myCard.getSuit() != trumpSuit) {
                    card = myCard;
                } else {
                    card = myCard;
                }
            }
        }

        return card;
    }
}
