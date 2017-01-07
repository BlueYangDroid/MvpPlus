package com.example.blue.myapplication.mvpbase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseMvpActivity<V extends BaseActivityView, L extends BaseLogic> extends AppCompatActivity {

    protected V mActivityView;
    protected L mLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityView = onCreateActivityView();
        if (null != mActivityView) {
            mActivityView.contentView = mActivityView.onInitView(this);
        }
        setContentView(mActivityView.contentView);
        mLogic = onCreateBaseLogic();
    }

    protected abstract V onCreateActivityView();

    protected abstract L onCreateBaseLogic();

}
