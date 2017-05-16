package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.GoodsListEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * GoodsListService
 *
 * @auther Czech.Yuan
 * @date 2017/5/16 11:40
 */
public interface GoodsListService {

    /**
     * 商品列表
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.FETCH_LIST)
    Observable<GoodsListEntity> getGoodsList(
            @Field("DataTypeName") String dataTypeName,
            @Field("PageInfo") String pageInfo,
            @Field("Body") String body,
            @Field("Param") String param,
            @Field("Token") String token
    );
}