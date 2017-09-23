package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.OrderSummaryEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by ZhangZeZhi on 2017-09-22.
 */

public interface OrderSummaryService {

    /**
     * 获取订单汇总
     *
     * @param token
     * @param typeName
     * @param body
     * @param param
     * @return
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.EXEC)
    Observable<OrderSummaryEntity> getOrderSummary(
        @Field("Token") String token,
        @Field("DataTypeName") String typeName,
        @Field("Body") String body,
        @Field("Param") String param
    );

}
