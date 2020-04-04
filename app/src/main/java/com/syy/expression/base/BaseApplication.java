package com.syy.expression.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.facebook.drawee.backends.pipeline.Fresco;

public class BaseApplication extends Application {
    private static Application application;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        mContext = this;
        Fresco.initialize(this);
    }

    public static Application getApplication() {
        return application;
    }

    public static Context getContext() {
        return mContext;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public Handler getMainHandler() {
        return mHandler;
    }
}
