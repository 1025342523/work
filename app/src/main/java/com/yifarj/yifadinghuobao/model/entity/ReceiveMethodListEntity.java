package com.yifarj.yifadinghuobao.model.entity;


import java.util.List;

/**
 * ReceiveMethodListEntity
 *
 * @auther Czech.Yuan
 * @date 2017/5/21 14:32
 */
public class ReceiveMethodListEntity {


    /**
     * Value : [{"Id":100,"Name":"1","Type":1,"Ordinal":0}]
     * HasError : false
     * Information : null
     * PageInfo : null
     * AffectedRowCount : 0
     * Tag : null
     * MValue : null
     */

    public boolean HasError;
    public Object Information;
    public Object PageInfo;
    public int AffectedRowCount;
    public Object Tag;
    public Object MValue;
    public List<ValueEntity> Value;

    public static class ValueEntity {
        /**
         * Id : 100
         * Name : 1
         * Type : 1
         * Ordinal : 0
         */

        public int Id;
        public String Name;
        public int Type;
        public int Ordinal;
    }
}
