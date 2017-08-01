package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.TraderEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * SaveTraderService
 *
 * @auther Czech.Yuan
 * @date 2017/8/1 10:13
 */
public interface SaveTraderService {

    /**
     * 保存收货地址
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.SAVE_LOAD)
    Observable<TraderEntity> saveTrader(
            @Field("DataTypeName") String dataTypeName,
            @Field("Body") String body,
            @Field("Param") String param,
            @Field("Token") String token
    );
}
