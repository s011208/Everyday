
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
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlayStoreCard extends CardsRelativeLayout {

    private static final boolean DEBUG = true;

    private static final String TAG = "QQQQ";

    private DatabaseHelper mDatabaseHelper;

    private ArrayList<PlayStoreItem> mData;

    private ProgressBar mLoadingProgress;

    private LinearLayout mTopLayout;

    private LayoutInflater mInflater;

    public PlayStoreCard(Context context) {
        this(context, null);
    }

    public PlayStoreCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayStoreCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void initCompoments() {
        mDatabaseHelper = DatabaseHelper.getInstance(mContext);
        mLoadingProgress = (ProgressBar)findViewById(R.id.loading_progress);
        mTopLayout = (LinearLayout)findViewById(R.id.top_layout);
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = mDatabaseHelper.getPlayStoreItems();
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
        private ArrayList<View> mContainers = new ArrayList<View>();

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
                            AllappsCard.startGoogleStorePage(
                                    mContext,
                                    item.getAppUrl().substring(
                                            item.getAppUrl().lastIndexOf("=") + 1));
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
                mTopLayout.removeAllViews();
                for (View v : mContainers) {
                    mTopLayout.addView(v);
                }
            }
            mTopLayout.setVisibility(View.VISIBLE);
            mLoadingProgress.setVisibility(View.GONE);
            onRefreshDone();
        }
    }
}
