
package com.bj4.yhh.everyday.activities;

import com.bj4.yhh.everyday.EverydayApplication;
import com.bj4.yhh.everyday.LoaderManager;
import com.bj4.yhh.everyday.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity implements LoaderManager.Callback {

    private LoaderManager mLoaderManager;

    private ListView mCardList;

    private CardListAdapter mCardListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    protected void onResume() {
        super.onResume();
        mLoaderManager.addCallback(this);
    }

    protected void onPause() {
        super.onPause();
        mLoaderManager.removeCallback(this);
    }

    private void init() {
        initBasic();
        forceReload();
        initCardsList();
    }

    private void initBasic() {
        mLoaderManager = EverydayApplication.getLoaderManager(getApplicationContext());
    }

    private void initCardsList() {
        mCardList = (ListView)findViewById(R.id.card_list);
        mCardListAdapter = new CardListAdapter(this, mLoaderManager,
                new CardListAdapter.RequestRefreshCallback() {

                    @Override
                    public void requestRefresh() {
                        if (mCardList != null) {
                            setListViewHeightBasedOnChildren(mCardList);
                        }
                    }
                });
        mCardList.setAdapter(mCardListAdapter);
    }

    private void forceReload() {
        if (mCardListAdapter != null) {
            mCardListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void dataLoadingDone() {
        forceReload();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
