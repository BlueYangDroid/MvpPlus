package com.example.blue.myapplication;

import android.app.Application;
import android.util.Log;

import com.example.blue.myapplication.widget.StrictModeWrapper;
import com.example.blue.myapplication.widget.thread.ThreadManager;

/**
 * Created by Administrator on 2016/12/10.
 */

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ThreadManager.init(10, 5);
        try {
            StrictModeWrapper.init(this);
        } catch(Throwable throwable) {
            Log.e("StrictMode", "... is not available...");
        }
    }
}
