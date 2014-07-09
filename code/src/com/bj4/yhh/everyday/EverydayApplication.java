
package com.bj4.yhh.everyday;

import android.app.Application;
import android.content.Context;

public class EverydayApplication extends Application {
    private static LoaderManager sLoaderManager;

    public synchronized static LoaderManager getLoaderManager(Context c) {
        if (sLoaderManager == null) {
            sLoaderManager = LoaderManager.getInstance(c);
        }
        return sLoaderManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // init loader manager
        getLoaderManager(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
