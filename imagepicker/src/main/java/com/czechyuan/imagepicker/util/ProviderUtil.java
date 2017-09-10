package com.czechyuan.imagepicker.util;

import android.content.Context;


/**
* 用于解决provider冲突的util
* @auther  Czech.Yuan
* @date 2017/8/24 16:37
*/

public class ProviderUtil {

    public static String getFileProviderName(Context context){
        return context.getPackageName()+".provider";
    }
}
