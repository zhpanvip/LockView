package com.zhpan.ovallockview.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

import com.zhpan.ovallockview.utils.DensityUtils;
import com.zhpan.ovallockview.R;


/**
 * Created by zhpan on 2017/5/31.
 * Description: 自定义圆
 */

public class CircleWaveView extends View {
    private int circleColor;
    private int mTextColor;
    private int mWidth;
    private int mHeight;
    private int mRadius;
    private String mText;
    private float mTextSize;
    private Scroller mScroller;
    //  圆心坐标
    private float mPieCenterX;
    private float mPieCenterY;
    private Paint mPaint;
    private Paint mPaintText;
    private Paint mPaintTrangel;
    private Path mPath;
    private Rect bounds;

    private boolean isLock = true;
    private ValueAnimator animator;
    private int waveDelta;
    private int transformDelta;
    private boolean transforming;
    private Context mContext;
    private boolean isLockPrepared;
    private boolean isUnLockPrePared;
    private boolean isBluetoothConnect;
    private boolean isConnecting;

    public CircleWaveView(Context context) {
        this(context, null);
    }


    public CircleWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleWaveView);
            circleColor = typedArray.getColor(R.styleable.CircleWaveView_wave_color, context.getResources().getColor(R.color.red));
            mTextColor = typedArray.getColor(R.styleable.CircleWaveView_wave_text_color, context.getResources().getColor(R.color.white));
            mTextSize = typedArray.getDimension(R.styleable.CircleWaveView_wave_text_size, DensityUtils.dp2px(context, 16));
            mText = typedArray.getString(R.styleable.CircleWaveView_wave_text_str);
            typedArray.recycle();
        }
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(circleColor);
        mPaintTrangel = new Paint();
        mPaintText = new Paint();
        mPaintText.setColor(mTextColor);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setAntiAlias(true);

        mScroller = new Scroller(context);
        mPath = new Path();
        bounds = new Rect();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getHeight();
        mWidth = getWidth();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mPieCenterX = mWidth / 2;
        mPieCenterY = mHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
        drawText(canvas);
        drawTriangle(canvas);
    }

    /**
     * 画三角形
     */
    private void drawTriangle(Canvas canvas) {
        int radius = Math.min(mHeight, mWidth) / 2 - Math.min(mHeight, mWidth) / 8;
        Log.e("TAG", "mWidth=" + mWidth + "mHeight=" + mHeight);
        mPaintTrangel.setStyle(Paint.Style.FILL);
        mPaintTrangel.setShadowLayer(4, 0, 3, Color.GRAY);
        //  三角形顶点到圆边的距离
        int h0 = DensityUtils.dp2px(mContext, 10);
        //  三角形高
        int h1 = DensityUtils.dp2px(mContext, 12);
        //  三角形底边长
        int w = DensityUtils.dp2px(mContext, 14);
        mPaintTrangel.setColor(getResources().getColor(R.color.transparent));
        mPath.moveTo(mWidth / 2, mHeight / 2 - (radius - h0));
        mPath.lineTo(mWidth / 2 - w, mHeight / 2 - (radius - h1 - h0));
        mPath.lineTo(mWidth / 2 + w, mHeight / 2 - (radius - h1 - h0));
        canvas.drawPath(mPath, mPaintTrangel);
        mPaintTrangel.setShadowLayer(4, 0, -3, Color.GRAY);
        mPath.moveTo(mWidth / 2, mHeight / 2 + (radius - h0));
        mPath.lineTo(mWidth / 2 - w, mHeight / 2 + (radius - h1 - h0));
        mPath.lineTo(mWidth / 2 + w, mHeight / 2 + (radius - h1 - h0));
        canvas.drawPath(mPath, mPaintTrangel);
    }

    private void drawCircle(Canvas canvas) {
        mPaint.setColor(circleColor);
        int verticalCenter = getHeight() / 2;
        int horizontalCenter = getWidth() / 2;
        mRadius = Math.min(verticalCenter, horizontalCenter) - Math.min(verticalCenter, horizontalCenter) / 5;
        if (transforming) {
            mPaint.setColor(getResources().getColor(R.color.red));
            canvas.drawCircle(mPieCenterX, mPieCenterY, mRadius, mPaint);
            if (!isLock) {
                mRadius = mRadius - transformDelta;
            } else {
                mRadius = transformDelta;
            }
            mPaint.setColor(getResources().getColor(R.color.green));
            canvas.drawCircle(mPieCenterX, mPieCenterY, mRadius, mPaint);
        } else {
            mRadius = mRadius - waveDelta;
            if (!isBluetoothConnect) {
                if (isLock)
                    mPaint.setColor(getColor(R.color.redLight));
                else
                    mPaint.setColor(getColor(R.color.greenLight));
            } else {
                if (isLockPrepared) {
                    mPaint.setColor(getColor(R.color.red_d));
                } else if (isUnLockPrePared) {
                    mPaint.setColor(getColor(R.color.green_d));
                } else {
                    if (!isLock) {
                        mPaint.setColor(getColor(R.color.green));
                    } else {
                        mPaint.setColor(getColor(R.color.red));
                    }
                }
            }

            canvas.drawCircle(mPieCenterX, mPieCenterY, mRadius, mPaint);
        }
    }

    /**
     * 画圆中的文字
     */
    private void drawText(Canvas canvas) {
        if (TextUtils.isEmpty(mText)||isConnecting) return;
        if(isBluetoothConnect){
            mPaintText.setTextSize(mTextSize);
            mPaintText.getTextBounds(mText, 0, mText.length(), bounds);
            Paint.FontMetricsInt fontMetricsInt = mPaintText.getFontMetricsInt();
            int baseline = (getMeasuredHeight() - fontMetricsInt.bottom + fontMetricsInt.top) / 2 - fontMetricsInt.top;
            canvas.drawText(mText, mPieCenterX, baseline, mPaintText);
        }else {
//            mText="蓝牙未连接";
            mPaintText.getTextBounds("蓝牙未连接", 0, mText.length(), bounds);
            mPaintText.setTextSize(mTextSize);
            Paint.FontMetricsInt fontMetricsInt = mPaintText.getFontMetricsInt();
            int baseline = (getMeasuredHeight() - fontMetricsInt.bottom + fontMetricsInt.top) / 2 - fontMetricsInt.top;
            canvas.drawText("蓝牙未连接", mPieCenterX, baseline-30, mPaintText);
            mPaintText.setTextSize(DensityUtils.dp2px(mContext,12));
            canvas.drawText(mText,mPieCenterX,baseline+30,mPaintText);
        }

    }

    public void startWave() {
        if (animator != null && animator.isRunning())
            animator.end();
        animator = ValueAnimator.ofFloat(0f, 1f, 0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setDuration(600);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int verticalCenter = getHeight() / 2;
                int horizontalCenter = getWidth() / 2;
                waveDelta = (int) (Math.min(verticalCenter, horizontalCenter) * (float) animation.getAnimatedValue() / 16);
                invalidate();
            }
        });

        animator.start();
    }

    public void stopWave() {
        if (animator != null && animator.isRunning())
            animator.end();
    }

    public void changeLockState(final boolean lock) {
        stopWave();
        if (this.isLock != lock) {
            transforming = true;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 0.99f);
            valueAnimator.setDuration(500);
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    transforming = false;
                    isLock = lock;
                    invalidate();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    transforming = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int verticalCenter = getHeight() / 2;
                    int horizontalCenter = getWidth() / 2;
                    transformDelta = (int) ((Math.min(verticalCenter, horizontalCenter) - Math.min(verticalCenter, horizontalCenter) / 6) * (float) animation.getAnimatedValue());
                    invalidate();
                }
            });
            valueAnimator.start();
        }
    }

    public int getRadius() {
        return mRadius;
    }

    /**
     * 是否能够开门
     *
     * @param lock
     */
    public void setLock(final boolean lock) {
        stopWave();
        isLock = lock;
        invalidate();
    }

    public boolean isLock() {
        return isLock;
    }


    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
        invalidate();
    }

    public void setCircleColor(int background) {
        this.circleColor = background;
        invalidate();
    }

    public boolean isBluetoothConnect() {
        return isBluetoothConnect;
    }

    public void setBluetoothConnect(boolean bluetoothConnect) {
        isBluetoothConnect = bluetoothConnect;
        invalidate();
    }

    public void smoothScroll(int destX, int destY) {
        int scrollY = getScrollY();
        int delta = destY - scrollY;
        mScroller.startScroll(destX, scrollY, 0, delta, 700);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    public void setLockPrepared(boolean lockPrepared) {
        isLockPrepared = lockPrepared;
        invalidate();
    }

    public void setUnLockPrePared(boolean unLockPrePared) {
        isUnLockPrePared = unLockPrePared;
        invalidate();
    }

    public Scroller getScroller() {
        return mScroller;
    }

    private int getColor(@ColorRes int color) {
        return getResources().getColor(color);
    }

    public boolean isConnecting() {
        return isConnecting;
    }

    public void setConnecting(boolean connecting) {
        isConnecting = connecting;
        invalidate();
    }
}
