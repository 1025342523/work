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

public interface SaveTraderContactService {
    /**
     * 保存登录密码
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.SAVE_LOAD)
    Observable<TraderContactEntity> SaveTraderContact(
            @Field("DataTypeName") String dataTypeName,
            @Field("Param") String param,
            @Field("Body") String body,
            @Field("Token") String token
    );
}
