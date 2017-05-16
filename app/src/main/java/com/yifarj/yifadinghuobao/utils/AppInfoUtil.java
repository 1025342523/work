package com.yifarj.yifadinghuobao.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.blankj.utilcode.util.StringUtils;
import com.facebook.stetho.common.LogUtil;
import com.yifarj.yifadinghuobao.network.ApiConstants;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.UUID;

/**
 * AppInfoUtil
 *
 * @auther Czech.Yuan
 * @date 2017/5/15 16:09
 */
public class AppInfoUtil {
    private static String deviceID;

    public static String genPicUrl(String picName) {
        String accountId = String.valueOf(PreferencesUtil.getInt(ApiConstants.CPreference.ACCOUNT_ID, 0));
        String filterStr = ApiConstants.CUrl.BASE_URL.substring(7);
        String tail = accountId + "/product/" + picName;
        String tempFilterStr = filterStr.substring(0, filterStr.indexOf("/"));
        return "http://" + tempFilterStr + "/" + tail;
    }

    public static void restoreToken(String token) {
        PreferencesUtil.putString("token", token);
    }

    public static String getToken() {
        return PreferencesUtil.getString("token", "");
    }

    /**
     * 重新设置BaseUrl
     *
     * @param url   要设置的url
     * @param store 是否保存到shared preference里
     */
    public static void resetBaseUrl(String url, boolean store) {
        if (store) {
            PreferencesUtil.putString(ApiConstants.CPreference.LOGIN_DOMAIN, url);
        }
        ApiConstants.CUrl.BASE_URL = url;
    }

    /**
     * 获取设备id
     */
    public static String getDeviceId(Context context) {
        if (deviceID == null) {
            synchronized (AppInfoUtil.class) {
                if (deviceID == null) {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    try {
                        deviceID = tm.getDeviceId();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (StringUtils.isEmpty(deviceID)) {
                        deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        if (StringUtils.isEmpty(deviceID)) {
                            deviceID = PreferencesUtil.getString("deviceId", null);
                        }
                        if (deviceID == null) {
                            deviceID = UUID.randomUUID().toString();
                            PreferencesUtil.putString("deviceId", deviceID);
                        }
                    }
                }
            }
        }
        return deviceID;
    }

    /**
     * 获取设备ip地址
     *
     * @return
     */
    public static String getIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            LogUtil.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }
}
