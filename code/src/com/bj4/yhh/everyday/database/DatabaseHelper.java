
package com.bj4.yhh.everyday.database;

import java.util.ArrayList;
import java.util.Collections;

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

    private static final String TABLE_CARDS_COLUMN_order = "column_order";

    private SQLiteDatabase mDb;

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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getDataBase().execSQL("PRAGMA synchronous = 1");
            setWriteAheadLoggingEnabled(true);
        }
        createTables();
    }

    public void createTables() {
        // cards table
        getDataBase().execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_CARDS + "(" + TABLE_CARDS_COLUMN_TYPE
                        + " INTEGER, " + TABLE_CARDS_COLUMN_order + " INTEGER)");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
