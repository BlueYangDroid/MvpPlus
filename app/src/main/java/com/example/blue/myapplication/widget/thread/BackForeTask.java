package com.example.blue.myapplication.widget.thread;

import java.lang.ref.WeakReference;

public abstract class BackForeTask<T, V> extends AbsThreadTask<V> {
    private WeakReference<T> reference;

    public BackForeTask() {
        super();
    }

    public BackForeTask(T outInstance) {
        super();
        if (null != outInstance) {
            reference = new WeakReference<T>(outInstance);
        }
    }

    public BackForeTask(T outInstance, boolean autoStart) {
        super(autoStart);
        if (null != outInstance) {
            reference = new WeakReference<T>(outInstance);
        }
    }

    public BackForeTask(T outInstance, boolean autoStart, boolean highPriority) {
        super(autoStart, highPriority);
        if (null != outInstance) {
            reference = new WeakReference<T>(outInstance);
        }
    }

    public T getOutInstance() {
        return null != reference ? reference.get() : null;
    }

    @Override
    protected final int getOrder() {
        return ORDER_BACK_FORE;
    }

    @Override
    protected void postedToFore(V v) {
        T t = null != reference ? reference.get() : null;
        postedToFore(t, v);
    }

    /**
     * called in the global thread pool
     * when call the outInstance wrapped with WeakReference, remember to verify null before call it !
     * @return the result of back task and will be posted to the @method postedToFore(T outInstance, V postedValue),
     * you can return null if not need
     */
    protected abstract V doInBack();

    /**
     * called in safe UI thread
     * the outInstance wrapped with WeakReference, remember to verify null before call it !
     * @param outInstance outter instance
     * @param postedValue return from @method doInBack(T outInstance)
     */
    protected abstract void postedToFore(T outInstance, V postedValue);
}