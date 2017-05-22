package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.SaleOrderListEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
* OrderListService
* @auther  Czech.Yuan
* @date 2017/5/22 14:55
*/

public interface OrderListService {

    /**
     * 订货单列表
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.FETCH_LIST)
    Observable<SaleOrderListEntity> getOrderList(
            @Field("DataTypeName") String dataTypeName,
            @Field("PageInfo") String pageInfo,
            @Field("Body") String body,
            @Field("Param") String param,
            @Field("Token") String token
    );
}