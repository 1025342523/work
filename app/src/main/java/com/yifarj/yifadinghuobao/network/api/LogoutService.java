package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.LoginEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 退出
 *
 * @auther Czech.Yuan
 * @date 2017/5/18 20:52
 */
public interface LogoutService {

    /**
     * 退出
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.EXIT)
    Observable<LoginEntity> logout(
            @Field("Body") String body,
            @Field("Param") String param,
            @Field("Token") String token
    );
}
