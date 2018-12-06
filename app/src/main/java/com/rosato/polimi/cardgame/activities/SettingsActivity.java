package com.rosato.polimi.cardgame.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rosato.polimi.cardgame.R;
import com.rosato.polimi.cardgame.models.Settings;

public class SettingsActivity extends Activity {

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String SETTINGS_KEY = "SETTINGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Settings settings = Settings.getSettings(getApplicationContext());

        final Spinner difficultySpinner = findViewById(R.id.difficulty_spinner);
        ArrayAdapter difficultyAdapter = ArrayAdapter.createFromResource(this, R.array.difficulty_array, R.layout.spinner);
        difficultyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        difficultySpinner.setAdapter(difficultyAdapter);
        difficultySpinner.setSelection(settings.getDifficulty());

        Button saveSettingsButton = findViewById(R.id.save_settings_button);
        Button cancelSettingsButton = findViewById(R.id.back_settings_button);

        final CheckBox showPlayer1HandCheckbox = findViewById(R.id.show_player1_hand_checkbox);
        final CheckBox showPlayer2HandCheckbox = findViewById(R.id.show_player2_hand_checkbox);

        showPlayer1HandCheckbox.setChecked(settings.isShowPlayer1Hand());
        showPlayer2HandCheckbox.setChecked(settings.isShowPlayer2Hand());

        final Spinner whoStartsSpinner = findViewById(R.id.who_starts_spinner);
        ArrayAdapter startAdapter = ArrayAdapter.createFromResource(this, R.array.who_starts_array, R.layout.spinner);
        startAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        whoStartsSpinner.setAdapter(startAdapter);
        whoStartsSpinner.setSelection(settings.getWhoStarts());

        final Spinner gameModeSpinner = findViewById(R.id.game_mode_spinner);
        ArrayAdapter gameModeAdapter = ArrayAdapter.createFromResource(this, R.array.game_mode_array, R.layout.spinner);
        gameModeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        gameModeSpinner.setAdapter(gameModeAdapter);
        gameModeSpinner.setSelection(settings.getGameMode());

        final Spinner deckSpinner = findViewById(R.id.deck_spinner);
        ArrayAdapter deckAdapter = ArrayAdapter.createFromResource(this, R.array.deck_array, R.layout.spinner);
        deckAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        deckSpinner.setAdapter(deckAdapter);
        deckSpinner.setSelection(settings.getDeck());

        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer difficulty = (Integer) difficultySpinner.getSelectedItemPosition();
                Integer mode = gameModeSpinner.getSelectedItemPosition();

                switch (mode) {
                    case 0:
                        settings.setPlayer1Computer(false);
                        settings.setPlayer2Computer(true);
                        settings.setRemoteGame(false);
                        break;
                    case 1:
                        settings.setPlayer1Computer(false);
                        settings.setPlayer2Computer(false);
                        settings.setRemoteGame(false);
                        break;
                    case 2:
                        settings.setPlayer1Computer(true);
                        settings.setPlayer2Computer(true);
                        settings.setRemoteGame(false);
                        break;
                    case 3:
                        settings.setPlayer1Computer(false);
                        settings.setPlayer2Computer(false);
                        settings.setRemoteGame(true);
                        break;
                    case 4:
                        settings.setPlayer1Computer(true);
                        settings.setPlayer2Computer(false);
                        settings.setRemoteGame(true);
                        break;
                }

                settings.setDifficulty(difficulty);
                settings.setGameMode(gameModeSpinner.getSelectedItemPosition());
                settings.setShowPlayer1Hand(showPlayer1HandCheckbox.isChecked());
                settings.setShowPlayer2Hand(showPlayer2HandCheckbox.isChecked());
                settings.setWhoStarts(whoStartsSpinner.getSelectedItemPosition());
                settings.setDeck(deckSpinner.getSelectedItemPosition());
                saveSettings(settings);
                Toast.makeText(getApplicationContext(), "Settings saved", Toast.LENGTH_LONG).show();
            }
        });

        cancelSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MenuActivity.class));
    }

    public void saveSettings(Settings settings) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        final Gson gson = new Gson();
        String serializedSettings = gson.toJson(settings);
        sharedPreferencesEditor.putString(SETTINGS_KEY, serializedSettings);
        sharedPreferencesEditor.apply();
    }
}
