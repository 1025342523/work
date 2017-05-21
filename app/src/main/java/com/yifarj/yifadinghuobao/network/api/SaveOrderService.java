package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.CreateOrderEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 保存订单
 *
 * @auther Czech.Yuan
 * @date 2017/5/21 18:25
 */
public interface SaveOrderService {


    @FormUrlEncoded
    @POST(ApiConstants.CUrl.SAVE)
    Observable<CreateOrderEntity> saveOrderInfo(
            @Field("DataTypeName") String dataTypeName,
            @Field("Param") String param,
            @Field("Body") String body,
            @Field("Token") String token
    );
}
