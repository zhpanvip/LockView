package com.zhpan.lockview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.zhpan.lockview.enums.LockOption;
import com.zhpan.lockview.listener.OnLockOperateListener;
import com.zhpan.lockview.utils.LockViewUtils;
import com.zhpan.lockview.view.CircleWaveView;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
import static com.zhpan.lockview.enums.LockOption.LOCK;
import static com.zhpan.lockview.enums.LockOption.UNLOCK;

/**
 * Created by zhpan on 2018/5/26.
 * Description: 自定义开关锁控件
 */
public class LockView extends FrameLayout {
    private CircleWaveView mCircleWaveView;
    private ImageView mIvUnlock;
    private TextView mTvLock;
    private TextView mTvUnlock;
    private Scroller mScroller;
    private int mLastY;
    private int mTouchSlop;
    private LockOption mLockOption;
    private boolean canSlide = true;
    private LinearLayout mProgressBar;
    //  阻尼系数
    private double damping = 2.0;
    //  小圆圆心到大圆圆心距离
    private int distance;
    private int border;
    private OnLockOperateListener mOnLockOperateListener;
    private static final Handler mHandler = new Handler();
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mCircleWaveView.startWave();
        }
    };
    private TextView mTvConnection;
    private boolean isFrozen;

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
        mCircleWaveView = view.findViewById(R.id.circle_wave_view);
        mIvUnlock = view.findViewById(R.id.green_cv);
        distance = ((LayoutParams) mIvUnlock.getLayoutParams()).topMargin;
        mProgressBar = view.findViewById(R.id.ll_progress);
        mTvConnection = view.findViewById(R.id.tv_connecting);
        mTvLock = view.findViewById(R.id.tv_lock);
        mTvUnlock = view.findViewById(R.id.tv_unlock);
        mScroller = mCircleWaveView.getScroller();
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        border = getSlideBorder();
    }

    //  可上下滑动的最大距离;
    private int getSlideBorder() {
        return (distance - mCircleWaveView.getRadius() + mIvUnlock.getHeight() / 2) +
                LockViewUtils.dp2px(25);
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
                if (!canSlide || isFrozen) {
                    return super.onTouchEvent(event);
                }
                onActionMove(scrollY, y);
                break;
            case MotionEvent.ACTION_UP:
                onActionUp(scrollY);
                break;
        }
        mLastY = y;
        return super.onTouchEvent(event);
    }

    private void onActionUp(int scrollY) {
        mCircleWaveView.setUnLockPrePared(false);
        mCircleWaveView.setLockPrepared(false);
        if (Math.abs(scrollY) > (distance - mCircleWaveView.getRadius() + mIvUnlock.getHeight() / 2) && mLockOption != null) {
            switch (mLockOption) {
                case LOCK:
                    if (mOnLockOperateListener != null)
                        mOnLockOperateListener.onLockStart();
                    break;
                case UNLOCK:
                    if (mOnLockOperateListener != null)
                        mOnLockOperateListener.onUnlockStart();
                    break;
                default:
                    break;
            }
        }
        mCircleWaveView.smoothScroll(0, 0);
    }

    private void onActionMove(int scrollY, int y) {
        if (mCircleWaveView.getScrollY() > mTouchSlop) {
            mLockOption = LOCK;
        } else if (mCircleWaveView.getScrollY() < -mTouchSlop) {
            mLockOption = UNLOCK;
        }
        if (Math.abs(scrollY) > (distance - mCircleWaveView.getRadius() + mIvUnlock.getHeight() / 2)) {
            if (mLockOption != null) {
                switch (mLockOption) {
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
                    default:
                        break;
                }
            }
        } else {
            mCircleWaveView.setUnLockPrePared(false);
            mCircleWaveView.setLockPrepared(false);
            mOnLockOperateListener.onNotPrepared();
        }
        int deltaY = (int) ((mLastY - y) / damping);
        //  当前上下滑动的距离
        int slideHeight = deltaY + mCircleWaveView.getScrollY();
        if (slideHeight > border) {
            mCircleWaveView.scrollTo(0, border);
        } else if (slideHeight + border < 0) {
            mCircleWaveView.scrollTo(0, -border);
        }
        mCircleWaveView.scrollBy(0, deltaY);
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
    }


    /**
     * 是否能够开门
     */
    public void setLock(boolean lock) {
        mCircleWaveView.setLock(lock);
    }

    /**
     * @return 是否上锁
     */
    public boolean isLock() {
        return mCircleWaveView.isLock();
    }

    /**
     * 设置上锁状态
     *
     * @param lock true：设置为上锁状态；false:设置为开锁状态
     */
    public void setLockState(boolean lock) {
        isFrozen = false;
        mCircleWaveView.changeLockState(lock);
        setLock(lock);
    }

    /**
     * 设置中心文字
     */
    public void setText(String text) {
        mCircleWaveView.setText(text);
    }

    /**
     * 设置中心文字大小
     */
    public void setTextSize(int textSize) {
        mTvUnlock.setTextSize(textSize);
        mTvLock.setTextSize(textSize);
        invalidate();
    }

    /**
     * 设置中心文字
     *
     * @param unlockText 开锁时文字
     * @param lockText   上锁时文字
     */
    public void setText(String unlockText, String lockText) {
        mTvUnlock.setText(unlockText);
        mTvLock.setText(lockText);
        invalidate();
    }

    /**
     * 设置中心文字颜色
     *
     * @param unlockColor 开锁状态时颜色
     * @param lockColor   开锁状态时颜色
     */
    public void setTextColor(int unlockColor, int lockColor) {
        mTvUnlock.setTextColor(unlockColor);
        mTvLock.setTextColor(lockColor);
    }


    public void showText(boolean showText) {
        if (showText) {
            mTvLock.setVisibility(VISIBLE);
            mTvUnlock.setVisibility(VISIBLE);
        } else {
            mTvLock.setVisibility(INVISIBLE);
            mTvUnlock.setVisibility(INVISIBLE);
        }
        invalidate();
    }

    /**
     * @param canSlide 是否可以滑动
     */
    public void setCanSlide(boolean canSlide) {
        this.canSlide = canSlide;
    }

    /**
     * 设置滑动阻尼
     */
    public void setDamping(double damping) {
        this.damping = damping;
    }

    public void setBluetoothConnect(boolean isConnect, String text) {
        mCircleWaveView.setBluetoothConnect(isConnect);
        mCircleWaveView.setText(text);
        setCanSlide(isConnect);
    }

    public void setBluetoothConnect(boolean isConnect) {
        mCircleWaveView.setBluetoothConnect(isConnect);
        setCanSlide(isConnect);
    }

    public void setNoNetData(boolean isNoData) {
        mCircleWaveView.setNoNetData(isNoData);
    }

    public void setNoNetData(boolean isNoData, String text) {
        mCircleWaveView.setNoNetData(isNoData, text);
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

    public boolean isConnecting() {
        return mCircleWaveView.isConnecting();
    }

    public void setCircleColor(int color) {
        mCircleWaveView.setCircleColor(color);
    }

    public void setCenterTextSize(float textSize) {
        mCircleWaveView.setTextSize(textSize);
    }

    public void setOnLockOperateListener(OnLockOperateListener onLockOperateListener) {
        mOnLockOperateListener = onLockOperateListener;
    }

    public void setConnectingText(String text) {
        mTvConnection.setText(text);
    }

    public void setDeviceFrozen(String frozen) {
        connecting(false);
        setBluetoothConnect(true);
        setLockState(true);
        isFrozen = true;
        setText(frozen);
    }
}
