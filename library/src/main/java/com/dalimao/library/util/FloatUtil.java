package com.dalimao.library.util;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
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

    private static final String TAG = "FloatUtil";

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
        StandOutWindowManager.getInstance(view.getContext()).showView(view, args , gravity , type);
    }

    /**
     * 显示浮窗，对齐方式
     * @param view
     * @param type 浮窗层级
     * @param point 坐标点
     * @param args
     */

    public static void showFloate(View view, int gravity,int type, Point point, Bundle args) {
        StandOutWindowManager.getInstance(view.getContext()).showView(view, args , gravity , type, point);
    }


    /**
     * 智能浮窗：更加系统版本、机型自动选择浮窗类型（type），绕过权限的限制
     * @param view
     * @param gravity
     * @param point
     * @param args
     */

    public static void showSmartFloate(View view, int gravity, Point point, Bundle args) {


        int type = SmartFloatUtil.askType(view.getContext());
        Log.d(TAG, "showSmartFloate:type=" + type);
        StandOutWindowManager.getInstance(view.getContext()).showView(view, args , gravity , type, point);
    }


    /**
     *
     * @param context
     * @param cls
     * @param cache 是否缓存
     */



    public static void hideFloatView(Context context , Class<? extends View> cls, boolean cache) {
        StandOutWindowManager.getInstance(context).hideView(cls, cache);
    }



}
