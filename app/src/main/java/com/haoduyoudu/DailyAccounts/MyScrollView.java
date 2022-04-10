package com.haoduyoudu.DailyAccounts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
    public Boolean isgetfocus = true;
    private OnTouchlistener mOnTouch = null;

    public MyScrollView(Context context) {
        super(context);
    }
    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mOnTouch != null)
            mOnTouch.run();
        if(isgetfocus) return super.onInterceptTouchEvent(ev);
        return false;
    }
    public interface OnTouchlistener {
        public void run();
    }
    public void setonTouchlistener(OnTouchlistener ot) {
        mOnTouch = ot;
    }
}