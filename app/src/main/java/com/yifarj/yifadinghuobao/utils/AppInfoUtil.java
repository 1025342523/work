package com.yifarj.yifadinghuobao.utils;

/**
 * AppInfoUtil
 *
 * @auther Czech.Yuan
 * @date 2017/5/15 16:09
 */
public class AppInfoUtil {

    public static String genPicUrl(String picName) {
        String accountId = String.valueOf(PreferencesUtil.getInt(Constants.CPreference.ACCOUNT_ID, 0));
        String filterStr = Constants.CUrl.BASE_URL.substring(7);
        String tail = accountId + "/product/" + picName;
        String tempFilterStr = filterStr.substring(0, filterStr.indexOf("/"));
        return "http://" + tempFilterStr + "/" + tail;
    }
}
