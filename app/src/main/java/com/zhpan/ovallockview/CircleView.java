package com.zhpan.ovallockview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Scroller;


/**
 * Created by zhpan on 2017/5/31.
 * Description: 自定义圆
 */

public class CircleView extends View {
    private int background;
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
    private Rect bounds;

    public CircleView(Context context) {
        this(context, null);
    }


    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
            background = typedArray.getColor(R.styleable.CircleView_circle_color, context.getResources().getColor(R.color.red));
            mTextColor = typedArray.getColor(R.styleable.CircleView_text_color, context.getResources().getColor(R.color.red));
            mTextSize = typedArray.getDimension(R.styleable.CircleView_text_size, 16.0f);
            mText = typedArray.getString(R.styleable.CircleView_text_str);
            typedArray.recycle();
        }
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(background);

        mPaintText = new Paint();
        mPaintText.setColor(mTextColor);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setAntiAlias(true);
        mScroller = new Scroller(context);
        bounds = new Rect();
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
        mRadius = Math.min(mHeight, mWidth) / 2 - Math.min(mHeight, mWidth) / 8;
        mPieCenterX = mWidth / 2;
        mPieCenterY = mHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mPieCenterX, mPieCenterY, mRadius, mPaint);
        drawText(canvas);
    }

    /**
     * 画圆中的文字
     */
    private void drawText(Canvas canvas) {
        if (TextUtils.isEmpty(mText)) return;
        mPaintText.getTextBounds(mText, 0, mText.length(), bounds);
        Paint.FontMetricsInt fontMetricsInt = mPaintText.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetricsInt.bottom + fontMetricsInt.top) / 2 - fontMetricsInt.top;
        canvas.drawText(mText, mPieCenterX, baseline, mPaintText);
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
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
}
