package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.TraderContactEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by zydx-pc on 2017/8/24.
 */

public interface FetchTraderContactService {
    /**
     * 查询登录密码
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.FETCH)
    Observable<TraderContactEntity> fetchTraderContact(
            @Field("DataTypeName") String dataTypeName,
            @Field("Body") String body,
            @Field("Param") String param,
            @Field("Token") String token
    );
}
