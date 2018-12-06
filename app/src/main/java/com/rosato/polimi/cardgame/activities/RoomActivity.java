package com.rosato.polimi.cardgame.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rosato.polimi.cardgame.R;
import com.rosato.polimi.cardgame.exceptions.GameTerminatedException;
import com.rosato.polimi.cardgame.exceptions.InvalidCardException;
import com.rosato.polimi.cardgame.exceptions.InvalidHandException;
import com.rosato.polimi.cardgame.exceptions.NotYourTurnException;
import com.rosato.polimi.cardgame.exceptions.UnauthorizedException;
import com.rosato.polimi.cardgame.models.Computer;
import com.rosato.polimi.cardgame.models.HumanPlayer;
import com.rosato.polimi.cardgame.models.RemoteGame;
import com.rosato.polimi.cardgame.models.Settings;
import com.rosato.polimi.cardgame.models.Util;
import com.rosato.polimi.cardgame.models.interfaces.Player;
import com.rosato.polimi.cardgame.services.RemoteGameService;
import com.rosato.polimi.cardgame.services.RemoteGameServiceImpl;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.OkHttpClient;

public class RoomActivity extends Activity {

    private ProgressDialog progressDialog;
    private Settings settings;
    private FindGameTask findGameTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

//        get the settings of the game
        settings = Settings.getSettings(getApplicationContext());

        Button groupRoom = findViewById(R.id.group_room_button);
        groupRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICKED ROOM 19");
                findGameTask = new FindGameTask("Group19");
                findGameTask.execute();
            }
        });

        Button publicRoom = findViewById(R.id.public_room_button);
        publicRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICKED PUBLIC ROOM");
                findGameTask = new FindGameTask("public");
                findGameTask.execute();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MenuActivity.class));
    }

    public class FindGameTask extends AsyncTask<Void, Void, RemoteGame> {
        private String room;
        private Boolean cancel = false;
        private OkHttpClient client;
        private String errorMessage;
        private Activity mActivity;

        public FindGameTask(String room) {
            this.room = room;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            show the progress dialog
            Util.lockOrientation(RoomActivity.this);
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(RoomActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Finding a game...");
                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        findGameTask.cancel(true);
                        cancel = true;
                        if (progressDialog != null) {
                            try {
                                progressDialog.dismiss();
                            } catch (Exception ex) {}
                            progressDialog = null;
                        }
                        Util.unlockOrientation(RoomActivity.this);
                    }
                });
                progressDialog.show();
            }
        }

        @Override
        protected RemoteGame doInBackground(Void... voids) {
            Boolean timeout = true;
            RemoteGame remoteGame = null;
            RemoteGameService remoteGameService;
            while(timeout && !cancel) {
                try {
                    remoteGameService = new RemoteGameServiceImpl();
                    Player player;
                    if (settings.getPlayer1Computer()) {
                        player = new Computer(settings.getDifficulty());
                        player.setName("AI 1");
                    } else {
                        player = new HumanPlayer();
                        player.setName("Player 1");
                    }
                    remoteGame = remoteGameService.findGame(this.room, player);
                    remoteGame.setSettings(settings);
                    timeout = false;
                } catch (SocketTimeoutException e) {
                    timeout = true;
                } catch (IOException | JSONException | InvalidHandException e) {
                    e.printStackTrace();
                    timeout = false;
                    errorMessage = "Something went wrong. Please try again later.";
                } catch (UnauthorizedException | GameTerminatedException | NotYourTurnException | InvalidCardException e) {
                    e.printStackTrace();
                    timeout = false;
                    errorMessage = e.getMessage();
                }
            }

            return remoteGame;
        }

        @Override
        protected void onPostExecute(RemoteGame game) {
            if (progressDialog != null) {
                try {
                    progressDialog.dismiss();
                } catch (Exception ex) {}
                progressDialog = null;
            }

            Util.unlockOrientation(RoomActivity.this);
            if (game != null) {
                Intent intent = new Intent(RoomActivity.this, RemoteActivity.class);
                intent.putExtra("game", game);
                startActivity(intent);
            } else {
//                error handling
                AlertDialog alertDialog = new AlertDialog.Builder(RoomActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(errorMessage);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }

        @Override
        protected void onCancelled() {
            if (progressDialog != null) {
                try {
                    progressDialog.dismiss();
                } catch (Exception ex) {}
                progressDialog = null;
            }
            Util.unlockOrientation(RoomActivity.this);
        }
    }
}
