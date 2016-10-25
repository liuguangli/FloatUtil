package com.dalimao.floateutil;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dalimao.library.ParamReceiver;

/**
 * Created by liuguangli on 16/10/25.
 */

public class SimpleViewWitchParam extends FrameLayout implements ParamReceiver {
    public static final java.lang.String PARAM = "PARAM";
    public static final String CONTENT = "content";

    public SimpleViewWitchParam(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.float_simple_witch_param, this);
    }

    @Override
    public void onParamReceive(Bundle bundle) {
        if (bundle != null) {
            String param = bundle.getString(PARAM);
            TextView textView = (TextView) findViewById(R.id.tv_param);
            textView.setText(param);

            String content = bundle.getString(CONTENT);
            if (!TextUtils.isEmpty(content)) {
                TextView tvContent = (TextView) findViewById(R.id.tv_content);
                tvContent.setText(content);
            }
        }
    }
}
