package com.yifarj.yifadinghuobao.network;

import com.facebook.stetho.common.LogUtil;
import com.google.gson.TypeAdapter;
import com.yifarj.yifadinghuobao.utils.ZipUtil;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * YifaResponseBodyConverter
 *
 * @auther Czech.Yuan
 * @date 2017/5/16 9:33
 */
public class YifaResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final TypeAdapter<T> adapter;

    public YifaResponseBodyConverter(TypeAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String responseBody = value.string();
        if (responseBody != null && responseBody.length() > 76) {
            responseBody = responseBody.substring(76, responseBody.length() - 9);
            if (!responseBody.contains("{")) {
                try {
                    responseBody = ZipUtil.ungzip(responseBody);
                } catch (Exception e) {
                    LogUtil.e("解压失败", "-------------");
                }
            }
        }
        return adapter.fromJson(responseBody);
    }
}
