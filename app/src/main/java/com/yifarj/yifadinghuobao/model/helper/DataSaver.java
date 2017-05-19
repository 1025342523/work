package com.yifarj.yifadinghuobao.model.helper;

import com.yifarj.yifadinghuobao.model.entity.MettingLoginEntity;

/**
 * DataSaver
 *
 * @auther Czech.Yuan
 * @date 2017/5/19 20:48
 */
public class DataSaver {

    private static MettingLoginEntity.ValueEntity mettingCustomerInfo;

    public static void setMettingCustomerInfo(MettingLoginEntity.ValueEntity customerInfo) {
        mettingCustomerInfo = customerInfo;
    }

    public static MettingLoginEntity.ValueEntity getMettingCustomerInfo() {
        return mettingCustomerInfo;
    }
}
