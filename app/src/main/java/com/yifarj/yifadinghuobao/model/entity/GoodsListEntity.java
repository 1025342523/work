package com.yifarj.yifadinghuobao.model.entity;

import java.util.List;

/**
 * 货品列表数据对象
 */
public class GoodsListEntity {

    public boolean HasError;
    public String Information;
    public PageInfoEntity PageInfo;
    public int AffectedRowCount;
    public Object Tag;
    public Object MValue;
    public List<ValueEntity> Value;

    public static class PageInfoEntity {
        public int TotalCount;
        public int PageIndex;
        public int PageLength;
        public int SortOrder;
        public String SortedColumn;
    }

    public static class ValueEntity {
        public int Id;
        public String Code;
        public int Status;
        public int AuditedStatus;
        public String AuditedStatusName;
        public String Name;
        public String Mnemonic;
        public String PackSpec;
        public int CategoryId;
        public int BrandId;
        public int TypeId;
        public int DefaultWarehouseId;
        public int DefaultLocationId;
        public int DefaultTraderId;
        public int PurchaserId;
        public int ManagerId;
        public double Cost;
        public double MinSalesQuantity;
        public double MaxSalesQuantity;
        public int CostCalcMethodId;
        public long CreatedTime;
        public int CreatedUserId;
        public String CreatedDevice;
        public long ModifiedTime;
        public int ModifiedUserId;
        public String ModifiedDevice;
        public String ModifiedDescription;
        public String Volume;
        public String Weight;
        public String Material;
        public String Origine;
        public String Specification;
        public String RelatedProduct;
        public boolean Limited;
        public boolean CanAssembly;
        public long ShelveTime;
        public boolean LockStock;
        public String Remark;
        public double MinSalesPrice;
        public double MaxPurchasePrice;
        public double Price0;
        public double Price1;
        public double Price2;
        public double Price3;
        public double Price4;
        public double Price5;
        public double Price6;
        public double Price7;
        public double Price8;
        public double Price9;
        public double Price10;
        public String Propery1;
        public String Propery2;
        public String Propery3;
        public String Propery4;
        public String Propery5;
        public String Propery6;
        public String Propery7;
        public String Propery8;
        public String Propery9;
        public String Propery10;
        public String Propery11;
        public String Propery12;
        public String Propery13;
        public String Propery14;
        public String Propery15;
        public String Propery16;
        public String Propery17;
        public String Propery18;
        public String Propery19;
        public String Propery20;
        public String Propery21;
        public String Propery22;
        public String Propery23;
        public String Propery24;
        public String Propery25;
        public String Propery26;
        public String BrandPath;
        public String CategoryPath;
        public boolean IsSeriesMain;
        public int SeriesId;
        public int VipRule;
        public int ProperyId1;
        public int ProperyId2;
        public String ProperyId1Name;
        public String ProperyId2Name;
        public int DepartmentId;
        public String DepartmentName;
        public boolean IsSimple;
        public List<ProductUnitEntity.ValueEntity> DeletingProductUnitList;
        public String CategoryName;
        public String SeriesName;
        public String BrandName;
        public String DefaultWarehouseName;
        public String DefaultLocationName;
        public String DefaultTraderName;
        public String PurchaserName;
        public String ManagerName;
        public String BarcodeStrings;
        public double GQIntermediaryRate;
        public int TraderId;
        public String TraderName;
        public double SalesTax;
        public int SaleTypeId;
        public double MemoryPrice;
        public List<ProductUnitEntity.ValueEntity> ProductUnitList;
        public List<PriceSystemListEntity> PriceSystemList;
        public List<ProductBarcodeListEntity> ProductBarcodeList;
        public List<ProductPictureListEntity> ProductPictureList;

        public static class ProductUnitListEntity {
            /**
             * Id : 128
             * ProductId : 110
             * Name : 支
             * Factor : 1
             * BasicFactor : 1
             * IsBasic : true
             * IsDefault : false
             * BreakupNotify : false
             * Ordinal : 0
             */

            public int Id;
            public int ProductId;
            public String Name;
            public int Factor;
            public int BasicFactor;
            public boolean IsBasic;
            public boolean IsDefault;
            public boolean BreakupNotify;
            public int Ordinal;
        }

        public static class PriceSystemListEntity {
            /**
             * Id : 1
             * Name : A
             * Ordinal : 1
             * Enable : true
             * IsOrderMeetingPrice : false
             * GuidePrice : false
             * IsProtectivePrice : false
             */

            public int Id;
            public String Name;
            public int Ordinal;
            public boolean Enable;
            public boolean IsOrderMeetingPrice;
            public boolean GuidePrice;
            public boolean IsProtectivePrice;
        }

        public static class ProductBarcodeListEntity {
            /**
             * ProductId : 110
             * Barcode : 69359361 90072
             * Ordinal : 0
             * UnitId : 0
             */

            public int ProductId;
            public String Barcode;
            public int Ordinal;
            public int UnitId;
        }

        public static class ProductPictureListEntity {
            /**
             * Id : 1
             * ProductId : 110
             * Name : ZXCV
             * Path : CP000013_0001.jpg
             * Description :
             * Ordinal : 0
             * ProductPictureFile : null
             */

            public int Id;
            public int ProductId;
            public String Name;
            public String Path;
            public String Description;
            public int Ordinal;
            public String ProductPictureFile;
        }
    }
}
