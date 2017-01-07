package com.example.blue.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Debug;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.blue.myapplication.controller.MainLogic;
import com.example.blue.myapplication.entity.ImageEntity;
import com.example.blue.myapplication.observer.MainObserver;
import com.example.blue.myapplication.widget.StaticHandler;
import com.example.blue.myapplication.widget.imageloader.LoaderConfig;
import com.example.blue.myapplication.widget.imageloader.ImageLoader;
import com.example.blue.myapplication.widget.thread.BackForeTask;
import com.example.blue.myapplication.widget.thread.BackTask;

public class MainActivity extends AppCompatActivity implements MainObserver {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MSG_BACK_TASK = 1;

    private Myhandler mainHandler;
    private Context mCtx;
    private MyBackTask mBackTask;
    private ImageView mImageView;
    private AsyncImageTask mImageTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.activity_main);
        mCtx = MainActivity.this;
        initDatas();

        Debug.startMethodTracing("traceFile");
        // 自行维护观察者关系，跟随生命周期取消观察
        initView();
        Debug.stopMethodTracing();

        // 第一种写法:直接Post, 线程回调后不一定达成懒加载
//        new MyBackTask().start();

        // 第二种写法:直接PostDelay DEALY_TIME比较难控制.
//        mainHandler.postDelayed(new MyBackTask(), DEALY_TIME);

        /**
         * 第三种写法:利用DecorView内部维护的Looper时机，优化的DelayLoad，在onResume之后回调
         * 需要使用本地handler进行承接，注意post和sendMessage方式在线程调度上的区别
         */
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "getDecorView.post: currentThread-> " + Thread.currentThread().getName());
                // 懒加载3.1：-》main Thread
//                mainHandler.post(new MyBackTask());
                // 懒加载3.2：-》work Thread
                mainHandler.obtainMessage(MSG_BACK_TASK).sendToTarget();

            }
        });

        /**
         * ect: a wrapped ImageLoader
         */
        mImageView = (ImageView) findViewById(R.id.img_glide);
/*        new ImageLoader.Builder()
                .imgView(mImageView)   // your ImageView on the layout
//                .placeHolder(android.R.mipmap.sym_def_app_icon)
//                .url("http://img.pconline.com.cn/images/upload/upc/tx/itbbs/1610/25/c47/28906783_1477398355944_mthumb.jpg")
                .resId(android.R.mipmap.sym_def_app_icon)
//                .errorHolder(android.R.mipmap.sym_def_app_icon)
                .transType(LoaderConfig.TRANS_CIRCLE) // your can choose a transType
                .build()    // build the ImageLoader instance
                .loadImage(mCtx);   // load with the delegate config through the LoaderConfig*/



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart:" );
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:" );
    }

    private void initDatas() {
        MainLogic.getInstance().addObserver(this);
        mainHandler = new Myhandler(this);
        mBackTask = new MyBackTask();
    }

    private void initView() {
        // 单后台任务
        findViewById(R.id.btn_backtask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MainLogic.getInstance().commitCacheData("CacheData");
                commitCacheData("CacheData");
            }
        });

        // 后台运行异步回调任务
        findViewById(R.id.btn_backforetask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Activity中起后台线程的安全方式1：通过逻辑类对象启动，在逻辑业务中进行观察
                 * 推荐理由：关系解耦，业务聚合
                 * 如果回调Activity，在逻辑类使用观察接口调用
                 */
                MainLogic.getInstance().commitLoginRequest("hello","123456");
            }
        });

        /**
         * BACK FORE 示例：异步加载大图片，返回bitmap对象，可以及时手工释放
         */
        findViewById(R.id.btn_async_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageTask = new AsyncImageTask(MainActivity.this);
                mImageTask.start();
            }
        });

    }

    @Override
    public void onLoginSuccess() {
        Log.d(TAG, "onLoginSuccess: currentThread-> " + Thread.currentThread().getName());
        Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginFailed(CharSequence errorMsg) {
        Log.d(TAG, "onLoginFailed: currentThread-> " + Thread.currentThread().getName());
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSuccess(ImageEntity entity) {
        Log.d(TAG, "onRequestSuccess: currentThread-> " + Thread.currentThread().getName());
    }

    public void commitCacheData(final String cacheData) {
        /**
         * Activity中起后台线程的安全方式2：通过静态内部类启动，引入的外部对象进行弱引用
         * 如果调用外部类成员，使用引入的弱outInstance调用，需判空
         * 为提高性能，外部被call的方法修饰为包访问
          */
        new MyBackTask(this).start();
    }

    private static class AsyncImageTask extends BackForeTask<MainActivity, Bitmap> {
        private Bitmap bitmap;

        AsyncImageTask(MainActivity activity) {
            super(activity);
        }

        @Override
        protected Bitmap doInBack() {
            Log.d(TAG, "AsyncImageTask: doInBack currentThread-> " + Thread.currentThread().getName());
            if (getOutInstance() != null) {
            }
            return new ImageLoader.Builder()
//                                .imgView(mImageView)   // your ImageView on the layout
                    .placeHolder(android.R.mipmap.sym_def_app_icon)
                    .url("http://img.pconline.com.cn/images/upload/upc/tx/itbbs/1610/25/c47/28906783_1477398355944_mthumb.jpg")
//                                .resId(R.drawable.ic_launcher)
                    .errorHolder(android.R.mipmap.sym_def_app_icon)
                    .transType(LoaderConfig.TRANS_ROUND) // your can choose a transType
//                    .scaleType(LoaderConfig.SCALE_CENTER_CROP)
                    .build()    // build the ImageLoader instance
                    .loadAsBitmap(getOutInstance().mCtx, getOutInstance().mImageView.getWidth(), getOutInstance().mImageView.getHeight());
        }

        @Override
        protected void postedToFore(MainActivity outInstance, Bitmap postedValue) {
            if (null != outInstance) {
                Log.d(TAG, "AsyncImageTask: postedToFore currentThread-> " + Thread.currentThread().getName());
                bitmap = postedValue;
                outInstance.mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                outInstance.mImageView.setImageBitmap(bitmap);
            }
        }

        private void release(){
            if (null != bitmap && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    private static class  MyBackTask extends BackTask<MainActivity, Void> {
        MyBackTask() {
        }

        MyBackTask(MainActivity activity) {
            super(activity);
        }

        @Override
        protected Void doInTheBack() {
            Log.d(TAG, "commitCacheData doInBack(): currentThread-> " + Thread.currentThread().getName());
            SystemClock.sleep(1000 * 60 * 5);
            Log.d(TAG, "commitCacheData(): cacheData done -> " );
            if (getOutInstance() == null) {
                Log.d(TAG, "commitCacheData(): cacheData done -> outInstance == null");
            }
            return null;
        }

        @Override
        protected void doneInTheBack(MainActivity outInstance, Void doneValue) {
            Log.d(TAG, "commitCacheData doInBack(): doneInTheBack-> " + Thread.currentThread().getName());
        }
    }

    private static class  Myhandler extends StaticHandler<MainActivity> {
        Myhandler(MainActivity refInstance) {
            super(refInstance);
        }

        @Override
        protected void handleMessage(MainActivity refInstance, Message msg) {
            // handle the messages, call outFunc with the refInstance
            switch (msg.what) {
                case MSG_BACK_TASK:
                    if (null != refInstance.mBackTask) {
                        refInstance.mBackTask.start();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseActivityResource();
        /** release 逻辑单例：如果是多个组件（Fragment）交叉使用的，当前组件不使用时remove当前组件观察者
         * 如果只是单个Activity绑定使用的，直接release该逻辑实例
         */
        // MainLogic.getInstance().removeObserver(this);
        MainLogic.getInstance().release();
    }

    private void releaseActivityResource() {
//        if (mImageTask != null) {
//            mImageTask.release();
//        }
        if (null != mainHandler) {
            mainHandler.removeCallbacksAndMessages(null);
        }
    }
}
