package com.example.blue.myapplication.widget.imageloader;

import android.content.Context;
import android.graphics.Bitmap;


/**
 * Created by Anthony on 2016/3/3.
 * Class Note:
 * using Picasso to load image
 */
public class PicassoImageLoaderStrategy implements BaseImageLoaderStrategy {
    @Override
    public void loadImage(Context ctx, ImageLoader img) {
    }

    @Override
    public Bitmap loadAsBitmap(Context ctx, ImageLoader img, int width, int height) {
        return null;
    }

    @Override
    public void clearMemory(Context ctx) {

    }

    @Override
    public void clearDiskCache(Context ctx) {

    }

    @Override
    public void pauseRequests(Context ctx) {

    }

    @Override
    public void resumeRequests(Context ctx) {

    }
}
