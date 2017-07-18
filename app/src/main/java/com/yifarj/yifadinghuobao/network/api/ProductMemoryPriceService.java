package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.ProductMemoryPriceEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * ProductMemoryPriceService
 *
 * @auther zydx-pc
 * @date 2017/7/10 15:13
 */

public interface ProductMemoryPriceService {

    /**
     * 记忆价格
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.FETCH)
    Observable<ProductMemoryPriceEntity> getProductMemoryPrice(
            @Field("DataTypeName") String dataTypeName,
            @Field("Body") String body,
            @Field("Param") String param,
            @Field("Token") String token
    );
}