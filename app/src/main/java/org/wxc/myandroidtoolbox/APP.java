package org.wxc.myandroidtoolbox;

import android.app.Application;
import android.content.Context;

/**
 * Created by wxc on 2016/11/29.
 */

public class APP extends Application{

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
