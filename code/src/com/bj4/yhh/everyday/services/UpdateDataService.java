
package com.bj4.yhh.everyday.services;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.bj4.yhh.everyday.Card;
import com.bj4.yhh.everyday.LoaderManager;
import com.bj4.yhh.everyday.cards.playstore.recommend.PlayStoreItem;
import com.bj4.yhh.everyday.database.DatabaseHelper;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class UpdateDataService extends Service {
    private static final String TAG = "QQQQ";

    private static final boolean DEBUG = true;

    public static final String INTENT_KEY_UPDATE_DATA_FROM_INTERNET = "ask_for_update";

    private DatabaseHelper mDatabaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabaseHelper = DatabaseHelper.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                int cardType = extras.getInt(INTENT_KEY_UPDATE_DATA_FROM_INTERNET);
                switch (cardType) {
                    case Card.CARD_TYPE_PLAY_STORE_RECOMMAND:
                        new ParserTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        break;
                }
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class ParserTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            String url = "https://play.google.com/store";
            ArrayList<PlayStoreItem> items = new ArrayList<PlayStoreItem>();
            try {
                Document doc = Jsoup.connect(url).get();
                Elements clusterContainer = doc
                        .select("div[class$=cluster-container cards-transition-enabled]");
                Log.e(TAG, "clusterContainer count: " + clusterContainer.size());
                for (Element ele : clusterContainer) {
                    Elements titleElement = ele.select("a[class$=title-link]");
                    String listType = titleElement.get(0).text();
                    Elements cardList = ele
                            .select("div[class$=card-content id-track-click id-track-impression]");
                    Log.i(TAG, "cardlist size: " + cardList.size());
                    for (Element card : cardList) {
                        String imgUrl = card.select("img").attr("src");
                        String appUrl = card.select("a[href]").attr("href");
                        String appName = card.select("a[title]").attr("title");
                        items.add(new PlayStoreItem(appUrl, imgUrl, listType, appName));
                    }
                }
                if (items.isEmpty() == false) {
                    mDatabaseHelper.addPlayStoreItems(items);
                }
            } catch (IOException e) {
                if (DEBUG)
                    Log.w(TAG, "failed", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            updatePlayStoreCards();
        }

        private void updatePlayStoreCards() {
            Intent intent = new Intent(LoaderManager.UpdateReceiver.INTENT_UPDATE);
            intent.putExtra(LoaderManager.UpdateReceiver.INTENT_EXTRA_UPDATE_TYPE,
                    Card.CARD_TYPE_PLAY_STORE_RECOMMAND);
            sendBroadcast(intent);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
