package com.yifarj.yifadinghuobao.model.entity;

/**
 * MettingLoginEntity
 *
 * @auther Czech.Yuan
 * @date 2017/5/19 19:54
 */
public class MettingLoginEntity {


    /**
     * Value : {"Id":112,"TraderId":122,"ContactName":"15111311956","Phone":null,"Mobile":"15111311956","Email":null,"Address":null,"Fax":null,"ModifiedTime":1495183300,"modify_date":-2209017600,"oper_date":1495183300,"vip_start_date":-2209017600,"vip_end_date":-2209017600,"birthday":-2209017600,"CreatedTime":1495183300,"card_id":0,"card_flowno":null,"card_type":null,"vip_sex":0,"card_status":0,"oper_id":0,"acc_num":0,"dec_num":0,"res_num":0,"memo":null,"homeplace":null,"marriage":null,"photo_file":null,"nation":null,"height":0,"weight":0,"blood_type":null,"favor":null,"social_id":null,"use_num":0,"consum_amt":0,"degree":null,"CardSerialNumbe":null,"DepartmentId":0,"CreatedUserId":0,"CreatedDevice":null,"ModifiedUserId":0,"VerifyCode":null,"FailureTime":null,"CodeSendTimes":0}
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
         * Id : 112
         * TraderId : 122
         * ContactName : 15111311956
         * Phone : null
         * Mobile : 15111311956
         * Email : null
         * Address : null
         * Fax : null
         * ModifiedTime : 1495183300
         * modify_date : -2209017600
         * oper_date : 1495183300
         * vip_start_date : -2209017600
         * vip_end_date : -2209017600
         * birthday : -2209017600
         * CreatedTime : 1495183300
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
         */

        public int Id;//编号
        public int TraderId;//往来单位id
        public String ContactName;//联系人
        public String Phone;//电话
        public String Mobile;//手机
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
        public double height;
        public double weight;
        public String blood_type;
        public String favor;
        public String social_id;
        public int use_num;
        public double consum_amt;
        public String degree;
        public String CardSerialNumbe;
        public int DepartmentId;//经办部门
        public int CreatedUserId;//创建人id
        public String CreatedDevice;//创建设备
        public int ModifiedUserId;//修改人id
        public String VerifyCode;//手机验证码
        public long FailureTime;//验证码失效时间
        public int CodeSendTimes;//失效时间内发送次数
    }
}
