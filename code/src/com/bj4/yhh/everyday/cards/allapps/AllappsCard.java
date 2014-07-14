
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
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bj4.yhh.everyday.R;
import com.bj4.yhh.everyday.cards.CardsRelativeLayout;
import com.bj4.yhh.everyday.cards.CardsRelativeLayout.ContentLoadingCallback;
import com.bj4.yhh.everyday.cards.weather.WeatherSettingActivity;
import com.bj4.yhh.everyday.database.DatabaseHelper;

public class AllappsCard extends CardsRelativeLayout {
    private static final String TAG = "QQQQ";

    private static final boolean DEBUG = true;

    private RelativeLayout mTopLayout;

    private LinearLayout mAllappsContainer;

    private ProgressBar mProgressHint;

    private ImageView mOption;

    private DatabaseHelper mDatabaseHelper;

    public AllappsCard(Context context) {
        this(context, null);
    }

    public AllappsCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllappsCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDatabaseHelper = DatabaseHelper.getInstance(mContext);
    }

    @Override
    public void updateContent(ContentLoadingCallback cb) {
        super.updateContent(cb);
        new AllappsLoaderTask().execute();
    }

    @Override
    public void initCompoments() {
        mTopLayout = (RelativeLayout)findViewById(R.id.top_layout);
        mAllappsContainer = (LinearLayout)findViewById(R.id.allapps_container);
        mProgressHint = (ProgressBar)findViewById(R.id.loading_progress);
        mOption = (ImageView)findViewById(R.id.allapps_option);
        mOption.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AllappsSettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        new AllappsLoaderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    class AllappsLoaderTask extends AsyncTask<Void, Void, Void> {
        private ArrayList<ShortCut> mShortCuts = new ArrayList<ShortCut>();

        @Override
        protected Void doInBackground(Void... arg0) {
            mShortCuts.addAll(mDatabaseHelper.getAllappsShortCut());
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            PackageManager pm = mContext.getPackageManager();
            List<ResolveInfo> pkgAppsList = pm.queryIntentActivities(mainIntent, 0);
            for (ResolveInfo info : pkgAppsList) {
                for (ShortCut s : mShortCuts) {
                    if (s.isEqual(info.activityInfo.packageName, info.activityInfo.name)) {
                        s.applyShortCutInfo(info.loadLabel(pm), info.loadIcon(pm));
                        break;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            onDataUpdated();
            onRefreshDone();
        }

        public void onDataUpdated() {
            mProgressHint.setVisibility(View.GONE);
            mTopLayout.setVisibility(View.VISIBLE);
            mAllappsContainer.removeAllViews();
            LayoutInflater inflater = (LayoutInflater)mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int iconSize = (int)mContext.getResources().getDimension(
                    R.dimen.allapps_shortcut_icon_size);
            for (final ShortCut s : mShortCuts) {
                TextView shortcut = (TextView)inflater.inflate(R.layout.allapps_shortcut, null);
                shortcut.setText(s.getLabel());
                shortcut.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            Intent start = new Intent();
                            start.setComponent(new ComponentName(s.getPackageName(), s
                                    .getClassName()));
                            start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(start);
                        } catch (Exception e) {
                            startGoogleStorePage(mContext, s.getPackageName());
                        }
                    }
                });
                Drawable icon = s.getIcon();
                if (icon != null) {
                    icon.setBounds(0, 0, iconSize, iconSize);
                    shortcut.setCompoundDrawablesRelative(null, icon, null, null);
                }
                mAllappsContainer.addView(shortcut);
            }
        }
    }

    public static void startGoogleStorePage(Context context, String packageName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                    + packageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                    .parse("http://play.google.com/store/apps/details?id=" + packageName))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}
