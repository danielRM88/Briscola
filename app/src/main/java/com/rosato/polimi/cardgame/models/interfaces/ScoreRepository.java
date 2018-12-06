package com.rosato.polimi.cardgame.models.interfaces;

import com.rosato.polimi.cardgame.models.Score;

import java.util.List;

public interface ScoreRepository {
    List<Score> findAll(Integer limit);
    void save(Score score);
    void deleteAll();
}
