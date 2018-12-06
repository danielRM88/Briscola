package com.rosato.polimi.cardgame.models.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rosato.polimi.cardgame.models.Score;
import com.rosato.polimi.cardgame.models.interfaces.ScoreRepository;

import java.util.ArrayList;
import java.util.List;

public class SQLiteScoreRepository extends SQLiteOpenHelper implements ScoreRepository {

    private static final int VERSION = 1;
    private static final String NAME = "scores";

    public SQLiteScoreRepository(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String creationSQL = "CREATE TABLE "+NAME+"(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "player1Name TEXT, " +
                                "player1Score INTEGER, " +
                                "player2Name TEXT, " +
                                "player2Score INTEGER" +
                                ")";
        db.execSQL(creationSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+NAME+"");
        this.onCreate(db);
    }

    @Override
    public List<Score> findAll(Integer limit) {
        ArrayList<Score> scores = new ArrayList<>();

        String sql = "SELECT * FROM "+NAME+" ORDER BY id DESC";

        if (limit != null) {
            sql += " LIMIT " + limit;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        Score score;

        if (cursor.moveToFirst()) {
            do {
                score = new Score();
                score.setPlayer1Name(cursor.getString(1));
                score.setPlayer1Score(cursor.getInt(2));
                score.setPlayer2Name(cursor.getString(3));
                score.setPlayer2Score(cursor.getInt(4));
                scores.add(score);
            } while (cursor.moveToNext());
        }

        return scores;
    }

    @Override
    public void save(Score score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("player1Name", score.getPlayer1Name());
        cv.put("player2Name", score.getPlayer2Name());
        cv.put("player1Score", score.getPlayer1Score());
        cv.put("player2Score", score.getPlayer2Score());

        long id = db.insert(NAME, null, cv);
        score.setId((int) id);
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM "+NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }
}
