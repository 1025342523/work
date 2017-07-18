package com.yifarj.yifadinghuobao.model.entity;

import java.util.List;

/**
 * 货品类别列表
 *
 * @auther Czech.Yuan
 * @date 2017/7/17 15:42
 */
public class ProductCategoryListEntity {

    /**
     * Value : [{"Id":128,"ParentId":0,"Level":1,"Name":"餐台","Path":"128.","Ordinal":0,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":129,"ParentId":0,"Level":1,"Name":"卧房","Path":"129.","Ordinal":0,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":130,"ParentId":129,"Level":2,"Name":"床","Path":"129.130.","Ordinal":0,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":131,"ParentId":129,"Level":2,"Name":"妆台","Path":"129.131.","Ordinal":0,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":132,"ParentId":0,"Level":1,"Name":"微店货品","Path":"132.","Ordinal":0,"ProductCount":0,"Abbreviate":"","GQIntermediaryRate":0},{"Id":133,"ParentId":132,"Level":2,"Name":"文具","Path":"132.133.","Ordinal":0,"ProductCount":0,"Abbreviate":"","GQIntermediaryRate":0},{"Id":134,"ParentId":132,"Level":2,"Name":"体育用品","Path":"132.134.","Ordinal":0,"ProductCount":0,"Abbreviate":"","GQIntermediaryRate":0},{"Id":116,"ParentId":115,"Level":2,"Name":"配件","Path":"115.116.","Ordinal":1,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":123,"ParentId":122,"Level":3,"Name":"杨桥面包","Path":"118.122.123.","Ordinal":1,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":124,"ParentId":123,"Level":4,"Name":"五谷面包","Path":"118.122.123.124.","Ordinal":1,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":126,"ParentId":125,"Level":2,"Name":"板材系列","Path":"125.126.","Ordinal":1,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":127,"ParentId":126,"Level":3,"Name":"太阳镜","Path":"125.126.127.","Ordinal":1,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":139,"ParentId":138,"Level":2,"Name":"手机","Path":"138.139.","Ordinal":1,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":100,"ParentId":0,"Level":1,"Name":"体育用品","Path":"100.","Ordinal":1,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":101,"ParentId":100,"Level":2,"Name":"篮球","Path":"100.101.","Ordinal":1,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":102,"ParentId":101,"Level":3,"Name":"皮篮球","Path":"100.101.102.","Ordinal":1,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":105,"ParentId":104,"Level":2,"Name":"笔","Path":"104.105.","Ordinal":1,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":106,"ParentId":105,"Level":3,"Name":"圆珠笔","Path":"104.105.106.","Ordinal":1,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":111,"ParentId":110,"Level":3,"Name":"笔袋","Path":"104.110.111.","Ordinal":1,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":114,"ParentId":113,"Level":2,"Name":"奶瓶","Path":"113.114.","Ordinal":1,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":119,"ParentId":118,"Level":2,"Name":"花生","Path":"118.119.","Ordinal":1,"ProductCount":0,"Abbreviate":"","GQIntermediaryRate":0},{"Id":120,"ParentId":119,"Level":3,"Name":"酒鬼花生","Path":"118.119.120.","Ordinal":1,"ProductCount":0,"Abbreviate":"","GQIntermediaryRate":0},{"Id":121,"ParentId":120,"Level":4,"Name":"五香味","Path":"118.119.120.121.","Ordinal":1,"ProductCount":0,"Abbreviate":"","GQIntermediaryRate":0},{"Id":122,"ParentId":118,"Level":2,"Name":"面包","Path":"118.122.","Ordinal":2,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":112,"ParentId":110,"Level":3,"Name":"订书机","Path":"104.110.112.","Ordinal":2,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":107,"ParentId":105,"Level":3,"Name":"中性笔","Path":"104.105.107.","Ordinal":2,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":103,"ParentId":100,"Level":2,"Name":"羽毛球拍","Path":"100.103.","Ordinal":2,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":104,"ParentId":0,"Level":1,"Name":"文化用品","Path":"104.","Ordinal":2,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":117,"ParentId":115,"Level":2,"Name":"手电筒","Path":"115.117.","Ordinal":2,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":110,"ParentId":104,"Level":2,"Name":"文具","Path":"104.110.","Ordinal":3,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":113,"ParentId":0,"Level":1,"Name":"母婴用品","Path":"113.","Ordinal":3,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":115,"ParentId":0,"Level":1,"Name":"电器","Path":"115.","Ordinal":4,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":118,"ParentId":0,"Level":1,"Name":"焙烤食品","Path":"118.","Ordinal":5,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":125,"ParentId":0,"Level":1,"Name":"眼镜","Path":"125.","Ordinal":6,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0},{"Id":135,"ParentId":0,"Level":1,"Name":"食品","Path":"135.","Ordinal":25,"ProductCount":0,"Abbreviate":"","GQIntermediaryRate":0},{"Id":136,"ParentId":135,"Level":2,"Name":"饮料","Path":"135.136.","Ordinal":26,"ProductCount":0,"Abbreviate":"","GQIntermediaryRate":0},{"Id":138,"ParentId":0,"Level":1,"Name":"电子产品","Path":"138.","Ordinal":26,"ProductCount":0,"Abbreviate":null,"GQIntermediaryRate":0}]
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
         * Id : 128
         * ParentId : 0
         * Level : 1
         * Name : 餐台
         * Path : 128.
         * Ordinal : 0
         * ProductCount : 0
         * Abbreviate : null
         * GQIntermediaryRate : 0.0
         */
        public int Id;
        public int ParentId;
        public int Level;
        public String Name;
        public String Path;
        public int Ordinal;
        public int ProductCount;
        public String Abbreviate;
        public double GQIntermediaryRate;
    }
}
