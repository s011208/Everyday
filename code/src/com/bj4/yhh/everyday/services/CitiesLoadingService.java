
package com.bj4.yhh.everyday.services;

import com.bj4.yhh.everyday.database.DatabaseHelper;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class CitiesLoadingService extends Service {
    private static final String TAG = "QQQQ";

    private static final boolean DEBUG = true;

    private DatabaseHelper mDatabaseHelper;

    private boolean mIsLoading = false;

    private int mLoadingProgess = 0;

    private ICitiesLoading.Stub mBinder = new ICitiesLoading.Stub() {

        @Override
        public boolean isLoading() throws RemoteException {
            return mIsLoading;
        }

        @Override
        public int getLoadingProgress() throws RemoteException {
            return mLoadingProgess;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mDatabaseHelper = DatabaseHelper.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "mIsLoading: " + mIsLoading);
        if (mIsLoading == false) {
            if (mDatabaseHelper.hasCitiesTableLoaded() == false) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mDatabaseHelper.loadCitiesTable(new DatabaseHelper.ProgressCallback() {
                            @Override
                            public void progress(int progress) {
                                mIsLoading = true;
                                mLoadingProgess = progress;
                            }
                        });
                        mIsLoading = false;
                    }
                }).start();
            } else {
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

}
