
package com.bj4.yhh.everyday.database;

import java.util.ArrayList;
import java.util.Collections;

import com.bj4.yhh.everyday.Card;
import com.bj4.yhh.everyday.SettingManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
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

    private SQLiteDatabase mDb;

    private Context mContext;

    private static DatabaseHelper sInstance;

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
            addNewCard(Card.CARD_TYPE_NEWS, Card.ORDER_NONE);
            addNewCard(Card.CARD_TYPE_WEATHER, Card.ORDER_NONE);
            addNewCard(Card.CARD_TYPE_APK, Card.ORDER_NONE);

            // weather cards
            addNewWeatherCards(1668355, 0);
            addNewWeatherCards(1670029, 1);
            addNewWeatherCards(1670310, 2);
            sm.setLoadDefault(true);
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
    }

    public void addNewWeatherCards(int cityId, int order) {
        ContentValues cv = new ContentValues();
        cv.put(TABLE_WEATHER_CARDS_COLUMN_CITY_ID, cityId);
        cv.put(TABLE_WEATHER_CARDS_COLUMN_ORDER, order);
        getDataBase().insert(TABLE_WEATHER_CARDS, null, cv);
    }

    public void removeWeatherCards(int cityId) {
        getDataBase().delete(TABLE_WEATHER_CARDS,
                TABLE_WEATHER_CARDS_COLUMN_CITY_ID + "='" + cityId + "'", null);
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

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
