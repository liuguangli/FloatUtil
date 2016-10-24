package com.dalimao.library.constants;

public class StandOutFlags {

	// This counter keeps track of which primary bit to set for each flag
	private static int flag_bit = 0;

	/**
	 * Setting this flag indicates that the window can be moved by dragging the
	 * body.
	 *
	 */
	public static final int FLAG_BODY_MOVE_ENABLE = 1 << flag_bit++;

	/**
	 * Setting this flag indicates that windows are able to be hidden
	 */
	public static final int FLAG_WINDOW_HIDE_ENABLE = 1 << flag_bit++;

	/**
	 * Setting this flag indicates that the window should be brought to the
	 * front upon user interaction.
	 *
	 * <p>
	 * Note that if you set this flag, there is a noticeable flashing of the
	 * window during {@link android.view.MotionEvent#ACTION_UP}. This the hack that allows
	 * the system to bring the window to the front.
	 */
	public static final int FLAG_WINDOW_BRING_TO_FRONT_ON_TOUCH = 1 << flag_bit++;

	/**
	 * Setting this flag indicates that the window should be brought to the
	 * front upon user tap.
	 *
	 * <p>
	 * Note that if you set this flag, there is a noticeable flashing of the
	 * window during {@link android.view.MotionEvent#ACTION_UP}. This the hack that allows
	 * the system to bring the window to the front.
	 */
	public static final int FLAG_WINDOW_BRING_TO_FRONT_ON_TAP = 1 << flag_bit++;

	/**
	 * Setting this flag indicates that the system should keep the window's
	 * position within the edges of the screen. If this flag is not set, the
	 * window will be able to be dragged off of the screen.
	 *
	 * <p>
	 * If this flag is set, the window's {@link android.view.Gravity} is recommended to be
	 * {@link android.view.Gravity#TOP} | {@link android.view.Gravity#LEFT}. If the gravity is anything
	 * other than TOP|LEFT, then even though the window will be displayed within
	 * the edges, it will behave as if the user can drag it off the screen.
	 *
	 */
	public static final int FLAG_WINDOW_EDGE_LIMITS_ENABLE = 1 << flag_bit++;

	/**
	 * Setting this flag indicates that the system should keep the window's
	 * aspect ratio constant when resizing.
	 *
	 * <p>
	 * The aspect ratio will only be enforced in
	 * {@link standout.StandOutWindowManager#onTouchHandleResize(standout.ui.Window, android.view.View, android.view.MotionEvent)}
	 * . The aspect ratio will not be enforced if you set the width or height of
	 * the window's LayoutParams manually.
	 *
	 * @see standout.StandOutWindowManager#onTouchHandleMove(standout.ui.Window, android.view.View, android.view.MotionEvent)
	 */
	public static final int FLAG_WINDOW_ASPECT_RATIO_ENABLE = 1 << flag_bit++;

	/**
	 * Setting this flag indicates that the window does not need focus. If this
	 * flag is set, the system will not take care of setting and unsetting the
	 * focus of windows based on user touch and key events.
	 *
	 * <p>
	 * You will most likely need focus if your window contains any of the
	 * following: Button, ListView, EditText.
	 *
	 * <p>
	 * The benefit of disabling focus is that your window will not consume any
	 * key events. Normally, focused windows will consume the Back and Menu
	 * keys.
	 *
	 * @see {@link standout.StandOutWindowManager#focus(int)}
	 * @see {@link standout.StandOutWindowManager#unfocus(int)}
	 *
	 */
	public static final int FLAG_WINDOW_FOCUSABLE_DISABLE = 1 << flag_bit++;

    /**
     * Setting this flag indicates that ignore the system status bar height in calculate the Window#displayHeight
     */
    public static final int FLAG_IGNORE_SYSTEM_STATUS_BAR = 1 << flag_bit++;

    /**
     * Setting this flag indicates that the window can be moved x axis by dragging the body.
     */
    public static final int FLAG_BODY_MOVE_X_ENABLE = 1 << flag_bit ++;

	/**
	 * 将window限制在屏幕高度以内
	 */
	public static final int FLAG_WINDOW_EDGE_Y_LIMITS_ENABLE = 1 << flag_bit ++;

	public static final int FLAG_WINDOW_INPUT_METHOD_RESIZE_ENABLE = 1 << flag_bit ++;

}