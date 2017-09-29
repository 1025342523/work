package com.yifarj.yifadinghuobao.model.helper;

import com.yifarj.yifadinghuobao.model.entity.MettingLoginEntity;
import com.yifarj.yifadinghuobao.model.entity.PasswordLoginEntity;
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
    private static PasswordLoginEntity.ValueBeanX.ValueEntity passwordCustomerInfo;
    private static boolean currentLoginType;// false：验证码登录  true：密码登录

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

    public static void setPasswordCustomerInfo(PasswordLoginEntity.ValueBeanX.ValueEntity pwdCustomerInfo) {
        passwordCustomerInfo = pwdCustomerInfo;
    }

    public static PasswordLoginEntity.ValueBeanX.ValueEntity getPasswordCustomerInfo() {
        return passwordCustomerInfo;
    }

    public static void setCurrentLoginType(boolean type) {
        currentLoginType = type;
    }

    public static boolean getCurrentLoginType() {
        return currentLoginType;
    }
}
