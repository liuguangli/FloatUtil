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




	public FloatBallView(Context context) {

		super(context);

		LayoutInflater.from(context).inflate(R.layout.float_small_drag_ball, this);
	}



}
