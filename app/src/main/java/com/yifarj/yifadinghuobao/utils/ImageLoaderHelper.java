package com.yifarj.yifadinghuobao.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ImageLoaderHelper {
    //	private static String TAG = "ImageLoaderHelper";
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    public static ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    public static void displayImage(ImageView view, String url) {
        displayImage(0, view, url, animateFirstListener);
    }

    public static void displayImage(int defaultResourceId, ImageView view,
                                    String url) {
        displayImage(defaultResourceId, view, url, animateFirstListener);
    }

    public static void displayImage(String url, ImageView view) {
        imageLoader.displayImage(url, view);
    }


    public static void displayImage(int defaultResourceId, ImageView view,
                                    String url, ImageLoadingListener aynsListenner) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultResourceId)
                .showImageForEmptyUri(defaultResourceId)
                .showImageOnFail(defaultResourceId)
                .cacheInMemory(false)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.displayImage(url, view, options, aynsListenner);
    }

    public static void clearCache() {
        imageLoader.clearMemoryCache();
        imageLoader.clearDiskCache();
    }
}
