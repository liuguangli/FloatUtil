package com.dalimao.library.util;

import android.content.Context;
import android.os.Bundle;
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


    public static void showFloatView(View view){

        StandOutWindowManager.getInstance(view.getContext()).showView(view, null);
    }

    public static void hideFloatView(Context context , Class<? extends View> cls) {
        StandOutWindowManager.getInstance(context).hideView(cls, false);
    }

    public static void showFloateView(View view, int flag, WindowManager.LayoutParams params) {

    }
    public static void showFloateView(View view, int flag, StandOutLayoutParams params, Bundle bundle) {
        Window window = new Window(view.getContext(),view,params);
        StandOutWindowManager.getInstance(view.getContext()).show(CommonWindowWrapper.class,null,window );
    }
}
