package com.yifarj.yifadinghuobao.model.entity;

/**
 * MettingLoginEntity
 *
 * @auther Czech.Yuan
 * @date 2017/5/19 19:54
 */
public class MettingLoginEntity {


    /**
     * Value : {"Id":105,"TraderId":105,"ContactName":"邓付红","Phone":null,"Mobile":"15399981540","Email":null,"Address":"湘潭","Fax":null,"ModifiedTime":1495176996,"modify_date":1495176996,"oper_date":1488772237,"vip_start_date":1488772237,"vip_end_date":3066609037,"birthday":1488772237,"CreatedTime":1488772237,"card_id":0,"card_flowno":null,"card_type":null,"vip_sex":0,"card_status":0,"oper_id":0,"acc_num":0,"dec_num":0,"res_num":0,"memo":null,"homeplace":null,"marriage":null,"photo_file":null,"nation":null,"height":0,"weight":0,"blood_type":null,"favor":null,"social_id":null,"use_num":0,"consum_amt":0,"degree":null,"CardSerialNumbe":null,"DepartmentId":0,"CreatedUserId":0,"CreatedDevice":null,"ModifiedUserId":1,"VerifyCode":null,"FailureTime":null,"CodeSendTimes":0}
     * HasError : false
     * Information :
     * PageInfo : null
     * AffectedRowCount : 0
     * Tag : null
     * MValue : null
     */

    public ValueEntity Value;
    public boolean HasError;
    public String Information;
    public Object PageInfo;
    public int AffectedRowCount;
    public Object Tag;
    public Object MValue;

    public static class ValueEntity {
        /**
         * Id : 105
         * TraderId : 105
         * ContactName : 邓付红
         * Phone : null
         * Mobile : 15399981540
         * Email : null
         * Address : 湘潭
         * Fax : null
         * ModifiedTime : 1495176996
         * modify_date : 1495176996
         * oper_date : 1488772237
         * vip_start_date : 1488772237
         * vip_end_date : 3066609037
         * birthday : 1488772237
         * CreatedTime : 1488772237
         * card_id : 0
         * card_flowno : null
         * card_type : null
         * vip_sex : 0
         * card_status : 0
         * oper_id : 0
         * acc_num : 0
         * dec_num : 0
         * res_num : 0
         * memo : null
         * homeplace : null
         * marriage : null
         * photo_file : null
         * nation : null
         * height : 0
         * weight : 0
         * blood_type : null
         * favor : null
         * social_id : null
         * use_num : 0
         * consum_amt : 0
         * degree : null
         * CardSerialNumbe : null
         * DepartmentId : 0
         * CreatedUserId : 0
         * CreatedDevice : null
         * ModifiedUserId : 1
         * VerifyCode : null
         * FailureTime : null
         * CodeSendTimes : 0
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
        public int acc_num;
        public int dec_num;
        public int res_num;
        public String memo;
        public String homeplace;
        public String marriage;
        public String photo_file;
        public String nation;
        public int height;
        public int weight;
        public String blood_type;
        public String favor;
        public String social_id;
        public int use_num;
        public int consum_amt;
        public String degree;
        public String CardSerialNumbe;
        public int DepartmentId;
        public int CreatedUserId;
        public String CreatedDevice;
        public int ModifiedUserId;
        public String VerifyCode;
        public long FailureTime;
        public long CodeSendTimes;
    }
}
