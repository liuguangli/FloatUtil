package com.dalimao.library;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by liuguangli on 16/11/11.
 */

public class DragView extends FrameLayout implements ListenerGetAble {
    protected OnClickListener mOnClickListener;
    public DragView(Context context) {
        super(context);
    }

    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
        //重写这个方法，不要调用 super.setOnClickListener(l),否则无法拖动
        //super.setOnClickListener(l);
    }

    @Override
    public OnClickListener getOnclickListener() {
        //务必返回这个监听器否则无法拖动
        return mOnClickListener;
    }
}
