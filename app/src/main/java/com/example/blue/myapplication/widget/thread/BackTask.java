package com.example.blue.myapplication.widget.thread;

import java.lang.ref.WeakReference;

public abstract class BackTask<T, V> extends AbsThreadTask<V> {
    private WeakReference<T> reference;

    public BackTask() {
        super();
    }

    public BackTask(T outInstance) {
        super();
        if (null != outInstance) {
            reference = new WeakReference<T>(outInstance);
        }
    }

    public BackTask(T outInstance, boolean autoStart) {
        super(autoStart);
        if (null != outInstance) {
            reference = new WeakReference<T>(outInstance);
        }
    }

    public BackTask(T outInstance, boolean autoStart, boolean highPriority) {
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
        return ORDER_BACK_ONLY;
    }

    @Override
    protected V doInBack(){
        V v = doInTheBack();
        doneInTheBack(reference != null ? reference.get() : null, v);
        return v;
    }

    protected abstract V doInTheBack();

    /**
     * this func still run on the work thread
     * @param outInstance the outter instance from the structure
     * @param doneValue the result return from the doInTheBack()
     */
    protected abstract void doneInTheBack(T outInstance, V doneValue);

    @Override
    protected void postedToFore(V t) {
        // do nothing, this subclass ignore the method
    }

    /**
     * the outInstance wrapped with WeakReference, remember to verify null before call it
     * @param outInstance outter instance
     * @return the result of back task, you can set void when new the structure V and return null
     */
//    protected abstract V doInBack(T outInstance);
}