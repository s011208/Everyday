
package com.bj4.yhh.everyday.cards.weather;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bj4.yhh.everyday.Card;
import com.bj4.yhh.everyday.LoaderManager;
import com.bj4.yhh.everyday.R;
import com.bj4.yhh.everyday.database.DatabaseHelper;
import com.bj4.yhh.everyday.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherPagerAdapter extends PagerAdapter {
    private static final String TAG = "QQQQ";

    private static final boolean DEBUG = true;

    private final ArrayList<WeatherData> mData = new ArrayList<WeatherData>();

    private Context mContext;

    private DatabaseHelper mDatabaseHelper;

    private LayoutInflater mInflater;

    private final LruCache<Integer, View> mCachedView = new LruCache<Integer, View>(5);

    private WeartherCards mWeartherCards;

    public WeatherPagerAdapter(Context c, WeartherCards wc) {
        mContext = c;
        mWeartherCards = wc;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDatabaseHelper = DatabaseHelper.getInstance(mContext);
        initData();
    }

    private void initData() {
        new WeatherDataLoader().execute();
    }

    public class WeatherDataLoader extends AsyncTask<Void, Void, Void> {
        private ArrayList<WeatherData> data = new ArrayList<WeatherData>();

        @Override
        protected Void doInBackground(Void... arg0) {
            ArrayList<Integer> cityIds = mDatabaseHelper.getWeatherCards();
            String url = "http://api.openweathermap.org/data/2.5/group?id=";
            for (Integer cityId : cityIds) {
                url += cityId + ",";
            }
            if (DEBUG)
                Log.d(TAG, "parseOnInternet, url: " + url + ", cityIds size: " + cityIds.size());
            final String stream = Utils.parseOnInternet(url);
            processWeatherData(stream);
            if (DEBUG)
                Log.d(TAG, stream);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mData.clear();
            mData.addAll(data);
            if (mData.size() == 0) {
                mCachedView.evictAll();
            }
            notifyDataSetChanged();
            if (DEBUG)
                Log.i(TAG, "parse done");
        }

        /**
         * http://openweathermap.org/weather-data
         * http://openweathermap.org/weather-conditions
         * http://openweathermap.org/help/city_list.txt
         * 
         * @param stream
         * @return
         */
        private String processWeatherData(String stream) {
            String rtn = null;
            try {
                JSONObject uselessOuter = new JSONObject(stream);
                JSONArray outerArray = uselessOuter.getJSONArray("list");
                for (int i = 0; i < outerArray.length(); i++) {
                    JSONObject json = outerArray.getJSONObject(i);
                    String cityName = json.getString("name");
                    int cityId = json.getInt("id");
                    JSONObject sys = json.getJSONObject("sys");
                    String nationName = sys.getString("country");
                    long sunRise = sys.getLong("sunrise");
                    long sunSet = sys.getLong("sunset");
                    JSONObject main = json.getJSONObject("main");
                    long tempature = (long)(main.getLong("temp") - 273.15);
                    long tempatureMax = (long)(main.getLong("temp_max") - 273.15);
                    long tempatureMin = (long)(main.getLong("temp_min") - 273.15);
                    int humidity = main.getInt("humidity");
                    JSONArray weather = json.getJSONArray("weather");
                    JSONObject weatherObject = weather.getJSONObject(0);
                    int conditionCode = weatherObject.getInt("id");
                    String conditionDes = weatherObject.getString("description");
                    String conditionIcon = weatherObject.getString("icon");
                    JSONObject wind = json.getJSONObject("wind");
                    int windSpeed = wind.getInt("speed");
                    data.add(new WeatherData(tempature, tempatureMax, tempatureMin, humidity,
                            sunSet, sunRise, cityId, cityName, nationName, conditionCode,
                            conditionDes, conditionIcon, windSpeed));
                }
            } catch (JSONException e) {
                if (DEBUG)
                    Log.w(TAG, "failed", e);
            }
            return rtn;
        }
    }

    public void forceReload() {
        initData();
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (mWeartherCards != null) {
            mWeartherCards.onDataUpdated();
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    private int getWeatherIconResource(int condition) {
        int rtn = R.drawable.ic_clear_sky;
        if (condition == 800) {
            rtn = R.drawable.ic_clear_sky;
        } else if (condition == 801) {
            rtn = R.drawable.ic_few_cloudy;
        } else if (condition == 802) {
            rtn = R.drawable.ic_scattered_cloudy;
        } else if (condition == 803) {
            rtn = R.drawable.ic_broken_cloudy;
        } else if (condition == 804) {
            rtn = R.drawable.ic_broken_cloudy;
        } else if (condition < 300) {
            rtn = R.drawable.ic_thunderstorm;
        } else if (condition < 400) {
            rtn = R.drawable.ic_shower_rain;
        } else if (condition < 500) {
            rtn = R.drawable.ic_shower_rain;
        } else if (condition < 600) {
            rtn = R.drawable.ic_rain;
        } else if (condition < 700) {
            rtn = R.drawable.ic_snow;
        } else if (condition < 800) {
            rtn = R.drawable.ic_mist;
        }
        return rtn;
    }

    public WeatherData getItem(int position) {
        return mData.get(position);
    }

    private View generateWeatherView(final WeatherData data, final int position) {
        View v = null;
        v = mInflater.inflate(R.layout.weather_card_pager_content, null);
        ImageView weatherImage = (ImageView)v.findViewById(R.id.weather_icon);
        weatherImage.setImageResource(getWeatherIconResource(data.mConditionCode));
        TextView weatherDescription = (TextView)v.findViewById(R.id.weather_description);
        weatherDescription.setText(data.mConditionDescription + "");
        TextView weatherHumidity = (TextView)v.findViewById(R.id.weather_humidity);
        weatherHumidity.setText(data.mHumidity + "");
        TextView weatherWind = (TextView)v.findViewById(R.id.weather_wind);
        weatherWind.setText(data.mWind + "");
        TextView weatherTemp = (TextView)v.findViewById(R.id.weather_temp);
        weatherTemp.setText(data.mCurrentTempature + " \u00b0");
        TextView weatherTempMax = (TextView)v.findViewById(R.id.weather_temp_max);
        weatherTempMax.setText(data.mMaxTempature + " \u00b0");
        TextView weatherTempMin = (TextView)v.findViewById(R.id.weather_temp_min);
        weatherTempMin.setText(data.mMinTempature + " \u00b0");
        return v;
    }

    @Override
    public Object instantiateItem(View collection, int position) {
        View v = mCachedView.get(position);
        if (v == null) {
            v = generateWeatherView(mData.get(position), position);
        }
        ((ViewPager)collection).addView(v);
        if (DEBUG)
            Log.d(TAG, "instantiateItem position: " + position);
        return v;
    }

    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((ViewPager)collection).removeView((View)view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
