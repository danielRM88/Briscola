package com.rosato.polimi.cardgame;

import com.rosato.polimi.cardgame.exceptions.InconsistentNumberOfCardsException;
import com.rosato.polimi.cardgame.exceptions.InconsistentTrumpSuitException;
import com.rosato.polimi.cardgame.exceptions.InvalidHandException;
import com.rosato.polimi.cardgame.models.Card;
import com.rosato.polimi.cardgame.models.Game;
import com.rosato.polimi.cardgame.models.StateManager;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StateManagerTest {
    /**
     * Test that asserts that a configuration is properly translated into game objects
     */
    @Test
    public void restoresConfiguration() {
        boolean success = false;
        try {
            String configuration = "0B5S4G6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B..JCKG2B.KS3G1C..";
            Game g = StateManager.restoreConfiguration(configuration);

            //        the trump suit is batons
            if (g.getTrumpSuit() == Card.SUIT.BATONS) {
                success = true;
            }
            assertEquals(true, success);

            success = false;
            //        the current player is player1
            if (g.getCurrentPlayer().equals(g.getPlayer1())) {
                success = true;
            }
            assertEquals(true, success);

            success = false;
            //        both players have three cards on hand
            if (g.getPlayer1().getHand().size() == 3 && g.getPlayer2().getHand().size() == 3) {
                success = true;
            }
            assertEquals(true, success);

            success = false;
            //        there are no surface cards
            if (g.getSurfaceCards().isEmpty()) {
                success = true;
            }
            assertEquals(true, success);

            success = false;
            //        both players have their piles empty
            if (g.getPlayer1().getPile().isEmpty() && g.getPlayer2().getPile().isEmpty()) {
                success = true;
            }
            assertEquals(true, success);

            configuration = "1S5B4G6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5S.JC.KG2B.1CKS3G..";
            g = StateManager.restoreConfiguration(configuration);

            success = false;
            //        the trump suit is batons
            if (g.getTrumpSuit() == Card.SUIT.SWORDS) {
                success = true;
            }
            assertEquals(true, success);

            success = false;
            //        the current player is player2
            if (g.getCurrentPlayer().equals(g.getPlayer2())) {
                success = true;
            }
            assertEquals(true, success);

            success = false;
            //        there is one surface card
            if (g.getSurfaceCards().size() == 1) {
                success = true;
            }
            assertEquals(true, success);

            success = false;
            //        player 1 has 2 cards and player 2 has 3
            if (g.getPlayer1().getHand().size() == 2 && g.getPlayer2().getHand().size() == 3) {
                success = true;
            }
            assertEquals(true, success);

            success = false;
            //        both players have their piles empty
            if (g.getPlayer1().getPile().isEmpty() && g.getPlayer2().getPile().isEmpty()) {
                success = true;
            }
            assertEquals(true, success);
            success = true;
        } catch (InconsistentNumberOfCardsException | InconsistentTrumpSuitException | InvalidHandException ex) {
            success = false;
        }

        assertEquals(true, success);
    }

    /**
     * Test that asserts that a game is properly serialized into an acceptable configuration format
     */
    @Test
    public void serializesGame() {
        boolean success = false;
        try {
            String configuration = "0B5S4G6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B..JCKG2B.1CKS3G..";
            Game g = StateManager.restoreConfiguration(configuration);
            assertEquals(StateManager.serialize(g), configuration);

            configuration = "0B5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B..KG4G6S.KS5S2C.3G2B.JC1C";
            g = StateManager.restoreConfiguration(configuration);

            assertEquals(StateManager.serialize(g), configuration);
            success = true;
        } catch (InconsistentNumberOfCardsException | InconsistentTrumpSuitException | InvalidHandException ex) {
            success = false;
        }

        assertEquals(true, success);
    }

    /**
     * Test that asserts that given a configuration and a set of movements
     * the moveTest method restores the game state from the configuration passed
     * and executes the required movements. Finally returns the proper game state after
     * the moves have been executed.
     */
    @Test
    public void moveTest() {
//        Expected result of the movements
        String expected = "1B5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B.KG.4G6S.KS5S2C.3G2B.JC1C";
//        actual result of the method
        String result = StateManager.moveTest("0B5S4G6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B..JCKG2B.1CKS3G..", "00110");
//        they should match
        assertEquals(expected, result);

//        actual result of the method
        expected = "WINNER166";
        result = StateManager.moveTest("0B5S4G6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B..JCKG2B.1CKS3G..", "0011002102100012210111012010101020000000");
        assertEquals(expected, result);

//        actual result of the method
        expected = "WINNER061";
        result = StateManager.moveTest("0B.2G.4S3B5B.3SJG.3G2B5G6S2CKB7B5SHCHG6B7CHB6G3CKC7G4CJB7S.JC1CKGKS4G6C1G4B1B5C2SHS1SJS", "01111");
        assertEquals(expected, result);

//        actual result of the method
        expected = "WINNER169";
        result = StateManager.moveTest("0B.2G.4S3B5B.3SJG.3G2B5G6S2CKB7B5SHCHG6B7CHB6G3CKC7G4CJB7S.JC1CKGKS4G6C1G4B1B5C2SHS1SJS", "10000");
        assertEquals(expected, result);

//        actual result of the method
        expected = "DRAW";
        result = StateManager.moveTest("0B.2G.4S3B5B.3SJG.3G2BHS6S2CKB7B5SHCHG6B7CHB6G3CKCKG4CJBJC.7S1C7GKS4G6C1G4B1B5C2S5G1SJS", "11100");
        assertEquals(expected, result);

//        restore the game with wrong number of cards (41)
        expected = "ERROR: There is an inconsistent number of cards in play";
        result = StateManager.moveTest("0B.2G.4S3B5B.3SJG.3G2BHS6S2CKB7B5SHCHG6B7CHB6G3CKCKG4CJBJCKP.7S1C7GKS4G6C1G4B1B5C2S5G1SJS", "11100");
        assertEquals(expected, result);

//        restore the game with wrong suit and card values (There are two 3 of Swords cards in play)
        expected = "ERROR: The cards in game do not form a proper 40 cards deck.";
        result = StateManager.moveTest("0B.2G.4S3B5B.3SJG.3S2BHS6S2CKB7B5SHCHG6B7CHB6G3CKCKG4CJBJC.7S1C7GKS4G6C1G4B1B5C2S5G1SJS", "11100");
        assertEquals(expected, result);

//        restore the game with a player with more than three cards on hand
        expected = "ERROR: Player cannot have more than 3 cards on hand";
        result = StateManager.moveTest("0B..4S3B5B2G.3SJG.3S2BHS6S2CKB7B5SHCHG6B7CHB6G3CKCKG4CJBJC.7S1C7GKS4G6C1G4B1B5C2S5G1SJS", "11100");
        assertEquals(expected, result);

    }
}