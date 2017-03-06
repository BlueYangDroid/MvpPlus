package com.example.blue.myapplication.widget.imageloader;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.example.blue.myapplication.widget.imageloader.transform.GlideCircleTransform;
import com.example.blue.myapplication.widget.imageloader.transform.GlideRoundTransform;
import com.example.blue.myapplication.widget.thread.BackTask;

import java.util.concurrent.ExecutionException;


/**
 * Created by Anthony on 2016/3/3.
 * Class Note:
 * using {@link Glide} to load image
 */
public class GlideImageLoaderStrategy implements BaseImageLoaderStrategy {
    private static final String TAG = GlideImageLoaderStrategy.class.getSimpleName();

    @Override
    public void loadImage(Context ctx, ImageLoader img) {
        if (img.resId != null) {
            loadResource(ctx, img); // load from resource
        } else {
            loadNetCache(ctx, img);   // load from net or cache
        }
    }

    /**
     * load image with Glide
     * return Bitmap
     */
    @Override
    public Bitmap loadAsBitmap(Context ctx, ImageLoader img, int width, int height) {
        Bitmap bitmap = null;
        if (img.resId != null) {
            // from resource
            DrawableTypeRequest<Integer> resTypeRequest = getResTypeRequest(ctx, img);
            BitmapTypeRequest<Integer> integerBitmapTypeRequest = resTypeRequest.asBitmap();
            BitmapRequestBuilder<Integer, Bitmap> builder = getBitmapRequestBuilder(ctx, img, integerBitmapTypeRequest);

            try {
                bitmap = builder
                        .into(width > 0 ? width : img.imgView.getWidth(), height > 0 ? height : img.imgView.getHeight())
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        } else {
            // from net cache
            DrawableTypeRequest<String> resTypeRequest = getUrlTypeRequest(ctx, img);
            BitmapTypeRequest<String> stringBitmapTypeRequest = resTypeRequest.asBitmap();
            BitmapRequestBuilder<String, Bitmap> builder = getBitmapRequestBuilder(ctx, img, stringBitmapTypeRequest);

            try {
                bitmap = builder
                        .into(width > 0 ? width : img.imgView.getWidth(), height > 0 ? height : img.imgView.getHeight())
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * load image with Glide
     */
    private void loadNetCache(Context ctx, ImageLoader img) {
        DrawableTypeRequest<String> urlTypeRequest = getUrlTypeRequest(ctx, img);
        DrawableRequestBuilder<String> builder = getDrawableRequestBuilder(ctx, img, urlTypeRequest);
        builder.crossFade(LoaderConfig.DEFAULT_DURATION_MS).into(img.imgView);
    }

    private DrawableTypeRequest<String> getUrlTypeRequest(Context ctx, ImageLoader img) {
        return Glide.with(ctx).load(img.url);
    }

    /**
     * load resource image with Glide
     */
    private void loadResource(Context ctx, ImageLoader img) {
        DrawableTypeRequest<Integer> resTypeRequest = getResTypeRequest(ctx, img);
        DrawableRequestBuilder<Integer> builder = getDrawableRequestBuilder(ctx, img, resTypeRequest);
        builder.crossFade(LoaderConfig.DEFAULT_DURATION_MS).into(img.imgView);
    }

    private DrawableTypeRequest<Integer> getResTypeRequest(Context ctx, ImageLoader img) {
        return Glide.with(ctx).load(img.resId);
    }

    /**
     * DrawableRequestBuilder 的通行配置
     * @param ctx 上下文
     * @param img ImageLoader封装器
     * @param typeRequest url or resource
     * @param <T> String or Integer
     * @return DrawableRequestBuilder<T>
     */
    private <T> DrawableRequestBuilder<T> getDrawableRequestBuilder(Context ctx, ImageLoader img, DrawableTypeRequest<T> typeRequest) {
        DrawableRequestBuilder<T> builder = typeRequest
                .placeholder(img.placeHolder)
                .error(img.errorHolder)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        // transform image type
        int transType = img.transType;
        if (transType == LoaderConfig.TRANS_CIRCLE) {
            builder = builder.transform(new GlideCircleTransform(ctx));
        } else if (transType == LoaderConfig.TRANS_ROUND) {
            builder = builder.transform(new GlideRoundTransform(ctx));
        }

        // SCALE type
        if (img.scaleType == LoaderConfig.SCALE_CENTER_CROP) {
            builder = builder.centerCrop();
        } else if (img.scaleType == LoaderConfig.SCALE_FIT_CENTER) {
            builder = builder.fitCenter();
        }
        return builder;
    }

    /**
     * DrawableRequestBuilder 的通行配置
     * @param ctx 上下文
     * @param img ImageLoader封装器
     * @param typeRequest url or resource
     * @param <T> String or Integer
     * @return DrawableRequestBuilder<T>
     */
    private <T> BitmapRequestBuilder<T, Bitmap> getBitmapRequestBuilder(Context ctx, ImageLoader img, BitmapTypeRequest<T> typeRequest) {
        BitmapRequestBuilder<T, Bitmap> builder = typeRequest.diskCacheStrategy(DiskCacheStrategy.ALL);

        // transform image type
        if (img.transType == LoaderConfig.TRANS_CIRCLE) {
            builder = builder.transform(new GlideCircleTransform(ctx));
        } else if (img.transType == LoaderConfig.TRANS_ROUND) {
            builder = builder.transform(new GlideRoundTransform(ctx));
        }

        // SCALE type
        if (img.scaleType == LoaderConfig.SCALE_CENTER_CROP) {
            builder = builder.centerCrop();
        } else if (img.scaleType == LoaderConfig.SCALE_FIT_CENTER) {
            builder = builder.fitCenter();
        }
        return builder;
    }

    /**
     * load cache image with Glide
     */
    /*private void loadCache(Context ctx, ImageLoader img) {
        Glide.with(ctx).using(new StreamModelLoader<String>() {
            @Override
            public DataFetcher<InputStream> getResourceFetcher(final String model, int i, int i1) {
                return new DataFetcher<InputStream>() {
                    @Override
                    public InputStream loadData(Priority priority) throws Exception {
                        throw new IOException();
                    }

                    @Override
                    public void cleanup() {

                    }

                    @Override
                    public String getId() {
                        return model;
                    }

                    @Override
                    public void cancel() {

                    }
                };
            }
        }).load(img.getUrl()).placeholder(img.getPlaceHolder()).diskCacheStrategy(DiskCacheStrategy.ALL).into(img.getImgView());
    }*/

    @Override
    public void clearMemory(final Context ctx){
        new BackTask<BaseImageLoaderStrategy, Void>() {
            @Override
            protected Void doInTheBack() {
                Glide.get(ctx.getApplicationContext()).clearMemory();
                return null;
            }

            @Override
            protected void doneInTheBack(BaseImageLoaderStrategy outInstance, Void doneValue) {

            }
        }.start();
    }

    @Override
    public void clearDiskCache(final Context ctx){
        new BackTask<BaseImageLoaderStrategy, Void>() {
            @Override
            protected Void doInTheBack() {
                Glide.get(ctx.getApplicationContext()).clearDiskCache();
                return null;
            }

            @Override
            protected void doneInTheBack(BaseImageLoaderStrategy outInstance, Void doneValue) {

            }
        }.start();
    }

    @Override
    public void pauseRequests(Context ctx){
        Glide.with(ctx.getApplicationContext()).pauseRequests();
    }

    @Override
    public void resumeRequests(Context ctx){
        Glide.with(ctx.getApplicationContext()).resumeRequests();
    }
}
