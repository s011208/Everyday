
package com.bj4.yhh.everyday.cards.playstore.recommend;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class PlayStoreParser {
    private static final boolean DEBUG = true;

    private static final String TAG = "QQQQ";

    private Context mContext;

    public PlayStoreParser(Context c) {
        mContext = c;
        new ParserTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static class ParserTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            String url = "https://play.google.com/store";
            try {
                Document doc = Jsoup.connect(url).get();
                Elements clusterContainer = doc
                        .select("div[class$=cluster-container cards-transition-enabled]");
                Log.e(TAG, "clusterContainer count: " + clusterContainer.size());
                for (Element ele : clusterContainer) {
                    Elements titleElement = ele.select("a[class$=title-link]");
                    String link = titleElement.get(0).attr("href");
                    String title = titleElement.get(0).text();
                    Log.e(TAG, "title: " + title + ", link: " + link);
                    Elements cardList = ele
                            .select("div[data-docid]");
                    Log.i(TAG, "cardlist size: " + cardList.size());
                    for (Element card : cardList) {
                        String imgUrl = card.select("img[class$=cover-image]").attr(
                                "data_cover_small");
                        Log.i(TAG, imgUrl);
                    }
                }
            } catch (IOException e) {
                if (DEBUG)
                    Log.w(TAG, "failed", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }
}
