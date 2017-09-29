package com.yifarj.yifadinghuobao.network.api;

import com.yifarj.yifadinghuobao.model.entity.GetInfoEntity;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * GetInfoService
 *
 * @auther Czech.Yuan
 * @date 2017/9/28 16:32
 */
public interface GetInfoService {

    /**
     * 获取服务器配置
     */
    @FormUrlEncoded
    @POST(ApiConstants.CUrl.GET_INFO)
    Observable<GetInfoEntity> getInfo(
            @Field("companyKey") String value
    );
}
