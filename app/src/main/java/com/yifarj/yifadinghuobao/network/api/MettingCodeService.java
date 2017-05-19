package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.MettingCodeEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * MettingCodeService
 *
 * @auther Czech.Yuan
 * @date 2017/5/19 16:38
 */
public interface MettingCodeService {

    /**
     * 获取验证码
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.METTINGCODE)
    Observable<MettingCodeEntity> getMettingCode(
            @Field("Host") String ip,
            @Field("Port") String port,
            @Field("AccountId") String accountId,
            @Field("PhoneNum") String phoneNum,
            @Field("MachineCode") String machineCode,
            @Field("MachineIp") String machineIp
    );
}
