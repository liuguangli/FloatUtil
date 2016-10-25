package com.dalimao.library.util;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.dalimao.library.CommonWindowWrapper;
import com.dalimao.library.StandOutLayoutParams;
import com.dalimao.library.StandOutWindowManager;
import com.dalimao.library.Window;
import com.dalimao.library.WindowWrapper;

/**
 * Created by liuguangli on 16/10/21.
 */
public class FloatUtil {

    /**
     * 显示浮窗，默认对齐方式：左上，默认浮窗层级：TYPE_PHONE,需要权限：SYSTEM_ALERT_WINDOW。
     * @param view
     * @param args 传递的参数，可以为 null
     */

    public static void showFloatView(View view, Bundle args){

        StandOutWindowManager.getInstance(view.getContext()).showView(view, args);
    }



    /**
     * 显示浮窗，默认浮窗层级：TYPE_PHONE,需要权限：SYSTEM_ALERT_WINDOW。
     * @param view
     * @param gravity 对齐方式
     * @param args 传递的参数，可以为 null
     */

    public static void showFloatView(View view, int gravity, Bundle args){

        StandOutWindowManager.getInstance(view.getContext()).showView(view, args , gravity);
    }

    /**
     *  显示浮窗。
     * @param view
     * @param gravity 对齐方式
     * @param type  浮窗层级
     * @param args 传递的参数，可以为 null
     */

    public static void showFloatView(View view,int gravity, int type, Bundle args) {

    }

    /**
     * 显示浮窗，对齐方式，左上
     * @param View
     * @param type 浮窗层级
     * @param point 坐标点
     */

    public static void showFloate(View View, int type, Point point) {

    }

    public static void hideFloatView(Context context , Class<? extends View> cls) {
        StandOutWindowManager.getInstance(context).hideView(cls, false);
    }


}
