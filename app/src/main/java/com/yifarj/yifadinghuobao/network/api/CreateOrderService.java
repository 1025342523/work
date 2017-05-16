package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.AccountListEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 下单
 *
 * @auther Czech.Yuan
 * @date 2017/5/15 18:25
 */
public interface CreateOrderService {

    /**
     * 下单
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.CREATE)
    Observable<AccountListEntity> createOrderInfo(
            @Field("DataTypeName") String dataTypeName,
            @Field("Param") String param,
            @Field("Token") String token
    );
}
