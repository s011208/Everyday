
package com.bj4.yhh.everyday.cards.weather;

import com.bj4.yhh.everyday.LoaderManager;
import com.bj4.yhh.everyday.R;
import com.bj4.yhh.everyday.cards.CardsRelativeLayout;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeartherCards extends CardsRelativeLayout {
    private static final boolean DEBUG = true;

    private static final String TAG = "QQQQ";

    private ViewPager mWeathersPager;

    private WeatherPagerAdapter mWeatherPagerAdapter;

    private Context mContext;

    private LoaderManager.Callback mCallback;

    private ProgressBar mLoading;

    private ImageView mOption, mNextPage, mPreviousPage;

    private TextView mPagerTitle;

    private RelativeLayout mMainLayout;

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
        mLoading.setVisibility(View.GONE);
        mMainLayout.setVisibility(View.VISIBLE);
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
        refreshContent(mWeathersPager.getCurrentItem());
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        initCompoments();
    }

    private void refreshContent(int position) {
        if (mWeatherPagerAdapter != null && mWeatherPagerAdapter.getCount() > 0) {
            final WeatherData data = mWeatherPagerAdapter.getItem(position);
            mPagerTitle.setText(data.mCityName + ", " + data.mNationName);
            if (position == 0) {
                mPreviousPage.setVisibility(View.INVISIBLE);
            } else {
                mPreviousPage.setVisibility(View.VISIBLE);
            }
            if (position == mWeatherPagerAdapter.getCount() - 1) {
                mNextPage.setVisibility(View.INVISIBLE);
            } else {
                mNextPage.setVisibility(View.VISIBLE);
            }
        } else {
            mNextPage.setVisibility(View.INVISIBLE);
            mPreviousPage.setVisibility(View.INVISIBLE);
            mPagerTitle.setText(R.string.none_data_hint);
        }
    }

    private void initCompoments() {
        mLoading = (ProgressBar)findViewById(R.id.loading_progress);
        mWeathersPager = (ViewPager)findViewById(R.id.weather_pager);
        mWeathersPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageSelected(int position) {
                refreshContent(position);
            }
        });
        mWeatherPagerAdapter = new WeatherPagerAdapter(mContext, this);
        mWeathersPager.setAdapter(mWeatherPagerAdapter);
        mPagerTitle = (TextView)findViewById(R.id.weather_location);
        mOption = (ImageView)findViewById(R.id.weather_option);
        mOption.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WeatherSettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        mNextPage = (ImageView)findViewById(R.id.weather_move_to_next);
        mNextPage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToNext();
            }
        });
        mPreviousPage = (ImageView)findViewById(R.id.weather_move_to_previous);
        mPreviousPage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPrevious();
            }
        });
        mMainLayout = (RelativeLayout)findViewById(R.id.weather_card_main_layout);
    }

    public void moveToPrevious() {
        mWeathersPager.setCurrentItem(mWeathersPager.getCurrentItem() - 1, true);
    }

    public void moveToNext() {
        mWeathersPager.setCurrentItem(mWeathersPager.getCurrentItem() + 1, true);
    }

    @Override
    public void updateContent() {
        if (mWeatherPagerAdapter != null) {
            mWeatherPagerAdapter.forceReload();
        }
    }
}
