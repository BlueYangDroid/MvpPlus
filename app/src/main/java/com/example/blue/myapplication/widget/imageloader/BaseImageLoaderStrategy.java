package com.example.blue.myapplication.widget.imageloader;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by Anthony on 2016/3/3.
 * Class Note:
 * abstract class/interface defined to load image
 * (Strategy Pattern used here)
 */
interface BaseImageLoaderStrategy {

   void loadImage(Context ctx, ImageLoader img);

   Bitmap loadAsBitmap(Context ctx, ImageLoader img, int width, int height);

   void clearMemory(Context ctx);

   void clearDiskCache(Context ctx);

   void pauseRequests(Context ctx);

   void resumeRequests(Context ctx);
}
