
package com.bj4.yhh.everyday.activities;

import com.bj4.yhh.everyday.R;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

    private LinearLayout mCardsContainer;

    private ProgressBar mLoadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initBasic();
        initCardsList();
    }

    private void initBasic() {
        mLoadingProgress = (ProgressBar)findViewById(R.id.cards_loading_progress);
    }

    private void initCardsList() {
        mCardsContainer = (LinearLayout)findViewById(R.id.cards_container);
    }

    public void addCard(View view) {
        mCardsContainer.addView(view);
    }

    public void removeCard(View view) {
        mCardsContainer.removeView(view);
    }
}
