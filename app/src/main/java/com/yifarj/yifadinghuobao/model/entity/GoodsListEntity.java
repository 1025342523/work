package com.yifarj.yifadinghuobao.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 货品列表数据对象
 */
public class GoodsListEntity {

    public boolean HasError;
    public Object Information;

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

    public static class ValueEntity implements Parcelable {
        public int Id;
        public String Code;
        public int Status;
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
        public int Cost;
        public int MinSalesQuantity;
        public int MaxSalesQuantity;
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
        public String BrandPath;
        public String CategoryPath;
        public boolean IsSeriesMain;
        public int SeriesId;
        public int VipRule;
        public int ProperyId1;
        public int ProperyId2;
        public String ProperyId1Name;
        public String ProperyId2Name;
        public List<ProductUnitEntity.ValueEntity> DeletingProductUnitList;
        public String CategoryName;
        public String SeriesName;
        public String BrandName;
        public String DefaultWarehouseName;
        public String DefaultLocationName;
        public String DefaultTraderName;
        public String PurchaserName;
        public String ManagerName;

        public List<ProductUnitEntity.ValueEntity> ProductUnitList;

        public List<PriceSystemListEntity> PriceSystemList;

        public List<ProductBarcodeListEntity> ProductBarcodeList;
        public List<ProductPicture> ProductPictureList;

        public static class PriceSystemListEntity implements Parcelable {
            public int Id;
            public String Name;
            public int Ordinal;
            public boolean Enable;
            public boolean IsOrderMeetingPrice;

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.Id);
                dest.writeString(this.Name);
                dest.writeInt(this.Ordinal);
                dest.writeByte(this.Enable ? (byte) 1 : (byte) 0);
                dest.writeByte(this.IsOrderMeetingPrice ? (byte) 1 : (byte) 0);
            }

            public PriceSystemListEntity() {
            }

            protected PriceSystemListEntity(Parcel in) {
                this.Id = in.readInt();
                this.Name = in.readString();
                this.Ordinal = in.readInt();
                this.Enable = in.readByte() != 0;
                this.IsOrderMeetingPrice = in.readByte() != 0;
            }

            public static final Creator<PriceSystemListEntity> CREATOR = new Creator<PriceSystemListEntity>() {
                @Override
                public PriceSystemListEntity createFromParcel(Parcel source) {
                    return new PriceSystemListEntity(source);
                }

                @Override
                public PriceSystemListEntity[] newArray(int size) {
                    return new PriceSystemListEntity[size];
                }
            };
        }

        public static class ProductBarcodeListEntity implements Parcelable {
            public int ProductId;
            public String Barcode;
            public int Ordinal;
            public int UnitId;

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.ProductId);
                dest.writeString(this.Barcode);
                dest.writeInt(this.Ordinal);
                dest.writeInt(this.UnitId);
            }

            public ProductBarcodeListEntity() {
            }

            protected ProductBarcodeListEntity(Parcel in) {
                this.ProductId = in.readInt();
                this.Barcode = in.readString();
                this.Ordinal = in.readInt();
                this.UnitId = in.readInt();
            }

            public static final Creator<ProductBarcodeListEntity> CREATOR = new Creator<ProductBarcodeListEntity>() {
                @Override
                public ProductBarcodeListEntity createFromParcel(Parcel source) {
                    return new ProductBarcodeListEntity(source);
                }

                @Override
                public ProductBarcodeListEntity[] newArray(int size) {
                    return new ProductBarcodeListEntity[size];
                }
            };
        }

        public ValueEntity() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.Id);
            dest.writeString(this.Code);
            dest.writeInt(this.Status);
            dest.writeString(this.Name);
            dest.writeString(this.Mnemonic);
            dest.writeString(this.PackSpec);
            dest.writeInt(this.CategoryId);
            dest.writeInt(this.BrandId);
            dest.writeInt(this.TypeId);
            dest.writeInt(this.DefaultWarehouseId);
            dest.writeInt(this.DefaultLocationId);
            dest.writeInt(this.DefaultTraderId);
            dest.writeInt(this.PurchaserId);
            dest.writeInt(this.ManagerId);
            dest.writeInt(this.Cost);
            dest.writeInt(this.MinSalesQuantity);
            dest.writeInt(this.MaxSalesQuantity);
            dest.writeInt(this.CostCalcMethodId);
            dest.writeLong(this.CreatedTime);
            dest.writeInt(this.CreatedUserId);
            dest.writeString(this.CreatedDevice);
            dest.writeLong(this.ModifiedTime);
            dest.writeInt(this.ModifiedUserId);
            dest.writeString(this.ModifiedDevice);
            dest.writeString(this.ModifiedDescription);
            dest.writeString(this.Volume);
            dest.writeString(this.Weight);
            dest.writeString(this.Material);
            dest.writeString(this.Origine);
            dest.writeString(this.Specification);
            dest.writeString(this.RelatedProduct);
            dest.writeByte(this.Limited ? (byte) 1 : (byte) 0);
            dest.writeByte(this.CanAssembly ? (byte) 1 : (byte) 0);
            dest.writeLong(this.ShelveTime);
            dest.writeByte(this.LockStock ? (byte) 1 : (byte) 0);
            dest.writeString(this.Remark);
            dest.writeDouble(this.MinSalesPrice);
            dest.writeDouble(this.MaxPurchasePrice);
            dest.writeDouble(this.Price0);
            dest.writeDouble(this.Price1);
            dest.writeDouble(this.Price2);
            dest.writeDouble(this.Price3);
            dest.writeDouble(this.Price4);
            dest.writeDouble(this.Price5);
            dest.writeDouble(this.Price6);
            dest.writeDouble(this.Price7);
            dest.writeDouble(this.Price8);
            dest.writeDouble(this.Price9);
            dest.writeDouble(this.Price10);
            dest.writeString(this.Propery1);
            dest.writeString(this.Propery2);
            dest.writeString(this.Propery3);
            dest.writeString(this.Propery4);
            dest.writeString(this.Propery5);
            dest.writeString(this.Propery6);
            dest.writeString(this.Propery7);
            dest.writeString(this.Propery8);
            dest.writeString(this.Propery9);
            dest.writeString(this.Propery10);
            dest.writeString(this.BrandPath);
            dest.writeString(this.CategoryPath);
            dest.writeByte(this.IsSeriesMain ? (byte) 1 : (byte) 0);
            dest.writeInt(this.SeriesId);
            dest.writeInt(this.VipRule);
            dest.writeInt(this.ProperyId1);
            dest.writeInt(this.ProperyId2);
            dest.writeString(this.ProperyId1Name);
            dest.writeString(this.ProperyId2Name);
            dest.writeTypedList(this.DeletingProductUnitList);
            dest.writeString(this.CategoryName);
            dest.writeString(this.SeriesName);
            dest.writeString(this.BrandName);
            dest.writeString(this.DefaultWarehouseName);
            dest.writeString(this.DefaultLocationName);
            dest.writeString(this.DefaultTraderName);
            dest.writeString(this.PurchaserName);
            dest.writeString(this.ManagerName);
            dest.writeTypedList(this.ProductUnitList);
            dest.writeTypedList(this.PriceSystemList);
            dest.writeTypedList(this.ProductBarcodeList);
            dest.writeTypedList(this.ProductPictureList);
        }

        protected ValueEntity(Parcel in) {
            this.Id = in.readInt();
            this.Code = in.readString();
            this.Status = in.readInt();
            this.Name = in.readString();
            this.Mnemonic = in.readString();
            this.PackSpec = in.readString();
            this.CategoryId = in.readInt();
            this.BrandId = in.readInt();
            this.TypeId = in.readInt();
            this.DefaultWarehouseId = in.readInt();
            this.DefaultLocationId = in.readInt();
            this.DefaultTraderId = in.readInt();
            this.PurchaserId = in.readInt();
            this.ManagerId = in.readInt();
            this.Cost = in.readInt();
            this.MinSalesQuantity = in.readInt();
            this.MaxSalesQuantity = in.readInt();
            this.CostCalcMethodId = in.readInt();
            this.CreatedTime = in.readLong();
            this.CreatedUserId = in.readInt();
            this.CreatedDevice = in.readString();
            this.ModifiedTime = in.readLong();
            this.ModifiedUserId = in.readInt();
            this.ModifiedDevice = in.readString();
            this.ModifiedDescription = in.readString();
            this.Volume = in.readString();
            this.Weight = in.readString();
            this.Material = in.readString();
            this.Origine = in.readString();
            this.Specification = in.readString();
            this.RelatedProduct = in.readString();
            this.Limited = in.readByte() != 0;
            this.CanAssembly = in.readByte() != 0;
            this.ShelveTime = in.readLong();
            this.LockStock = in.readByte() != 0;
            this.Remark = in.readString();
            this.MinSalesPrice = in.readDouble();
            this.MaxPurchasePrice = in.readDouble();
            this.Price0 = in.readDouble();
            this.Price1 = in.readDouble();
            this.Price2 = in.readDouble();
            this.Price3 = in.readDouble();
            this.Price4 = in.readDouble();
            this.Price5 = in.readDouble();
            this.Price6 = in.readDouble();
            this.Price7 = in.readDouble();
            this.Price8 = in.readDouble();
            this.Price9 = in.readDouble();
            this.Price10 = in.readDouble();
            this.Propery1 = in.readString();
            this.Propery2 = in.readString();
            this.Propery3 = in.readString();
            this.Propery4 = in.readString();
            this.Propery5 = in.readString();
            this.Propery6 = in.readString();
            this.Propery7 = in.readString();
            this.Propery8 = in.readString();
            this.Propery9 = in.readString();
            this.Propery10 = in.readString();
            this.BrandPath = in.readString();
            this.CategoryPath = in.readString();
            this.IsSeriesMain = in.readByte() != 0;
            this.SeriesId = in.readInt();
            this.VipRule = in.readInt();
            this.ProperyId1 = in.readInt();
            this.ProperyId2 = in.readInt();
            this.ProperyId1Name = in.readString();
            this.ProperyId2Name = in.readString();
            this.DeletingProductUnitList = in.createTypedArrayList(ProductUnitEntity.ValueEntity.CREATOR);
            this.CategoryName = in.readString();
            this.SeriesName = in.readString();
            this.BrandName = in.readString();
            this.DefaultWarehouseName = in.readString();
            this.DefaultLocationName = in.readString();
            this.DefaultTraderName = in.readString();
            this.PurchaserName = in.readString();
            this.ManagerName = in.readString();
            this.ProductUnitList = in.createTypedArrayList(ProductUnitEntity.ValueEntity.CREATOR);
            this.PriceSystemList = in.createTypedArrayList(PriceSystemListEntity.CREATOR);
            this.ProductBarcodeList = in.createTypedArrayList(ProductBarcodeListEntity.CREATOR);
            this.ProductPictureList = in.createTypedArrayList(ProductPicture.CREATOR);
        }

        public static final Creator<ValueEntity> CREATOR = new Creator<ValueEntity>() {
            @Override
            public ValueEntity createFromParcel(Parcel source) {
                return new ValueEntity(source);
            }

            @Override
            public ValueEntity[] newArray(int size) {
                return new ValueEntity[size];
            }
        };
    }
}
