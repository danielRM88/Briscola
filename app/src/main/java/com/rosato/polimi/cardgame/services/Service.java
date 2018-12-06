package com.rosato.polimi.cardgame.services;

import com.rosato.polimi.cardgame.exceptions.GameTerminatedException;
import com.rosato.polimi.cardgame.exceptions.InvalidCardException;
import com.rosato.polimi.cardgame.exceptions.NotYourTurnException;
import com.rosato.polimi.cardgame.exceptions.UnauthorizedException;

import okhttp3.MediaType;
import okhttp3.Response;

/**
 * Created by danielrosato on 12/29/17.
 */

public class Service {

    public static final int HTTP_SUCCESS_CODE = 200;
    public static final int HTTP_UNAUTHORIZED_CODE = 401;
    public static final int HTTP_NOT_YOUR_TURN_CODE = 403;
    public static final int HTTP_INVALID_CARD_CODE = 409;
    public static final int HTTP_GAME_TERMINATED_CODE = 410;

    public static final String SERVER = "http://mobile17.ifmledit.org";
    public static final MediaType TEXT = MediaType.parse("text/plain");
    public static final int TIMEOUT = 30;
    public static final String API_KEY = "a6ce8d94-0985-46f7-b61d-1a2030e246ea";

    /**
     * Method that checks if the response is successful
     *
     * @param response response object from the call
     * @return true if it is successful
     * @throws UnauthorizedException not authorized to play the game
     * @throws GameTerminatedException the game has been terminated
     * @throws InvalidCardException the player does not have that card in hand
     * @throws NotYourTurnException it is not the turn of the player
     */
    public static Boolean isResponseSuccessful(Response response) throws UnauthorizedException, NotYourTurnException, InvalidCardException, GameTerminatedException {
        Boolean success = false;

        switch (response.code()) {
            case HTTP_SUCCESS_CODE:
                success = true;
                break;
            case HTTP_UNAUTHORIZED_CODE:
                throw new UnauthorizedException("You cannot access this room");
            case HTTP_NOT_YOUR_TURN_CODE:
                throw new NotYourTurnException("It is not your turn");
            case HTTP_INVALID_CARD_CODE:
                throw new InvalidCardException("You do not have that card");
            case HTTP_GAME_TERMINATED_CODE:
                throw new GameTerminatedException("Game is terminated");
        }

        return success;
    }
}
