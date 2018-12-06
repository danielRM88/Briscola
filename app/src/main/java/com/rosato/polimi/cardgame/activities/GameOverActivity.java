package com.rosato.polimi.cardgame.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rosato.polimi.cardgame.R;
import com.rosato.polimi.cardgame.models.Game;
import com.rosato.polimi.cardgame.models.Score;

public class GameOverActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Score score = (Score) getIntent().getSerializableExtra("score");

        TextView winnerTextView = findViewById(R.id.winner);
        winnerTextView.setText("The winner is: "+score.getWinner());

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameOverActivity.this, MenuActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MenuActivity.class));
    }
}
