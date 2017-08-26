package com.yifarj.yifadinghuobao.model.helper;

import com.yifarj.yifadinghuobao.model.entity.MettingLoginEntity;
import com.yifarj.yifadinghuobao.model.entity.TraderEntity;
import com.yifarj.yifadinghuobao.utils.PreferencesUtil;

/**
 * DataSaver
 *
 * @auther Czech.Yuan
 * @date 2017/5/19 20:48
 */
public class DataSaver {

    private static MettingLoginEntity.ValueEntity mettingCustomerInfo;
    private static TraderEntity.ValueEntity traderInfo;
    private static int mPriceSystemId;

    public static void setMettingCustomerInfo(MettingLoginEntity.ValueEntity customerInfo) {
        mettingCustomerInfo = customerInfo;
    }

    public static MettingLoginEntity.ValueEntity getMettingCustomerInfo() {
        return mettingCustomerInfo;
    }

    public static void setPriceSystemId(int priceSystemId) {
        mPriceSystemId = priceSystemId;
    }

    public static int getPriceSystemId() {
        int priceSystemId = PreferencesUtil.getInt("PriceSystemId", -1);
        if (priceSystemId != -1) {
            mPriceSystemId = priceSystemId;
        }
        return mPriceSystemId;
    }

    public static void setTraderInfo(TraderEntity.ValueEntity mTraderInfo) {
        traderInfo = mTraderInfo;
    }

    public static TraderEntity.ValueEntity getTraderInfo() {
        return traderInfo;
    }

}
