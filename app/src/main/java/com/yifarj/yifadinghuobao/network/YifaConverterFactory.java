package com.yifarj.yifadinghuobao.network;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * YifaConverterFactory
 *
 * @auther Czech.Yuan
 * @date 2017/5/16 9:22
 */
public class YifaConverterFactory extends Converter.Factory {
    public static YifaConverterFactory create() {
        return create(new Gson());
    }

    public static YifaConverterFactory create(Gson gson) {
        return new YifaConverterFactory(gson);
    }

    private final Gson gson;

    private YifaConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }


    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new YifaResponseBodyConverter<>(adapter);
    }

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new YifaRequestBodyConverter<>(gson, adapter);
    }
}
