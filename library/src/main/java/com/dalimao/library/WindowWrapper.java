package com.dalimao.library;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.dalimao.library.util.DeviceInfoUtil;


/**
 * a abstract business logic class, define a set of window life-cycle and logic callback<br>
 * It's subclasses need to implement the abstract method to define the window's attributes (such as layout, view, action etc)<br>
 *
 */
public abstract class WindowWrapper {

    public final int WindowId;

    protected Context mContext;
    protected StandOutWindowManager mWindowManager;
    protected boolean mIsShowing = false;
    protected Window mAnchorWindow;
    private Bundle mParams;
    public boolean isCreated = false;
    protected StandOutLayoutParams mStandOutLayoutParams;
    protected boolean canMove;

    public WindowWrapper(StandOutWindowManager manager, int id) {
        mWindowManager = manager;
        mContext = manager.getContext();
        WindowId = id;
        mStandOutLayoutParams  = new StandOutLayoutParams(mContext, WindowManager.LayoutParams.TYPE_PHONE, onRequestWindowFlags(),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0, 0);
        mStandOutLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
    }

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public StandOutLayoutParams getStandOutLayoutParams() {
        return mStandOutLayoutParams;
    }

    public void setStandOutLayoutParams(StandOutLayoutParams mStandOutLayoutParams) {
        this.mStandOutLayoutParams = mStandOutLayoutParams;
    }

    /**
     * return application context
     * @return
     */
    public Context getContext() {
        return mContext.getApplicationContext();
    }

    public StandOutWindowManager getWindowManager() {
        return mWindowManager;
    }

    public void setWindowAnchor(Window window) {
        this.mAnchorWindow = window;
    }
    
    public boolean isShowing() {
        return mIsShowing;
    }
    
    /**

     * Return the animation to play when the window corresponding to the id is
     * shown.
     *
     * @return The animation to play or null.
     */
    public Animation getShowAnimation() {
        return AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
    }

    /**
     * Return the animation to play when the window corresponding to the id is
     * hidden.
     *
     * @return The animation to play or null.
     */
    public Animation getHideAnimation() {
        return AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out);
    }

    /**
     * Return the animation to play when the window corresponding to the id is
     * closed.
     *
     * @return The animation to play or null.
     */
    public Animation getCloseAnimation() {
        return AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out);
    }

    /**
     * Return the {@link StandOutLayoutParams} for define the initial layout of window.
     * The system will set the layout params on the view for this StandOut
     * window. The layout params may be reused.
     *
     * @return the {@link StandOutLayoutParams} for define the layout of window.
     *         The layout params will be set on the window. The layout params
     *         returned will be reused whenever possible, minimizing the number
     *         of times onRequestLayoutParams() will be called.
     */
    public abstract StandOutLayoutParams onRequestLayoutParams();

    /**
     * Implement this method to change modify the behavior and appearance of the
     * window corresponding to the id.
     *
     * <p>
     This
     * method will be called many times, so keep it fast.
     *
     * <p>
     * Use bitwise OR (|) to set flags, and bitwise XOR (^) to unset flags.
     *
     * @return A combination of flags.
     */
    public int onRequestWindowFlags() {
        return 0;
    }

    /**
     * Create a new {@link View} and add it as a child
     * to the frame. The view will become the contents of this StandOut window.
     * The view MUST be newly created, and you MUST attach it to the frame.
     *
     * <p>
     * If you are inflating your view from XML, make sure you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)} to attach your
     * view to frame. Set the ViewGroup to be frame, and the boolean to true.
     *
     * <p>
     * If you are creating your view programmatically, make sure you use
     * {@link FrameLayout#addView(View)} to add your view to the frame.
     *
     * @param frame
     *            The {@link FrameLayout} to attach your view as a child to.
     */
    public abstract void onCreateAndAttachView(FrameLayout frame);

    /**
     * one step of life-cycle, invoke this before add window to android.os.WindowManager,
     * the anchor window will not null if caller pass,
     * then can update the window's layout that created by {@link #onRequestLayoutParams()}
     *
     * @param window the window binding with this wrapper
     * @param anchor the anchor window, maybe null if caller not pass it
     */
    public void onPrepareShow(Window window, Window anchor) {
    }

    /**
     * one step of life-cycle, invoke this after window add to android.os.WindowManager,
     * subclasses can implement this to do something on window display on screen
     *
     * @param window the window binding with this wrapper
     * @param args
     * @return
     */
    public boolean onShown(Window window, Bundle args) {
        mIsShowing = true;
        /*final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 16) {
            //api 16以上，全屏模式
            window.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }*/
        return false;
    }

    /**
     * one step of life-cycle, invoke when window remove from android.os.WindowManage but not from WindowCache,,
     * the window and this wrapper can reuse when call by {@link StandOutWindowManager again
     *
     * @param window the window binding with this wrapper
     * @return
     */
    public boolean onHidden(Window window) {
        mAnchorWindow = null;
        mIsShowing = false;
        return false;
    }

    /**
     * one step of life-cycle, invoke when window remove from android.os.WindowManage and WindowCache
     *
     * @param window the window binding with this wrapper
     * @return
     */
    public boolean onClosed(Window window) {
        mAnchorWindow = null;
        mIsShowing = false;
        return false;
    }

    /**
     * implement for handle the command
     * @see StandOutWindowManager#sendWindowCommand(Class, int, Bundle)
     *
     * @param type
     * @param data
     * @return
     */
    public boolean onReceiveCommand(int type, Bundle data) {
        return false;
    }

    public boolean onKeyEvent(KeyEvent event) {
        return false;
    }

    public void onBackKeyPressed() { }

    /**
     * Implement this callback to be alerted when a window corresponding to the
     * id is about to have its focus changed. This callback will occur before
     * the window's focus is changed.
     *
     * @param window
     *            The window about to be brought to the front.
     * @param focus
     *            Whether the window is gaining or losing focus.
     * @return Return true to cancel the window's focus from being changed, or
     *         false to continue.
     * @see StandOutWindowManager#focus(int)
     */
    public boolean onFocusChange(Window window, boolean focus) {
        return false;
    }

    /**
     * Implement this method to be alerted to touch events in the body of the
     * window corresponding to the id.
     *
     * @see {@link View.OnTouchListener#onTouch(View, MotionEvent)}
     *
     * @param window
     *            The window corresponding to the id, provided as a courtesy.
     * @param view
     *            The view where the event originated from.
     * @param event
     *            See linked method.
     */
    public boolean onTouchBody(Window window, View view, MotionEvent event) {
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE) {
            return handleOutSideAction();
        }
        return false;
    }

    public boolean handleOutSideAction() {
        return false;
    }

    /**
     * Implement this method to be alerted to when the window corresponding to
     * the id is moved.
     *
     * @param window
     *            The window corresponding to the id, provided as a courtesy.
     * @param view
     *            The view where the event originated from.
     * @param event
     *            See linked method.
     *
     */
    public void onMove(Window window, View view, MotionEvent event) { }

    protected boolean onPrepareMove(Window window, View view, MotionEvent event) {
        return false;
    }

    /**
     * show window when a window is already show
     * @param window the shown window
     * @param args
     */
    protected void onReShown(Window window, Bundle args) {

    }

    /**
     * 是否响应长按事件
     * **/
    public boolean handleLongClick(){
        return false;
    }

    public void onLongPressed(){

    }

    void setParams(Bundle params) {
        mParams = params;
        onInitParam(params);
    }

    protected abstract void onInitParam(Bundle params);

    public Bundle getBundleParams() {
        return mParams;
    }

}
