package com.haoduyoudu.DailyAccounts;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class ScrollerViewLayout extends LinearLayout {

    private int measuredHeight;//全部item高度

    private int height; //可见内容高度

    private boolean canScroller = true; //是否可以滑动

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private float mMaxVelocity;

    public ScrollerViewLayout(Context context) {
        this(context, null, 0);
    }

    public ScrollerViewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollerViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        mScroller = new Scroller(context, new OvershootInterpolator());
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        mMaxVelocity = vc.getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredHeight = 0;
        //得到控件原始显示高度
        height = getMeasuredHeight();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            //测量子控件的大小
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
            measuredHeight += (childView.getMeasuredHeight() + layoutParams.bottomMargin + layoutParams.topMargin);
        }
        //调用此方法 重新更改高度
        setMeasuredDimension(getMeasuredWidth(), measuredHeight);
    }

    private float downY;
    private int pointerId;
    private boolean isSilde = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handlerScroll(event);
        return true;
    }

    /**
     * 处理滚动事件
     * @param event
     */
    private void handlerScroll(MotionEvent event) {
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                pointerId = event.getPointerId(0);
                //停止一切滚动
                mScroller.forceFinished(true);
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                downY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                int move = (int) (downY - y);
                if (isSilde || move == 0) {
                    return;
                }
                //向下滑动
                if (move < 0) {
                    scrollBy(0, move);
                    downY = y;
                }
                //向上滑动
                else if (move > 0) {
                    scrollBy(0, move);
                    downY = y;
                }

                Log.e("kawa", ">>>>move:" + move
                        + ">>>downY:" + downY
                        + ">>>y:" + y
                        + ">>>height:" + height
                        + ">>>measuredHeight:" + measuredHeight
                        + ">>>getScrollY:" + getScrollY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if ((measuredHeight - height) < getScrollY() || getScrollY() < 0) {
                    scrollReset();
                } else {
                    scrollFling();
                }
                break;
        }
    }

    @Override  //每次执行draw都会执行，获取当前的滚动位置进行重绘制
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            isSilde = true;
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        } else {
            isSilde = false;
        }
    }

    /**
     *  超出顶部/底部的进行复位
     */
    private void scrollReset() {
        int scrollY = getScrollY();
        if (scrollY < 0) {
            int startY = scrollY;
            int endY = -scrollY;
            mScroller.startScroll(0, startY, 0, endY);
            invalidate();
        } else {
            //向上滑动超出底部界限时才进行复位
            if ((measuredHeight - height) < getScrollY()) {
                int startY = scrollY;
                int endY = -(scrollY - (measuredHeight - height));
                mScroller.startScroll(0, startY, 0, endY);
                invalidate();
            }
        }
    }

    /**
     * 惯性滚动
     */
    private void scrollFling() {
        mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
        float yVelocity = mVelocityTracker.getYVelocity(pointerId);
        /**
         * fling 方法参数注解
         *
         * startX 滚动起始点X坐标
         * startY 滚动起始点Y坐标
         * velocityX   当滑动屏幕时X方向初速度，以每秒像素数计算
         * velocityY   当滑动屏幕时Y方向初速度，以每秒像素数计算
         * minX    X方向的最小值，scroller不会滚过此点。
         *　maxX    X方向的最大值，scroller不会滚过此点。
         *　minY    Y方向的最小值，scroller不会滚过此点。
         *　maxY    Y方向的最大值，scroller不会滚过此点。
         */
        mScroller.fling(0, getScrollY(), 0, (int) -yVelocity * 2, 0, 0, 0, measuredHeight - height);
        invalidate();
    }

    public void setCanScroller(Boolean OK){canScroller = OK;}

}
