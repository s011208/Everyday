
package com.bj4.yhh.everyday.cards.playstore.recommend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.bj4.yhh.everyday.Card;
import com.bj4.yhh.everyday.R;
import com.bj4.yhh.everyday.cards.CardsRelativeLayout;
import com.bj4.yhh.everyday.cards.CardsRelativeLayout.ContentLoadingCallback;
import com.bj4.yhh.everyday.cards.allapps.AllappsCard;
import com.bj4.yhh.everyday.database.DatabaseHelper;
import com.bj4.yhh.everyday.services.UpdateDataService;
import com.bj4.yhh.everyday.utils.Utils;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class PlayStoreCard extends CardsRelativeLayout {

    private static final boolean DEBUG = true;

    private static final String TAG = "QQQQ";

    private DatabaseHelper mDatabaseHelper;

    private ArrayList<PlayStoreItem> mData;

    private ProgressBar mLoadingProgress;

    private FrameLayout mTopLayout;

    private LayoutInflater mInflater;

    private ArrayList<View> mContainers = new ArrayList<View>();

    private long mCurrentIndicator = 0;

    private Handler mHandler;

    private ValueAnimator mSwitchViewHelper;

    private Runnable mUpdateContentRunnable = new Runnable() {
        @Override
        public void run() {
            if (mContainers.size() > 0) {
                ++mCurrentIndicator;
                mSwitchViewHelper.start();
                mHandler.removeCallbacks(mUpdateContentRunnable);
                mHandler.postDelayed(mUpdateContentRunnable, 5000);
            }
        }
    };

    public PlayStoreCard(Context context) {
        this(context, null);
    }

    public PlayStoreCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayStoreCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mHandler.removeCallbacks(mUpdateContentRunnable);
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            mHandler.removeCallbacks(mUpdateContentRunnable);
            mHandler.postDelayed(mUpdateContentRunnable, 5000);
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    @Override
    public void initCompoments() {
        mDatabaseHelper = DatabaseHelper.getInstance(mContext);
        mLoadingProgress = (ProgressBar)findViewById(R.id.loading_progress);
        mTopLayout = (FrameLayout)findViewById(R.id.top_layout);
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = mDatabaseHelper.getPlayStoreItems();
        mSwitchViewHelper = ValueAnimator.ofFloat(0, 1);
        mSwitchViewHelper.setDuration(400);
        mSwitchViewHelper.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mContainers.size() > 0) {
                    float value = (Float)animation.getAnimatedValue();
                    int currentIndex = (int)((mCurrentIndicator - 1) % mContainers.size());
                    int nextIndex = (int)(mCurrentIndicator % mContainers.size());
                    if (currentIndex != nextIndex) {
                        if (value == 0) {
                            mContainers.get(nextIndex).setScaleX(0);
                            mContainers.get(nextIndex).setVisibility(View.VISIBLE);
                            mContainers.get(nextIndex).bringToFront();
                        } else if (value == 1) {
                            mContainers.get(currentIndex).setVisibility(View.GONE);
                        } else {
                            mContainers.get(nextIndex).setScaleX(value);
                            mContainers.get(currentIndex).setScaleX(1 - value);
                        }
                    }
                }
            }
        });
        if (mData.isEmpty()) {
            Intent service = new Intent(mContext, UpdateDataService.class);
            service.putExtra(UpdateDataService.INTENT_KEY_UPDATE_DATA_FROM_INTERNET,
                    Card.CARD_TYPE_PLAY_STORE_RECOMMAND);
            mContext.startService(service);
        } else {
            updateContent(false);
        }
    }

    @Override
    public void updateContent(ContentLoadingCallback cb) {
        super.updateContent(cb);
        updateContent(true);
    }

    private void updateContent(boolean reload) {
        if (reload) {
            mData = mDatabaseHelper.getPlayStoreItems();
        }
        if (mData.isEmpty() == false) {
            new UpdateViewTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    class UpdateViewTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            HashMap<String, ArrayList<PlayStoreItem>> itemMap = new HashMap<String, ArrayList<PlayStoreItem>>();
            for (PlayStoreItem item : mData) {
                ArrayList<PlayStoreItem> list = itemMap.get(item.getListType());
                if (list == null) {
                    list = new ArrayList<PlayStoreItem>();
                    list.add(item);
                    itemMap.put(item.getListType(), list);
                } else {
                    list.add(item);
                }
            }
            Iterator<String> iter = itemMap.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                ArrayList<PlayStoreItem> items = itemMap.get(key);
                View row = mInflater.inflate(R.layout.play_store_card_row, null);
                LinearLayout container = (LinearLayout)row
                        .findViewById(R.id.play_store_items_container);
                TextView listType = (TextView)row.findViewById(R.id.play_store_list_type);
                listType.setText(key);
                for (final PlayStoreItem item : items) {
                    View itemView = mInflater.inflate(R.layout.play_store_card_item, null);
                    TextView appName = (TextView)itemView.findViewById(R.id.play_store_app_name);
                    appName.setText(item.getAppName());
                    ImageView appPreview = (ImageView)itemView
                            .findViewById(R.id.play_store_app_img);
                    UrlImageViewHelper.setUrlDrawable(appPreview, item.getImgUrl(),
                            R.drawable.ic_launcher);
                    itemView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            if (item.getAppUrl().startsWith("/store/books/")) {
                                Intent i = new Intent(Intent.ACTION_VIEW, Uri
                                        .parse("https://play.google.com" + item.getAppUrl()))
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(i);
                            } else {
                                Utils.startGoogleStorePage(
                                        mContext,
                                        item.getAppUrl().substring(
                                                item.getAppUrl().lastIndexOf("=") + 1));
                            }
                        }
                    });
                    container.addView(itemView);
                }
                mContainers.add(row);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mContainers.size() > 0) {
                if (mSwitchViewHelper.isStarted()) {
                    mSwitchViewHelper.cancel();
                }
                mTopLayout.removeAllViews();
                for (View v : mContainers) {
                    mTopLayout.addView(v);
                    v.setVisibility(View.GONE);
                }
                if (mHandler == null) {
                    mHandler = new Handler();
                }
                mHandler.removeCallbacks(mUpdateContentRunnable);
                mHandler.post(mUpdateContentRunnable);
            }
            mTopLayout.setVisibility(View.VISIBLE);
            mLoadingProgress.setVisibility(View.GONE);
            onRefreshDone();
        }
    }
}
