
package com.bj4.yhh.everyday.cards.weather;

import java.util.ArrayList;

import com.bj4.yhh.everyday.Card;
import com.bj4.yhh.everyday.LoaderManager;
import com.bj4.yhh.everyday.R;
import com.bj4.yhh.everyday.database.DatabaseHelper;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeatherSettingActivity extends Activity {
    public static final boolean DEBUG = true;

    public static final String TAG = "QQQQ";

    private View mLoadingView;

    private RelativeLayout mListContent;

    private AutoCompleteTextView mAutoCompleteCity;

    private static ArrayList<String> sAllCities;

    private TextView mProgressTxt;

    private ImageView mAddNewCity, mRemoveCity;

    private DatabaseHelper mDatabaseHelper;

    private ListView mCityList;

    private CityListAdapter mCityListAdapter;

    private RelativeLayout mBottomLayout;

    private int mBottomLayoutHeight;

    // drag&drop
    private int mDraggedItemPosition = -1;

    private boolean mAcceptDropDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_setting_activity);
        init();
    }

    public void onResume() {
        super.onResume();
        if (sAllCities == null) {
            new DataLoaderTask().execute();
        } else {
            refreshContent();
        }
    }

    private class DataLoaderTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            sAllCities = mDatabaseHelper.getAllCitiesName();
            System.gc();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            refreshContent();
        }
    }

    private ValueAnimator mTranslationBottomLayoutYAnim;

    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            addNewCity();
            return true;
        }
        return super.dispatchKeyEvent(e);
    };

    private void addNewCity() {
        int cityId = mDatabaseHelper.getCityId(mAutoCompleteCity.getText().toString());
        if (cityId == -1) {
            if (DEBUG)
                Log.e(TAG, "cityId is -1");
        } else {
            boolean success = mDatabaseHelper.addNewWeatherCards(cityId) > -1;
            if (success) {
                updateWeatherCards();
                mCityListAdapter.notifyDataSetChanged();
                mAutoCompleteCity.setText("");
            } else {// XXX
            }
        }
    }

    private void init() {
        mDatabaseHelper = DatabaseHelper.getInstance(this);
        mListContent = (RelativeLayout)findViewById(R.id.list_content);
        mAutoCompleteCity = (AutoCompleteTextView)findViewById(R.id.autocompletecity);
        mLoadingView = findViewById(R.id.loading_progress);
        mProgressTxt = (TextView)findViewById(R.id.progress_txt);
        mAddNewCity = (ImageView)findViewById(R.id.add_new_city);
        mCityList = (ListView)findViewById(R.id.city_list);
        mBottomLayout = (RelativeLayout)findViewById(R.id.bottom_layout);
        mBottomLayoutHeight = (int)getBaseContext().getResources().getDimension(
                R.dimen.weather_setting_remove_bar_height);
        mTranslationBottomLayoutYAnim = ValueAnimator.ofInt(mBottomLayoutHeight, 0);
        mTranslationBottomLayoutYAnim.setDuration(300);
        mTranslationBottomLayoutYAnim.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBottomLayout.setTranslationY((Integer)animation.getAnimatedValue());
            }
        });
        mBottomLayout.setTranslationY(mBottomLayoutHeight);
        mBottomLayout.setOnDragListener(new OnDragListener() {

            @Override
            public boolean onDrag(final View v, DragEvent event) {
                boolean rtn = true;
                int action = event.getAction();
                final View dragView = (View)event.getLocalState();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        mTranslationBottomLayoutYAnim.start();
                        rtn = true;
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        mRemoveCity.setImageResource(R.drawable.ic_remove_press);
                        rtn = true;
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        mRemoveCity.setImageResource(R.drawable.ic_remove_unpress);
                        rtn = false;
                        break;
                    case DragEvent.ACTION_DROP:
                        mAcceptDropDelete = true;
                        ValueAnimator va = ValueAnimator.ofFloat(1, 0);
                        va.setDuration(200);
                        va.addUpdateListener(new AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float value = (Float)animation.getAnimatedValue();
                                dragView.setAlpha(value);
                                dragView.setAlpha(value);
                            }
                        });
                        va.addListener(new AnimatorListener() {

                            @Override
                            public void onAnimationStart(Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mDatabaseHelper.removeWeatherCards(mCityListAdapter
                                        .getItem(mDraggedItemPosition).mCityId);
                                mCityListAdapter.notifyDataSetChanged();
                                updateWeatherCards();
                                mAcceptDropDelete = false;
                                mTranslationBottomLayoutYAnim.reverse();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                        va.start();
                        rtn = true;
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        if (!mAcceptDropDelete) {
                            mTranslationBottomLayoutYAnim.reverse();
                        }
                        rtn = true;
                        break;
                    default:
                        rtn = true;
                        break;
                }
                return rtn;
            }
        });
        mRemoveCity = (ImageView)findViewById(R.id.remove_city);
        // drag & drop
        mCityList.setOnDragListener(mCityListDragListener);
        mCityList.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ClipData data = ClipData.newPlainText("", "");
                DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                mDraggedItemPosition = position;
                return true;
            }
        });

        mCityListAdapter = new CityListAdapter(this);
        mCityList.setAdapter(mCityListAdapter);
        mAddNewCity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCity();
            }
        });

        if (mDatabaseHelper.hasCitiesTableLoaded() == false) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    mDatabaseHelper.loadCitiesTable(new DatabaseHelper.ProgressCallback() {

                        @Override
                        public void progress(final int progress) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    mProgressTxt.setText(progress + " %");
                                }
                            });
                        }
                    });
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    new DataLoaderTask().execute();
                }
            }.execute();
        }
    }

    private void updateWeatherCards() {
        Intent intent = new Intent(LoaderManager.UpdateReceiver.INTENT_UPDATE);
        intent.putExtra(LoaderManager.UpdateReceiver.INTENT_EXTRA_UPDATE_TYPE,
                Card.CARD_TYPE_WEATHER);
        sendBroadcast(intent);
    }

    private OnDragListener mCityListDragListener = new OnDragListener() {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            int position = -1;
            Rect fr = new Rect();
            mCityList.getGlobalVisibleRect(fr);
            int y = (int)event.getY() + fr.top;
            for (int i = mCityList.getFirstVisiblePosition(); i < mCityList.getChildCount(); i++) {
                View child = mCityList.getChildAt(i);
                Rect r = new Rect(0, 0, child.getWidth(), child.getHeight());
                mCityList.getChildVisibleRect(child, r, null);
                if (r.bottom >= y && r.top <= y) {
                    position = i;
                    break;
                }
            }
            boolean rtn = true;
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    rtn = true;
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    rtn = true;
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    rtn = false;
                    break;
                case DragEvent.ACTION_DROP:
                    if (!(position == mDraggedItemPosition) && position >= 0) {
                        mDatabaseHelper.switchWeatherCardsPosition(
                                mCityListAdapter.getItem(position).mCityId,
                                mCityListAdapter.getItem(mDraggedItemPosition).mCityId);
                        updateWeatherCards();
                        mCityListAdapter.notifyDataSetChanged();
                    }
                    rtn = true;
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (mDraggedItemPosition != -1 && !mAcceptDropDelete) {
                        try {
                            mCityList.getChildAt(mDraggedItemPosition).setVisibility(View.VISIBLE);
                            mDraggedItemPosition = -1;
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                    rtn = true;
                    break;
                default:
                    rtn = true;
                    break;
            }
            return rtn;
        }
    };

    public static class CityListAdapter extends BaseAdapter {
        private final ArrayList<City> mData = new ArrayList<City>();

        private Context mContext;

        private LayoutInflater mInflater;

        private DatabaseHelper mDatabaseHelper;

        public CityListAdapter(Context c) {
            mContext = c;
            mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mDatabaseHelper = DatabaseHelper.getInstance(mContext);
            initData();
        }

        public void initData() {
            mData.clear();
            mData.addAll(mDatabaseHelper.getAllTrackingCities());
        }

        public void notifyDataSetChanged() {
            initData();
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public City getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.weather_setting_city_list_row, null);
                holder = new ViewHolder();
                holder.mTxt = (TextView)convertView.findViewById(R.id.txt);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            final City city = getItem(position);
            convertView.setVisibility(View.VISIBLE);
            convertView.setAlpha(1);
            holder.mTxt.setText(city.mCity + ", " + city.mNation);
            return convertView;
        }

        public static class ViewHolder {
            TextView mTxt;
        }
    }

    private void refreshContent() {
        if (mLoadingView != null) {
            mLoadingView.setVisibility(View.GONE);
        }
        if (mListContent != null) {
            mListContent.setVisibility(View.VISIBLE);
        }
        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(this,
                R.layout.auto_complete_item_view, sAllCities);
        mAutoCompleteCity.setAdapter(citiesAdapter);
        mCityListAdapter.notifyDataSetChanged();
    }
}
