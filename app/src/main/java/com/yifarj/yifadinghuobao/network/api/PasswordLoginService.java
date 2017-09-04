package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.PasswordLoginEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by zydx-pc on 2017/8/25.
 */

public interface PasswordLoginService {

    /**
     * 密码登录
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.PASSWORDLOGIN)
    Observable<PasswordLoginEntity> passwordLogin(
            @Field("Host") String ip,
            @Field("Port") String port,
            @Field("AccountId") String accountId,
            @Field("PhoneNum") String phoneNum,
            @Field("PassWord") String password,
            @Field("MachineCode") String machineCode,
            @Field("MachineIp") String machineIp
    );
}
