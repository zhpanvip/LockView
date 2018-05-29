package com.zhpan.ovallockview.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Scroller;

import com.zhpan.ovallockview.R;
import com.zhpan.ovallockview.listener.OnLockOperateListener;
import com.zhpan.ovallockview.utils.DensityUtils;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class OvalLockView extends FrameLayout {
    private CircleWaveView mCircleWaveView;
    private CircleView mCircleView;
    private Scroller mScroller;
    private int mLastY;
    private int mTouchSlop;
    private int mHeight;
    private Context mContext;
    private Option mOption;
    private boolean isOpreating;
    private boolean canSlide = true;
    private boolean isBluetoothConnect;
    private ProgressBar mProgressBar;
    private boolean isConnecting;
    private OnLockOperateListener mOnLockOperateListener;
    private static Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mCircleWaveView.startWave();
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
        int distance = DensityUtils.dp2px(mContext, 100);
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
                int deltaY = mLastY - y;
                if (mCircleWaveView.getScrollY() > mTouchSlop) {
                    mOption = Option.LOCK;
                } else if (mCircleWaveView.getScrollY() < -mTouchSlop) {
                    mOption = Option.UNLOCK;
                }
                if (Math.abs(scrollY) > (distance - mCircleWaveView.getRadius() + mCircleView.getRadius())) {
                    if (mOption != null) {
                        switch (mOption) {
                            case LOCK:
                                mOnLockOperateListener.onLockPrepared();
                                mCircleWaveView.setLockPrepared(true);
                                isOpreating = true;
                                break;
                            case UNLOCK:
                                mOnLockOperateListener.onUnLockPrepared();
                                mCircleWaveView.setUnLockPrePared(true);
                                isOpreating = true;
                                break;
                        }
                    }
                } else {
                    mCircleWaveView.setUnLockPrePared(false);
                    mCircleWaveView.setLockPrepared(false);
                    if(isLock()){
                        mCircleWaveView.setText("已上锁");
                    }else {
                        mCircleWaveView.setText("未上锁");
                    }
                    isOpreating = false;
                }
                /**
                 * 控制滑动边界
                 */
                if (deltaY + mCircleWaveView.getScrollY() > (mHeight / 2 - mCircleWaveView.getWidth() / 2 - mHeight / 20)) {
                    mCircleWaveView.scrollTo(0, (mHeight / 2 - mCircleWaveView.getWidth() / 2 - mHeight / 20));
                    return true;
                } else if (deltaY + mCircleWaveView.getScrollY() + (mHeight / 2 - mCircleWaveView.getWidth() / 2 - mHeight / 20) < 0) {
                    mCircleWaveView.scrollTo(0, -(mHeight / 2 - mCircleWaveView.getWidth() / 2 - mHeight / 20));
                    return true;
                }
                mCircleWaveView.scrollBy(0, deltaY);
                break;
            case MotionEvent.ACTION_UP:
                mCircleWaveView.setUnLockPrePared(false);
                mCircleWaveView.setLockPrepared(false);
                scrollY = mCircleWaveView.getScrollY();
                distance = DensityUtils.dp2px(mContext, 100);
                if (Math.abs(scrollY) > (distance - mCircleWaveView.getRadius() + mCircleView.getRadius()) && mOption != null) {
                    switch (mOption) {
                        case LOCK:
                            mOnLockOperateListener.onLockStart();
                            break;
                        case UNLOCK:
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

    private void init(Context context, AttributeSet attrs) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        View view = View.inflate(context, R.layout.layout_oval_lock, this);
        mCircleWaveView = view.findViewById(R.id.oval_view);
        mCircleView = view.findViewById(R.id.green_cv);
        mProgressBar=view.findViewById(R.id.progress);
        mScroller = mCircleWaveView.getScroller();
        mContext = context;
        mCircleWaveView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public void startWave() {
        mHandler.postDelayed(mRunnable, 500);
    }

    public void stopWave() {
        mHandler.removeCallbacks(mRunnable);
        mCircleWaveView.stopWave();
        isOpreating = false;
    }

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

    public void changeLockState(boolean lock) {
        mCircleWaveView.changeLockState(lock);
    }

    public void setText(String text) {
        mCircleWaveView.setText(text);
    }

    public void setCanSlide(boolean canSlide) {
        this.canSlide = canSlide;
    }


    public void setBluetoothConnect(boolean isConnect) {
        mCircleWaveView.setBluetoothConnect(isConnect);
        setCanSlide(isConnect);
       /* if(isConnect){
            mProgressBar.setVisibility(GONE);
        }else {
            mProgressBar.setVisibility(VISIBLE);
        }*/
    }

    public void connecting(boolean isConnecting){
        this.isConnecting=isConnecting;
        mCircleWaveView.setConnecting(isConnecting);
        if(isConnecting){
            mProgressBar.setVisibility(VISIBLE);
        }else {
            mProgressBar.setVisibility(GONE);
        }
    }



    public void setCircleColor(int viewColor) {
        mCircleWaveView.setCircleColor(viewColor);
    }


    public void setOnLockOperateListener(OnLockOperateListener onLockOperateListener) {
        mOnLockOperateListener = onLockOperateListener;
    }
}
