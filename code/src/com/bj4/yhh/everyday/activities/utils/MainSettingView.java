
package com.bj4.yhh.everyday.activities.utils;

import com.bj4.yhh.everyday.R;
import com.bj4.yhh.everyday.cards.allapps.AllappsSettingActivity;
import com.bj4.yhh.everyday.cards.weather.WeatherSettingActivity;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MainSettingView extends RelativeLayout {
    private static final String TAG = "QQQQ";

    private static final boolean DEBUG = true;

    private Context mContext;

    private ListView mSettingView;

    public MainSettingView(Context context) {
        this(context, null);
    }

    public MainSettingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainSettingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        mSettingView = (ListView)findViewById(R.id.main_setting_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_expandable_list_item_1, mContext.getResources()
                        .getStringArray(R.array.cards_set));
        mSettingView.setAdapter(adapter);
        mSettingView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        intent = new Intent(mContext, WeatherSettingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(mContext, AllappsSettingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        break;
                }
            }
        });
    }
}
