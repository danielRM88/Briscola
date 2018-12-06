package com.rosato.polimi.cardgame.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rosato.polimi.cardgame.R;
import com.rosato.polimi.cardgame.exceptions.InconsistentNumberOfCardsException;
import com.rosato.polimi.cardgame.exceptions.InvalidHandException;
import com.rosato.polimi.cardgame.models.Card;
import com.rosato.polimi.cardgame.models.Computer;
import com.rosato.polimi.cardgame.models.Game;
import com.rosato.polimi.cardgame.models.HumanPlayer;
import com.rosato.polimi.cardgame.models.RuntimeTypeAdapterFactory;
import com.rosato.polimi.cardgame.models.Settings;
import com.rosato.polimi.cardgame.models.Util;
import com.rosato.polimi.cardgame.models.interfaces.Player;
import com.rosato.polimi.cardgame.models.interfaces.ScoreRepository;
import com.rosato.polimi.cardgame.models.repositories.SQLiteScoreRepository;
import com.rosato.polimi.cardgame.views.CardImageView;

import java.util.List;

public class GameActivity extends Activity {

    private ProgressDialog progressDialog;

    private Game game;

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

    private boolean showPlayer1Hand = true;
    private boolean showPlayer2Hand = true;

    private Animation slideRight;

    private Context context;

    private int cardBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        context = getApplicationContext();

        if (savedInstanceState != null) {
            game = (Game) savedInstanceState.getSerializable("game");
        } else {
            game = (Game) getIntent().getSerializableExtra("game");
        }

        showPlayer1Hand = game.getSettings().isShowPlayer1Hand();
        showPlayer2Hand = game.getSettings().isShowPlayer2Hand();

        if (game.getSettings().getDeck().compareTo(Settings.BLUE_DECK) == 0) {
            cardBack = R.drawable.card_back;
        } else {
            cardBack = R.drawable.n_back;
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

        player1Card1ImageView.setImageResource(cardBack);
        player1Card2ImageView.setImageResource(cardBack);
        player1Card3ImageView.setImageResource(cardBack);
        renderBoard(true);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                game.collectSurfaceCards();
                renderBoard(true);
            }
        }, 500);

        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    game.drawRoundCards();
                } catch (InvalidHandException e) {
                    e.printStackTrace();
                }
                renderBoard(true);

                boolean player1Turn = game.getCurrentPlayerString().equals("0");

                if (game.getCurrentPlayer().isComputer()) {
                    final Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            move(0);
                        }
                    }, 500);
                } else if (player1Turn) {
                    flipForwardPlayer1(100, true);
                } else {
                    flipForwardPlayer2(100);
                }

                enableInput();
            }
        }, 1500);

        System.out.println("GAME SETUP");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        removeGame();
        saveGame(game);
        startActivity(new Intent(GameActivity.this, MenuActivity.class));
    }

    @Override
    protected void onDestroy() {
        removeGame();
        saveGame(game);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        removeGame();
        saveGame(game);
        super.onStop();
    }

    private void move(int cardInHand) {
        disableInput();
        String currentPlayer = game.getCurrentPlayerString();
        boolean player1Turn = currentPlayer.equals("0");
        try {
            game.play(cardInHand);
        } catch (InconsistentNumberOfCardsException e) {
            e.printStackTrace();
        }
        renderBoard(false);
        boolean roundWon = game.roundWinner();
        if (roundWon) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    game.collectSurfaceCards();
                }
            }, 1000);

            progressDialog = new ProgressDialog(GameActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.dialog);
            TextView text = progressDialog.findViewById(R.id.text);
            text.setText(game.checkForRoundWinner().getName()+" won the round");
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

                    renderBoard(false);
                }
            }, 3200);

            final Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        game.drawRoundCards();
                    } catch (InvalidHandException e) {
                        e.printStackTrace();
                    }
                    renderBoard(false);

                    String currentPlayer = game.getCurrentPlayerString();
                    boolean player1Turn = currentPlayer.equals("0");
                    if (game.getCurrentPlayer().isComputer()) {
                        final Handler handler3 = new Handler();
                        handler3.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                move(0);
                            }
                        }, 1000);
                    } else if (player1Turn) {
                        flipForwardPlayer1(1000, false);
                    } else {
                        flipForwardPlayer2(1000);
                    }

                }
            }, 3800);

            final Handler enableInputHandler = new Handler();
            enableInputHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    enableInput();
                }
            }, 4800);
        } else {
            final Handler handler5 = new Handler();
            handler5.postDelayed(new Runnable() {
                @Override
                public void run() {
                    renderBoard(false);

                    String currentPlayer = game.getCurrentPlayerString();
                    boolean player1Turn = currentPlayer.equals("0");
                    if (game.getCurrentPlayer().isComputer()) {
                        final Handler handler3 = new Handler();
                        handler3.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                move(0);
                            }
                        }, 0000);
                    } else if (player1Turn) {
                        flipForwardPlayer1(0, false);
                    } else {
                        flipForwardPlayer2(0);
                    }

                    enableInput();
                }
            }, 1000);
        }
    }

    private void disableInput() {
        player1Card1ImageView.setOnClickListener(null);
        player1Card2ImageView.setOnClickListener(null);
        player1Card3ImageView.setOnClickListener(null);

        player2Card1ImageView.setOnClickListener(null);
        player2Card2ImageView.setOnClickListener(null);
        player2Card3ImageView.setOnClickListener(null);
//        System.out.println("INPUT DISABLED!");
    }

    private void enableInput() {
        if (game.getCurrentPlayer().getName().equals(game.getPlayer1().getName()) && !game.getPlayer1().isComputer()) {
            player1Card1ImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Clicked!");
                    disableInput();
                    move(0);
                    renderBoard(false);
                }
            });

            player1Card2ImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Clicked!");
                    disableInput();
                    move(1);
                    renderBoard(false);
                }
            });

            player1Card3ImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Clicked!");
                    disableInput();
                    move(2);
                    renderBoard(false);
                }
            });
        }

        if (game.getCurrentPlayer().getName().equals(game.getPlayer2().getName()) && !game.getPlayer2().isComputer()) {
            player2Card1ImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Clicked!");
                    disableInput();
                    move(0);
                    renderBoard(false);
                }
            });

            player2Card2ImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Clicked!");
                    disableInput();
                    move(1);
                    renderBoard(false);
                }
            });

            player2Card3ImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Clicked!");
                    disableInput();
                    move(2);
                    renderBoard(false);
                }
            });
        }
    }

    private void renderBoard(boolean firstRender) {
        System.out.println("RENDERING BOARD!");

        if (game.isGameOver() && !gameOver) {
            gameOver = true;
            disableInput();
            ScoreRepository repo = new SQLiteScoreRepository(getApplicationContext());
            repo.save(game.getGameScore());
            removeGame();
            Intent intent = new Intent(this, GameOverActivity.class);
            intent.putExtra("score", game.getGameScore());
            startActivity(intent);
        } else {
            List<Card> deck = game.getDeck();
            List<Card> surfaceCards = game.getSurfaceCards();
            List<Card> player1Hand = game.getPlayer1().getHand();
            List<Card> player2Hand = game.getPlayer2().getHand();

            Integer player1Score = game.getGameScore().getPlayer1Score();
            Integer player2Score = game.getGameScore().getPlayer2Score();

            scorePlayer1.setText(game.getPlayer1().getName()+" \nScore: "+ Integer.toString(player1Score));
            scorePlayer2.setText(game.getPlayer2().getName()+" \nScore: "+ Integer.toString(player2Score));

            deckImageView.setVisibility(View.GONE);
            briscolaImageView.setVisibility(View.GONE);
            if (!deck.isEmpty()) {
                deckImageView.setImageResource(cardBack);
                deckImageView.setVisibility(View.VISIBLE);
                Card briscolaCard = game.getDeck().get(game.getDeck().size() - 1);
                briscolaImageView.setImageResource(Util.getResourceId(briscolaCard.getImageName(), context));
                briscolaImageView.setVisibility(View.VISIBLE);
            }

            surfaceCardImageView.setVisibility(View.INVISIBLE);
            surfaceCard2ImageView.setVisibility(View.INVISIBLE);
            if (surfaceCards.size() >= 1) {
                Card surfaceCard1 = surfaceCards.get(0);
                surfaceCardImageView.setImageResource(Util.getResourceId(surfaceCard1.getImageName(), context));
                surfaceCardImageView.setVisibility(View.VISIBLE);
                if (surfaceCards.size() >= 2) {
                    Card surfaceCard2 = surfaceCards.get(1);
                    surfaceCard2ImageView.setImageResource(Util.getResourceId(surfaceCard2.getImageName(), context));
                    surfaceCard2ImageView.setVisibility(View.VISIBLE);
                }
            }

            player1Card1ImageView.setVisibility(View.INVISIBLE);
            player1Card2ImageView.setVisibility(View.INVISIBLE);
            player1Card3ImageView.setVisibility(View.INVISIBLE);

            if (player1Hand.size() >= 1) {
                if ((!game.getPlayer2().isComputer() || game.getPlayer1().isComputer() || firstRender) && !showPlayer1Hand) {
                    player1Card1ImageView.setImageResource(cardBack);
                } else {
                    player1Card1ImageView.setImageResource(Util.getResourceId(player1Hand.get(0).getImageName(), context));
                }
                player1Card1ImageView.setVisibility(View.VISIBLE);

                if (player1Hand.size() >= 2) {
                    if ((!game.getPlayer2().isComputer() || game.getPlayer1().isComputer() || firstRender) && !showPlayer1Hand) {
                        player1Card2ImageView.setImageResource(cardBack);
                    } else {
                        player1Card2ImageView.setImageResource(Util.getResourceId(player1Hand.get(1).getImageName(), context));
                    }
                    player1Card2ImageView.setVisibility(View.VISIBLE);
                    if (player1Hand.size() >= 3) {
                        if ((!game.getPlayer2().isComputer() || game.getPlayer1().isComputer() || firstRender) && !showPlayer1Hand) {
                            player1Card3ImageView.setImageResource(cardBack);
                        } else {
                            player1Card3ImageView.setImageResource(Util.getResourceId(player1Hand.get(2).getImageName(), context));
                        }
                        player1Card3ImageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            player2Card1ImageView.setVisibility(View.INVISIBLE);
            player2Card2ImageView.setVisibility(View.INVISIBLE);
            player2Card3ImageView.setVisibility(View.INVISIBLE);

            if (player2Hand.size() >= 1) {
                if(showPlayer2Hand) {
                    player2Card1ImageView.setImageResource(Util.getResourceId(player2Hand.get(0).getImageName(), context));
                } else {
                    player2Card1ImageView.setImageResource(cardBack);
                }
                player2Card1ImageView.setVisibility(View.VISIBLE);
                if (player2Hand.size() >= 2) {
                    if(showPlayer2Hand) {
                        player2Card2ImageView.setImageResource(Util.getResourceId(player2Hand.get(1).getImageName(), context));
                    } else {
                        player2Card2ImageView.setImageResource(cardBack);
                    }
                    player2Card2ImageView.setVisibility(View.VISIBLE);
                    if (player2Hand.size() >= 3) {
                        if(showPlayer2Hand) {
                            player2Card3ImageView.setImageResource(Util.getResourceId(player2Hand.get(2).getImageName(), context));
                        } else {
                            player2Card3ImageView.setImageResource(cardBack);
                        }
                        player2Card3ImageView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private void flipForwardPlayer1(int delay, boolean force) {
        List<Card> player1Hand = game.getPlayer1().getHand();
        Player player2 = game.getPlayer2();
//        if (showPlayer1Hand) {
            if (player1Hand.size() >= 1) {
                Card player1Card1 = player1Hand.get(0);
                if (player2.isComputer() && !force) {
                    player1Card1ImageView.setImageResource(Util.getResourceId(player1Card1.getImageName(), getApplicationContext()));
                } else {
                    player1Card1ImageView.flipToForward(player1Card1, context, delay);
                }
                if (player1Hand.size() >= 2) {
                    Card player1Card2 = player1Hand.get(1);
                    if (player2.isComputer() && !force) {
                        player1Card2ImageView.setImageResource(Util.getResourceId(player1Card2.getImageName(), getApplicationContext()));
                    } else {
                        player1Card2ImageView.flipToForward(player1Card2, context, delay);
                    }
                    if (player1Hand.size() >= 3) {
                        Card player1Card3 = player1Hand.get(2);
                        if (player2.isComputer() && !force) {
                            player1Card3ImageView.setImageResource(Util.getResourceId(player1Card3.getImageName(), getApplicationContext()));
                        } else {
                            player1Card3ImageView.flipToForward(player1Card3, context, delay);
                        }
                    }
                }
            }
//        }
    }

    private void flipForwardPlayer2(int delay) {
        List<Card> player2Hand = game.getPlayer2().getHand();
//        if (showPlayer2Hand) {
            if (player2Hand.size() >= 1) {
                Card player2Card1 = player2Hand.get(0);
                player2Card1ImageView.flipToForward(player2Card1, context, delay);
                if (player2Hand.size() >= 2) {
                    Card player2Card2 = player2Hand.get(1);
                    player2Card2ImageView.flipToForward(player2Card2, context, delay);
                    if (player2Hand.size() >= 3) {
                        Card player2Card3 = player2Hand.get(2);
                        player2Card3ImageView.flipToForward(player2Card3, context, delay);
                    }
                }
            }
//        }
    }

    public void saveGame(Game game) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SettingsActivity.PREFS_NAME, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        final RuntimeTypeAdapterFactory<Player> typeFactory = RuntimeTypeAdapterFactory
                .of(Player.class, "type")
                .registerSubtype(HumanPlayer.class)
                .registerSubtype(Computer.class);
        final Gson gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();
        String serializedSettings = gson.toJson(game);
        sharedPreferencesEditor.putString(Game.GAME_KEY, serializedSettings);
        sharedPreferencesEditor.apply();
    }

    public void removeGame() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SettingsActivity.PREFS_NAME, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.remove(Game.GAME_KEY);
        sharedPreferencesEditor.apply();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("game", game);
        super.onSaveInstanceState(savedInstanceState);
    }
}
