
package com.bj4.yhh.everyday.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import com.bj4.yhh.everyday.Card;
import com.bj4.yhh.everyday.SettingManager;
import com.bj4.yhh.everyday.cards.allapps.ShortCut;
import com.bj4.yhh.everyday.cards.weather.City;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "card.db";

    private static final String TAG = "DatabaseHelper";

    // cards table
    private static final String TABLE_CARDS = "cards";

    private static final String TABLE_CARDS_COLUMN_TYPE = "column_type";

    private static final String TABLE_CARDS_COLUMN_ORDER = "column_order";

    private static final String TABLE_CARDS_COLUMN_ID = "_id";

    // weather cards' table
    private static final String TABLE_WEATHER_CARDS = "weather_cards";

    private static final String TABLE_WEATHER_CARDS_COLUMN_CITY_ID = "city_id";

    private static final String TABLE_WEATHER_CARDS_COLUMN_ORDER = "column_order";

    // weather cards cities table
    private static final String TABLE_CITIES_LIST = "cities_list";

    private static final String TABLE_CITIES_LIST_ID = "city_id";

    private static final String TABLE_CITIES_LIST_LON = "city_lon";

    private static final String TABLE_CITIES_LIST_LAT = "city_lat";

    private static final String TABLE_CITIES_LIST_NAME = "city_name";

    private static final String TABLE_CITIES_LIST_NATION = "city_nation";

    // allapps table
    private static final String TABLE_ALL_APPS = "all_apps";

    private static final String TABLE_ALL_APPS_ID = "_id";

    private static final String TABLE_ALL_APPS_PKG = "pkg";

    private static final String TABLE_ALL_APPS_CLZ = "clz";

    private static final String TABLE_ALL_APPS_ORDER = "all_apps_order";

    // play store recommended apps
    
    private SQLiteDatabase mDb;

    private Context mContext;

    private static DatabaseHelper sInstance;

    public interface ProgressCallback {
        public void progress(int progress);
    }

    public synchronized static DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context);
        }
        return sInstance;
    }

    private SQLiteDatabase getDataBase() {
        if ((mDb == null) || (mDb != null && mDb.isOpen() == false)) {
            try {
                mDb = getWritableDatabase();
            } catch (SQLiteFullException e) {
                Log.w(TAG, "SQLiteFullException", e);
            } catch (SQLiteException e) {
                Log.w(TAG, "SQLiteException", e);
            } catch (Exception e) {
                Log.w(TAG, "Exception", e);
            }
        }
        return mDb;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context.getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getDataBase().execSQL("PRAGMA synchronous = 1");
            setWriteAheadLoggingEnabled(true);
        }
        createTables();
        loadDefaultCards();
    }

    private void loadDefaultCards() {
        SettingManager sm = SettingManager.getInstance(mContext);
        if (sm.isLoadDefault() == false) {
            // cards type
            addNewCard(Card.CARD_TYPE_PLAY_STORE_RECOMMAND, Card.ORDER_NONE);
            addNewCard(Card.CARD_TYPE_WEATHER, Card.ORDER_NONE);
            addNewCard(Card.CARD_TYPE_ALLAPPS, Card.ORDER_NONE);

            // weather cards
            addNewWeatherCards(1668355);
            addNewWeatherCards(1670029);
            addNewWeatherCards(1670310);
            sm.setLoadDefault(true);

            // allapps shortcuts
            addNewAllappsShortCut("com.asus.message", "com.android.mms.ui.ConversationList");
            addNewAllappsShortCut("com.asus.filemanager", "com.asus.filemanager.activity.FileManagerActivity");
            addNewAllappsShortCut("com.asus.browser", "com.android.browser.BrowserActivity");
        }
    }

    private void createTables() {
        // cards table
        getDataBase().execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_CARDS + "(" + TABLE_CARDS_COLUMN_ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TABLE_CARDS_COLUMN_TYPE
                        + " INTEGER, " + TABLE_CARDS_COLUMN_ORDER + " INTEGER)");
        // weather cards' table
        getDataBase().execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_WEATHER_CARDS + "("
                        + TABLE_WEATHER_CARDS_COLUMN_CITY_ID + " INTEGER PRIMARY KEY, "
                        + TABLE_WEATHER_CARDS_COLUMN_ORDER + " INTEGER)");
        // cities list
        getDataBase().execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_CITIES_LIST + "(" + TABLE_CITIES_LIST_ID
                        + " INTEGER PRIMARY KEY, " + TABLE_CITIES_LIST_LAT + " TEXT, "
                        + TABLE_CITIES_LIST_LON + " TEXT, " + TABLE_CITIES_LIST_NAME + " TEXT, "
                        + TABLE_CITIES_LIST_NATION + " TEXT)");
        // allapps
        getDataBase().execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_ALL_APPS + "(" + TABLE_ALL_APPS_ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TABLE_ALL_APPS_PKG + " TEXT, "
                        + TABLE_ALL_APPS_CLZ + " TEXT, " + TABLE_ALL_APPS_ORDER + " INTEGER)");
    }

    // +++allapps
    public ArrayList<ShortCut> getAllappsShortCut() {
        ArrayList<ShortCut> rtn = new ArrayList<ShortCut>();
        Cursor data = getDataBase().query(TABLE_ALL_APPS, new String[] {
                TABLE_ALL_APPS_ID, TABLE_ALL_APPS_PKG, TABLE_ALL_APPS_CLZ
        }, null, null, null, null, TABLE_ALL_APPS_ORDER);
        if (data != null) {
            while (data.moveToNext()) {
                rtn.add(new ShortCut(data.getString(1), data.getString(2), data.getInt(0)));
            }
            data.close();
        }
        return rtn;
    }

    public void addNewAllappsShortCut(String pkg, String clz) {
        int order = getAllappsShotCutMaxCount() + 1;
        ContentValues cv = new ContentValues();
        cv.put(TABLE_ALL_APPS_PKG, pkg);
        cv.put(TABLE_ALL_APPS_CLZ, clz);
        cv.put(TABLE_ALL_APPS_ORDER, order);
        getDataBase().insert(TABLE_ALL_APPS, null, cv);
    }

    public void removeAllappsShortCut(int id) {
        getDataBase().delete(TABLE_ALL_APPS, TABLE_ALL_APPS_ID + "=" + id, null);
    }

    public void removeAllappsShortCut(String pkg, String clz) {
        getDataBase().delete(TABLE_ALL_APPS,
                TABLE_ALL_APPS_PKG + "='" + pkg + "' and " + TABLE_ALL_APPS_CLZ + "='" + clz + "'",
                null);
    }

    public int getAllappsShotCutMaxCount() {
        int rtn = 0;
        Cursor data = getDataBase().rawQuery(
                "select max(" + TABLE_ALL_APPS_ORDER + ") from " + TABLE_ALL_APPS, null);
        if (data != null) {
            while (data.moveToNext()) {
                rtn = data.getInt(0);
            }
            data.close();
        }
        return rtn;
    }

    // ---allapps
    // +++ weather related

    public boolean hasCitiesTableLoaded() {
        Cursor data = getDataBase().rawQuery("select count(*) from " + TABLE_CITIES_LIST, null);
        boolean rtn = false;
        if (data != null) {
            while (data.moveToNext()) {
                rtn = data.getInt(0) > 0;
            }
            data.close();
        }
        return rtn;
    }

    public ArrayList<String> getAllCitiesName() {
        ArrayList<String> rtn = new ArrayList<String>();
        Cursor data = getDataBase().query(TABLE_CITIES_LIST, new String[] {
                TABLE_CITIES_LIST_NAME, TABLE_CITIES_LIST_NATION
        }, null, null, null, null, null, null);
        if (data != null) {
            while (data.moveToNext()) {
                String name = data.getString(0);
                String nation = data.getString(1);
                rtn.add(name + "," + nation);
            }
            data.close();
        }
        return rtn;
    }

    public void loadCitiesTable(ProgressCallback cb) {
        android.content.res.AssetManager am = mContext.getAssets();
        try {
            InputStream in = am.open("city_list.txt");
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] data = new byte[2048];
            int count = -1;
            if (cb != null) {
                cb.progress(1);
            }
            while ((count = in.read(data, 0, 2048)) != -1) {
                outStream.write(data, 0, count);
            }
            if (cb != null) {
                cb.progress(30);
            }
            data = null;
            String[] dataArray = new String(outStream.toByteArray(), "ISO-8859-1").split("\n");
            if (dataArray.length <= 0) {
                return;
            }
            getDataBase().beginTransaction();
            final float unit = 70 / (float)dataArray.length;
            int counter = 0;
            try {
                for (String raw : dataArray) {
                    ContentValues cv = new ContentValues();
                    String[] rawArray = raw.split("\t");
                    cv.put(TABLE_CITIES_LIST_ID, rawArray[0]);
                    cv.put(TABLE_CITIES_LIST_LAT, rawArray[2]);
                    cv.put(TABLE_CITIES_LIST_LON, rawArray[3]);
                    cv.put(TABLE_CITIES_LIST_NAME, rawArray[1]);
                    cv.put(TABLE_CITIES_LIST_NATION, rawArray[4]);
                    getDataBase().insertOrThrow(TABLE_CITIES_LIST, null, cv);
                    if (cb != null) {
                        cb.progress((int)(30 + (++counter) * unit));
                    }
                }
                getDataBase().setTransactionSuccessful();
            } finally {
                getDataBase().endTransaction();
                if (cb != null) {
                    cb.progress(100);
                }
                System.gc();
            }
        } catch (IOException e) {
            Log.e(TAG, "failed", e);
        }
    }

    public int getCityId(String cityAndNation) {
        String[] raw = cityAndNation.split(",");
        int rtn = -1;
        if (raw.length == 2) {
            Cursor data = getDataBase().query(
                    TABLE_CITIES_LIST,
                    new String[] {
                        TABLE_CITIES_LIST_ID
                    },
                    TABLE_CITIES_LIST_NAME + "='" + raw[0] + "' and " + TABLE_CITIES_LIST_NATION
                            + "='" + raw[1] + "'", null, null, null, null, null);

            if (data != null) {
                while (data.moveToNext()) {
                    rtn = data.getInt(0);
                }
                data.close();
            }
        }
        return rtn;
    }

    public ArrayList<City> getAllTrackingCities() {
        ArrayList<City> rtn = new ArrayList<City>();
        ArrayList<Integer> weatherCards = getWeatherCards();
        for (Integer cityId : weatherCards) {
            Cursor data = getDataBase().query(TABLE_CITIES_LIST, new String[] {
                    TABLE_CITIES_LIST_ID, TABLE_CITIES_LIST_NAME, TABLE_CITIES_LIST_NATION
            }, TABLE_CITIES_LIST_ID + "=" + cityId, null, null, null, null, null);
            if (data != null) {
                while (data.moveToNext()) {
                    rtn.add(new City(data.getInt(0), data.getString(1), data.getString(2)));
                }
                data.close();
            }
        }
        return rtn;
    }

    public void switchWeatherCardsPosition(int cityId1, int cityId2) {
        int cityId1Order = getWeatherCardsOrder(cityId1);
        int cityId2Order = getWeatherCardsOrder(cityId2);
        ContentValues cv = new ContentValues();
        cv.put(TABLE_WEATHER_CARDS_COLUMN_ORDER, cityId1Order);
        getDataBase().update(TABLE_WEATHER_CARDS, cv,
                TABLE_WEATHER_CARDS_COLUMN_CITY_ID + "=" + cityId2, null);
        cv = new ContentValues();
        cv.put(TABLE_WEATHER_CARDS_COLUMN_ORDER, cityId2Order);
        getDataBase().update(TABLE_WEATHER_CARDS, cv,
                TABLE_WEATHER_CARDS_COLUMN_CITY_ID + "=" + cityId1, null);
    }

    public int getWeatherCardsOrder(int cityId) {
        int id = 0;
        Cursor data = getDataBase().query(TABLE_WEATHER_CARDS, new String[] {
            TABLE_WEATHER_CARDS_COLUMN_ORDER
        }, TABLE_WEATHER_CARDS_COLUMN_CITY_ID + "=" + cityId, null, null, null, null);
        if (data != null) {
            while (data.moveToNext()) {
                id = data.getInt(0);
            }
            data.close();
        }
        return id;
    }

    public long addNewWeatherCards(int cityId) {
        int order = getWeatherCards().size();
        ContentValues cv = new ContentValues();
        cv.put(TABLE_WEATHER_CARDS_COLUMN_CITY_ID, cityId);
        cv.put(TABLE_WEATHER_CARDS_COLUMN_ORDER, order);
        return getDataBase().insert(TABLE_WEATHER_CARDS, null, cv);
    }

    public void removeWeatherCards(int cityId) {
        getDataBase().delete(TABLE_WEATHER_CARDS,
                TABLE_WEATHER_CARDS_COLUMN_CITY_ID + "='" + cityId + "'", null);
        ArrayList<Integer> allCards = getWeatherCards();
        getDataBase().delete(TABLE_WEATHER_CARDS, null, null);
        for (Integer id : allCards) {
            addNewWeatherCards(id);
        }
    }

    public ArrayList<Integer> getWeatherCards() {
        ArrayList<Integer> rtn = new ArrayList<Integer>();
        Cursor data = getDataBase().query(TABLE_WEATHER_CARDS, new String[] {
            TABLE_WEATHER_CARDS_COLUMN_CITY_ID
        }, null, null, null, null, TABLE_WEATHER_CARDS_COLUMN_ORDER);
        if (data != null) {
            while (data.moveToNext()) {
                rtn.add(data.getInt(0));
            }
            data.close();
        }
        return rtn;
    }

    // --- weather related
    // +++ basic cards
    public ArrayList<Card> getCards() {
        ArrayList<Card> rtn = new ArrayList<Card>();
        Cursor data = getDataBase().query(TABLE_CARDS, null, null, null, null, null,
                TABLE_CARDS_COLUMN_ORDER, null);
        if (data != null) {
            while (data.moveToNext()) {
                rtn.add(new Card(data.getInt(data.getColumnIndex(TABLE_CARDS_COLUMN_ID)), data
                        .getInt(data.getColumnIndex(TABLE_CARDS_COLUMN_TYPE)), data.getInt(data
                        .getColumnIndex(TABLE_CARDS_COLUMN_ORDER))));
            }
            data.close();
        }
        return rtn;
    }

    public void removeCard(int id) {
        getDataBase().delete(TABLE_CARDS, id + "=_id", null);
    }

    public void addNewCard(int type, int order) {
        ContentValues cv = new ContentValues();
        cv.put(TABLE_CARDS_COLUMN_TYPE, type);
        cv.put(TABLE_CARDS_COLUMN_ORDER, order);
        getDataBase().insert(TABLE_CARDS, null, cv);
    }

    // --- basic cards

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
