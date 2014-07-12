
package com.bj4.yhh.everyday.cards.allapps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bj4.yhh.everyday.Card;
import com.bj4.yhh.everyday.LoaderManager;
import com.bj4.yhh.everyday.R;
import com.bj4.yhh.everyday.database.DatabaseHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AllappsSettingActivity extends Activity {
    private ListView mAllappsList;

    private AllappsListAdapter mAllappsListAdapter;

    private boolean mHasUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allapps_setting_activity);
        init();
    }

    private void init() {
        mAllappsList = (ListView)findViewById(R.id.allapps_list);
        mAllappsListAdapter = new AllappsListAdapter(this);
        mAllappsList.setAdapter(mAllappsListAdapter);
        mAllappsList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAllappsListAdapter.switchStatue(position);
                mAllappsListAdapter.notifyDataSetChanged(false);
                mHasUpdate = true;
            }
        });
    }

    public void onPause() {
        super.onPause();
        if (mHasUpdate) {
            updateAllAppsCards();
        }
        mHasUpdate = false;
    }

    private void updateAllAppsCards() {
        Intent intent = new Intent(LoaderManager.UpdateReceiver.INTENT_UPDATE);
        intent.putExtra(LoaderManager.UpdateReceiver.INTENT_EXTRA_UPDATE_TYPE,
                Card.CARD_TYPE_ALLAPPS);
        sendBroadcast(intent);
    }

    public static class AllappsListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        private Context mContext;

        private LruCache<String, Bitmap> mIconCache;

        private LruCache<String, String> mLabelCache;

        private List<ResolveInfo> mPkgAppsList = new ArrayList<ResolveInfo>();

        private SparseArray<Boolean> mCheckedList = new SparseArray<Boolean>();

        private PackageManager mPackageManager;

        private DatabaseHelper mDatabaseHelper;

        private int mIconSize;

        public AllappsListAdapter(Context context) {
            mContext = context;
            mIconCache = new LruCache<String, Bitmap>(150);
            mLabelCache = new LruCache<String, String>(150);
            mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mPackageManager = mContext.getPackageManager();
            mIconSize = (int)mContext.getResources().getDimension(
                    R.dimen.allapps_setting_shortcut_size);
            mDatabaseHelper = DatabaseHelper.getInstance(mContext);
            initData();
        }

        private void initData() {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            mPkgAppsList = mPackageManager.queryIntentActivities(mainIntent, 0);
            Collections.sort(mPkgAppsList, new Comparator<ResolveInfo>() {

                @Override
                public int compare(ResolveInfo arg0, ResolveInfo arg1) {
                    String info0 = mLabelCache.get(arg0.activityInfo.name);
                    if (info0 == null) {
                        info0 = (String)arg0.activityInfo.loadLabel(mPackageManager);
                        mLabelCache.put(arg0.activityInfo.name, info0);
                    }
                    String info1 = mLabelCache.get(arg1.activityInfo.name);
                    if (info1 == null) {
                        info1 = (String)arg1.activityInfo.loadLabel(mPackageManager);
                        mLabelCache.put(arg1.activityInfo.name, info1);
                    }
                    return info0.compareTo(info1);
                }
            });
            mCheckedList.clear();
            ArrayList<ShortCut> shortCuts = new ArrayList<ShortCut>();
            shortCuts.addAll(mDatabaseHelper.getAllappsShortCut());
            for (int i = 0; i < mPkgAppsList.size(); i++) {
                boolean hasFind = false;
                for (ShortCut s : shortCuts) {
                    ResolveInfo info = mPkgAppsList.get(i);
                    if (s.isEqual(info.activityInfo.packageName, info.activityInfo.name)) {
                        hasFind = true;
                        break;
                    }
                }
                mCheckedList.put(i, hasFind);
            }
        }

        public void switchStatue(int position) {
            boolean status = !mCheckedList.get(position);
            mCheckedList.put(position, status);
            ResolveInfo info = getItem(position);
            if (status) {
                mDatabaseHelper.addNewAllappsShortCut(info.activityInfo.packageName,
                        info.activityInfo.name);
            } else {
                mDatabaseHelper.removeAllappsShortCut(info.activityInfo.packageName,
                        info.activityInfo.name);
            }
        }

        public void notifyDataSetChanged() {
            initData();
            super.notifyDataSetChanged();
        }

        public void notifyDataSetChanged(boolean updateContent) {
            if (updateContent) {
                initData();
            }
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mPkgAppsList.size();
        }

        @Override
        public ResolveInfo getItem(int position) {
            return mPkgAppsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.allapps_setting_list_row, null);
                holder = new ViewHolder();
                holder.mIcon = (ImageView)convertView.findViewById(R.id.icon);
                holder.mLabel = (TextView)convertView.findViewById(R.id.label);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            ResolveInfo info = getItem(position);
            String className = info.activityInfo.name;
            String label = mLabelCache.get(className);
            if (label == null) {
                label = (String)info.loadLabel(mPackageManager);
                mLabelCache.put(className, label);
            }
            holder.mLabel.setText(label);
            Bitmap bitmap = mIconCache.get(className);
            if (bitmap == null) {
                Drawable icon = info.loadIcon(mPackageManager);
                icon.setBounds(0, 0, mIconSize, mIconSize);
                bitmap = Bitmap.createBitmap(mIconSize, mIconSize,
                        android.graphics.Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                icon.draw(canvas);
                canvas.setBitmap(null);
                mIconCache.put(info.activityInfo.name, bitmap);
            }
            holder.mIcon.setImageBitmap(bitmap);
            if (mCheckedList.get(position)) {
                convertView.setBackgroundColor(Color.argb(60, 0, 236, 255));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            return convertView;
        }

        static class ViewHolder {
            ImageView mIcon;

            TextView mLabel;
        }
    }
}
