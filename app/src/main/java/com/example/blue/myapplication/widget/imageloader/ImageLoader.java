package com.example.blue.myapplication.widget.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;




/**
 * Created by Anthony on 2016/3/3.
 * Class Note:
 * encapsulation of ImageView,Build Pattern used
 */
public class ImageLoader {
    String url; //url to parse
    Integer resId;// local resource

    int placeHolder; //placeholder when fail to load pics
    int errorHolder; //errorHolder if return error when load pics
    
    int transType;  // (normal,circle,round)
    int scaleType;//load netStrategy ,weather under wifi
    
    ImageView imgView; //ImageView instance
    
    private ImageLoader(Builder builder) {
        this.transType = builder.transType;
        this.url = builder.url;
        this.resId = builder.resId;
        this.placeHolder = builder.placeHolder;
        this.errorHolder = builder.errorHolder;
        this.imgView = builder.imgView;
        this.scaleType = builder.scaleType;
    }

    public int getTransType() {
        return transType;
    }

    public String getUrl() {
        return url;
    }

    public Integer getResId() {
        return resId;
    }

    public int getPlaceHolder() {
        return placeHolder;
    }

    public int getErrorHolder() {
        return errorHolder;
    }

    public ImageView getImgView() {
        return imgView;
    }

    public int getScaleType() {
        return scaleType;
    }

    public static class Builder {
        private int transType;
        private String url;
        private Integer resId;
        private int placeHolder;
        private int errorHolder;
        private ImageView imgView;
        private int scaleType;

        public Builder() {
            this.url = "";
            this.placeHolder = LoaderConfig.DEFAULT_PLACE_HOLDER;
            this.errorHolder = LoaderConfig.DEFAULT_ERROR_HOLDER;
            this.transType = LoaderConfig.TRANS_NORMAL;
            this.scaleType = LoaderConfig.SCALE_NORMAL;
            this.imgView = null;
        }

        public Builder transType(int transType) {
            this.transType = transType;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder resId(Integer resourceId) {
            this.resId = resourceId;
            return this;
        }

        public Builder placeHolder(int placeHolder) {
            this.placeHolder = placeHolder;
            return this;
        }

        public Builder errorHolder(int errorHolder) {
            this.errorHolder = errorHolder;
            return this;
        }

        public Builder imgView(ImageView imgView) {
            this.imgView = imgView;
            return this;
        }

        public Builder scaleType(int scaleType) {
            this.scaleType = scaleType;
            return this;
        }

        public ImageLoader build() {
            return new ImageLoader(this);
        }
    }

    /**
     * 异步方法，内部已管理线程调度
     * @param context
     */
    public void loadImage(Context context) {
        LoaderConfig.mStrategy.loadImage(context, this);
    }

    /**
     * 同步方法，请在子线程执行
     * @param context
     * @param width
     * @param height
     * @return
     */
    public Bitmap loadAsBitmap(Context context, int width, int height) {
        return LoaderConfig.mStrategy.loadAsBitmap(context, this, width, height);
    }
}
