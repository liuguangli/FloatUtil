package com.dalimao.library;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;

import com.dalimao.library.constants.StandOutFlags;
import com.dalimao.library.util.Utils;


import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Handler;

public class StandOutWindowManager {

    static final String TAG = "StandOutWindowManager";
    static final boolean DEBUG = false;

    public void Log(String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }

    // internal map of ids to shown/hidden views
    static WindowCache sWindowCache;
    static Window sFocusedWindow;

    // static constructors
    static {
        if (sWindowCache == null) {
            sWindowCache = new WindowCache();
        }
        sFocusedWindow = null;
    }

    private static StandOutWindowManager instance;
    /**
     * Return the ids of all shown or hidden windows.
     * @param cls
     * @return A set of ids, or an empty set.
     */
    public final static Set<Integer> getExistingIds(Class<? extends Context> cls) {
        return sWindowCache.getCacheIds(cls);
    }

    /**
     * check the special window wrapper if exited
     * @param servClass class of service which contains the target window wrapper
     * @param cls special window wrapper class
     * @return true if target is exit
     */
    public final static boolean isCached(Class<? extends Context> servClass, Class<? extends WindowWrapper> cls) {
        int id = cls.hashCode();
        return sWindowCache.isCached(id, servClass);
    }

    public final static void clearCache(Class<? extends Context> servClass) {
        if (sWindowCache != null) {
            sWindowCache.clear(servClass);
        }
    }

    /**
     * window 是否可见
     * @param cls window的类对象
     * @return
     */
    public boolean isWindowShowing(Class<? extends WindowWrapper> cls) {
        int id = cls.hashCode();
        Window window = getWindow(id);
        if (window != null) {
            return window.isShown();
        } else {
            return false;
        }
    }

    private Context mContext;
    //the class of service which contains this menuManager
    private Class<? extends Context> mServClass;
    //window wrapper cache, the key is wrapper's hash code, that it's the id of window too
    private SparseArray<WindowWrapper> mWindowWrappers = new SparseArray<WindowWrapper>();
    // internal system services
    private android.view.WindowManager mSysWindowManager;

    private StandOutWindowManager(Context context) {
        this.mContext = context;
        this.mServClass = context.getClass();
        this.mSysWindowManager = (android.view.WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public static StandOutWindowManager getInstance(Context context){
        if (instance == null){
            instance = new StandOutWindowManager(context);
        }
        return instance;
    }
    public Context getContext() {
        return mContext.getApplicationContext();
    }

    public Context getServiceContext() {
        return mContext;
    }

    /**
     * check WindowWrapper is exist(not close)
     * @param cls the check class of wrapper
     * @return is exist
     */
    public boolean isWindowExist(Class<? extends WindowWrapper> cls) {
        int id = cls.hashCode();
        WindowWrapper windowWrapper = mWindowWrappers.get(id);
        return windowWrapper != null;
    }

    /**
     * return a new one window wrapper if not exit in cache
     * @param cls the class of special wrapper
     * @return instance
     */
    public WindowWrapper getWindowWrapper(Class<? extends WindowWrapper> cls, Integer id) {

        WindowWrapper windowWrapper = mWindowWrappers.get(id);
        if (windowWrapper == null) {
            windowWrapper = createWindowWrapperInstance(cls, id);
        }else if (windowWrapper.getClass() != cls) {

        }

        if (windowWrapper == null) {
            String msg = String.format("%s can not be instant !", cls.getSimpleName());
            if (DEBUG) {
                throw new NullPointerException(msg);
            } else {
                Log.w(TAG, msg);
            }
        }

        return windowWrapper;
    }

    /**
     * return window wrapper that set with attach window
     * @param cls
     * @param anchor anchor for window calculate it's real showing position
     * @return
     */
    public WindowWrapper getWindowWrapper(Class<? extends WindowWrapper> cls, Window anchor, Integer id) {
        WindowWrapper windowWrapper = getWindowWrapper(cls, id);
        if (windowWrapper != null) {
            windowWrapper.setWindowAnchor(anchor);
        }
        return windowWrapper;
    }

    private WindowWrapper createWindowWrapperInstance(Class<? extends WindowWrapper> cls, Integer id) {
        WindowWrapper windowWrapper = null;

        if (cls != null) {
            try {
                Class[] paramTypes = { StandOutWindowManager.class, Integer.class };
                Object[] params = { this, id};
                Constructor con = cls.getConstructor(paramTypes);
                windowWrapper = (WindowWrapper) con.newInstance(params);
            } catch (InstantiationException e) {
                Log.e(TAG,e.getMessage());
            } catch (IllegalAccessException e) {
                Log.e(TAG, e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }

        if (windowWrapper != null) {
            mWindowWrappers.put(id, windowWrapper);
        }

        return windowWrapper;
    }

    private WindowWrapper getWindowWrapperFromCache(int windowId) {
        return mWindowWrappers.get(windowId);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Return whether the window corresponding to the id exists. This is useful
     * for testing if the id is being restored (return true) or shown for the
     * first time (return false).
     *
     * @param id
     *            The id of the window.
     * @return True if the window corresponding to the id is either shown or
     *         hidden, or false if it has never been shown or was previously
     *         closed.
     */
    public final boolean isExistingId(int id) {
        return sWindowCache.isCached(id, mServClass);
    }

    /**
     * Return the ids of all shown or hidden windows.
     *
     * @return A set of ids, or an empty set.
     */
    public final Set<Integer> getExistingIds() {
        return sWindowCache.getCacheIds(mServClass);
    }

    /**
     * Return the window corresponding to the id, if it exists in cache. The
     * window will not be created with
     * value will be null if the window is not shown or hidden.
     *
     * @param id
     *            The id of the window.
     * @return The window if it is shown/hidden, or null if it is closed.
     */
    public final Window getWindow(int id) {
        return sWindowCache.getCache(id, mServClass);
    }

    /**
     * Return the window that currently has focus.
     *
     * @return The window that has focus.
     */
    public final Window getFocusedWindow() {
        return sFocusedWindow;
    }

    /**
     * Sets the window that currently has focus.
     */
    public final void setFocusedWindow(Window window) {
        sFocusedWindow = window;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * open a window with special window wrapper class
     *

     *
     * @param cls
     * @param args the args will pass to the special window wrapper:
     *
     * @param shouldCloseAll close all showing window before target window is open
     */
    public void show(Class<? extends WindowWrapper> cls, Bundle args, boolean shouldCloseAll) {

        int targetId = cls.hashCode();

        if (shouldCloseAll) {
            closeAll(targetId);
        }

        Window window = getWindow(targetId);
        if (window != null) {
            Log("can not open an exited window: " + cls.getSimpleName());
            return;
        }

        show(cls, args, null);
    }

    public void showView(View view, Bundle args, int gravity) {
        final WindowWrapper wrapper = getWindowWrapper(CommonWindowWrapper.class, null , view.getClass().hashCode());
        StandOutLayoutParams params = wrapper.getStandOutLayoutParams();
        params.gravity = gravity;
        wrapper.setStandOutLayoutParams(params);
        showWrapper(wrapper, args, null, view);
    }

    /**
     *
     * @param view
     * @param args
     */

    public void showView(View view, Bundle args){

        showView(view, args, Gravity.TOP | Gravity.LEFT, WindowManager.LayoutParams.TYPE_PHONE);
    }

    /**
     *
     * @param view
     * @param args
     * @param gravity
     * @param type
     */

    public void showView(View view, Bundle args, int gravity ,int type){

        final WindowWrapper wrapper = getWindowWrapper(CommonWindowWrapper.class, null , view.getClass().hashCode());
        StandOutLayoutParams params = wrapper.getStandOutLayoutParams();
        params.gravity = gravity;
        params.type = type;
        wrapper.setStandOutLayoutParams(params);
        showWrapper(wrapper, args, null, view);
    }
    /**
     *
     * @param view
     * @param args
     * @param gravity
     * @param type
     */

    public void showView(View view, Bundle args, int gravity , int type, Point point){

        showView(view, args, gravity, type, point, false);
    }

    /**
     *
     * @param view
     * @param args
     * @param gravity
     * @param type
     * @param point
     * @param drag
     */

    public void showView(View view, Bundle args, int gravity, int type, Point point, boolean drag) {
        final WindowWrapper wrapper = getWindowWrapper(CommonWindowWrapper.class, null , view.getClass().hashCode());
        wrapper.setCanMove(drag);
        StandOutLayoutParams params = wrapper.getStandOutLayoutParams();
        params.gravity = gravity;
        params.type = type;
        params.x = point.x;
        params.y = point.y;
        wrapper.setStandOutLayoutParams(params);
        showWrapper(wrapper, args, null, view);
    }

    /**
     * open a window and pass it a anchor window object <p>
     * or you can close the anchor window by pass it's wrapper class and closeAnchor with true, before open the target window
     *
     *
     * @param cls the class of target window
     * @param args the args will pass to the special window wrapper:
     * @param anchor wrapper class of anchor window,
     * @param closeAnchor if true close the anchor window before window open
     */
    public void show(Class<? extends WindowWrapper> cls, Bundle args, Class<? extends WindowWrapper> anchor, boolean closeAnchor) {
        int anchorId = anchor.hashCode();
        if (closeAnchor && isExistingId(anchorId)) {
            close(anchorId);
        }

        Log("start window : " + cls.getSimpleName());

        if (closeAnchor) {
            show(cls, args, null);
            return;
        }

        Window anchorWindow = getWindow(anchorId);
        if(anchorWindow == null) {
            Log("window caller is null: " + anchorId);
            return;
        }

        show(cls, args, anchorWindow);
    }

    protected final void show(Class<? extends WindowWrapper> cls) {
        show(cls, null, null);
    }

    protected final void show(Class<? extends WindowWrapper> cls, Bundle args) {
        show(cls, args, null);
    }

    /**
     * Show or restore a window corresponding to the wrapper class.
     * Return the window that was shown/restored.
     *
     * @param cls the class of target window
     * @param args the args will pass to the special window wrapper:
     * @param anchor wrapper class of anchor window,
     *               you can get the anchor window object on target wrapper {@link WindowWrapper#onPrepareShow(Window, Window)}
     * @return The window shown.
     */
    public synchronized Window show(Class<? extends WindowWrapper> cls, Bundle args, Window anchor) {
        final WindowWrapper wrapper = getWindowWrapper(cls, anchor , cls.hashCode());
        Window window = showWrapper(wrapper, args, anchor, null);

        return window;
    }

    private Window showWrapper(WindowWrapper wrapper, Bundle args, Window anchor, View child) {
        if(wrapper == null) {
            return null;
        }
        final int id = wrapper.WindowId;
        wrapper.setParams(args);
        // get the window corresponding to the id
        Window cachedWindow = getWindow(id);
        if (cachedWindow != null && wrapper != cachedWindow.getWindowWrapper()) {

        }

        final Window window;
        // check cache first
        if (cachedWindow != null && wrapper.isCreated) {
            window = cachedWindow;
        } else {
            window = new Window(wrapper);
        }
        if (child != null && cachedWindow == null){
            window.addView(child);
            if (args != null) {
                try {
                    ((ParamReceiver)child).onParamReceive(args);
                } catch (ClassCastException e) {
                    Log.e(TAG, "Your custom view must implement ParamReceiver, or you can not receive params!");
                }

            }
        }
        if (window.visibility == Window.VISIBILITY_VISIBLE) {
//            String msg = "Tried to show " + cls.getSimpleName()+ " that is already shown.";
//            if (DEBUG) {
//                throw new IllegalStateException(msg);
//            } else {
//                Log.e(TAG, msg);
//            }
            wrapper.onReShown(window, args);
            return null;
        }

        // alert callbacks and cancel if instructed
        wrapper.onPrepareShow(window, anchor);

        window.visibility = Window.VISIBILITY_VISIBLE;

        // get animation
        Animation animation = wrapper.getShowAnimation();
        wrapper.onShown(window, args);
        // get the params corresponding to the id
        StandOutLayoutParams params = window.getLayoutParams();


        try {


            // add the view to the window menuManager
            mSysWindowManager.addView(window, params);

            // animate
            if (animation != null) {
                window.getChildAt(0).startAnimation(animation);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

        // add view to internal map
        sWindowCache.putCache(id, mServClass, window);

        // mContext.startServiceForeground();



        focus(id);
        return window;
    }


    /**
     * Create a window corresponding to the wrapper class.
     * Return the window that was shown/restored.
     *
     * @param cls the class of target window
     * @param args the args will pass to the special window wrapper:
     * @param anchor wrapper class of anchor window,
     *               you can get the anchor window object on target wrapper {@link WindowWrapper#onPrepareShow(Window, Window)}
     * @return The window shown.
     */
    public synchronized Window create(Class<? extends WindowWrapper> cls, Bundle args, Window anchor) {
        final WindowWrapper wrapper = getWindowWrapper(cls, anchor, cls.hashCode());
        if(wrapper == null) {
            return null;
        }
        final int id = wrapper.WindowId;
        wrapper.setParams(args);
        // get the window corresponding to the id
        Window cachedWindow = getWindow(id);
        if (cachedWindow != null && wrapper != cachedWindow.getWindowWrapper()) {

        }
        final Window window;
        // check cache first
        if (cachedWindow != null && wrapper.isCreated) {
            window = cachedWindow;
        } else {
            window = new Window(wrapper);
        }

        // add view to internal map
        sWindowCache.putCache(id, mServClass, window);
       // mContext.startServiceForeground();
        return window;
    }

    private void hide(int windowId, final boolean ignoreAnim) {
        WindowWrapper wrapper = mWindowWrappers.get(windowId);
        if (wrapper != null) {
            hide(wrapper, ignoreAnim);
        }
    }

    /**
     * Hide a window corresponding to the wrapper class, then  will be invoke <br>
     * this hide action request  return
     * or it will be the same as invoke the method {@link #close(Class)}
     *
     * @param cls
     *            The class of the window.
     */
    public final void hide(Class<? extends WindowWrapper> cls) {

        final int id = cls.hashCode();
        final WindowWrapper wrapper = getWindowWrapperFromCache(id);
        if (wrapper == null) {
            return;
        }

        hide(wrapper);
    }

    private void hide(final WindowWrapper wrapper) {
        hide(wrapper, false);
    }

    private synchronized void hide(final WindowWrapper wrapper, final boolean ignoreAnim) {

        final int id = wrapper.WindowId;
        // get the view corresponding to the id
        final Window window = getWindow(id);

        if (window == null) {
            /*final String windowName = wrapper.getClass().getSimpleName();
            String msg = "Tried to hide(" + windowName + ") a null window.";
            if (DEBUG) {
                throw new IllegalArgumentException(msg);
            } else {
                Log.e(TAG, msg);
            }*/
            return;
        }

        if (window.visibility == Window.VISIBILITY_GONE) {
            /*final String windowName = wrapper.getClass().getSimpleName();
            String msg = "Tried to hide(" + windowName + ") a window that is not shown.";
            if (DEBUG) {
                throw new IllegalStateException(msg);
            } else {
                Log.e(TAG, msg);
            }*/
            return;
        }

        // alert callbacks and cancel if instructed
        wrapper.onHidden(window);

        // check if hide enabled
        if (Utils.isSet(window.flags, StandOutFlags.FLAG_WINDOW_HIDE_ENABLE)) {
            window.visibility = Window.VISIBILITY_TRANSITION;

            // get animation
            Animation animation = wrapper.getHideAnimation();

            try {
                View windowContent = window.getChildAt(0);
                // animate
                if (!ignoreAnim && animation != null && windowContent!=null) {
                    AnimationSet animationSet = new AnimationSet(false);
                    animationSet.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            // remove the window from the window menuManager
                            try {
                                mSysWindowManager.removeView(window);
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                            window.visibility = Window.VISIBILITY_GONE;
                        }
                    });
                    animationSet.addAnimation(animation);
                    windowContent.startAnimation(animationSet);

                } else {
                    // remove the window from the window menuManager
                    mSysWindowManager.removeView(window);
                    window.visibility = Window.VISIBILITY_GONE;
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }

           // mContext.updateNotificationOnHide();

        } else {
            // if hide not enabled, close window
            close(wrapper);
        }
    }

    public void hideAll() {
        for (int id : getExistingIds()) {
            hide(id, true);
        }
    }

    /**
     * Close a window corresponding to the wrapper class.
     *
     * @param cls
     *           The class of the window.
     */
    public final synchronized void close(Class<? extends WindowWrapper> cls) {
        final int id = cls.hashCode();
        final WindowWrapper wrapper = getWindowWrapperFromCache(id);
        if (wrapper == null) {
            return;
        } else {
            close(wrapper);
        }
    }

    public final synchronized void close(final WindowWrapper wrapper) {

        final int id = wrapper.WindowId;
        // get the view corresponding to the id
        final Window window = getWindow(wrapper.WindowId);

        if (window == null) {
            return;
        }

        if (window.visibility == Window.VISIBILITY_TRANSITION) {
            return;
        }

       // mContext.cancelNotificationOnClose();

        unfocus(window);

        window.visibility = Window.VISIBILITY_TRANSITION;

        // get animation
        Animation animation = wrapper.getCloseAnimation();
        // remove window
        try {
            View windowContent = window.getChildAt(0);
            if (animation != null && windowContent != null) {  // animate
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeWindowView(id, window, wrapper);
                    }
                }, animation.getDuration());

                windowContent.startAnimation(animation);

            } else {
                removeWindowView(id, window, wrapper);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

    }

    private void removeWindowView(int id, Window window, WindowWrapper wrapper) {
        // remove the window from the window menuManager
        try{
            mSysWindowManager.removeView(window);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
            Log.w(TAG, "View not attached to window menuManager, maybe it's a hided window ?");
        }

        window.visibility = Window.VISIBILITY_GONE;

        // remove view from internal map
        sWindowCache.removeCache(id, mContext.getClass());

        // if we just released the last window, quit
        if (sWindowCache.getCacheSize(mServClass) == 0) {
           // mContext.stopServiceForeground();
        }

        wrapper.onClosed(window);
        mWindowWrappers.remove(id);
        Log.d(TAG, "remove window id=" + id);
    }

    private void closeAll(int target) {
        LinkedList<Integer> ids = new LinkedList<Integer>();
        for (int id : getExistingIds()) {
            if(id==target) {
                continue;
            }
            ids.add(id);
        }

        close(ids);
    }

    public void closeAll() {
        for (int id : getExistingIds()) {
            close(id);
        }
    }

    private void close(LinkedList<Integer> ids) {
        // close each window
        for (int id : ids) {
            if(isExistingId(id)) {
                close(id);
            } else {
                Log("try close a not exited window :" + id);
            }
        }
    }

    private void close(int windowId) {
        WindowWrapper wrapper = mWindowWrappers.get(windowId);
        if (wrapper != null) {
            close(wrapper);
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Internal touch handler for handling moving the window.
     *
     * @see {@link View#onTouchEvent(MotionEvent)}
     *
     * @param window
     * @param view
     * @param event
     * @return
     */
    public boolean onTouchHandleMove(Window window, View view, MotionEvent event) {
        WindowWrapper wrapper = mWindowWrappers.get(window.id);
        if (wrapper == null) {
            return true;
        }

        if (wrapper.onPrepareMove(window, view, event)){
            return true;
        }

        StandOutLayoutParams params = window.getLayoutParams();

        // how much you have to move in either direction in order for the
        // gesture to be a move and not tap

        int totalDeltaX = window.touchInfo.lastX - window.touchInfo.firstX;
        int totalDeltaY = window.touchInfo.lastY - window.touchInfo.firstY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                window.touchInfo.lastX = (int) event.getRawX();
                window.touchInfo.lastY = (int) event.getRawY();

                window.touchInfo.firstX = window.touchInfo.lastX;
                window.touchInfo.firstY = window.touchInfo.lastY;
                break;

            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) event.getRawX() - window.touchInfo.lastX;
                int deltaY = (int) event.getRawY() - window.touchInfo.lastY;

                window.touchInfo.lastX = (int) event.getRawX();
                window.touchInfo.lastY = (int) event.getRawY();

                /**
                 * 非长按的才执行移动window操作
                 * **/
                if(!window.touchInfo.isLongPress) {
                    if (window.touchInfo.moving
                            || Math.abs(totalDeltaX) >= params.threshold
                            || Math.abs(totalDeltaY) >= params.threshold) {
                        window.touchInfo.moving = true;

                        if (Utils.isSet(window.flags, StandOutFlags.FLAG_BODY_MOVE_X_ENABLE)) {
                            //only move x axis
                            if (event.getPointerCount() == 1) {
                                params.x += deltaX;
                            }
                            window.edit().setPosition(params.x, params.y).commit();
                        } else if (Utils.isSet(window.flags,
                                StandOutFlags.FLAG_BODY_MOVE_ENABLE)) {

                            // update the position of the window
                            if (event.getPointerCount() == 1) {
                                params.x += deltaX;
                                params.y += deltaY;
                            }

                            window.edit().setPosition(params.x, params.y).commit();
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                window.touchInfo.moving = false;
                window.touchInfo.isLongPress = false;

                if (event.getPointerCount() == 1) {

                    // bring to front on tap
                    boolean tap = Math.abs(totalDeltaX) < params.threshold
                            && Math.abs(totalDeltaY) < params.threshold;
                    if (tap
                            && Utils.isSet(
                            window.flags,
                            StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TAP)) {
                        bringToFront(window.id);
                    }
                }

                // bring to front on touch
                else if (Utils.isSet(window.flags,
                        StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TOUCH)) {
                    bringToFront(window.id);
                }

                break;

            case MotionEvent.ACTION_CANCEL:
                window.touchInfo.isLongPress = false;
                break;
        }

        if (wrapper != null) {
            wrapper.onMove(window, view, event);
        }

        return !window.touchInfo.isLongPress;

    }

    /**
     * Bring the window corresponding to this id in front of all other windows.
     * The window may flicker as it is removed and restored by the system.
     *
     * @param id
     *            The id of the window to bring to the front.
     */
    public final synchronized void bringToFront(int id) {
        Window window = getWindow(id);
        if (window == null) {
            if (DEBUG) {
                throw new IllegalArgumentException("Tried to bringToFront(" + id
                        + ") a null window.");
            }
            return;
        }

        if (window.visibility == Window.VISIBILITY_GONE) {
            if (DEBUG) {
                throw new IllegalStateException("Tried to bringToFront(" + id
                        + ") a window that is not shown.");
            }
            return;
        }

        if (window.visibility == Window.VISIBILITY_TRANSITION) {
            return;
        }

        StandOutLayoutParams params = window.getLayoutParams();

        // remove from window menuManager then add back
        try {
            mSysWindowManager.removeView(window);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        try {
            mSysWindowManager.addView(window, params);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * Request focus for the window corresponding to this id. A maximum of one
     * window can have focus, and that window will receive all key events,
     * including Back and Menu.
     *
     * @param id
     *            The id of the window.
     * @return True if focus changed successfully, false if it failed.
     */
    public final synchronized boolean focus(int id) {
        // check if that window is focusable
        final Window window = getWindow(id);
        if (window == null) {
            if (DEBUG) {
                throw new IllegalArgumentException("Tried to focus(" + id
                        + ") a null window.");
            }
            return false;
        }

        if (!Utils.isSet(window.flags,
                StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE)) {
            // remove focus from previously focused window
            if (sFocusedWindow != null) {
                unfocus(sFocusedWindow);
            }

            return window.onFocus(true);
        }

        return false;
    }

    /**
     * Remove focus for the window corresponding to this id. Once a window is
     * unfocused, it will stop receiving key events.
     *
     * @param id
     *            The id of the window.
     * @return True if focus changed successfully, false if it failed.
     */
    public final synchronized boolean unfocus(int id) {
        Window window = getWindow(id);
        return unfocus(window);
    }

    /**
     * Remove focus for the window, which could belong to another application.
     *
     * @param window
     *            The window to unfocus.
     *
     * @return True if focus changed successfully, false if it failed.
     */
    public synchronized boolean unfocus(Window window) {
        if (window == null) {
            if (DEBUG) {
                throw new IllegalArgumentException(
                        "Tried to unfocus a null window.");
            }
            return false;
        }
        return window.onFocus(false);
    }

    /**
     * Update the window corresponding to this id with the given params.
     *
     * @param id
     *            The id of the window.
     * @param params
     *            The updated layout params to apply.
     */
    public void updateViewLayout(int id, StandOutLayoutParams params) {
        Window window = getWindow(id);

        if (window == null) {
            if (DEBUG) {
                throw new IllegalArgumentException("Tried to updateViewLayout("
                        + id + ") a null window.");
            }
            return;
        }

        if (window.visibility == Window.VISIBILITY_GONE) {
            return;
        }

        if (window.visibility == Window.VISIBILITY_TRANSITION) {
            return;
        }

        try {
            window.setLayoutParams(params);
            mSysWindowManager.updateViewLayout(window, params);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    //----------------------------------------------------------------------------------------------

    /**
     * sent a special command to a target window wrapper,
     * the target wrapper's method {@link WindowWrapper#onReceiveCommand(int, Bundle)} will be invoked when command get
     * @param cls  the class of target window wrapper
     * @param type command type declared by custom window wrapper
     * @param data
     */
    public void sendWindowCommand(Class<? extends WindowWrapper> cls, int type, Bundle data) {
        int targetId = cls.hashCode();
        Window window = getWindow(targetId);
        if (window == null) {
            Log("can not send command to a null window");
            return;
        }

        WindowWrapper windowWrapper = mWindowWrappers.get(targetId);
        if (windowWrapper != null) {
            windowWrapper.onReceiveCommand(type, data);
        }
    }


    public void hideView(Class<? extends View> viewClass, boolean cache) {
        final WindowWrapper wrapper = getWindowWrapperFromCache(viewClass.hashCode());
        if (wrapper == null) {
            return;
        }

        if (cache) {
            hide(wrapper);
        } else {
            close(viewClass.hashCode());
        }


    }



}
