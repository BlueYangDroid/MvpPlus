package com.example.blue.myapplication.observer;

import com.example.blue.myapplication.entity.ImageEntity;

public interface MainObserver {
    void onLoginSuccess();
    void onLoginFailed(CharSequence errorMsg);
    void onRequestSuccess(ImageEntity entity);
}
