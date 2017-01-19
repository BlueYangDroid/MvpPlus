package com.example.blue.myapplication.widget.imageloader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.io.InputStream;


/**
 * Class Note:
 * using Picasso to load image
 */
public class CustomImageLoaderStrategy implements BaseImageLoaderStrategy {

    private LruCache<String, Bitmap> mMemoryCache;

    private void initMemCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize =  maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

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

    /**
     * Async
     * @param resId
     * @param imageView
     */
    public void loadBitmap(int resId, ImageView imageView) {

    }

    /**
     * Sync
     * @param context
     * @param resId
     * @return Bitmap
     */
    public Bitmap loadResBitmap(Context context, int resId) {
        return readBitMap(context, resId);
    }

    private Bitmap readBitMap(Context context, int resId){

        BitmapFactory.Options opt = new BitmapFactory.Options();

        opt.inPreferredConfig = Bitmap.Config.RGB_565;   //小于ARGB_8888  样式大小

        //获取资源图片
        opt.inPurgeable = true; // 保留像素等信息备份，加快图片二次处理
        opt.inInputShareable = true;    // 输入流共享，加快二次加载

        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    private int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap decodeBitmapFromResource(Resources res, int resId,
                                                   int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
