package com.rosato.polimi.cardgame.services;

import com.rosato.polimi.cardgame.exceptions.GameTerminatedException;
import com.rosato.polimi.cardgame.exceptions.InvalidCardException;
import com.rosato.polimi.cardgame.exceptions.InvalidHandException;
import com.rosato.polimi.cardgame.exceptions.NotYourTurnException;
import com.rosato.polimi.cardgame.exceptions.UnauthorizedException;
import com.rosato.polimi.cardgame.models.Card;
import com.rosato.polimi.cardgame.models.RemoteGame;
import com.rosato.polimi.cardgame.models.interfaces.Player;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by danielrosato on 1/4/18.
 */

/**
 * Interface for interacting with the web server for online gaming
 */
public interface RemoteGameService {
    RemoteGame findGame(String room, Player player) throws IOException,
            JSONException,
            InvalidHandException,
            UnauthorizedException,
            GameTerminatedException, InvalidCardException, NotYourTurnException;

    List<Card> fetchOpponentsMove(String gameUrl) throws IOException,
            JSONException,
            UnauthorizedException,
            GameTerminatedException, InvalidCardException, NotYourTurnException;

    Card performMove(String gameUrl, Card card) throws IOException,
                                                        InvalidCardException,
                                                        NotYourTurnException,
                                                        UnauthorizedException,
                                                        GameTerminatedException;

    void deleteGame(String gameUrl) throws IOException;
}
