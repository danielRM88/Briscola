package com.rosato.polimi.cardgame.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.rosato.polimi.cardgame.R;
import com.rosato.polimi.cardgame.models.Score;
import com.rosato.polimi.cardgame.models.interfaces.ScoreRepository;
import com.rosato.polimi.cardgame.models.repositories.SQLiteScoreRepository;

import java.util.List;

public class HistoryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Button resetHistoryButton = findViewById(R.id.reset_history_button);
        resetHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetHistory();
                renderTable();
            }
        });

        Button backButton = findViewById(R.id.back_history_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryActivity.this, MenuActivity.class));
            }
        });

        renderTable();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HistoryActivity.this, MenuActivity.class));
    }

    private void renderTable() {
        ScoreRepository repo = new SQLiteScoreRepository(getApplicationContext());
        List<Score> scores = repo.findAll(5);

        TableLayout historiesTable = findViewById(R.id.histories_table);
        historiesTable.removeAllViews();

        TableRow row;
        TextView player1Name;
        TextView player1Score;
        TextView player2Name;
        TextView player2Score;

        if (scores.size() > 0) {
            for (Score score : scores) {
                row = new TableRow(this);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

                player1Name = new TextView(this);
                player1Name.setTextColor(getResources().getColor(R.color.black));
                player1Name.setText(score.getPlayer1Name());
                player1Name.setTextSize(18);
//                player1Name.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
                player1Name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                row.addView(player1Name);

                player1Score = new TextView(this);
                player1Score.setText(Integer.toString(score.getPlayer1Score()));
                player1Score.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
                player1Score.setTextColor(getResources().getColor(R.color.black));
                player1Score.setTextSize(18);
                player1Score.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                row.addView(player1Score);

                player2Name = new TextView(this);
                player2Name.setText(score.getPlayer2Name());
                player2Name.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
                player2Name.setTextColor(getResources().getColor(R.color.black));
                player2Name.setTextSize(18);
                player2Name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                row.addView(player2Name);

                player2Score = new TextView(this);
                player2Score.setText(Integer.toString(score.getPlayer2Score()));
                player2Score.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
                player2Score.setTextColor(getResources().getColor(R.color.black));
                player2Score.setTextSize(18);
                player2Score.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                row.addView(player2Score);

                historiesTable.addView(row);
            }
        } else {
            row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

            player1Name = new TextView(this);
            player1Name.setText("No games played yet!");
            player1Name.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
            player1Name.setTextColor(getResources().getColor(R.color.white));
            player1Name.setTextSize(18);
            player1Name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            row.addView(player1Name);
            historiesTable.addView(row);
        }
    }

    private void resetHistory() {
        ScoreRepository repo = new SQLiteScoreRepository(getApplicationContext());
        repo.deleteAll();
    }
}
