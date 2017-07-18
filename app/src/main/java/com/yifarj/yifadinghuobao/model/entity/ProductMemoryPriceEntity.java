package com.yifarj.yifadinghuobao.model.entity;

/**
 * 记忆价格
 */
public class ProductMemoryPriceEntity {

    /**
     * Value : {"ProductId":100,"TraderId":1,"PriceSystemId":1,"Price":23,"ProductUnitId":100,"Discount":1}
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
         * ProductId : 100
         * TraderId : 1
         * PriceSystemId : 1
         * Price : 23.0
         * ProductUnitId : 100
         * Discount : 1.0
         */
        public int ProductId;
        public int TraderId;
        public int PriceSystemId;
        public double Price;
        public int ProductUnitId;
        public double Discount;
    }
}
