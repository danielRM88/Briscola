package com.rosato.polimi.cardgame.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.rosato.polimi.cardgame.R;
import com.rosato.polimi.cardgame.exceptions.GameTerminatedException;
import com.rosato.polimi.cardgame.exceptions.InvalidCardException;
import com.rosato.polimi.cardgame.exceptions.NotYourTurnException;
import com.rosato.polimi.cardgame.exceptions.UnauthorizedException;
import com.rosato.polimi.cardgame.models.Card;
import com.rosato.polimi.cardgame.models.RemoteGame;
import com.rosato.polimi.cardgame.models.Settings;
import com.rosato.polimi.cardgame.models.Util;
import com.rosato.polimi.cardgame.models.interfaces.Player;
import com.rosato.polimi.cardgame.models.interfaces.ScoreRepository;
import com.rosato.polimi.cardgame.models.repositories.SQLiteScoreRepository;
import com.rosato.polimi.cardgame.services.RemoteGameService;
import com.rosato.polimi.cardgame.services.RemoteGameServiceImpl;
import com.rosato.polimi.cardgame.views.CardImageView;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class RemoteActivity extends Activity {

    private ProgressDialog progressDialog;

    private CardImageView deckImageView;
    private CardImageView briscolaImageView;

    private CardImageView surfaceCardImageView;
    private CardImageView surfaceCard2ImageView;

    private CardImageView player1Card1ImageView;
    private CardImageView player1Card2ImageView;
    private CardImageView player1Card3ImageView;

    private CardImageView player2Card1ImageView;
    private CardImageView player2Card2ImageView;
    private CardImageView player2Card3ImageView;

    private TextView scorePlayer1;
    private TextView scorePlayer2;

    private boolean gameOver = false;

    private Animation slideRight;

    private RemoteGame game;

    private Context context;

    private FetchOpponentsMoveTask fetchTask;
    private Move moveTask;

    private int cardBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        context = getApplicationContext();

        if (savedInstanceState != null) {
            game = (RemoteGame) savedInstanceState.getSerializable("game");
        } else {
            game = (RemoteGame) getIntent().getSerializableExtra("game");
        }

        deckImageView = findViewById(R.id.deck);
        briscolaImageView = findViewById(R.id.briscola);

        surfaceCardImageView = findViewById(R.id.surface_card1);
        surfaceCard2ImageView = findViewById(R.id.surface_card2);

        player1Card1ImageView = findViewById(R.id.player1_card1);
        player1Card2ImageView = findViewById(R.id.player1_card2);
        player1Card3ImageView = findViewById(R.id.player1_card3);

        player2Card1ImageView = findViewById(R.id.player2_card1);
        player2Card2ImageView = findViewById(R.id.player2_card2);
        player2Card3ImageView = findViewById(R.id.player2_card3);

        slideRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);

        scorePlayer1 = findViewById(R.id.player1_score);
        scorePlayer2 = findViewById(R.id.player2_score);

        if (game.getSettings().getDeck().compareTo(Settings.BLUE_DECK) == 0) {
            cardBack = R.drawable.card_back;
        } else {
            cardBack = R.drawable.n_back;
        }

        renderBoard();
        player1Card1ImageView.setImageResource(cardBack);
        player1Card2ImageView.setImageResource(cardBack);
        player1Card3ImageView.setImageResource(cardBack);
        flipForwardPlayer1(500);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                game.collectSurfaceCards();
                renderBoard();
            }
        }, 1000);

        if (game.getCurrentPlayer().equals(game.getPlayer1())) {
            final Handler enableInputHandler = new Handler();
            if (game.getCurrentPlayer().isComputer()) {
                enableInputHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        moveTask = new Move(0);
                        moveTask.execute();
                    }
                }, 500);
            } else {
                enableInputHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enableInput();
                    }
                }, 500);
            }
        } else {
            final Handler fetchOpponentsMoveHandler = new Handler();
            fetchOpponentsMoveHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fetchTask = new FetchOpponentsMoveTask();
                    fetchTask.execute();
                }
            }, 500);
        }
    }

    private void enableInput() {
        if (game.getCurrentPlayer().equals(game.getPlayer1())) {
            player1Card1ImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Clicked!");
                    disableInput();
                    moveTask = new Move(0);
                    moveTask.execute();
                }
            });

            player1Card2ImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Clicked!");
                    disableInput();
                    moveTask = new Move(1);
                    moveTask.execute();
                }
            });

            player1Card3ImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Clicked!");
                    disableInput();
                    moveTask = new Move(2);
                    moveTask.execute();
                }
            });
        }
    }

    private void flipForwardPlayer1(int delay) {
        List<Card> player1Hand = game.getPlayer1().getHand();
        if (player1Hand.size() >= 1) {
            Card player1Card1 = player1Hand.get(0);
            player1Card1ImageView.flipToForward(player1Card1, context, delay);
            if (player1Hand.size() >= 2) {
                Card player1Card2 = player1Hand.get(1);
                player1Card2ImageView.flipToForward(player1Card2, context, delay);
                if (player1Hand.size() >= 3) {
                    Card player1Card3 = player1Hand.get(2);
                    player1Card3ImageView.flipToForward(player1Card3, context, delay);
                }
            }
        }
    }

    private void disableInput() {
        player1Card1ImageView.setOnClickListener(null);
        player1Card2ImageView.setOnClickListener(null);
        player1Card3ImageView.setOnClickListener(null);
    }

    private void move(Card card, final Card nextCard) {
        disableInput();

        game.play(card);
        refreshCards();
        renderBoard();

        boolean roundWon = game.roundWinner();
        if(roundWon) {
            String winner = game.checkForRoundWinner().getName();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    game.collectSurfaceCards();
                }
            }, 1000);

            progressDialog = new ProgressDialog(RemoteActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.dialog);
            TextView text = progressDialog.findViewById(R.id.text);
            text.setText(winner + " won the round");
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            final Handler dialogHandler = new Handler();
            dialogHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            }, 2000);

            surfaceCardImageView.flipToBack(context, 2800, cardBack);
            surfaceCard2ImageView.flipToBack(context, 2800, cardBack);

            final Handler handlerAnim = new Handler();
            handlerAnim.postDelayed(new Runnable() {
                @Override
                public void run() {
                    surfaceCardImageView.startAnimation(slideRight);
                    surfaceCard2ImageView.startAnimation(slideRight);

                    renderBoard();
                }
            }, 3200);

            final Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (game.getRound() <= 18) {
                        game.drawCards(nextCard);
                        refreshCards();
                        renderBoard();
                    }
                }
            }, 3800);

            if (game.getCurrentPlayer().equals(game.getPlayer1())) {
                final Handler enableInputHandler = new Handler();
                if (game.getCurrentPlayer().isComputer()) {
                    enableInputHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moveTask = new Move(0);
                            moveTask.execute();
                        }
                    }, 4500);
                } else {
                    enableInputHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            enableInput();
                        }
                    }, 4500);
                }
            } else {
                final Handler fetchOpponentsMoveHandler = new Handler();
                fetchOpponentsMoveHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchTask = new FetchOpponentsMoveTask();
                        fetchTask.execute();
                    }
                }, 3200);
            }
            game.setRound(game.getRound()+1);
        } else {
            if (game.getCurrentPlayer().equals(game.getPlayer1())) {
                final Handler enableInputHandler = new Handler();
                if (game.getCurrentPlayer().isComputer()) {
                    enableInputHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moveTask = new Move(0);
                            moveTask.execute();
                        }
                    }, 1500);
                } else {
                    enableInputHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            enableInput();
                        }
                    }, 500);
                }
            } else {
                final Handler fetchOpponentsMoveHandler = new Handler();
                fetchOpponentsMoveHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchTask = new FetchOpponentsMoveTask();
                        fetchTask.execute();
                    }
                }, 1500);
            }
        }
    }

    private void renderBoard() {
        if (game.isGameOver() && !gameOver) {
            moveTask.cancel(true);
            fetchTask.cancel(true);
            gameOver = true;
            disableInput();
            ScoreRepository repo = new SQLiteScoreRepository(getApplicationContext());
            repo.save(game.getGameScore());
            Intent intent = new Intent(this, GameOverActivity.class);
            intent.putExtra("score", game.getGameScore());
            startActivity(intent);
        } else {
            List<Card> surfaceCards = game.getSurfaceCards();
            List<Card> player1Hand = game.getPlayer1().getHand();
            List<Card> player2Hand = game.getPlayer2().getHand();

            Integer player1Score = game.getGameScore().getPlayer1Score();
            Integer player2Score = game.getGameScore().getPlayer2Score();

            scorePlayer1.setText(game.getPlayer1().getName() + " \nScore: " + Integer.toString(player1Score));
            scorePlayer2.setText("Player 2 \nScore: " + Integer.toString(player2Score));

            deckImageView.setVisibility(View.GONE);
            briscolaImageView.setVisibility(View.GONE);
            if (game.getRound() <= 17) {
                deckImageView.setImageResource(cardBack);
                deckImageView.setVisibility(View.VISIBLE);
                Card briscolaCard = game.getTrumpCard();
                briscolaImageView.setImageResource(Util.getResourceId(briscolaCard.getImageName(), getApplicationContext()));
                briscolaImageView.setVisibility(View.VISIBLE);
            }

            surfaceCardImageView.setVisibility(View.INVISIBLE);
            surfaceCard2ImageView.setVisibility(View.INVISIBLE);
            if (surfaceCards.size() >= 1) {
                Card surfaceCard1 = surfaceCards.get(0);
                surfaceCardImageView.setImageResource(Util.getResourceId(surfaceCard1.getImageName(), getApplicationContext()));
                surfaceCardImageView.setVisibility(View.VISIBLE);
                if (surfaceCards.size() >= 2) {
                    Card surfaceCard2 = surfaceCards.get(1);
                    surfaceCard2ImageView.setImageResource(Util.getResourceId(surfaceCard2.getImageName(), getApplicationContext()));
                    surfaceCard2ImageView.setVisibility(View.VISIBLE);
                }
            }

            player1Card1ImageView.setVisibility(View.INVISIBLE);
            player1Card2ImageView.setVisibility(View.INVISIBLE);
            player1Card3ImageView.setVisibility(View.INVISIBLE);

            if (player1Hand.size() >= 1) {
                //            player1Card1ImageView.setImageResource(R.drawable.card_back);
                player1Card1ImageView.setVisibility(View.VISIBLE);
                if (player1Hand.size() >= 2) {
                    //                player1Card2ImageView.setImageResource(R.drawable.card_back);
                    player1Card2ImageView.setVisibility(View.VISIBLE);
                    if (player1Hand.size() >= 3) {
                        //                    player1Card3ImageView.setImageResource(R.drawable.card_back);
                        player1Card3ImageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            player2Card1ImageView.setVisibility(View.INVISIBLE);
            player2Card2ImageView.setVisibility(View.INVISIBLE);
            player2Card3ImageView.setVisibility(View.INVISIBLE);

            if (player2Hand.size() >= 1) {
                player2Card1ImageView.setImageResource(cardBack);
                player2Card1ImageView.setVisibility(View.VISIBLE);
                if (player2Hand.size() >= 2) {
                    player2Card2ImageView.setImageResource(cardBack);
                    player2Card2ImageView.setVisibility(View.VISIBLE);
                    if (player2Hand.size() >= 3) {
                        player2Card3ImageView.setImageResource(cardBack);
                        player2Card3ImageView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private void refreshCards() {
        List<Card> player1Hand = game.getPlayer1().getHand();
        if (player1Hand.size() >= 1) {
            player1Card1ImageView.setImageResource(Util.getResourceId(player1Hand.get(0).getImageName(), getApplicationContext()));
            if (player1Hand.size() >= 2) {
                player1Card2ImageView.setImageResource(Util.getResourceId(player1Hand.get(1).getImageName(), getApplicationContext()));
                if (player1Hand.size() >= 3) {
                    player1Card3ImageView.setImageResource(Util.getResourceId(player1Hand.get(2).getImageName(), getApplicationContext()));
                }
            }
        }
    }

    public class FetchOpponentsMoveTask extends AsyncTask<Void, Void, List<Card>> {

        private String errorMessage;
        private OkHttpClient client = null;
        private Boolean cancel = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Util.lockOrientation(RemoteActivity.this);
            if (progressDialog == null) {
                final FetchOpponentsMoveTask fetchOpponentsMove = this;
                progressDialog = new ProgressDialog(RemoteActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Waiting for opponent...");
                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        fetchOpponentsMove.cancel(true);
                        new DeleteTask().execute();
                        if (client != null) {
                            client.dispatcher().cancelAll();
                        }
                        cancel = true;
                        if (progressDialog != null) {
                            try {
                                progressDialog.dismiss();
                            } catch (Exception ex) {}
                            progressDialog = null;
                        }
                        Util.unlockOrientation(RemoteActivity.this);
                    }
                });
                progressDialog.show();
            }
        }

        @Override
        protected List<Card> doInBackground(Void... voids) {
            Boolean timeout = true;
            List<Card> cards = new ArrayList<>();
            RemoteGameService remoteGameService;
            while(timeout && !gameOver && !cancel) {
                try {
                    remoteGameService = new RemoteGameServiceImpl();
                    cards = remoteGameService.fetchOpponentsMove(game.getGameUrl());
                    timeout = false;
                } catch (SocketTimeoutException e) {
                    timeout = true;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    timeout = false;
                    errorMessage = "Something went wrong. Please try again later.";
                } catch (UnauthorizedException | GameTerminatedException | NotYourTurnException | InvalidCardException e) {
                    e.printStackTrace();
                    timeout = false;
                    errorMessage = e.getMessage();
                }
            }

            return cards;
        }

        @Override
        protected void onPostExecute(final List<Card> cards) {
            if (progressDialog != null) {
                try {
                    progressDialog.dismiss();
                } catch (Exception ex) {}
                progressDialog = null;
            }

            if (errorMessage == null && cards.size() >= 1) {
                game.getPlayer2().getCard(0);
                Card opponentsCard = cards.get(0);
                opponentsCard.setPlayedBy(game.getPlayer2().getName());

                Card nextCard = null;
                if (cards.size() > 1) {
                    nextCard = cards.get(1);
                }

                move(opponentsCard, nextCard);
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(RemoteActivity.this).create();
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
            Util.unlockOrientation(RemoteActivity.this);
        }

        @Override
        protected void onCancelled() {
            if (progressDialog != null) {
                try {
                    progressDialog.dismiss();
                } catch (Exception ex) {}
                progressDialog = null;
            }
            Util.unlockOrientation(RemoteActivity.this);
        }
    }

    public class Move extends AsyncTask<Void, Void, Card> {

        private Card cardPlayed;
        private String errorMessage;
        private OkHttpClient client = null;
        private Boolean cancel = false;

        public Move(Integer cardInHand) {
            this.cardPlayed = game.getPlayer1().getCard(cardInHand);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Util.lockOrientation(RemoteActivity.this);
            if (progressDialog == null) {
                final Move moveTask = this;
                progressDialog = new ProgressDialog(RemoteActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Please Wait");
                progressDialog.setMessage("Contacting game server...");
                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        moveTask.cancel(true);
                        new DeleteTask().execute();
                        if (client != null) {
                            client.dispatcher().cancelAll();
                        }
                        cancel = true;
                        if (progressDialog != null) {
                            try {
                                progressDialog.dismiss();
                            } catch (Exception ex) {}
                            progressDialog = null;
                        }
                        Util.unlockOrientation(RemoteActivity.this);
                    }
                });
                progressDialog.show();
            }
        }

        @Override
        protected Card doInBackground(Void... voids) {
            if (cardPlayed == null) {
                errorMessage = "No card has been played";
                return null;
            }

            Card nextCard = null;
            Boolean timeout = true;
            while(timeout && !gameOver && !cancel) {
                try {
                    RemoteGameService remoteGameService = new RemoteGameServiceImpl();
                    nextCard = remoteGameService.performMove(game.getGameUrl(), this.cardPlayed);
                    timeout = false;
                } catch (SocketTimeoutException e) {
                    timeout = true;
                } catch (IOException e) {
                    timeout = false;
                    e.printStackTrace();
                    errorMessage = "Something went wrong. Please try again later.";
                } catch (UnauthorizedException | GameTerminatedException e) {
                    timeout = false;
                    e.printStackTrace();
                    errorMessage = e.getMessage();
                } catch (NotYourTurnException | InvalidCardException e) {
                    timeout = true;
                    e.printStackTrace();
                    errorMessage = e.getMessage();
                }
            }

            return nextCard;
        }

        @Override
        protected void onPostExecute(final Card card) {
            if (progressDialog != null) {
                try {
                    progressDialog.dismiss();
                } catch (Exception ex) {}
                progressDialog = null;
            }

            if (errorMessage == null) {
                Player player1 = game.getPlayer1();
                this.cardPlayed.setPlayedBy(player1.getName());
                move(this.cardPlayed, card);
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(RemoteActivity.this).create();
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

            Util.unlockOrientation(RemoteActivity.this);
        }

        @Override
        protected void onCancelled() {
            try {
                progressDialog.dismiss();
            } catch (Exception ex) {}
            progressDialog = null;
            Util.unlockOrientation(RemoteActivity.this);
        }
    }

    public class DeleteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            RemoteGameService remoteGameService = new RemoteGameServiceImpl();
            try {
                remoteGameService.deleteGame(game.getGameUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        new DeleteTask().execute();
        startActivity(new Intent(RemoteActivity.this, RoomActivity.class));
    }

    @Override
    protected void onStop() {
//        new DeleteTask().execute();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        new DeleteTask().execute();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("game", game);
        super.onSaveInstanceState(savedInstanceState);
    }
}
