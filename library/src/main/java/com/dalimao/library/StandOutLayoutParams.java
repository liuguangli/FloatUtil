package com.dalimao.library;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import com.dalimao.library.constants.StandOutFlags;
import com.dalimao.library.util.Utils;



public class StandOutLayoutParams extends WindowManager.LayoutParams {
    /**
     * Special value for x position that represents the left of the screen.
     */
    public static final int LEFT = 0;
    /**
     * Special value for y position that represents the top of the screen.
     */
    public static final int TOP = 0;
    /**
     * Special value for x position that represents the right of the screen.
     */
    public static final int RIGHT = Integer.MAX_VALUE;
    /**
     * Special value for y position that represents the bottom of the
     * screen.
     */
    public static final int BOTTOM = Integer.MAX_VALUE;
    /**
     * Special value for x or y position that represents the center of the
     * screen.
     */
    public static final int CENTER = Integer.MIN_VALUE;
    /**
     * Special value for x or y position which requests that the system
     * determine the position.
     */
    public static final int AUTO_POSITION = Integer.MIN_VALUE + 1;

    /**
     * The distance that distinguishes a tap from a drag.
     */
    public int threshold;

    /**
     * Optional constraints of the window.
     */
    public int minWidth, minHeight, maxWidth, maxHeight;

    /**
     * @param windowFlags
     *            The flags of the window.
     */
    public StandOutLayoutParams(int type,int windowFlags) {
        super(200, 200, type,
                StandOutLayoutParams.FLAG_NOT_TOUCH_MODAL
                        | StandOutLayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT);

        setFocusFlag(false);

        if (Utils.isSet(windowFlags,
                StandOutFlags.FLAG_WINDOW_INPUT_METHOD_RESIZE_ENABLE)) {
            flags |= (FLAG_LAYOUT_INSET_DECOR | FLAG_LAYOUT_IN_SCREEN | FLAG_NOT_FOCUSABLE);
            softInputMode = (SOFT_INPUT_STATE_UNSPECIFIED | SOFT_INPUT_ADJUST_RESIZE);
        } else if (!Utils.isSet(windowFlags,
                StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE)) {
            // windows may be moved beyond edges
            // 加上FLAG_LAYOUT_IN_SCREEN 之后，浮窗可以移动至通知栏所在的区域，但会被通知栏遮挡
            flags |= (FLAG_LAYOUT_IN_SCREEN | FLAG_LAYOUT_NO_LIMITS);
        }

        gravity = Gravity.TOP | Gravity.LEFT;

        threshold = 10;
        minWidth = minHeight = 0;
        maxWidth = maxHeight = Integer.MAX_VALUE;
    }

    /**
     * @param flags
     *            The flags of the window.
     * @param w
     *            The width of the window.
     * @param h
     *            The height of the window.
     */
    public StandOutLayoutParams(int type,int flags, int w, int h) {
        this(type,flags);
        width = w;
        height = h;
    }

    /**
     * @param context
     *            The context from window.
     * @param flags
     *            The flags of the window.
     * @param w
     *            The width of the window.
     * @param h
     *            The height of the window.
     * @param xpos
     *            The x position of the window.
     * @param ypos
     *            The y position of the window.
     */
    public StandOutLayoutParams(Context context,int type, int flags, int w, int h, int xpos, int ypos) {
        this(type,flags, w, h);

        if (xpos != AUTO_POSITION) {
            x = xpos;
        }
        if (ypos != AUTO_POSITION) {
            y = ypos;
        }

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int displayWidth = metrics.widthPixels;
        int displayHeight = metrics.heightPixels;

        if (x == RIGHT) {
            x = displayWidth - w;
        } else if (x == CENTER) {
            x = (displayWidth - w) / 2;
        }

        if (y == BOTTOM) {
            y = displayHeight - h;
        } else if (y == CENTER) {
            y = (displayHeight - h) / 2;
        }
    }



    /**
     * @param context
     *            The context from window.
     * @param flags
     *            The flags of the window.
     * @param w
     *            The width of the window.
     * @param h
     *            The height of the window.
     * @param xpos
     *            The x position of the window.
     * @param ypos
     *            The y position of the window.
     * @param minWidth
     *            The minimum width of the window.
     * @param minHeight
     *            The mininum height of the window.
     */
    public StandOutLayoutParams(Context context, int type,int flags, int w, int h, int xpos, int ypos,
                                int minWidth, int minHeight) {
        this(context, type ,flags, w, h, xpos, ypos);

        this.minWidth = minWidth;
        this.minHeight = minHeight;
    }

    /**
     * @param context
     *            The context from window.

     * @param flags
     *            The flags of the window.
     * @param w
     *            The width of the window.
     * @param h
     *            The height of the window.
     * @param xpos
     *            The x position of the window.
     * @param ypos
     *            The y position of the window.
     * @param minWidth
     *            The minimum width of the window.
     * @param minHeight
     *            The mininum height of the window.
     * @param threshold
     *            The touch distance threshold that distinguishes a tap from
     *            a drag.
     */
    public StandOutLayoutParams(Context context,int type, int flags, int w, int h, int xpos, int ypos,
                                int minWidth, int minHeight, int threshold) {
        this(context,type, flags, w, h, xpos, ypos, minWidth, minHeight);

        this.threshold = threshold;
    }

    public void setFocusFlag(boolean focused) {
        if (focused) {
            flags = flags ^ StandOutLayoutParams.FLAG_NOT_FOCUSABLE;
        } else {
            flags = flags | StandOutLayoutParams.FLAG_NOT_FOCUSABLE;
        }
    }
}