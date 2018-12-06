package com.rosato.polimi.cardgame;


import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.rosato.polimi.cardgame.models.Score;
import com.rosato.polimi.cardgame.models.interfaces.ScoreRepository;
import com.rosato.polimi.cardgame.models.repositories.SQLiteScoreRepository;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SQLiteScoreRepositoryTest {
    /**
     * Test that asserts that a score is successfully stored, deleted and retrieved
     * in the SQLite database of the app
     */
    @Test
    public void managesScoresInSQLiteDB() {
//        an app context is simulated in the emulator. this is necessary to create db
        ScoreRepository repo = new SQLiteScoreRepository(InstrumentationRegistry.getTargetContext());
//        the db is cleared of previous records
        repo.deleteAll();

        Score s = new Score();
        s.setPlayer1Name("Player 1");
        s.setPlayer2Name("Player 2");
        s.setPlayer1Score(59);
        s.setPlayer2Score(61);

//        the record is saved
        repo.save(s);

        s = new Score();
        s.setPlayer1Name("Player 1");
        s.setPlayer2Name("Player 2");
        s.setPlayer1Score(30);
        s.setPlayer2Score(70);

//        the record is saved
        repo.save(s);

//        all scores are retrieved from the db
        List<Score> scores = repo.findAll(null);
        assertEquals(2, scores.size());
    }
}
