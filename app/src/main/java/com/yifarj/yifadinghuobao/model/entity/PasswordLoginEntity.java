package com.yifarj.yifadinghuobao.model.entity;

/**
 * Created by zydx-pc on 2017/8/25.
 */

public class PasswordLoginEntity {

    /**
     * Value : {"ServerContext":{"Error":null,"ErrorDetail":null},"Value":{"Id":114,"TraderId":119,"ContactName":"15399981540","Phone":"","Mobile":"15399981540","Email":"","Address":"","Fax":"","ModifiedTime":1502760454,"modify_date":1498551049,"oper_date":1495546148,"vip_start_date":1502760442,"vip_end_date":1502760443,"birthday":1502760443,"CreatedTime":1495546148,"card_id":0,"card_flowno":"15399981540","card_type":"1","vip_sex":0,"card_status":2,"oper_id":0,"acc_num":4.6,"dec_num":0,"res_num":4.6,"memo":"","homeplace":"","marriage":"","photo_file":"","nation":"","height":0,"weight":0,"blood_type":"","favor":"","social_id":"","use_num":0,"consum_amt":-23,"degree":null,"CardSerialNumbe":null,"DepartmentId":0,"CreatedUserId":0,"CreatedDevice":null,"ModifiedUserId":0,"VerifyCode":"","FailureTime":-2209017600,"CodeSendTimes":0,"card_password":"123456"},"HasError":false,"Information":null,"PageInfo":null,"AffectedRowCount":0,"Tag":null,"MValue":null}
     * HasError : false
     * Information : null
     * PageInfo : null
     * AffectedRowCount : 0
     * Tag : d275f24f-adf2-400f-a562-da6dcdad7c39
     * MValue : null
     */

    public ValueBeanX Value;
    public boolean HasError;
    public Object Information;
    public Object PageInfo;
    public int AffectedRowCount;
    public String Tag;
    public Object MValue;

    public static class ValueBeanX {
        /**
         * ServerContext : {"Error":null,"ErrorDetail":null}
         * Value : {"Id":114,"TraderId":119,"ContactName":"15399981540","Phone":"","Mobile":"15399981540","Email":"","Address":"","Fax":"","ModifiedTime":1502760454,"modify_date":1498551049,"oper_date":1495546148,"vip_start_date":1502760442,"vip_end_date":1502760443,"birthday":1502760443,"CreatedTime":1495546148,"card_id":0,"card_flowno":"15399981540","card_type":"1","vip_sex":0,"card_status":2,"oper_id":0,"acc_num":4.6,"dec_num":0,"res_num":4.6,"memo":"","homeplace":"","marriage":"","photo_file":"","nation":"","height":0,"weight":0,"blood_type":"","favor":"","social_id":"","use_num":0,"consum_amt":-23,"degree":null,"CardSerialNumbe":null,"DepartmentId":0,"CreatedUserId":0,"CreatedDevice":null,"ModifiedUserId":0,"VerifyCode":"","FailureTime":-2209017600,"CodeSendTimes":0,"card_password":"123456"}
         * HasError : false
         * Information : null
         * PageInfo : null
         * AffectedRowCount : 0
         * Tag : null
         * MValue : null
         */

        public ServerContextBean ServerContext;
        public ValueEntity Value;
        public boolean HasError;
        public Object Information;
        public Object PageInfo;
        public int AffectedRowCount;
        public Object Tag;
        public Object MValue;

        public static class ServerContextBean {
            /**
             * Error : null
             * ErrorDetail : null
             */

            public Object Error;
            public Object ErrorDetail;
        }

        public static class ValueEntity {
            /**
             * Id : 114
             * TraderId : 119
             * ContactName : 15399981540
             * Phone :
             * Mobile : 15399981540
             * Email :
             * Address :
             * Fax :
             * ModifiedTime : 1502760454
             * modify_date : 1498551049
             * oper_date : 1495546148
             * vip_start_date : 1502760442
             * vip_end_date : 1502760443
             * birthday : 1502760443
             * CreatedTime : 1495546148
             * card_id : 0
             * card_flowno : 15399981540
             * card_type : 1
             * vip_sex : 0
             * card_status : 2
             * oper_id : 0
             * acc_num : 4.6
             * dec_num : 0
             * res_num : 4.6
             * memo :
             * homeplace :
             * marriage :
             * photo_file :
             * nation :
             * height : 0
             * weight : 0
             * blood_type :
             * favor :
             * social_id :
             * use_num : 0
             * consum_amt : -23
             * degree : null
             * CardSerialNumbe : null
             * DepartmentId : 0
             * CreatedUserId : 0
             * CreatedDevice : null
             * ModifiedUserId : 0
             * VerifyCode :
             * FailureTime : -2209017600
             * CodeSendTimes : 0
             * card_password : 123456
             */

            public int Id;
            public int TraderId;
            public String ContactName;
            public String Phone;
            public String Mobile;
            public String Email;
            public String Address;
            public String Fax;
            public long ModifiedTime;
            public long modify_date;
            public long oper_date;
            public long vip_start_date;
            public long vip_end_date;
            public long birthday;
            public long CreatedTime;
            public int card_id;
            public String card_flowno;
            public String card_type;
            public int vip_sex;
            public int card_status;
            public int oper_id;
            public double acc_num;
            public double dec_num;
            public double res_num;
            public String memo;
            public String homeplace;
            public String marriage;
            public String photo_file;
            public String nation;
            public double height;
            public double weight;
            public String blood_type;
            public String favor;
            public String social_id;
            public int use_num;
            public double consum_amt;
            public String degree;
            public String CardSerialNumbe;
            public int DepartmentId;
            public int CreatedUserId;
            public String CreatedDevice;
            public int ModifiedUserId;
            public String VerifyCode;
            public long FailureTime;
            public int CodeSendTimes;
            public String card_password;
        }
    }
}
