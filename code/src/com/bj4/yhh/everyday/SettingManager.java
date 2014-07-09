
package com.bj4.yhh.everyday;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingManager {

    public static final String SHARED_PREFERENCES_NAME = "settings_manager";

    public static final String KEY_LOAD_DEFAULT = "load_default";

    private static SettingManager sInstance;

    private Context mContext;

    private SharedPreferences mPref;

    public synchronized static final SettingManager getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new SettingManager(c);
        }
        return sInstance;
    }

    private SettingManager(Context c) {
        mContext = c.getApplicationContext();
    }

    private synchronized SharedPreferences getPref() {
        if (mPref == null) {
            mPref = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
        return mPref;
    }

    public boolean isLoadDefault() {
        return getPref().getBoolean(KEY_LOAD_DEFAULT, false);
    }

    public void setLoadDefault(boolean v) {
        getPref().edit().putBoolean(KEY_LOAD_DEFAULT, v).apply();
    }
}
