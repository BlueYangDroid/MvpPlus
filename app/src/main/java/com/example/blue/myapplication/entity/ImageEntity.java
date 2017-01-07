package com.example.blue.myapplication.entity;

import android.graphics.Bitmap;

import com.example.blue.myapplication.mvpbase.BaseEntity;

/**
 * Created by Administrator on 2016/12/13.
 */

public class ImageEntity extends BaseEntity<Bitmap> {

    public ImageEntity() {
    }

    public ImageEntity(Bitmap bitmap) {
        rContent = bitmap;
    }

}
