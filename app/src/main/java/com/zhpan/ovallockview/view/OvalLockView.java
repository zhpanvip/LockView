package com.zhpan.ovallockview.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.Toast;

import com.zhpan.ovallockview.R;
import com.zhpan.ovallockview.listener.OnLockOperateListener;
import com.zhpan.ovallockview.listener.OnLockViewClickListener;
import com.zhpan.ovallockview.utils.DensityUtils;

public class OvalLockView extends FrameLayout {
    private CircleWaveView mOvalView;
    private CircleView mCircleView;
    private Scroller mScroller;
    private int mLastY;
    private int mTouchSlop;
    private int mHeight;
    private Context mContext;
    private Option mOption;
    private boolean isOpreating;
    private OnLockOperateListener mOnLockOperateListener;
    private OnLockViewClickListener mOnLockViewClickListener;
    private static Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mOvalView.startWave();
        }
    };

    private enum Option {
        LOCK,
        UNLOCK
    }

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        int scrollY = mOvalView.getScrollY();
        int distance = DensityUtils.dp2px(mContext, 100);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = mLastY - y;
                if (mOvalView.getScrollY() > mTouchSlop) {
                    mOption = Option.LOCK;
                } else if (mOvalView.getScrollY() < -mTouchSlop) {
                    mOption = Option.UNLOCK;
                }
                if (Math.abs(scrollY) > (distance - mOvalView.getRadius() + mCircleView.getRadius())) {
                    if (mOption != null) {
                        switch (mOption) {
                            case LOCK:
                                mOnLockOperateListener.onLockPrepared();
                                isOpreating=true;
                                break;
                            case UNLOCK:
                                mOnLockOperateListener.onUnLockPrepared();
                                isOpreating=true;
                                break;
                        }
                    }
                }else {
                    mOvalView.setText("已上锁");
                    isOpreating=false;
                }
                /**
                 * 控制滑动边界
                 */
                if(deltaY+mOvalView.getScrollY()>(mHeight / 2 - mOvalView.getWidth() / 2 - mHeight / 20)){
                    mOvalView.scrollTo(0,(mHeight / 2 - mOvalView.getWidth() / 2 - mHeight / 20));
                    return true;
                }else if(deltaY+mOvalView.getScrollY()+(mHeight / 2 - mOvalView.getWidth() / 2 - mHeight / 20)<0){
                    mOvalView.scrollTo(0,-(mHeight / 2 - mOvalView.getWidth() / 2 - mHeight / 20));
                    return true;
                }
                mOvalView.scrollBy(0, deltaY);
                break;
            case MotionEvent.ACTION_UP:
                scrollY = mOvalView.getScrollY();
                distance = DensityUtils.dp2px(mContext, 100);
                Toast.makeText(mContext, "ScrollY=" + scrollY + "radius=" + (distance - mOvalView.getRadius()), Toast.LENGTH_SHORT).show();

                if (Math.abs(scrollY) > (distance - mOvalView.getRadius() + mCircleView.getRadius()) && mOption != null) {
                    switch (mOption) {
                        case LOCK:
                            mOnLockOperateListener.onLockStart();
                            break;
                        case UNLOCK:
                            mOnLockOperateListener.onUnlockStart();
                            break;
                    }
                }
                mOvalView.smoothScroll(0, 0);
                performClick();
                break;
        }
        mLastY = y;
        return super.onTouchEvent(event);
    }

    private void init(Context context, AttributeSet attrs) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        View view = View.inflate(context, R.layout.layout_oval_lock, this);
        mOvalView = view.findViewById(R.id.oval_view);
        mCircleView = view.findViewById(R.id.green_cv);
        mOvalView.setText("已上锁");
        mScroller = mOvalView.getScroller();
        mContext = context;
        mOvalView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(mOnLockViewClickListener!=null)
                mOnLockViewClickListener.onClick();*/
            }
        });
    }

    public void startWave() {
        mHandler.postDelayed(mRunnable, 500);
    }

    public void stopWave() {
        mHandler.removeCallbacks(mRunnable);
        mOvalView.stopWave();
        isOpreating=false;
    }

    /**
     * 是否能够开门
     *
     * @param lock
     */
    public void setLock(boolean lock) {
        mOvalView.setLock(lock);
    }

    public void changeLockState(boolean lock) {
        mOvalView.changeLockState(lock);
    }

    public void setText(String text) {
        mOvalView.setText(text);
    }


    public void setCircleColor(int viewColor) {
        mOvalView.setCircleColor(viewColor);
    }

    public void setOnLockViewClickListener(OnLockViewClickListener onLockViewClickListener) {
        mOnLockViewClickListener = onLockViewClickListener;
    }

    public void setOnLockOperateListener(OnLockOperateListener onLockOperateListener) {
        mOnLockOperateListener = onLockOperateListener;
    }
}
