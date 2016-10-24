package com.dalimao.library;

import android.graphics.Point;
import android.os.Bundle;
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
        int screenHeight = DeviceInfoUtil.getScreenLongSize(mContext);
        int screenWidth = DeviceInfoUtil.getScreenShortSize(mContext);
        StandOutLayoutParams params  = new StandOutLayoutParams(mContext, WindowManager.LayoutParams.TYPE_TOAST, onRequestWindowFlags(),
                screenWidth,
                screenHeight,
                0, 0);
        return params;
    }

    @Override
    public int onRequestWindowFlags() {

        return  StandOutFlags.FLAG_BODY_MOVE_ENABLE
                | StandOutFlags.FLAG_WINDOW_HIDE_ENABLE
                | StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TAP
                | StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE;

    }
    @Override
    public void onCreateAndAttachView(FrameLayout frame) {

        prepareAnimations();
    }
    private void prepareAnimations() {

        updateOrInitAnimation();
    }

    private void updateOrInitAnimation() {

        Point point = new Point(DeviceInfoUtil.getScreenShortSize(getContext()) / 2, DeviceInfoUtil.getScreenLongSize(getContext()) / 2);
        float endx = new Float(point.x)/new Float(DeviceInfoUtil.getScreenLongSize(getContext()));
        float endy = new Float(point.y)/new Float(DeviceInfoUtil.getScreenShortSize(getContext())+0.1f);
        if (endx < 0){
            endx = 0;
        }
        if (endy < 0){
            endy = 0;
        }

        if (mAnimationCloseWindow != null){
            mAnimationCloseWindow.cancel();
        }
        if (mAnimationShowContainer != null){
            mAnimationShowContainer.cancel();
        }

        mAnimationCloseWindow = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF,
                endx, Animation.RELATIVE_TO_SELF,endy);
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


        mAnimationShowContainer = new ScaleAnimation(0, 1.0f, 0, 1.0f, Animation.RELATIVE_TO_SELF,
                endx, Animation.RELATIVE_TO_SELF,
                endy);
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
