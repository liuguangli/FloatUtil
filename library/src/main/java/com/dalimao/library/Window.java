package com.dalimao.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.dalimao.library.constants.StandOutFlags;
import com.dalimao.library.util.DeviceInfoUtil;
import com.dalimao.library.util.Utils;


/**
 * Special view that represents a floating window.
 */
public final class Window extends FrameLayout {

    public static final int VISIBILITY_GONE = 0;
    public static final int VISIBILITY_VISIBLE = 1;
    public static final int VISIBILITY_TRANSITION = 2;

    static final String TAG = "Window";

    public final int id;
    /**
     * Whether the window is shown, hidden/closed, or in transition.
     */
    public int visibility;

    /**
     * Whether the window is focused.
     */
    public boolean focused;

    /**
     * Original params from {@link WindowWrapper#onRequestLayoutParams()} (int, Window)}.
     */
    public StandOutLayoutParams originalParams;

    /**
     * Original flags from {@link WindowWrapper#onRequestWindowFlags()} (int)}.
     */
    public int flags;

    /**
     * Touch information of the window.
     */
    public TouchInfo touchInfo;

    /**
     * Data attached to the window.
     */
    public Bundle data;

    /**
     * Width and height of the screen.
     */
    //int displayWidth, displayHeight;

    /**
     * Context of the window.
     */
    private final Context mContext;

    public WindowWrapper getWindowWrapper() {
        return mWindowWrapper;
    }

    private WindowWrapper mWindowWrapper;
    private View.OnClickListener mOnClickListener;
    private int mLastMotionX, mLastMotionY;
    //是否移动了
    private boolean isMoved;
    //长按的runnable
    private Runnable mLongPressRunnable;
    //移动的阈值
    private static final int TOUCH_SLOP = 20;





    public Window(WindowWrapper windowWrapper) {
        super(windowWrapper.getContext());

        mContext = windowWrapper.getContext();
        mWindowWrapper = windowWrapper;

        this.id = mWindowWrapper.WindowId;
        this.originalParams = windowWrapper.onRequestLayoutParams();
        this.flags = windowWrapper.onRequestWindowFlags();
        this.touchInfo = new TouchInfo();
        touchInfo.ratio = (float) originalParams.width / originalParams.height;
        this.data = new Bundle();

        // create the window contents
        FrameLayout content = new FrameLayout(mContext);
        content.setId(android.R.id.content);
        addView(content);

        mLongPressRunnable = new Runnable() {

            @Override
            public void run() {
                if (mWindowWrapper.handleLongClick()){
                    touchInfo.isLongPress = true;
                    mWindowWrapper.onLongPressed();
                }
            }
        };

        content.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // pass all touch events to the implementation
                boolean consumed = false;

                final StandOutWindowManager windowManager = mWindowWrapper.getWindowManager();


                dispatchLongPress(event);
                // handle move and bring to front
                consumed = windowManager.onTouchHandleMove(Window.this, v, event)
                        || consumed;

                // alert implementation
                consumed = mWindowWrapper.onTouchBody(Window.this, v, event)
                        || consumed;

                return consumed;
            }
        });


        // attach the view corresponding to the id from the
        // implementation
        windowWrapper.onCreateAndAttachView(content);

        // make sure the implementation attached the view
        if (content.getChildCount() == 0) {
            Log.e(TAG, "You must attach your view to the given frame in onCreateAndAttachView()");
        }

        // attach the existing tag from the frame to the window
        setTag(content.getTag());
        windowWrapper.isCreated = true;
    }


    public OnClickListener getOnClickListener() {
        return mOnClickListener;
    }
    @Override
    public void addView(View child) {
        FrameLayout content = (FrameLayout) findViewById(android.R.id.content);
        if (content != null) {
            content.addView(child);
        } else {
            super.addView(child);
        }

    }






    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // focus window
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                final StandOutWindowManager windowManager = mWindowWrapper.getWindowManager();
                if (windowManager.getFocusedWindow() != this) {
                    if (windowManager.isExistingId(id)) {
                        windowManager.focus(id);
                    }
                }
                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // handle touching outside
        switch (event.getAction()) {
            case MotionEvent.ACTION_OUTSIDE:
                final StandOutWindowManager windowManager = mWindowWrapper.getWindowManager();
                // unfocus window
                if (windowManager.getFocusedWindow() == this) {
                    windowManager.unfocus(this);
                }
                // notify implementation that ACTION_OUTSIDE occurred
                mWindowWrapper.onTouchBody(this, this, event);
                break;
        }

        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mWindowWrapper.onKeyEvent(event)) {
            Log.d(TAG, "Window " + id + " key event " + event
                    + " cancelled by implementation.");
            return false;
        }

        if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    final StandOutWindowManager windowManager = mWindowWrapper.getWindowManager();
                    windowManager.unfocus(this);
                    mWindowWrapper.onBackKeyPressed();
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    /**
     * Request or remove the focus from this window.
     *
     * @param focus Whether we want to gain or lose focus.
     * @return True if focus changed successfully, false if it failed.
     */
    public boolean onFocus(boolean focus) {

        if (!Utils.isSet(flags, StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE)) {
            // window is focusable

            if (focus == focused) {
                // window already focused/unfocused
                return false;
            }

            focused = focus;

            final StandOutWindowManager windowManager = mWindowWrapper.getWindowManager();
            // alert callbacks and cancel if instructed
            if (mWindowWrapper.onFocusChange(this, focus)) {
                Log.d(TAG, "Window " + id + " focus change "
                        + (focus ? "(true)" : "(false)")
                        + " cancelled by implementation.");
                focused = !focus;
                return false;
            }

            // set window menuManager params
            StandOutLayoutParams params = getLayoutParams();
            params.setFocusFlag(focus);
            windowManager.updateViewLayout(id, params);

            if (focus) {
                windowManager.setFocusedWindow(this);
            } else {
                if (windowManager.getFocusedWindow() == this) {
                    windowManager.setFocusedWindow(null);
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params instanceof StandOutLayoutParams) {
            super.setLayoutParams(params);
        } else {
            throw new IllegalArgumentException(
                    "Window"
                            + id
                            + ": LayoutParams must be an instance of StandOutLayoutParams.");
        }
    }

    /**
     * Convenience method to start editting the size and position of this
     * window. Make sure you call {@link android.content.SharedPreferences.Editor#commit()} when you are done to
     * update the window.
     *
     * @return The Editor associated with this window.
     */
    public Editor edit() {
        return new Editor();
    }

    @Override
    public StandOutLayoutParams getLayoutParams() {
        StandOutLayoutParams params = (StandOutLayoutParams) super
                .getLayoutParams();
        if (params == null) {
            params = originalParams;
        }
        return params;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 19) {
            //api 16以上，全屏模式
            hideNavigateBar();
            //setOnSystemUiVisibilityChangeListener(onSystemUiVisibilityChangeListener);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void hideNavigateBar() {
        setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    /**
     * Convenient way to resize or reposition a Window. The Editor allows you to
     * easily resize and reposition the window around anchor points.
     *
     * @author Mark Wei <markwei@gmail.com>
     */
    public class Editor {
        /**
         * Special value for width, height, x, or y positions that represents
         * that the value should not be changed.
         */
        public static final int UNCHANGED = Integer.MIN_VALUE;

        /**
         * Layout params of the window associated with this Editor.
         */
        StandOutLayoutParams mParams;

        /**
         * The position of the anchor point as a percentage of the window's
         * width/height. The anchor point is only used by the {@link android.content.SharedPreferences.Editor}.
         * <p/>
         * <p/>
         * The anchor point effects the following methods:
         * <p/>
         * <p/>
         * {@link #setPosition(int, int)}, {@link #setPosition(int, int)}.
         * <p/>
         * The window will move, expand, or shrink around the anchor point.
         * <p/>
         * <p/>
         * Values must be between 0 and 1, inclusive. 0 means the left/top, 0.5
         * is the center, 1 is the right/bottom.
         */
        float anchorX, anchorY;

        public Editor() {
            mParams = getLayoutParams();
            anchorX = anchorY = 0;
        }


        /**
         * Set the position of this window as percentages of max screen size.
         * The window's top-left corner will be positioned at the given x and y,
         * unless you've set a different anchor point with

         * <p/>
         * Changes will not applied until you {@link #commit()}.
         *
         * @param percentWidth
         * @param percentHeight
         * @return The same Editor, useful for method chaining.
         */
        public Editor setPosition(float percentWidth, float percentHeight) {
            return setPosition((int) (DeviceInfoUtil.getScreenLongSize(getContext()) * percentWidth),
                    (int) (DeviceInfoUtil.getScreenShortSize(getContext()) * percentHeight));
        }

        /**
         * Set the position of this window in absolute pixels. The window's
         * top-left corner will be positioned at the given x and y, unless
         * you've set a different anchor point with

         * <p/>
         * Changes will not applied until you {@link #commit()}.
         *
         * @param x
         * @param y
         * @return The same Editor, useful for method chaining.
         */
        public Editor setPosition(int x, int y) {
            return setPosition(x, y, false);
        }

        /**
         * Set the position of this window in absolute pixels. The window's
         * top-left corner will be positioned at the given x and y, unless
         * you've set a different anchor point with

         * <p/>
         * Changes will not applied until you {@link #commit()}.
         *
         * @param x
         * @param y
         * @param skip Don't call {@link #setPosition(int, int)} and
         *
         * @return The same Editor, useful for method chaining.
         */
        private Editor setPosition(int x, int y, boolean skip) {
            if (mParams != null) {
                if (anchorX < 0 || anchorX > 1 || anchorY < 0 || anchorY > 1) {
                    throw new IllegalStateException(
                            "Anchor point must be between 0 and 1, inclusive.");
                }

                // sets the x and y correctly according to anchorX and
                // anchorY
                if (x != UNCHANGED) {
                    mParams.x = (int) (x - mParams.width * anchorX);
                }
                if (y != UNCHANGED) {
                    mParams.y = (int) (y - mParams.height * anchorY);
                }

                if (Utils.isSet(flags,
                        StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE)) {


                    // keep window inside edges
                    if (DeviceInfoUtil.getOrientation(getContext()) == DeviceInfoUtil.LANDSCAPE){
                        mParams.x = Math.min(Math.max(mParams.x, 0), DeviceInfoUtil.getScreenLongSize(getContext())
                                - mParams.width);
                        mParams.y = Math.min(Math.max(mParams.y, 0), DeviceInfoUtil.getScreenShortSize(getContext())
                                - mParams.height);
                    } else {
                        mParams.y = Math.min(Math.max(mParams.y, 0), DeviceInfoUtil.getScreenLongSize(getContext())
                                - mParams.width);
                        mParams.x = Math.min(Math.max(mParams.x, 0), DeviceInfoUtil.getScreenShortSize(getContext())
                                - mParams.height);
                    }

                } else if (Utils.isSet(flags,
                        StandOutFlags.FLAG_WINDOW_EDGE_Y_LIMITS_ENABLE)) {
                    // if gravity is not TOP|LEFT throw exception
                    if (mParams.gravity != (Gravity.TOP | Gravity.LEFT)) {
                        throw new IllegalStateException(
                                "The window "
                                        + id
                                        + " gravity must be TOP|LEFT if FLAG_WINDOW_EDGE_LIMITS_ENABLE or FLAG_WINDOW_EDGE_TILE_ENABLE is set.");
                    }

                    // keep window inside y edges
                    mParams.y = Math.min(Math.max(mParams.y, 0), DeviceInfoUtil.getScreenShortSize(getContext())
                            - mParams.height);
                }
            }

            return this;
        }

        /**
         * Commit the changes to this window. Updates the layout. This Editor
         * cannot be used after you commit.
         */
        public void commit() {
            if (mParams != null) {
                final StandOutWindowManager windowManager = mWindowWrapper.getWindowManager();
                if (windowManager.isExistingId(id)) {
                    windowManager.updateViewLayout(id, mParams);
                }
                mParams = null;
            }
        }
    }

    public boolean dispatchLongPress(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                isMoved = false;
                postDelayed(mLongPressRunnable, 380);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isMoved) break;
                if(Math.abs(mLastMotionX - x) > TOUCH_SLOP
                        || Math.abs(mLastMotionY - y) > TOUCH_SLOP) {
                    //移动超过阈值，则表示移动了
                    isMoved = true;
                    removeCallbacks(mLongPressRunnable);
                }
                break;
            case MotionEvent.ACTION_CANCEL:

            case MotionEvent.ACTION_UP:
                //释放了
                removeCallbacks(mLongPressRunnable);
                if(!isMoved && !touchInfo.isLongPress) {

                    handleOnClick();

                }

                break;
        }
        return !isMoved;
    }

    private void handleOnClick(){
        ViewGroup content = (ViewGroup) findViewById(android.R.id.content);
        if (content == null) {
         return;
        }
        View child = content.getChildAt(0);
        if (child instanceof ListenerGetAble) {
            mOnClickListener = ((ListenerGetAble)child).getOnclickListener();
            if (mOnClickListener != null) {
                mOnClickListener.onClick(getChildAt(0));
            }
        }
    };
}

