package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.ProductCategoryListEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * ProductCategoryListService
 *
 * @auther Czech.Yuan
 * @date 2017/7/18 11:44
 */

public interface ProductCategoryListService {

    /**
     * 商品类别列表
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.FETCH_LIST)
    Observable<ProductCategoryListEntity> getProductCategoryList(
            @Field("DataTypeName") String dataTypeName,
            @Field("PageInfo") String pageInfo,
            @Field("Body") String body,
            @Field("Param") String param,
            @Field("Token") String token
    );
}