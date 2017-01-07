package com.example.blue.myapplication.widget;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public abstract class AbsHandlerThread extends HandlerThread implements Handler.Callback {
    private Handler mWorkerHandler; //与工作线程相关联的Handler
    private HandlerObserver observer; //提供回调接口
    private Handler mUIHandler; //与UI线程相关联的Handler

    public AbsHandlerThread(String name) {
        super(name);
    }

    public AbsHandlerThread(String name, int priority) {
        super(name, priority);
    }

    public void setHandlerObserver(HandlerObserver cb) {
        this.observer = cb;
    }

    public Handler getWorkerHandler() {
        return mWorkerHandler;
    }

    @Override
    protected void onLooperPrepared() {
        mWorkerHandler = new Handler(getLooper(), this);
    }

    @Override
    public boolean handleMessage(final Message msg) {
        final Object object = onHandleMessage(msg.what, msg.arg1, msg.arg2, msg.obj);

        if (observer != null) {
            if (mUIHandler == null) {
                mUIHandler = new Handler(Looper.getMainLooper());
            }
            //回调到主线程
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    observer.onPostExecute(msg.what, object);
                }
            });
        }
        return true;
    }

    protected abstract Object onHandleMessage(int what, int arg1, int arg2, Object obj);

    /**
     * 最终提供给外部组件的接口
     */
    public void enqueueMessage(int what, int arg1, int arg2, Object object) {
        if (null != mWorkerHandler) {
            mWorkerHandler.obtainMessage(what, arg1, arg2, object).sendToTarget();
        }
    }

    @Override
    public boolean quitSafely() {
        release();
        return super.quitSafely();
    }

    /**
     * 释放资源的接口
     */
    @Override
    public boolean quit() {
        release();
        return super.quit();
    }

    private void release() {
        if (null != mUIHandler) {
            mUIHandler.removeCallbacksAndMessages(null);
            mUIHandler = null;
        }
        if (null != observer) {
            observer.onQuit();
            observer = null;
        }
    }

    /**
     * 自定义的回调
     */
    interface HandlerObserver {
        void onPostExecute(int what, Object result);
        void onQuit();
    }
}