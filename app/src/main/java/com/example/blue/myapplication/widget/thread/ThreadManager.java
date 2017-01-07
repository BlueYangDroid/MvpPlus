package com.example.blue.myapplication.widget.thread;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public class ThreadManager {
    private static Handler uiHandler = null;
    private static ScheduledExecutorService pool = null;
    private static ScheduledExecutorService poolHigh = null;

    public ThreadManager() {
    }

    private static void checkInited() {
        if (uiHandler == null) {
            throw new IllegalStateException("ThreadManager.init NOT inited, you must call ThreadManager.init first");
        }
    }

    public static Handler getUIHandler() {
        checkInited();
        return uiHandler;
    }

    public static ScheduledExecutorService getPool() {
        checkInited();
        return pool;
    }

    public static ScheduledExecutorService getPoolHigh() {
        checkInited();
        return poolHigh;
    }

    public static synchronized void shutdown() {
        uiHandler.removeCallbacksAndMessages(null);
        uiHandler = null;
        if (pool != null) {
            pool.shutdown();
        }

        if (poolHigh != null) {
            poolHigh.shutdown();
        }

    }

    public static synchronized void shutdownNow() {
        uiHandler.removeCallbacksAndMessages(null);
        uiHandler = null;
        if (pool != null) {
            pool.shutdownNow();
        }

        if (poolHigh != null) {
            poolHigh.shutdownNow();
        }

    }

    public static synchronized void init(int poolSize, int highPoolSize) {
        if (uiHandler != null) {
            throw new IllegalStateException("ThreadManager.init already inited");
        } else {
            uiHandler = new Handler(Looper.getMainLooper());
            if (poolSize > 0) {
                pool = Executors.newScheduledThreadPool(poolSize, new ThreadFactory() {
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setPriority(Thread.NORM_PRIORITY);
                        t.setDaemon(true);
                        return t;
                    }
                });
            }

            if (highPoolSize > 0) {
                poolHigh = Executors.newScheduledThreadPool(highPoolSize, new ThreadFactory() {
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setPriority(Thread.MAX_PRIORITY);
                        t.setDaemon(true);
                        return t;
                    }
                });
            }

        }
    }
}
