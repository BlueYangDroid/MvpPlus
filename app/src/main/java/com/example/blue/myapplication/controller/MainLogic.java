package com.example.blue.myapplication.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import com.example.blue.myapplication.entity.ImageEntity;
import com.example.blue.myapplication.mvpbase.BaseLogic;
import com.example.blue.myapplication.observer.MainObserver;
import com.example.blue.myapplication.widget.imageloader.ImageLoader;
import com.example.blue.myapplication.widget.imageloader.LoaderConfig;
import com.example.blue.myapplication.widget.thread.BackForeTask;
import com.example.blue.myapplication.widget.thread.BackTask;
import com.example.blue.myapplication.widget.Singlton;

import java.util.List;

public class MainLogic extends BaseLogic<MainObserver> {
    private static final String TAG = MainLogic.class.getSimpleName();

    public static MainLogic getInstance() {
        return Singlton.getInstance(MainLogic.class);
    }

    public void commitLoginRequest(final String pUserName, final String pUserPwd) {

        new BackForeTask<MainLogic, String>() {
            @Override
            protected String doInBack() {
                Log.d(TAG, "commitLoginRequest doInBack(): currentThread-> " + Thread.currentThread().getName());
                // ------- mock network request ------
                SystemClock.sleep(1000 * 10);
                return "postedToFore Login done";
            }

            @Override
            protected void postedToFore(MainLogic outInstance, String postedValue) {
                Log.d(TAG, "commitLoginRequest postedToFore(): currentThread-> " + Thread.currentThread().getName());
                if (null != outInstance) {
                    double randomValue = Math.random() * 10;
                    if (randomValue < 5) {
                        // 匿名内部类可以直接掉外部成员，强持外部类
                        observeLoginSuccess();
                    } else {
                        observeLoginFailed(new StringBuilder(pUserName).append(" Login Failed --> ").append(pUserPwd));
                    }
                } else {
                    Log.d(TAG, "commitLoginRequest postedToFore(): ignore cause outInstance = null");
                }
            }
        }.start();
    }

    /**
     * 供内部类调用，遵循Android开发最佳实践，采用包访问修饰
     */
    void observeLoginSuccess() {
        List<MainObserver> tmpList = getObservers();
        for (MainObserver o : tmpList) {
            o.onLoginSuccess();
        }
    }

    void observeLoginFailed(CharSequence errorMsg) {
        List<MainObserver> tmpList = getObservers();
        for (MainObserver o : tmpList) {
            o.onLoginFailed(errorMsg);
        }
    }

    void observeRequestImageSuccess(ImageEntity postedValue) {
        Log.d(TAG, "observeRequestImageSuccess(): rCode -> " + postedValue.rCode);
        List<MainObserver> tmpList = getObservers();
        for (MainObserver o : tmpList) {
            o.onRequestSuccess(postedValue);
        }
    }

    public void commitCacheData(final String cacheData) {
        CacheBackTask cacheBackTask = new CacheBackTask(this);
        cacheBackTask.setData(cacheData);
        cacheBackTask.start();
    }

    public void requestImage(Context context) {
        AsyncImageTask mImageTask = new AsyncImageTask(this, context);
        mImageTask.start();
    }

    private static class AsyncImageTask extends BackForeTask<MainLogic, ImageEntity> {
        private Bitmap bitmap;
        private Context context;

        AsyncImageTask(MainLogic logic, Context context) {
            super(logic);
            this.context = context;
        }

        @Override
        protected ImageEntity doInBack() {
            Log.d(TAG, "AsyncImageTask: doInBack currentThread-> " + Thread.currentThread().getName());
            if (getOutInstance() != null) {

                Bitmap bitmap = new ImageLoader.Builder()
//                        .imgView(getOutInstance().mActivityView.mImageView)   // your ImageView on the layout
                        .placeHolder(android.R.mipmap.sym_def_app_icon)
                        .url("http://img.pconline.com.cn/images/upload/upc/tx/itbbs/1610/25/c47/28906783_1477398355944_mthumb.jpg")
//                                .resId(R.drawable.ic_launcher)
                        .errorHolder(android.R.mipmap.sym_def_app_icon)
                        .transType(LoaderConfig.TRANS_ROUND) // your can choose a transType
//                    .scaleType(LoaderConfig.SCALE_CENTER_CROP)
                        .build()    // build the ImageLoader instance
                        .loadAsBitmap(context, 500, 500);
                ImageEntity imageEntity = new ImageEntity(bitmap);
                imageEntity.rCode = 0;
                return imageEntity;
            }
            return null;
        }

        @Override
        protected void postedToFore(MainLogic outInstance, ImageEntity postedValue) {
            if (null != outInstance && null != postedValue) {
                Log.d(TAG, "AsyncImageTask: postedToFore currentThread-> " + Thread.currentThread().getName());
                bitmap = postedValue.rContent;
                if (bitmap != null && !bitmap.isRecycled()) {
                    outInstance.observeRequestImageSuccess(postedValue);
                }
            }
        }

        private void release(){
            if (null != bitmap && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }

    }

    private static class  CacheBackTask extends BackTask<MainLogic, Void> {
        private String mCacheData;
        public CacheBackTask(MainLogic logic) {
            super(logic);
        }

        public void setData(String cacheData) {
            mCacheData = cacheData;
        }

        @Override
        protected Void doInTheBack() {
            Log.d(TAG, "CacheBackTask doInBack(): currentThread-> " + Thread.currentThread().getName());
            SystemClock.sleep(1000 * 60 * 5);
            // 调用弱引用对象成员
            if (getOutInstance() != null) {
                getOutInstance().onCacheDataDone(mCacheData);
            }
            return null;
        }

        @Override
        protected void doneInTheBack(MainLogic outInstance, Void doneValue) {
            Log.d(TAG, "CacheBackTask doneInTheBack(): currentThread-> " + Thread.currentThread().getName());
        }
    }

    private void onCacheDataDone(String cacheData) {
        Log.d(TAG, "onCacheDataDone(): cacheData done -> " + cacheData);
    }

    @Override
    public void release() {
        super.release();    // 父类负责释放observes
        Singlton.removeInstance(MainLogic.class);  // 子类从单例容器释放
        // release other instances in this instance, make a work thread if need
    }
}
