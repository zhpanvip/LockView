package com.zhpan.ovallockview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class OvalLockView extends FrameLayout {
    private Handler mHandler = new Handler();
    private CircleView mOvalView;
    private Scroller mScroller;
    private int mLastY;
    private int mTouchSlop;
    private int mHeight;
    private int mWidth;
   /* private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mOvalView.stopWave();

            mOvalView.setLock(!mOvalView.isLock());
        }
    };*/

    public OvalLockView(Context context) {
        this(context, null);
    }

    public OvalLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OvalLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View view = getChildAt(0);
        view.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getHeight();
        mWidth = getWidth();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = true;
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercepted = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(y - mLastY) > mTouchSlop) {
                    intercepted = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
        }
        mLastY = y;
        return intercepted;
    }

    private void smoothScroll() {
        int deltaX = 0;
        if (getScrollX() < -getMeasuredWidth() / 4) {
            deltaX = -getScrollX() - getMeasuredWidth() / 2;

        }

        if (getScrollX() >= -getMeasuredWidth() / 4) {
            deltaX = -getScrollX();
        }
        mScroller.startScroll(0, getScrollY(), 0, 100, 500);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = mLastY - y;
                if (Math.abs(mOvalView.getScrollY()) > (mHeight / 2 - mOvalView.getWidth() / 2-mHeight/13)){
                    return true;
                } else
                    mOvalView.scrollBy(0, deltaY);
                break;
            case MotionEvent.ACTION_UP:
                mOvalView.smoothScroll(0,0);
                break;
        }
        mLastY = y;
        return super.onTouchEvent(event);
    }

    private void init(Context context, AttributeSet attrs) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        View view = View.inflate(context, R.layout.layout_oval_lock, this);
        mOvalView = view.findViewById(R.id.oval_view);
        mScroller = new Scroller(context);
        mOvalView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*mHandler.postDelayed(mRunnable,3000);
                mOvalView.startWave();*/
            }
        });
        /*setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }


}
