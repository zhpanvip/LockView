package com.zhpan.lockview.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Scroller;

import com.zhpan.lockview.R;
import com.zhpan.lockview.listener.OnLockOperateListener;
import com.zhpan.lockview.utils.DensityUtils;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by zhpan on 2018/5/26.
 * Description: 自定义开关锁控件
 */
public class LockView extends FrameLayout {
    private CircleWaveView mCircleWaveView;
    private CircleView mCircleView;
    private Scroller mScroller;
    private int mLastY;
    private int mTouchSlop;
    private Context mContext;
    private Option mOption;
//    private boolean isOperating;
    private boolean canSlide = true;
    private ProgressBar mProgressBar;
    //  阻尼系数
    private double damping = 1.7;
    //  小圆圆心到大圆圆心距离
    private int distance;
    private OnLockOperateListener mOnLockOperateListener;
    private static Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mCircleWaveView.startWave();
        }
    };

    public LockView(Context context) {
        this(context, null);
    }

    public LockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        View view = View.inflate(context, R.layout.layout_oval_lock, this);
        mCircleWaveView = (CircleWaveView) view.findViewById(R.id.circle_wave_view);
        mCircleView = (CircleView) view.findViewById(R.id.green_cv);
        distance = ((LayoutParams) mCircleView.getLayoutParams()).topMargin;
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
        mScroller = mCircleWaveView.getScroller();
        mContext = context;
        mCircleWaveView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View view = getChildAt(0);
        view.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = true;
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case ACTION_DOWN:
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
        int scrollY = mCircleWaveView.getScrollY();
        switch (event.getAction()) {
            case ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!canSlide) {
                    return super.onTouchEvent(event);
                }
                if (mCircleWaveView.getScrollY() > mTouchSlop) {
                    mOption = Option.LOCK;
                } else if (mCircleWaveView.getScrollY() < -mTouchSlop) {
                    mOption = Option.UNLOCK;
                }
                if (Math.abs(scrollY) > (distance - mCircleWaveView.getRadius() + mCircleView.getRadius())) {
                    if (mOption != null) {
                        switch (mOption) {
                            case LOCK:
                                if (mOnLockOperateListener != null)
                                    mOnLockOperateListener.onLockPrepared();
                                mCircleWaveView.setLockPrepared(true);
                                break;
                            case UNLOCK:
                                if (mOnLockOperateListener != null)
                                    mOnLockOperateListener.onUnLockPrepared();
                                mCircleWaveView.setUnLockPrePared(true);
                                break;
                        }
                    }
                } else {
                    mCircleWaveView.setUnLockPrePared(false);
                    mCircleWaveView.setLockPrepared(false);
                    mOnLockOperateListener.onNotPrepared();
                   /* if (isLock()) {
                        mCircleWaveView.setText(mContext.getResources().getString(R.string.device_control_unlock));
                    } else {
                        mCircleWaveView.setText(mContext.getResources().getString(R.string.device_control_lock));
                    }*/
//                    isOperating = false;
                }

                /**
                 * 控制滑动边界
                 */
                int border = (distance - mCircleWaveView.getRadius() + mCircleView.getRadius()) +
                        DensityUtils.dp2px(mContext, 25);//  可上下滑动的最大距离
                int deltaY = (int) ((mLastY - y) / damping);
                //  当前上下滑动的距离
                int slideHeight = deltaY + mCircleWaveView.getScrollY();
                if (slideHeight > border) {
                    mCircleWaveView.scrollTo(0, border);
                    return true;
                } else if (slideHeight + border < 0) {
                    mCircleWaveView.scrollTo(0, -border);
                    return true;
                }
                mCircleWaveView.scrollBy(0, deltaY);
                break;
            case MotionEvent.ACTION_UP:
                mCircleWaveView.setUnLockPrePared(false);
                mCircleWaveView.setLockPrepared(false);
                scrollY = mCircleWaveView.getScrollY();
                if (Math.abs(scrollY) > (distance - mCircleWaveView.getRadius() + mCircleView.getRadius()) && mOption != null) {
                    switch (mOption) {
                        case LOCK:
                            if (mOnLockOperateListener != null)
                                mOnLockOperateListener.onLockStart();
                            break;
                        case UNLOCK:
                            if (mOnLockOperateListener != null)
                                mOnLockOperateListener.onUnlockStart();
                            break;
                    }
                }
                mCircleWaveView.smoothScroll(0, 0);
                break;
        }
        mLastY = y;
        return super.onTouchEvent(event);
    }

    private long timestamp = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!canSlide)
            switch (ev.getAction()) {
                case ACTION_DOWN:
                    timestamp = System.currentTimeMillis();
                    break;
                case ACTION_UP:
                    if (System.currentTimeMillis() - timestamp < 500) {
                        performClick();
                        return true;
                    }
                    break;
            }
        return super.dispatchTouchEvent(ev);
    }

    public void startWave() {
        mHandler.postDelayed(mRunnable, 300);
    }

    public void stopWave() {
        mHandler.removeCallbacks(mRunnable);
        mCircleWaveView.stopWave();
//        isOperating = false;
    }

//    public boolean isOperating() {
//        return isOperating;
//    }

    /**
     * 是否能够开门
     *
     * @param lock
     */
    public void setLock(boolean lock) {
        mCircleWaveView.setLock(lock);
    }

    public boolean isLock() {
        return mCircleWaveView.isLock();
    }

    public void setLockState(boolean lock) {
        mCircleWaveView.changeLockState(lock);
        setLock(lock);
    }

    public void setText(String text) {
        mCircleWaveView.setText(text);
    }

    /**
     * @param canSlide 是否可以滑动
     */
    public void setCanSlide(boolean canSlide) {
        this.canSlide = canSlide;
    }

    /**
     * 设置滑动阻尼
     *
     * @param damping 阻尼系数
     */
    public void setDamping(double damping) {
        this.damping = damping;
    }

    public void setBluetoothConnect(boolean isConnect,String text) {
        mCircleWaveView.setBluetoothConnect(isConnect);
        mCircleWaveView.setText(text);
        setCanSlide(isConnect);
    }

    public void setBluetoothConnect(boolean isConnect) {
        mCircleWaveView.setBluetoothConnect(isConnect);
        setCanSlide(isConnect);
    }

    public void setNoNetData(boolean isNoData){
        mCircleWaveView.setNoNetData(isNoData);
    }

    public void connecting(boolean isConnecting) {
        mCircleWaveView.setConnecting(isConnecting);
        if (isConnecting) {
            mProgressBar.setVisibility(VISIBLE);
            setCanSlide(false);
        } else {
            mProgressBar.setVisibility(GONE);
        }
    }

    public boolean isConnecting(){
        return mCircleWaveView.isConnecting();
    }

    public void setCircleColor(int color) {
        mCircleWaveView.setCircleColor(color);
    }

    public void setOnLockOperateListener(OnLockOperateListener onLockOperateListener) {
        mOnLockOperateListener = onLockOperateListener;
    }

    private enum Option {
        LOCK,
        UNLOCK
    }
}
