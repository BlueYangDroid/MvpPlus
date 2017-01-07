package com.example.blue.myapplication.widget.thread;

import android.util.Log;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by momocsdn on 16/5/26.
 */
public abstract class AbsThreadTask<T> implements Runnable{
    private static final String TAG = AbsThreadTask.class.getSimpleName();
    public static final int ORDER_BACK_FORE = 0;
    public static final int ORDER_BACK_ONLY = 1;
    private boolean highPriority;
    private boolean started;
    private boolean cancled;
    private T t;

    public AbsThreadTask() {
        this(false);
    }

    public AbsThreadTask(boolean autoStart) {
        this(autoStart, false);
    }

    public AbsThreadTask(boolean autoStart, boolean highPriority) {
        this.started = false;
        this.highPriority = highPriority;
        if (autoStart) {
            this.start();
        }

    }

    public void cancle() {
        this.cancled = true;
    }

    public boolean isCancled(){
        return this.cancled;
    }

    @Override
    public void run() {
        int order = this.getOrder();
        this.callBack(order == ORDER_BACK_FORE);
    }

    public void start() {
        if (!this.started) {
            this.started = true;
            submitToPool();
        }
    }

    private void submitToPool() {
        ScheduledExecutorService es;
        if (this.highPriority) {
            es = ThreadManager.getPoolHigh();
        } else {
            es = ThreadManager.getPool();
        }
        es.submit(this);
    }

    private void callBack( final boolean callFore) {
        try {
            if (!cancled) {
                t = doInBack();
            }
        } catch (Exception var2) {
            Log.e(TAG, "doInBack(): ", var2);
        }

        if (!cancled && callFore) {
            callFore(t);
        }
    }

    private void callFore(final T t) {
        ThreadManager.getUIHandler().post(new Runnable() {
            public void run() {
                try {
                    if (!cancled) {
                        postedToFore(t);
                    }
                } catch (Exception var2) {
                    Log.e(TAG, "postedToFore(): ", var2);
                }
            }
        });
    }

    protected abstract int getOrder();

    protected abstract T doInBack();

    protected abstract void postedToFore(T t);

}
