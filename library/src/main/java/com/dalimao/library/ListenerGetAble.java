package com.dalimao.library;

import android.view.View;

/**
 *  可获取监听器接口，在接口实现类中返回监听器
 * Created by liuguangli on 16/11/11.
 */

public interface ListenerGetAble {
    public View.OnClickListener getOnclickListener();

}
