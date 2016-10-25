package com.dalimao.floateutil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by liuguangli on 16/10/25.
 */

public class SimpleView extends FrameLayout {
    public SimpleView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.float_simple, this);
    }
}
