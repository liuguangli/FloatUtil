package com.dalimao.floateutil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dalimao.library.util.FloatUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void addSimpleView(View view) {

        SimpleView floatView = new SimpleView(this);
        FloatUtil.showFloatView(floatView);
        floatView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatUtil.hideFloatView(MainActivity.this, SimpleView.class);
                Log.d(TAG, "close");
            }
        });
    }
}
