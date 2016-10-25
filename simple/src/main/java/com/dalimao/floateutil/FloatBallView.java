package com.dalimao.floateutil;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.LinearLayout;


import com.dalimao.library.util.DeviceInfoUtil;

import java.lang.reflect.Field;

public class FloatBallView extends LinearLayout {

	/**
	 *  停留位置标记
	 */
	private static final int LOCATION_RIGHT = 0;
	private static final int LOCATION_LEFT = 1;
	/**
	 *  动画类型标记
	 */
	private static final int ANIM_OUT = 0;
	private static final int ANIM_IN = 1;
	/**
	 * 纵向边界判断差量
	 */
	public static final int  V_DIFF = 80;
	private static final String TAG = "FloatBallView";
	/**
	 * 按下和弹起时的距离：小于 CLICK_RANGE 算是点击
	 */
	private static final float CLICK_RANGE = 15;
	/**
	 * 记录小悬浮窗的宽度
	 */
	public static int viewWidth;

	/**
	 * 记录小悬浮窗的高度
	 */
	public static int viewHeight;

	/**
	 * 记录系统状态栏的高度
	 */
	 private static int statusBarHeight;

	/**
	 * 用于更新小悬浮窗的位置
	 */
	private WindowManager windowManager;

	/**
	 * 小悬浮窗的参数
	 */
	private WindowManager.LayoutParams mParams;

	/**
	 * 记录当前手指位置在屏幕上的横坐标值
	 */
	private float xInScreen;

	/**
	 * 记录当前手指位置在屏幕上的纵坐标值
	 */
	private float yInScreen;

	/**
	 * 记录手指按下时在屏幕上的横坐标的值
	 */
	private float xDownInScreen;

	/**
	 * 记录手指按下时在屏幕上的纵坐标的值
	 */
	private float yDownInScreen;

	/**
	 * 记录手指按下时在小悬浮窗的View上的横坐标的值
	 */
	private float xInView;

	/**
	 * 记录手指按下时在小悬浮窗的View上的纵坐标的值
	 */
	private float yInView;
    private int winWidth;
    private OnPosChangeListener listener;
	/**
	 * 停留的位置
	 */
	private int stopLocation = LOCATION_LEFT;
	/**
	 * 动画是否播放中
	 */
	private  boolean isAnimPlaying = false;
	/**
	 * 是否半隐藏
	 *
	 */
	private boolean halfShowing = false;
	/**
	 * 是否拖动
	 */
    private	boolean draging;
	/**
	 * 移动动画
	 */


	private boolean isAttached;


	public FloatBallView(Context context) {

		super(context);
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater.from(context).inflate(R.layout.float_small_drag_ball, this);
		View view = findViewById(R.id.small_window_layout);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
        winWidth = DeviceInfoUtil.getScreenLongSize(context);




	}



	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);

	}




	public static int getViewHeight() {
		return viewHeight;
	}

	public static int getViewWidth(){
		return viewWidth;
	}
	public void setOnPosChangeListener(OnPosChangeListener listener){
		this.listener = listener;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
				xInView = event.getX();
				yInView = event.getY();
				xDownInScreen = event.getRawX();
				yDownInScreen = event.getRawY();
				xInScreen = event.getRawX();
				yInScreen = event.getRawY() ;
				draging = false;
				break;
			case MotionEvent.ACTION_MOVE:
				draging = true;
				xInScreen = event.getRawX();
				yInScreen = event.getRawY();
				// 手指移动的时候更新小悬浮窗的位置
				updateViewPosition();
				if (!isAnimPlaying){

				}

				break;
			case MotionEvent.ACTION_UP:
				// 如果手指离开屏幕时，xDownInScreen和xInScreen约等，且yDownInScreen和yInScreen约等，则视为触发了单击事件。
				if (Math.abs(xDownInScreen - xInScreen) < CLICK_RANGE
						&& Math.abs(yDownInScreen - yInScreen) < CLICK_RANGE) {

				} else {
					doDragEnd();
				}
				draging = false;
				break;
			default:
				break;
		}




		return true;
	}

	private void doDragEnd() {

		updateViewPosition();
		if (listener != null){
			listener.onHideWarn();
		}




	}

	/**
	 * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
	 * 
	 * @param params
	 *            小悬浮窗的参数
	 */
	public void setParams(WindowManager.LayoutParams params) {
		mParams = params;
		xInScreen = params.x;
		yInScreen = params.y;
		updateStopLocation();
	}

	private void updateStopLocation() {
		 if (xInScreen>winWidth/2){

			stopLocation = LOCATION_RIGHT;

		} else {
			stopLocation = LOCATION_LEFT;

		}
	}

	/**
	 * 更新小悬浮窗在屏幕中的位置。
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void updateViewPosition() {

		try {
			if (isAttached) {
				mParams.x = (int) (xInScreen - xInView);
				mParams.y = (int) (yInScreen - yInView);
				windowManager.updateViewLayout((View) getTopParent(), mParams);
			}
		} catch (IllegalArgumentException e) {

		}


	}

    private ViewParent getTopParent(){

        ViewParent parent = this;
        while (true) {
            ViewParent tP = parent.getParent();
            if ( tP == null) {
                return parent;
            } else {
                parent = tP;
            }

        }
    }


	/**
	 * 用于获取状态栏的高度。
	 * 
	 * @return 返回状态栏高度的像素值。
	 */
	public int getStatusBarHeight() {
		if (statusBarHeight == 0) {
			try {
				Class<?> c = Class.forName("com.android.internal.R$dimen");
				Object o = c.newInstance();
				Field field = c.getField("status_bar_height");
				int x = (Integer) field.get(o);
				statusBarHeight = getResources().getDimensionPixelSize(x);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
		return statusBarHeight;
	}

	public float getXInScreen() {
		return xInScreen;
	}
	public float getYInScreen(){
		return yInScreen;
	}



	public WindowManager.LayoutParams getWindowLayoutParam() {
		return mParams;
	}

	public static interface OnPosChangeListener{
		public void onDrag(float x, float y);
		public void onShowCloseWarn(boolean overlapping);
		public void onRemoveBall();

		public  void onHideWarn();
	}


	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		isAttached = false;

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		isAttached = true;


	}
}
