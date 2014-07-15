
package com.bj4.yhh.everyday.activities.utils;

import com.bj4.yhh.everyday.R;
import com.bj4.yhh.everyday.activities.MainActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class MainActionBar extends RelativeLayout {
    private Context mContext;

    private MainActivity mActivity;

    private AnimatedUpdatingView mAnimatedUpdatingView;

    public MainActionBar(Context context) {
        this(context, null);
    }

    public MainActionBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public void setActivity(MainActivity activity) {
        mActivity = activity;
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        mAnimatedUpdatingView = (AnimatedUpdatingView)findViewById(R.id.animated_background);
    }

    public void startUpdating() {
        mAnimatedUpdatingView.startAnimation();
    }

    public void finishUpdating() {
        mAnimatedUpdatingView.finishAnimation();
    }
}
