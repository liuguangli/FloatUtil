package com.dalimao.floateutil;
import android.content.Context;
import android.view.LayoutInflater;
import com.dalimao.library.DragView;



public class FloatBallView extends DragView {




	public FloatBallView(Context context) {

		super(context);

		LayoutInflater.from(context).inflate(R.layout.float_small_drag_ball, this);
	}


}
