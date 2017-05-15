package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.AccountListEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * AccountService
 *
 * @auther Czech.Yuan
 * @date 2017/5/15 18:25
 */
public interface AccountService {

    /**
     * 获取账套
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.GET_ACCOUNT_LIST)
    Observable<AccountListEntity.ValueEntity> getAccountList(
            @Field("Host") String ip,
            @Field("Port") String port,
            @Field("KeyCode") String keyCode
    );
}
