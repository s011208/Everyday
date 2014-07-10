
package com.bj4.yhh.everyday.cards.allapps;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bj4.yhh.everyday.R;
import com.bj4.yhh.everyday.cards.CardsRelativeLayout;

public class AllappsCard extends CardsRelativeLayout {
    private HorizontalScrollView mHorizontalScrollView;

    private LinearLayout mAllappsContainer;

    private ProgressBar mProgressHint;

    public AllappsCard(Context context) {
        this(context, null);
    }

    public AllappsCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllappsCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void updateContent() {
    }

    @Override
    public void initCompoments() {
        mHorizontalScrollView = (HorizontalScrollView)findViewById(R.id.allapps_main_container);
        mAllappsContainer = (LinearLayout)findViewById(R.id.allapps_container);
        mProgressHint = (ProgressBar)findViewById(R.id.loading_progress);
        new AllappsLoaderTask().execute();
    }

    class AllappsLoaderTask extends AsyncTask<Void, Void, Void> {
        private ArrayList<ShortCut> mShortCuts = new ArrayList<ShortCut>();

        @Override
        protected Void doInBackground(Void... arg0) {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            PackageManager pm = mContext.getPackageManager();
            List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, 0);
            for (ResolveInfo info : pkgAppsList) {
                mShortCuts.add(new ShortCut(info.activityInfo.packageName, info.activityInfo.name,
                        info.loadLabel(pm), info.loadIcon(pm)));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            onDataUpdated();
        }

        public void onDataUpdated() {
            mProgressHint.setVisibility(View.GONE);
            mHorizontalScrollView.setVisibility(View.VISIBLE);
            int shortCutSize = (int)mContext.getResources().getDimension(
                    R.dimen.allapps_shortcut_size);
            int iconSize = (int)mContext.getResources().getDimension(
                    R.dimen.allapps_shortcut_icon_size);
            int iconPadding = (int)mContext.getResources().getDimension(
                    R.dimen.allapps_shortcut_padding);
            for (final ShortCut s : mShortCuts) {
                TextView shortcut = new TextView(mContext);
                shortcut.setText(s.getLabel());
                shortcut.setMaxLines(2);
                shortcut.setEllipsize(TruncateAt.END);
                shortcut.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
                shortcut.setTextColor(Color.BLACK);
                shortcut.setGravity(Gravity.CENTER);
                shortcut.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent start = new Intent();
                        start.setComponent(new ComponentName(s.getPackageName(), s.getClassName()));
                        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(start);
                    }
                });
                Drawable icon = s.getIcon();
                icon.setBounds(0, 0, iconSize, iconSize);
                shortcut.setCompoundDrawables(null, icon, null, null);
                LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(shortCutSize,
                        shortCutSize);
                mAllappsContainer.addView(shortcut, ll);
            }
        }
    }

}
