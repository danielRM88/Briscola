package com.rosato.polimi.cardgame.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rosato.polimi.cardgame.R;
import com.rosato.polimi.cardgame.exceptions.InconsistentNumberOfCardsException;
import com.rosato.polimi.cardgame.exceptions.InvalidHandException;
import com.rosato.polimi.cardgame.models.Game;
import com.rosato.polimi.cardgame.models.Settings;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button newGameButton = findViewById(R.id.new_game_button);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings settings = Settings.getSettings(getApplicationContext());

                if (settings.getRemoteGame()) {
                    Intent intent = new Intent(MenuActivity.this, RoomActivity.class);
                    intent.putExtra("settings", settings);
                    startActivity(intent);
                } else {
                    Game game = new Game();
                    game.setSettings(settings);
                    try {
                        game.initGame();
                    } catch (InvalidHandException | InconsistentNumberOfCardsException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(MenuActivity.this, GameActivity.class);
                    intent.putExtra("game", game);
                    startActivity(intent);
                }
            }
        });

        Button continueGameButton = findViewById(R.id.continue_game_button);
        continueGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game game = Game.getGame(getApplicationContext());
                Settings settings;
                if (game == null) {
                    game = new Game();
                    settings = Settings.getSettings(getApplicationContext());
                    game.setSettings(settings);
                } else {
                    settings = game.getSettings();
                }

                Intent intent = new Intent(MenuActivity.this, GameActivity.class);
                intent.putExtra("game", game);
                startActivity(intent);
            }
        });

        Game game = Game.getGame(getApplicationContext());
        if (game == null || game.isGameOver()) {
            continueGameButton.setVisibility(View.GONE);
        }

        Button historyButton = findViewById(R.id.history_button);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, HistoryActivity.class));
            }
        });

        Button settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        moveTaskToBack(true);
    }
}
