package com.yifarj.yifadinghuobao.network;

import com.yifarj.yifadinghuobao.utils.PreferencesUtil;

/**
 * API常量类
 *
 * @auther Czech.Yuan
 * @date 2017/5/15 18:38
 */
public class ApiConstants {

    public static final String COMMON_UA_STR = "YiDingHuo Android Client (Czech.Yuan)";

    public final class CPreference {
        /**
         * 登录名
         */
        public static final String USER_NAME = "user_name";
        /**
         * 密码
         */
        public static final String PASSWORD = "password";
        /**
         * 账套号
         */
        public static final String ACCOUNT_ID = "account_id";
        /**
         * 登录域名
         */
        public static final String LOGIN_DOMAIN = "login_domain";
        /**
         * 登录ip
         */
        public static final String LOGIN_IP = "login_ip";
        /**
         * 登录端口
         */
        public static final String LOGIN_PORT = "login_port";
        /**
         * 登录keycode
         */
        public static final String LOGIN_KEY_CODE = "login_keycode";
        /**
         * 打印机名称
         */
        public static final String PRINTER_NAME = "printer_name";

        /**
         * 服务端版本号
         */
        public static final String SERVER_VERSION = "server_version";
        /**
         * 支付方式
         */
        public static final String MODE_PAYMENT = "mode_payment";
    }


    /**
     * 定义URL地址
     */
    public static class CUrl {
        public static String BASE_URL = PreferencesUtil.getString(ApiConstants.CPreference.LOGIN_DOMAIN, "");
        public static final String EXPERIENCE_URL = "http://14.215.120.120:8884/yifa.asmx";
        public static final String PHONE_USER_INFO_URL = "http://duokaile.6655.la:6666/yifa.asmx";
        /**
         * 登录
         */
        public static final String LOGIN = "/Login";
        public static final String LOGIN_OTHER = "/Login2";
        /**
         * 退出登录
         */
        public static final String EXIT = "/Exit";

        public static final String GET_ACCOUNT_LIST = "/GetAccountList";

        /**
         * 获取列表接口,包括客户列表等列表,与传过去的字段有关
         */
        public static final String FETCH_LIST = "/FetchList";

        /**
         * 获取单条数据
         */
        public static final String FETCH = "/Fetch";

        /**
         * 创建接口
         */
        public static final String CREATE = "/Create";

        /**
         * 保存接口
         */
        public static final String SAVE = "/Save";

        /**
         * 货品客户信息保存接口
         */
        public static final String SAVE_LOAD = "/SaveLoad";

        /**
         * 删除接口
         */
        public static final String DELETE = "/Delete";
        /**
         * 执行其它命令接口
         */
        public static final String EXEC = "/Execute";

        /**
         * 手机配置信息上传
         */
        public static final String PHONEUSERINFO_UPLOAD = "/UploadPhoneUserInfo";

        /**
         * 手机配置信息下载
         */
        public static final String PHONEUSERINFO_DOWNLOAD = "/DownPhoneUserInfo";
    }

}
