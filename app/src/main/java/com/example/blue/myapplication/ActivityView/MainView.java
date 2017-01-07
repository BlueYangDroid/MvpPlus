package com.example.blue.myapplication.ActivityView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.blue.myapplication.R;
import com.example.blue.myapplication.mvpbase.BaseActivityView;
import com.example.blue.myapplication.observer.MainListener;
import com.example.blue.myapplication.widget.Singlton;

import java.util.List;

/**
 * Created by Administrator on 2016/12/12.
 */

public class MainView extends BaseActivityView<MainListener> {
    private static final String TAG = MainView.class.getSimpleName();
    private Context mContext;
    public ImageView mImageView;

    public static MainView getInstance() {
        return Singlton.getInstance(MainView.class);
    }

    @Override
    protected View onInitView(Context context) {
        View inflateView = LayoutInflater.from(context).inflate(R.layout.activity_main, null);
        mImageView = (ImageView) inflateView.findViewById(R.id.img_glide);

        // 单后台任务
        inflateView.findViewById(R.id.btn_backtask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                commitCacheData("CacheData");
                listenClick(view);
            }
        });

        // 后台运行异步回调任务
        inflateView.findViewById(R.id.btn_backforetask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Activity中起后台线程的安全方式1：通过逻辑类对象启动，在逻辑业务中进行观察
                 * 推荐理由：关系解耦，业务聚合
                 * 如果回调Activity，在逻辑类使用观察接口调用
                 */
//                MainLogic.getInstance().commitLoginRequest("hello","123456");
                listenClick(view);
            }
        });

        /**
         * BACK FORE 示例：异步加载大图片，返回bitmap对象，可以及时手工释放
         */
        inflateView.findViewById(R.id.btn_async_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mImageTask = new AsyncImageTask(MainMvpActivity.this);
//                mImageTask.start();
                listenClick(view);
            }
        });

        return inflateView;
    }

    void listenClick(View view) {
        List<MainListener> tmpList = getListeners();
        for (MainListener o : tmpList) {
            o.onClickListener(view.getId());
        }
    }

    @Override
    public void release() {
        super.release();    // 父类中移除观察者
        Singlton.removeInstance(MainView.class);  // 子类从单例容器释放

    }
}
