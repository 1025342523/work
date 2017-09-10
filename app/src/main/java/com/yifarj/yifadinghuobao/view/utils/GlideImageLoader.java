package com.yifarj.yifadinghuobao.view.utils;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.czechyuan.imagepicker.loader.ImageLoader;
import com.yifarj.yifadinghuobao.R;

import java.io.File;

/**
* GlideImageLoader
* @auther  Czech.Yuan
* @date 2017/9/8 14:38
*/
public class GlideImageLoader  implements ImageLoader{

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        Glide.with(activity)
                .load(new File(path))
                .centerCrop()
                .crossFade()
                .error(R.mipmap.default_image)
                .override(width, height)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {
        //这里是清除缓存的方法,根据需要自己实现
    }

}
