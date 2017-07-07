package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.StockInfoForToolTipListEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * StockInfoForToolTipListService
 *
 * @auther rlj
 * @date 2017/7/6 10:59
 */

public interface StockInfoForToolTipListService {

    /**
     * 库存列表
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.FETCH_LIST)
    Observable<StockInfoForToolTipListEntity> getStockInfoList(
            @Field("DataTypeName") String dataTypeName,
            @Field("PageInfo") String pageInfo,
            @Field("Body") String body,
            @Field("Param") String param,
            @Field("Token") String token
    );
}