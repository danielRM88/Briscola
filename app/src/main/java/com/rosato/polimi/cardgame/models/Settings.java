package com.rosato.polimi.cardgame.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.rosato.polimi.cardgame.activities.SettingsActivity;

import java.io.Serializable;

/**
 * Default game settings set by the user
 */
public class Settings implements Serializable {

    public static final Integer PLAYER_1_STARTS = 0;
    public static final Integer PLAYER_2_STARTS = 1;
    public static final Integer RANDOM = 2;

    public static final Integer BLUE_DECK = 0;
    public static final Integer RED_DECK = 1;

    private Integer difficulty;
//    indicates if the first player is an AI
    private Boolean player1Computer;
//    indicates if the second player is an AI
    private Boolean player2Computer;

    private Boolean remoteGame;

    private Integer gameMode;

    private boolean showPlayer1Hand;
    private boolean showPlayer2Hand;

    private Integer whoStarts;

    private Integer deck;

    public Settings() {
//        Default difficulty for the AI
        this.difficulty = Computer.EASY;
        this.player1Computer = false;
        this.player2Computer = false;
        this.remoteGame = false;
        this.gameMode = 1;
        this.showPlayer1Hand = false;
        this.showPlayer2Hand = false;
        this.whoStarts = PLAYER_1_STARTS;
        this.deck = 0;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public void setDifficulty(String difficulty) {
        switch(difficulty) {
            case "Easy":
                setDifficulty(0);
                break;
            case "Medium":
                setDifficulty(1);
                break;
            case "Hard":
                setDifficulty(2);
                break;
        }
    }

    public Integer getDeck() {
        return deck;
    }

    public void setDeck(Integer deck) {
        this.deck = deck;
    }

    public Integer getGameMode() {
        return gameMode;
    }

    public void setGameMode(Integer gameMode) {
        this.gameMode = gameMode;
    }

    public Boolean getPlayer1Computer() {
        return player1Computer;
    }

    public void setPlayer1Computer(Boolean player1Computer) {
        this.player1Computer = player1Computer;
    }

    public Boolean getPlayer2Computer() {
        return player2Computer;
    }

    public void setPlayer2Computer(Boolean player2Computer) {
        this.player2Computer = player2Computer;
    }

    public Boolean getRemoteGame() {
        return remoteGame;
    }

    public void setRemoteGame(Boolean remoteGame) {
        this.remoteGame = remoteGame;
    }

    public boolean isShowPlayer1Hand() {
        return showPlayer1Hand;
    }

    public void setShowPlayer1Hand(boolean showPlayer1Hand) {
        this.showPlayer1Hand = showPlayer1Hand;
    }

    public boolean isShowPlayer2Hand() {
        return showPlayer2Hand;
    }

    public void setShowPlayer2Hand(boolean showPlayer2Hand) {
        this.showPlayer2Hand = showPlayer2Hand;
    }

    public Integer getWhoStarts() {
        return whoStarts;
    }

    public void setWhoStarts(Integer whoStarts) {
        this.whoStarts = whoStarts;
    }

    /**
     * Method that retrieves the settings selected by the user
     * from the shared preference storage of the device.
     *
     * @param context context of the activity
     * @return settings object
     */
    public static Settings getSettings(Context context) {
        Settings settings = new Settings();

        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SettingsActivity.PREFS_NAME, 0);
            String serializedSettings = sharedPreferences.getString(SettingsActivity.SETTINGS_KEY, "");

            if (!serializedSettings.isEmpty()) {
                final Gson gson = new Gson();
                settings = gson.fromJson(serializedSettings, Settings.class);
            }
        }

        return settings;
    }
}
