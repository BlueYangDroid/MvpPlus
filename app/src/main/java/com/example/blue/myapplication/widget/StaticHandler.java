package com.example.blue.myapplication.widget;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by T.F Guo on 2016/12/6.
 */

public abstract class StaticHandler<T> extends Handler {

    private final WeakReference<T> myClassWeakReference;

    public StaticHandler(T refInstance) {
        myClassWeakReference = new WeakReference<T>(refInstance);
    }

    @Override
    public void handleMessage(Message msg) {
        T refInstance = myClassWeakReference.get();
        if (refInstance != null) {
            handleMessage(refInstance, msg);
        }
    }

    protected abstract void handleMessage(T refInstance, Message msg);
}