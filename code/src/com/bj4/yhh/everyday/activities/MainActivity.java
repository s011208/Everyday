
package com.bj4.yhh.everyday.activities;

import com.bj4.yhh.everyday.EverydayApplication;
import com.bj4.yhh.everyday.LoaderManager;
import com.bj4.yhh.everyday.R;
import com.bj4.yhh.everyday.activities.utils.MainActionBar;
import com.bj4.yhh.everyday.activities.utils.MainSettingView;
import com.bj4.yhh.everyday.services.CitiesLoadingService;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

public class MainActivity extends Activity implements LoaderManager.Callback {
    private static final boolean DEBUG = true;

    private static final String TAG = "QQQQ";

    private LoaderManager mLoaderManager;

    private ListView mCardList;

    private CardListAdapter mCardListAdapter;

    private MainActionBar mActionBar;

    private MainSettingView mSettingView;

    private ValueAnimator mSettingViewAnimator;

    private ViewSwitcher mOption;

    private ImageView mRefreshContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startPreloadService();
        init();
    }

    private void startPreloadService() {
        Intent intent = new Intent(this, CitiesLoadingService.class);
        startService(intent);
    }

    protected void onResume() {
        super.onResume();
        forceReload();
        mLoaderManager.addCallback(this);
        collapseSettingView(false);
    }

    protected void onPause() {
        super.onPause();
        mLoaderManager.removeCallback(this);
    }

    public void onBackPressed() {
        if (mSettingView.getScaleX() != 0) {
            collapseSettingView(true);
        } else {
            super.onBackPressed();
        }
    }

    private void init() {
        initBasic();
        initCardsList();
    }

    private void initBasic() {
        mLoaderManager = EverydayApplication.getLoaderManager(getApplicationContext());
        // action bar
        mActionBar = (MainActionBar)findViewById(R.id.main_action_bar);
        mActionBar.setActivity(this);
        mOption = (ViewSwitcher)findViewById(R.id.main_option);
        mOption.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                expandSettingView(true);
            }
        });
        mRefreshContent = (ImageView)findViewById(R.id.refresh_btn);
        mRefreshContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoaderManager != null) {
                    mActionBar.startUpdating();
                    mLoaderManager.forceReload();
                }
            }
        });
        // setting view
        mSettingView = (MainSettingView)findViewById(R.id.main_setting_parent);
        mSettingView.setPivotX(0);
        mSettingViewAnimator = ValueAnimator.ofFloat(0, 1);
        mSettingViewAnimator.setDuration(300);
        mSettingViewAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSettingView.setScaleX((Float)animation.getAnimatedValue());
            }
        });
    }

    public void expandSettingView(boolean animation) {
        mSettingView.bringToFront();
        if (mSettingView.getScaleX() == 1) {
            collapseSettingView(animation);
            return;
        } else if (mSettingView.getScaleX() > 0) {
            return;
        }
        if (animation) {
            mSettingViewAnimator.removeAllListeners();
            mSettingViewAnimator.addListener(new AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                    mSettingView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            mSettingViewAnimator.start();
        } else {
            mSettingView.setScaleX(1f);
            mSettingView.setVisibility(View.VISIBLE);
        }
        mOption.setDisplayedChild(1);
    }

    public void collapseSettingView(boolean animation) {
        if (animation) {
            mSettingViewAnimator.removeAllListeners();
            mSettingViewAnimator.addListener(new AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mSettingView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            mSettingViewAnimator.reverse();
        } else {
            mSettingView.setScaleX(0f);
            mSettingView.setVisibility(View.GONE);
        }
        mOption.setDisplayedChild(0);
    }

    private void initCardsList() {
        mCardList = (ListView)findViewById(R.id.card_list);
        mCardListAdapter = new CardListAdapter(this, mLoaderManager,
                new CardListAdapter.RequestRefreshCallback() {

                    @Override
                    public void requestRefresh() {
                        if (mCardList != null) {
                            setListViewHeightBasedOnChildren();
                        }
                    }
                });
        mCardList.setAdapter(mCardListAdapter);
        // mRefreshLayout =
        // (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        // mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
        //
        // @Override
        // public void onRefresh() {
        // if (mLoaderManager != null) {
        // Log.d(TAG, "main activity forceReload");
        // mLoaderManager.forceReload();
        // }
        // }
        // });
        // mRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
        // android.R.color.holo_green_light, android.R.color.holo_orange_light,
        // android.R.color.holo_red_light);
    }

    private void forceReload() {
        if (mCardListAdapter != null) {
            mCardListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void dataLoadingDone() {
        forceReload();
        Log.d(TAG, "main activity dataLoadingDone");
    }

    public void setListViewHeightBasedOnChildren() {
        ListAdapter listAdapter = mCardList.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = MeasureSpec.makeMeasureSpec(mCardList.getWidth(), MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, mCardList);
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = mCardList.getLayoutParams();
        params.height = totalHeight + (mCardList.getDividerHeight() * (listAdapter.getCount() - 1));
        mCardList.setLayoutParams(params);
        mCardList.requestLayout();
    }

    @Override
    public void allContentRefreshDone() {
        mActionBar.finishUpdating();
    }

}
