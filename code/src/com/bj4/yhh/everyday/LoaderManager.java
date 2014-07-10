
package com.bj4.yhh.everyday;

import java.util.ArrayList;
import java.util.Iterator;

import com.bj4.yhh.everyday.activities.MainActivity;
import com.bj4.yhh.everyday.cards.CardsRelativeLayout;
import com.bj4.yhh.everyday.cards.weather.WeartherCards;
import com.bj4.yhh.everyday.database.DatabaseHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

public class LoaderManager {
    private static final boolean DEBUG = true;

    private static final String TAG = "QQQQ";

    private static LoaderManager sInstance;

    private Context mContext;

    private DatabaseHelper mDatabaseHelper;

    private final ArrayList<Card> mCards = new ArrayList<Card>();

    private Callback mCallbacks;

    private final LruCache<Integer, CardsRelativeLayout> mCardsContentCache = new LruCache<Integer, CardsRelativeLayout>(
            50);

    public interface Callback {
        public void dataLoadingDone();

        public void setListViewHeightBasedOnChildren();
    }

    public interface LoadingCardCallback {
        public static final int RESULT_FROM_CACHE = -1;

        public static final int RESULT_OK = 0;

        public static final int RESULT_FAILED = 1;

        public void loadComplete(int result);
    }

    public synchronized static final LoaderManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LoaderManager(context);
        }
        return sInstance;
    }

    private LoaderManager(Context context) {
        mContext = context.getApplicationContext();
        mDatabaseHelper = DatabaseHelper.getInstance(mContext);
        forceReload();
    }

    public class UpdateReceiver extends BroadcastReceiver {
        public static final String INTENT_UPDATE = "com.bj4.yhh.everyday.update_content";

        public static final String INTENT_EXTRA_UPDATE_TYPE = "update_type";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (INTENT_UPDATE.equals(intent.getAction())) {
                Bundle extra = intent.getExtras();
                if (extra != null) {
                    int cardType = extra.getInt(INTENT_EXTRA_UPDATE_TYPE);
                    Iterator<CardsRelativeLayout> iter = mCardsContentCache.snapshot().values()
                            .iterator();
                    while (iter.hasNext()) {
                        CardsRelativeLayout card = iter.next();
                        if (card.getCardType() == cardType) {
                            card.updateContent();
                        }
                    }
                }
            }
        }
    }

    private UpdateReceiver mReceiver = new UpdateReceiver();

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UpdateReceiver.INTENT_UPDATE);
        mContext.registerReceiver(mReceiver, filter);
    }

    public void unregisterReceiver() {
        mContext.unregisterReceiver(mReceiver);
    }

    public void forceReload() {
        mCards.clear();
        mCards.addAll(mDatabaseHelper.getCards());
        if (mCallbacks != null) {
            mCallbacks.dataLoadingDone();
        }
    }

    public ArrayList<Card> getCards() {
        return mCards;
    }

    public void addCallback(Callback cb) {
        mCallbacks = cb;
    }

    public void removeCallback(Callback cb) {
        mCallbacks = null;
    }

    public void loadCardContent(Card card, FrameLayout container, LoadingCardCallback cb,
            int position) {
        View content = mCardsContentCache.get(card.getId());
        if (content == null) {
            new CardLoader(card, container, cb, position)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            ViewParent parent = content.getParent();
            if (parent != null) {
                ((ViewGroup)parent).removeView(content);
            }
            container.addView(content);
            if (cb != null) {
                cb.loadComplete(LoadingCardCallback.RESULT_FROM_CACHE);
            }
        }
    }

    private class CardLoader extends AsyncTask<Void, Void, Integer> {
        private FrameLayout mContainer;

        private LoadingCardCallback mLoadingCardCallback;

        private int mPosition;

        private Card mCard;

        public CardLoader(Card card, FrameLayout container, LoadingCardCallback cb, int position) {
            mContainer = container;
            mLoadingCardCallback = cb;
            mPosition = position;
            mCard = card;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = LoadingCardCallback.RESULT_FAILED;
            switch (mCard.getType()) {
                case Card.CARD_TYPE_WEATHER:
                    if (DEBUG)
                        Log.v(TAG, "create weather");
                    LayoutInflater inflater = (LayoutInflater)mContext
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    CardsRelativeLayout v = (CardsRelativeLayout)inflater.inflate(
                            R.layout.weather_card, null);
                    v.setCardType(mCard.getType());
                    ((WeartherCards)v).setCallback(mCallbacks);
                    mCardsContentCache.put(mCard.getId(), v);
                    result = LoadingCardCallback.RESULT_OK;
                    break;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (mPosition == (Integer)mContainer.getTag()) {
                if (result == LoadingCardCallback.RESULT_OK) {
                    View content = mCardsContentCache.get(mCard.getId());
                    if (content != null) {
                        mContainer.addView(content);
                        if (mLoadingCardCallback != null) {
                            mLoadingCardCallback.loadComplete(LoadingCardCallback.RESULT_OK);
                        }
                    } else {
                        if (mLoadingCardCallback != null) {
                            mLoadingCardCallback.loadComplete(LoadingCardCallback.RESULT_FAILED);
                        }
                    }
                } else {
                    if (mLoadingCardCallback != null) {
                        mLoadingCardCallback.loadComplete(LoadingCardCallback.RESULT_FAILED);
                    }
                }
            }
        }
    }
}
