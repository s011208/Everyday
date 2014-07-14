
package com.bj4.yhh.everyday.activities.utils;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class AnimatedIndicatorView extends View {
    private Context mContext;

    private Paint mBackgroundPaint;

    private RadialGradient mRadialGradient;

    private int mHoloBlue, mHoloGreen, mHoloOrange, mHoloRed;

    private int mRadialRadius = 0;

    private ValueAnimator mBackgroundAnimator;

    private int mTouchingX, mTouchingY;

    private boolean hasTouching = false;

    public AnimatedIndicatorView(Context context) {
        this(context, null);
    }

    public AnimatedIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mHoloBlue = mContext.getResources().getColor(android.R.color.holo_blue_bright);
        mHoloGreen = mContext.getResources().getColor(android.R.color.holo_green_light);
        mHoloOrange = mContext.getResources().getColor(android.R.color.holo_orange_light);
        mHoloRed = mContext.getResources().getColor(android.R.color.holo_red_light);
        mBackgroundAnimator = ValueAnimator.ofInt(200, 600);
        mBackgroundAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRadialRadius = (Integer)animation.getAnimatedValue();
                invalidate();
            }
        });
        mBackgroundAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mBackgroundAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mBackgroundAnimator.setDuration(1000);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    public void onFinishInflate() {
        super.onFinishInflate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        mTouchingX = (int)event.getX();
        mTouchingY = (int)event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                hasTouching = true;
                mBackgroundAnimator.setRepeatCount(ValueAnimator.INFINITE);
                mBackgroundAnimator.start();
                break;
            case MotionEvent.ACTION_UP:
                mBackgroundAnimator.setRepeatCount(1);
                hasTouching = false;
                break;
        }
        return true;
    }

    public void onDraw(Canvas canvas) {
        if (hasTouching) {
            mRadialGradient = new RadialGradient(mTouchingX, mTouchingY, mRadialRadius, new int[] {
                    mHoloBlue, mHoloGreen, mHoloOrange, mHoloRed
            }, null, Shader.TileMode.REPEAT);
            mBackgroundPaint.setShader(mRadialGradient);
            canvas.drawPaint(mBackgroundPaint);
        }
        super.onDraw(canvas);
    }
}
