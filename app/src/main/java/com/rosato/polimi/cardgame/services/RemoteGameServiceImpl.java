package com.rosato.polimi.cardgame.services;

import com.rosato.polimi.cardgame.exceptions.GameTerminatedException;
import com.rosato.polimi.cardgame.exceptions.InvalidCardException;
import com.rosato.polimi.cardgame.exceptions.InvalidHandException;
import com.rosato.polimi.cardgame.exceptions.NotYourTurnException;
import com.rosato.polimi.cardgame.exceptions.UnauthorizedException;
import com.rosato.polimi.cardgame.models.Card;
import com.rosato.polimi.cardgame.models.LoggingInterceptor;
import com.rosato.polimi.cardgame.models.RemoteGame;
import com.rosato.polimi.cardgame.models.StateManager;
import com.rosato.polimi.cardgame.models.interfaces.Player;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by danielrosato on 1/4/18.
 */

/**
 * Implementation of the interface for interacting with the game server for online gaming
 */
public class RemoteGameServiceImpl implements RemoteGameService {
    /**
     * Method that finds a game to play remotely
     *
     * @param room room in which the game will take place
     * @param player player that will play the game. AI or human
     * @return returns a remote game with all the attributes
     * @throws IOException
     * @throws JSONException
     * @throws InvalidHandException the players hand contains an invalid number of cards
     * @throws UnauthorizedException not authorized to play the game
     * @throws GameTerminatedException the game has been terminated
     * @throws InvalidCardException the player does not have that card in hand
     * @throws NotYourTurnException it is not the turn of the player
     */
    @Override
    public RemoteGame findGame(String room, Player player) throws IOException, JSONException, InvalidHandException, UnauthorizedException, GameTerminatedException, InvalidCardException, NotYourTurnException {
        RemoteGame remoteGame= null;

        Request.Builder builder = new Request.Builder();
        builder.header("Content-Type", "text/plain")
                .addHeader("Authorization", "APIKey " + Service.API_KEY);
        Request request = builder
                .url(Service.SERVER + "/api/room/" + room)
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Service.TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Service.TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Service.TIMEOUT, TimeUnit.SECONDS)
                .build();

        Response response = client.newCall(request).execute();
        if(Service.isResponseSuccessful(response)) {
            final String jsonData = response.body().string();

            JSONObject jsonResponse = new JSONObject(jsonData);
            remoteGame = new RemoteGame();
            remoteGame.setGameId(jsonResponse.getString("game"));
            String lastCard = jsonResponse.getString("last_card");
            Card trumpCard = new Card(Card.SUIT.fromString(lastCard.substring(1,2)), Card.VALUE.fromString(lastCard.substring(0, 1)));
            remoteGame.setTrumpCard(trumpCard);
            remoteGame.setTrumpSuit(trumpCard.getSuit());

            player.setHand(StateManager.deserializeCards(jsonResponse.getString("cards")));
            player.setTrumpCard(trumpCard);
            player.setTrumpSuit(trumpCard.getSuit());
            remoteGame.setPlayer(player);
            remoteGame.setupRemotePlayer();
            remoteGame.setYourTurn(jsonResponse.getBoolean("your_turn"));
            remoteGame.setGameUrl(jsonResponse.getString("url"));
        }

        return remoteGame;
    }

    /**
     * Method that fetches the opponents move on the game
     *
     * @param gameUrl url to consume the game from
     * @return the card played by the opponent and / or the next card drawn from the deck for the player
     * @throws IOException
     * @throws JSONException
     * @throws UnauthorizedException not authorized to play the game
     * @throws GameTerminatedException the game has been terminated
     * @throws InvalidCardException the player does not have that card in hand
     * @throws NotYourTurnException it is not the turn of the player
     */
    @Override
    public List<Card> fetchOpponentsMove(String gameUrl) throws IOException, JSONException, UnauthorizedException, GameTerminatedException, InvalidCardException, NotYourTurnException {
        List<Card> cards = new ArrayList<>();

        Request.Builder builder = new Request.Builder();
        builder.header("Content-Type", "text/plain")
                .addHeader("Authorization", "APIKey " + Service.API_KEY);
        Request request = builder
                .url(gameUrl)
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Service.TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Service.TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Service.TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .build();

        Response response;response = client.newCall(request).execute();
        if(Service.isResponseSuccessful(response)) {
            final String jsonData = response.body().string();

            JSONObject jsonResponse = new JSONObject(jsonData);
            String opponentCard = jsonResponse.getString("opponent");
            Card opponentsCard = new Card(Card.SUIT.fromString(opponentCard.substring(1,2)), Card.VALUE.fromString(opponentCard.substring(0, 1)));
            cards.add(opponentsCard);
            String nextCardString = null;
            try {
                nextCardString = jsonResponse.getString("card");
                Card nextCard = new Card(Card.SUIT.fromString(nextCardString.substring(1, 2)), Card.VALUE.fromString(nextCardString.substring(0, 1)));
                cards.add(nextCard);
            } catch (JSONException ex) {

            }
        }

        return cards;
    }

    /**
     * Method that sends the card from the player
     *
     * @param gameUrl url to consume the game from
     * @param card card to play
     * @return
     * @throws IOException
     * @throws UnauthorizedException not authorized to play the game
     * @throws GameTerminatedException the game has been terminated
     * @throws InvalidCardException the player does not have that card in hand
     * @throws NotYourTurnException it is not the turn of the player
     */
    @Override
    public Card performMove(String gameUrl, Card card) throws IOException, InvalidCardException, NotYourTurnException, UnauthorizedException, GameTerminatedException {
        Card nextCard = null;

        byte[] byteCard = null;
        try {
            byteCard = card.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(Service.TEXT, byteCard);
        System.out.println(body.toString());
        Request.Builder builder = new Request.Builder();

        builder.header("content-type", "text/plain")
                .addHeader("Authorization", "APIKey " + Service.API_KEY);
        builder.post(body);
        Request request = builder
                .url(gameUrl)
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Service.TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Service.TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Service.TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .build();

        Response response = client.newCall(request).execute();
        if(Service.isResponseSuccessful(response)) {
            try {
                final String jsonData = response.body().string();
                String nextCardString = null;
                JSONObject jsonResponse = new JSONObject(jsonData);
                nextCardString = jsonResponse.getString("card");
                nextCard = new Card(Card.SUIT.fromString(nextCardString.substring(1, 2)),
                                    Card.VALUE.fromString(nextCardString.substring(0, 1)));
            } catch (JSONException e) {
                nextCard = null;
            }
        }

        return nextCard;
    }

    /**
     * Method that deletes the game from the web server
     *
     * @param gameUrl url of the game
     * @throws IOException
     */
    @Override
    public void deleteGame(String gameUrl) throws IOException {
        Request.Builder builder = new Request.Builder();
        builder.header("Authorization", "APIKey " + Service.API_KEY);
        builder.delete();
        Request request = builder
                .url(gameUrl)
                .build();

        final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Service.TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Service.TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Service.TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .build();

        Response response = client.newCall(request).execute();
    }
}
