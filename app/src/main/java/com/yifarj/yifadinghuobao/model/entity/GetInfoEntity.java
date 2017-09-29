package com.yifarj.yifadinghuobao.model.entity;

/**
 * 获取服务器配置
 */
public class GetInfoEntity {


    public ValueEntity Value;
    public boolean HasError;
    public String Information;
    public Object PageInfo;
    public int AffectedRowCount;
    public Object Tag;
    public Object MValue;

    public static class ValueEntity {
        /**
         * CompanyKey : 9953
         * Company : Czech.Yuan
         * Url : http://192.168.3.110:8888/yifa.asmx
         * Address : 127.0.0.1
         * Port : 5218
         * KeyCode :
         * AccsetId : 2
         */
        public String CompanyKey;
        public String Company;
        public String Url;
        public String Address;
        public String Port;
        public String KeyCode;
        public String AccsetId;
    }
}
