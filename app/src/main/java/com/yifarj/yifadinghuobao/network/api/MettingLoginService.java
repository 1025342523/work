package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.MettingLoginEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * MettingLoginService
 *
 * @auther Czech.Yuan
 * @date 2017/5/19 20:20
 */
public interface MettingLoginService {

    /**
     * 获取验证码
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.METTINGLOGIN)
    Observable<MettingLoginEntity> mettingLogin(
            @Field("Token") String token,
            @Field("PhoneNum") String phoneNum,
            @Field("SecurityCode") String securityCode);
}
