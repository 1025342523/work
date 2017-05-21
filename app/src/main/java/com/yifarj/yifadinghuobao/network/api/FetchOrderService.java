package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.CreateOrderEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 查单
 *
 * @auther Czech.Yuan
 * @date 2017/5/15 18:25
 */
public interface FetchOrderService {

    /**
     * 下单
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.FETCH)
    Observable<CreateOrderEntity> fetchOrderInfo(
            @Field("DataTypeName") String dataTypeName,
            @Field("Body") String body,
            @Field("Param") String param,
            @Field("Token") String token
    );
}
