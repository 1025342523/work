package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.LoginEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * LoginService
 *
 * @auther Czech.Yuan
 * @date 2017/5/15 18:25
 */
public interface LoginService {

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.LOGIN_OTHER)
    Observable<LoginEntity> login(
            @Field("Id") String name,
            @Field("Password") String password,
            @Field("Host") String ip,
            @Field("Port") String port,
            @Field("AccountId") String accountId,
            @Field("MachineCode") String machineCode,
            @Field("MachineIp") String machineIp,
            @Field("Type") String Type
    );
}
