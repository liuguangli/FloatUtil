package com.dalimao.floateutil;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.dalimao.library.util.FloatUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

    }

    /**
     * 添加一个浮窗
     * @param view
     */
    public void addSimpleView(View view) {

        SimpleView floatView = new SimpleView(this);
        FloatUtil.showFloatView(floatView, null);
        floatView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatUtil.hideFloatView(MainActivity.this, SimpleView.class, false);
                Log.d(TAG, "close");
            }
        });
    }

    /**
     * 添加一个浮窗并向浮窗传参
     * @param view
     */

    public void addSimpleViewWithParam(View view) {
        SimpleViewWitchParam floatView = new SimpleViewWitchParam(this);
        Bundle bundle = new Bundle();
        bundle.putString(SimpleViewWitchParam.PARAM, "我是传过来的参数");
        FloatUtil.showFloatView(floatView, bundle);
        floatView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatUtil.hideFloatView(MainActivity.this, SimpleViewWitchParam.class, false);
                Log.d(TAG, "close");
            }
        });
    }


    /**
     * 添加一个浮窗并向浮窗传参, 指定浮窗齐方式
     * @param view
     */

    public void addSimpleViewWithGravity(View view) {
        SimpleViewWitchParam floatView = new SimpleViewWitchParam(this);
        Bundle bundle = new Bundle();
        bundle.putString(SimpleViewWitchParam.PARAM, "我是传过来的参数");
        bundle.putString(SimpleViewWitchParam.CONTENT,getString(R.string.add_simple_view_with_gravity));
        FloatUtil.showFloatView(floatView, Gravity.CENTER, bundle);
        floatView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatUtil.hideFloatView(MainActivity.this, SimpleViewWitchParam.class, false);
                Log.d(TAG, "close");
            }
        });
    }
    /**
     * 添加一个浮窗并向浮窗传参, 指定浮窗齐方式, 指定显示层级
     * @param view
     */

    public void addSimpleViewWithType(View view) {
        SimpleViewWitchParam floatView = new SimpleViewWitchParam(this);
        Bundle bundle = new Bundle();
        bundle.putString(SimpleViewWitchParam.PARAM, "我是传过来的参数");
        bundle.putString(SimpleViewWitchParam.CONTENT,getString(R.string.add_simple_view_with_type));
        FloatUtil.showFloatView(floatView, Gravity.CENTER,WindowManager.LayoutParams.TYPE_TOAST , bundle);
        floatView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatUtil.hideFloatView(MainActivity.this, SimpleViewWitchParam.class, false);
                Log.d(TAG, "close");
            }
        });
    }

    /**
     * 添加一个浮窗并向浮窗传参, 指定浮窗齐方式, 指定显示层级, 指定坐标
     * @param view
     */

    public void addSimpleViewWithPoint(View view) {
        SimpleViewWitchParam floatView = new SimpleViewWitchParam(this);
        Bundle bundle = new Bundle();
        bundle.putString(SimpleViewWitchParam.PARAM, "我是传过来的参数");
        bundle.putString(SimpleViewWitchParam.CONTENT,getString(R.string.add_simple_view_with_point));
        Point point = new Point();
        point.x = 100;
        point.y = 300;
        FloatUtil.showFloatView(floatView, Gravity.TOP, WindowManager.LayoutParams.TYPE_TOAST, point, bundle);
        floatView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatUtil.hideFloatView(MainActivity.this, SimpleViewWitchParam.class, false);
                Log.d(TAG, "close");
            }
        });
    }

    /**
     * 添加智能浮窗，智能的意思是自动根据当前系统版本和机型选择何时的 type ，绕过系统权限限制使用浮窗
     * @param view
     */

    public void addSimpleSmartFloatView(View  view) {
        SimpleViewWitchParam floatView = new SimpleViewWitchParam(this);
        Bundle bundle = new Bundle();
        bundle.putString(SimpleViewWitchParam.PARAM, "智能浮窗");
        bundle.putString(SimpleViewWitchParam.CONTENT,getString(R.string.add_simple_view_with_smart));
        Point point = new Point();
        point.x = 0;
        point.y = 0;
        FloatUtil.showSmartFloat(floatView, Gravity.CENTER, point, bundle);
        floatView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatUtil.hideFloatView(MainActivity.this, SimpleViewWitchParam.class, false);
                Log.d(TAG, "close");
            }
        });
    }

    /**
     * 添加一个可以拖动的View
     *
     * @param view
     */

    public void addDragView(View view) {
        FloatBallView floatBallView = new FloatBallView(this);
        FloatUtil.showFloatView(floatBallView, Gravity.LEFT | Gravity.TOP, WindowManager.LayoutParams.TYPE_TOAST,new Point(0,0), null, true);

        SimpleView simpleView = new SimpleView(this);
        FloatUtil.showSmartFloat(simpleView, Gravity.LEFT | Gravity.TOP, new Point(0,0), null, true);

        simpleView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatUtil.hideFloatView(MainActivity.this, SimpleView.class, false);
                Log.d(TAG, "close");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatUtil.hideFloatView(this, FloatBallView.class, false);
    }
}
