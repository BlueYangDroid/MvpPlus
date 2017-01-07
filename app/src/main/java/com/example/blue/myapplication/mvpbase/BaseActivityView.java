package com.example.blue.myapplication.mvpbase;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/12.
 */

public abstract class BaseActivityView<T> {

    public BaseActivityView() {
    }

    protected View contentView;

    public View getContentView() {
        return contentView;
    }

    protected abstract View onInitView(Context context);

    /**
     * 观察者列表
     */
    private List<T> listeners = new ArrayList<T>();

    /**
     * @Description:添加观察者
     */
    public synchronized void addListener(T observer) {
        if (!listeners.contains(observer)) {
            listeners.add(observer);
        }
    }

    /**
     * 移除观察者
     */
    public synchronized void removeListener(T observer) {
        if (null != observer && listeners.contains(observer)) {
            listeners.remove(observer);
        }
    }

    /**
     * 清空观察者
     */
    private synchronized void clearListeners() {
        if (!listeners.isEmpty()) {
            this.listeners.clear();
        }
    }

    /**
     * 返回只读的所有的监听器列表
     */
    public synchronized List<T> getListeners() {
        List<T> list = new ArrayList<T>(listeners.size());

        list.addAll(listeners);

        return list;
    }

    public void release(){
        this.clearListeners();
    }
}
