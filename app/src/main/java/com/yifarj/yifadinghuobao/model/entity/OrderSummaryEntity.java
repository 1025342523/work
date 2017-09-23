package com.yifarj.yifadinghuobao.model.entity;

import java.util.List;

/**
 * Created by ZhangZeZhi on 2017-09-22.
 */

public class OrderSummaryEntity {

    public ValueEntity Value;
    public boolean HasError;
    public Object Information;
    public Object PageInfo;
    public int AffectedRowCount;
    public Object Tag;
    public Object MValue;

    public static class ValueEntity{
        public String SqlStr;
        public List<Product> SummaryResult;
        public static class Product{
           public String ProductName;
           public double Quantity;
           public String UnitName;
           public double TotalPrice;
           public String SalesTypeName;
           public String Code;
           public String Name;
       }
    }

}
