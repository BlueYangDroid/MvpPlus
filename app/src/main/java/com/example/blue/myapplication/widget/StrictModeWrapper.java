package com.example.blue.myapplication.widget;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.StrictMode;

/**
 * Created by Administrator on 2016/12/12.
 使用方法：
 try {
 StrictModeWrapper.init(this);
 } catch(Throwable throwable) {
 Log.e("StrictMode", "... is not available...");
 }
 */

public class StrictModeWrapper {
    public static void init(Context context) {
        // check if android:debuggable is set to true
        int appFlags = context.getApplicationInfo().flags;
        if ((appFlags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }
}
