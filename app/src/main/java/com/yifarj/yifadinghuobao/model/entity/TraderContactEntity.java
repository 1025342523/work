package com.yifarj.yifadinghuobao.model.entity;

/**
 * Created by zydx-pc on 2017/8/24.
 */

public class TraderContactEntity {

    /**
     * Value : {"Id":0,"TraderId":0,"ContactName":null,"Phone":null,"Mobile":null,"Email":null,"Address":null,"Fax":null,"ModifiedTime":-62135625600,"modify_date":-62135625600,"oper_date":-62135625600,"vip_start_date":-62135625600,"vip_end_date":-62135625600,"birthday":-62135625600,"CreatedTime":-62135625600,"card_id":0,"card_flowno":null,"card_type":null,"vip_sex":0,"card_status":0,"oper_id":0,"acc_num":0,"dec_num":0,"res_num":0,"memo":null,"homeplace":null,"marriage":null,"photo_file":null,"nation":null,"height":0,"weight":0,"blood_type":null,"favor":null,"social_id":null,"use_num":0,"consum_amt":0,"degree":null,"CardSerialNumbe":null,"DepartmentId":0,"CreatedUserId":0,"CreatedDevice":null,"ModifiedUserId":0,"VerifyCode":null,"FailureTime":null,"CodeSendTimes":0,"card_password":null}
     * HasError : false
     * Information : null
     * PageInfo : null
     * AffectedRowCount : 0
     * Tag : null
     * MValue : null
     */

    public ValueEntity Value;
    public boolean HasError;
    public Object Information;
    public Object PageInfo;
    public int AffectedRowCount;
    public Object Tag;
    public Object MValue;

    public static class ValueEntity {
        /**
         * Id : 0
         * TraderId : 0
         * ContactName : null
         * Phone : null
         * Mobile : null
         * Email : null
         * Address : null
         * Fax : null
         * ModifiedTime : -62135625600
         * modify_date : -62135625600
         * oper_date : -62135625600
         * vip_start_date : -62135625600
         * vip_end_date : -62135625600
         * birthday : -62135625600
         * CreatedTime : -62135625600
         * card_id : 0
         * card_flowno : null
         * card_type : null
         * vip_sex : 0
         * card_status : 0
         * oper_id : 0
         * acc_num : 0.0
         * dec_num : 0.0
         * res_num : 0.0
         * memo : null
         * homeplace : null
         * marriage : null
         * photo_file : null
         * nation : null
         * height : 0.0
         * weight : 0.0
         * blood_type : null
         * favor : null
         * social_id : null
         * use_num : 0
         * consum_amt : 0.0
         * degree : null
         * CardSerialNumbe : null
         * DepartmentId : 0
         * CreatedUserId : 0
         * CreatedDevice : null
         * ModifiedUserId : 0
         * VerifyCode : null
         * FailureTime : null
         * CodeSendTimes : 0
         * card_password : null
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
