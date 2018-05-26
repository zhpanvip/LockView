package com.zhpan.ovallockview.view;/**
 * Created by jun on 16-7-14.
 */

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.zhpan.ovallockview.utils.DensityUtils;
import com.zhpan.ovallockview.R;

/**
 * @Author Jun Cheung (Email:jun@huatune.com)
 * @Date 16-7-14
 */
public class OvalView extends View {

    private boolean isLock = true;
    private ValueAnimator animator;
    private int waveDelta;
    private int transformDelta;
    private float viewX;
    private boolean transforming;
    private int oriX;
    private int oriY;

    public OvalView(Context context) {
        super(context);
    }

    public OvalView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OvalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int verticalCenter = getHeight() / 2;
        int horizontalCenter = getWidth() / 2;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        int strokeWidth = DensityUtils.dp2px(getContext(), 1);
        int radius = Math.min(verticalCenter, horizontalCenter) - strokeWidth;
        if (transforming) {
            paint.setColor(getResources().getColor(R.color.red));
            canvas.drawCircle(verticalCenter, horizontalCenter, radius, paint);
            if (!isLock) {
                radius = radius - transformDelta;
            } else {
                radius = transformDelta;
            }
            paint.setColor(getResources().getColor(R.color.green));
            canvas.drawCircle(verticalCenter, horizontalCenter, radius, paint);
        } else {
            radius = radius - waveDelta;
            if (!isLock) {
                paint.setColor(getResources().getColor(R.color.green));
            } else {
                paint.setColor(getResources().getColor(R.color.red));
            }
            canvas.drawCircle(verticalCenter, horizontalCenter, radius, paint);
        }
        oriX = getWidth() / 2;
        oriY = getHeight() / 2;
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

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public void stopWave() {
        if (animator != null && animator.isRunning())
            animator.end();
    }

    public boolean isLock() {
        return isLock;
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
                    transformDelta = (int) (Math.min(verticalCenter, horizontalCenter) * (float) animation.getAnimatedValue());
                    invalidate();
                }
            });
            valueAnimator.start();
        }
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

    private int mLastX;
    private int mLastY;

 /*   @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;//计算x坐标上的差值
                int deltaY = y - mLastY;//计算y坐标上的差值
                float tranX = getTranslationX() + deltaX;//要平移的x值
                float tranY = getTranslationY() + deltaY;//要平移的y值
                setTranslationX(tranX);//设置值
                setTranslationY(tranY);
                scrollTo(deltaX, deltaY);
                break;
            case MotionEvent.ACTION_UP:
                scrollTo(oriX,oriY);
                break;
            default:
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }*/
}
