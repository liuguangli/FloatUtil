package com.dalimao.library;

import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;


import com.dalimao.library.constants.StandOutFlags;
import com.dalimao.library.util.DeviceInfoUtil;

/**
 * Created by liuguangli on 16/10/21.
 */
public class CommonWindowWrapper extends WindowWrapper {
    private Animation mAnimationShowContainer;
    private Animation mAnimationCloseWindow;

    private boolean mAnimating;


    public CommonWindowWrapper(StandOutWindowManager manager, Integer id) {
        super(manager, id);
    }

    @Override
    public void setWindowAnchor(Window window) {
        super.setWindowAnchor(window);
    }

    @Override
    public StandOutLayoutParams onRequestLayoutParams() {

        return mStandOutLayoutParams;
    }

    @Override
    public int onRequestWindowFlags() {

        return  StandOutFlags.FLAG_BODY_MOVE_ENABLE
                | StandOutFlags.FLAG_WINDOW_HIDE_ENABLE
                | StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TAP
                | StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE;

    }

    @Override
    protected boolean onPrepareMove(Window window, View view, MotionEvent event) {
        return !canMove;
    }

    @Override
    public void onCreateAndAttachView(FrameLayout frame) {

        prepareAnimations();
    }
    private void prepareAnimations() {

        updateOrInitAnimation();
    }
    @Override
    public boolean handleOutSideAction() {
        if (mAnimating) {
            return super.handleOutSideAction();
        }


        return super.handleOutSideAction();
    }

    private void updateOrInitAnimation() {


        if (mAnimationCloseWindow != null){
            mAnimationCloseWindow.cancel();
        }
        if (mAnimationShowContainer != null){
            mAnimationShowContainer.cancel();
        }

        mAnimationCloseWindow = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        mAnimationCloseWindow.setFillAfter(true);
        mAnimationCloseWindow.setDuration(300);
        mAnimationCloseWindow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAnimating = false;


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        mAnimationShowContainer = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mAnimationShowContainer.setFillAfter(true);
        mAnimationShowContainer.setDuration(300);
        mAnimationShowContainer.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }


    @Override
    public Animation getShowAnimation() {
        return mAnimationShowContainer;
    }

    @Override
    public Animation getHideAnimation() {
        return mAnimationCloseWindow;
    }

    @Override
    public Animation getCloseAnimation() {
        return mAnimationCloseWindow;
    }

    @Override
    protected void onInitParam(Bundle params) {

    }
}
