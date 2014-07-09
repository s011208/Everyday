
package com.bj4.yhh.everyday.cards.weather;

import com.bj4.yhh.everyday.LoaderManager;
import com.bj4.yhh.everyday.R;
import com.bj4.yhh.everyday.cards.CardsRelativeLayout;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

public class WeartherCards extends CardsRelativeLayout {
    private static final boolean DEBUG = true;

    private static final String TAG = "QQQQ";

    private ViewPager mWeathersPager;

    private WeatherPagerAdapter mWeatherPagerAdapter;

    private Context mContext;

    private LoaderManager.Callback mCallback;

    public WeartherCards(Context context) {
        this(context, null);
    }

    public WeartherCards(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeartherCards(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public void setCallback(LoaderManager.Callback cb) {
        mCallback = cb;
    }

    public void onDataUpdated() {
        if (mCallback != null) {
            // content updated
            // 1. measure view pager
            int desiredWidth = MeasureSpec.makeMeasureSpec(mWeathersPager.getWidth(),
                    MeasureSpec.AT_MOST);
            int height = 0;
            if (mWeathersPager.getChildCount() > 0) {
                mWeathersPager.getChildAt(0).measure(desiredWidth, MeasureSpec.UNSPECIFIED);
                height = mWeathersPager.getChildAt(0).getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mWeathersPager.getLayoutParams();
            params.height = height;
            mWeathersPager.setLayoutParams(params);
            mWeathersPager.requestLayout();
            // 2. measure list view
            mCallback.setListViewHeightBasedOnChildren();
        }
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        updateContent();
        initCompoments();
    }

    private void initCompoments() {
        mWeathersPager = (ViewPager)findViewById(R.id.weather_pager);
        mWeatherPagerAdapter = new WeatherPagerAdapter(mContext, this);
        mWeathersPager.setAdapter(mWeatherPagerAdapter);
    }

    @Override
    public void updateContent() {
    }
}
