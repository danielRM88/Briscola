package com.rosato.polimi.cardgame.views;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.rosato.polimi.cardgame.R;
import com.rosato.polimi.cardgame.models.Card;

/**
 * Created by danielrosato on 12/29/17.
 */

public class CardImageView extends android.support.v7.widget.AppCompatImageView {

    private boolean back = true;
    private Animation flipAnimation;
    private Animation slideRightAnimation;

    public CardImageView(Context context) {
        super(context);
        flipAnimation = AnimationUtils.loadAnimation(context, R.anim.flip);
        slideRightAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_left);
    }

    public CardImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        flipAnimation = AnimationUtils.loadAnimation(context, R.anim.flip);
        slideRightAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_left);
    }

    public CardImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        flipAnimation = AnimationUtils.loadAnimation(context, R.anim.flip);
        slideRightAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_left);
    }

    public boolean isBack() {
        return back;
    }

    public void setBack(boolean back) {
        this.back = back;
    }

    public void flipToForward(Card card, Context context, int delay) {
        final ImageView imageView = this;
        final Handler flipAnimationHandler = new Handler();
        final Handler changeToBackHandler = new Handler();
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(card.getImageName(), "drawable", context.getPackageName());

        flipAnimationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.startAnimation(flipAnimation);
            }
        }, delay);
        changeToBackHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(resourceId);
            }
        }, (delay+200));
    }

    public void flipToBack(Context context, int delay, final int cardBack) {
        final ImageView imageView = this;
        final Handler flipAnimationHandler = new Handler();
        final Handler changeToBackHandler = new Handler();
        flipAnimationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.startAnimation(flipAnimation);
            }
        }, delay);
        changeToBackHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(cardBack);
            }
        }, (delay+200));
    }

    public void slideRight(int delay) {
        final ImageView imageView = this;
        final Handler slideRight = new Handler();
        slideRight.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.startAnimation(slideRightAnimation);
            }
        }, delay);
    }
}
