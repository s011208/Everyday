
package com.bj4.yhh.everyday.cards.weather;

import org.json.JSONException;
import org.json.JSONObject;

import com.bj4.yhh.everyday.R;
import com.bj4.yhh.everyday.cards.CardsRelativeLayout;
import com.bj4.yhh.everyday.utils.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class WeartherCards extends CardsRelativeLayout {

    private static final String PREVIOUS_URL = "http://api.openweathermap.org/data/2.5/weather?q=";

    private TextView mTxt;

    public WeartherCards(Context context) {
        this(context, null);
    }

    public WeartherCards(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeartherCards(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        mTxt = (TextView)findViewById(R.id.txt);
        mTxt.setText("123456");
        updateContent();
    }

    @Override
    public void updateContent() {
        sWorker.post(new Runnable() {
            @Override
            public void run() {
                final String stream = Utils.parseOnInternet(PREVIOUS_URL + "Taipei,tw");
                final String preProcessData = preProcessWeatherData(stream);
                getHandler().post(new Runnable() {

                    @Override
                    public void run() {
                        mTxt.setText(preProcessData);
                    }
                });
            }
        });
    }

    /**
     * http://openweathermap.org/weather-data
     * http://openweathermap.org/weather-conditions
     * http://openweathermap.org/help/city_list.txt
     * 
     * @param stream
     * @return
     */
    private String preProcessWeatherData(String stream) {
        String rtn = null;
        try {
            JSONObject json = new JSONObject(stream);
            JSONObject main = json.getJSONObject("main");
            long tempature = (long)(main.getLong("temp") - 273.15);
            long tempatureMax = (long)(main.getLong("temp_max") - 273.15);
            long tempatureMin = (long)(main.getLong("temp_min") - 273.15);
            int weatherCondition = json.getInt("cod");
            String city = json.getString("name");
            rtn = "cod: " + weatherCondition + ", city: " + city + "\ntempature: " + tempature
                    + ", tempatureMax: " + tempatureMax + ", tempatureMin: " + tempatureMin;
        } catch (JSONException e) {
            Log.e("QQQQ", "failed", e);
        }
        return rtn;
    }
}
