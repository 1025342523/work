package com.yifarj.yifadinghuobao.model.entity;

import java.util.List;

/**
 * Created by zydx-pc on 2017/7/6.
 */

public class StockInfoForToolTipListEntity {

    /**
     * Value : [{"WarehouseId":101,"ProductId":100,"WarehouseName":"总仓","ProductName":"得力篮球1833","Quantity":-249,"SalesQuantity":-3041,"BatchId":"","SalesOrderQuantity":32,"SalesOutBillQuantity":2792,"OutQuantity":2792,"QuantityPackString":"-7件-3箱-1个","OutQuantityPackString":"87件1箱","SalesQuantityPackString":"-95件-1个","ProperyName1":null,"ProperyName2":null,"ProperyId1":0,"ProperyId2":0,"LocationId":0,"LocationName":""},{"WarehouseId":101,"ProductId":100,"WarehouseName":"总仓","ProductName":"得力篮球1833","Quantity":32000,"SalesQuantity":31996,"BatchId":"1","SalesOrderQuantity":0,"SalesOutBillQuantity":4,"OutQuantity":4,"QuantityPackString":"1000件","OutQuantityPackString":"4个","SalesQuantityPackString":"999件3箱4个","ProperyName1":null,"ProperyName2":null,"ProperyId1":0,"ProperyId2":0,"LocationId":0,"LocationName":""}]
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
         * WarehouseId : 101
         * ProductId : 100
         * WarehouseName : 总仓
         * ProductName : 得力篮球1833
         * Quantity : -249.0
         * SalesQuantity : -3041.0
         * BatchId :
         * SalesOrderQuantity : 32.0
         * SalesOutBillQuantity : 2792.0
         * OutQuantity : 2792.0
         * QuantityPackString : -7件-3箱-1个
         * OutQuantityPackString : 87件1箱
         * SalesQuantityPackString : -95件-1个
         * ProperyName1 : null
         * ProperyName2 : null
         * ProperyId1 : 0
         * ProperyId2 : 0
         * LocationId : 0
         * LocationName :
         */

        public int WarehouseId;
        public int ProductId;
        public String WarehouseName;
        public String ProductName;
        public double Quantity;
        public double SalesQuantity;
        public String BatchId;
        public double SalesOrderQuantity;
        public double SalesOutBillQuantity;
        public double OutQuantity;
        public String QuantityPackString;
        public String OutQuantityPackString;
        public String SalesQuantityPackString;
        public String ProperyName1;
        public String ProperyName2;
        public int ProperyId1;
        public int ProperyId2;
        public int LocationId;
        public String LocationName;
    }
}
