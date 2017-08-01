package com.yifarj.yifadinghuobao.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * 开单接口对应的货品信息
 */
public class SaleGoodsItem {

    public ValueEntity Value;

    public boolean HasError;
    public Object Information;
    public Object PageInfo;
    public int AffectedRowCount;
    public Object Tag;
    public Object MValue;

    public static class ValueEntity implements Cloneable, Parcelable {
        /**
         * 价格系数,用于本地状态保存,不做json序列化
         */
        @Expose(serialize = false, deserialize = false)
        public int priceFactor = 1;
        /**
         * 原始价格,用于本地状态保存,不做json序列化
         */
        @Expose(serialize = false, deserialize = false)
        public double CurrentPrice;

        @Expose(serialize = false, deserialize = false)
        public String ImagePath;

        public int Id;
        public String Code = "";
        public int BillId;
        public int SalesType;
        public int WarehouseId;
        public int LocationId;
        public int ProductId;
        public String BatchId;
        public int Quantity;
        public double BasicQuantity;
        public String PackString = "";
        public int UnitId;
        public int BasicUnitId;
        public int PriceSystemId;
        public double BasicUnitPrice;
        public double ActualUnitPrice;
        public double UnitPrice;
        public double ActualPrice;
        public float Discount;
        public double TaxRate;
        public String Remark = "";
        public double TotalPrice;
        public int ReferenceBillId;
        public int ReferenceBillTypeId;
        public String PrimaryBarcode = "";
        public int ReferenceBillItemId;
        public double ReferencedQuantity;
        public String WarehouseRemark = "";
        public double OweQuantity;
        public double OweReferencedQuantity;
        public int OweStatus;
        public String OweRemark = "";
        public String AluminumColor = "";
        public String GlassColor = "";
        public String Pedestal = "";
        public String Direction = "";
        public String Lengthc = "";
        public String width = "";
        public String high = "";
        public String CAddressAndPhone = "";
        public String Specification = "";
        public double RebateAmount;
        public double VipPoint;
        public boolean IsActivity;
        public boolean IsVip;
        public long BeginTime;
        public long EndTime;
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
        public double MinSalesQuantity;
        public double MaxSalesQuantity;
        public double MinSalesPrice;
        public double MaxPurchasePrice;
        public double MemoryPrice;
        public String ProductMenmonic = "";
        public String ProductName = "";
        public String ProductCode = "";
        public String BasicUnitName = "";
        public String WarehouseName = "";
        public String DefaultLocationName = "";
        public String CategoryName = "";
        public String BrandName = "";
        public String OrderCode = "";
        public String PackSpec;
        public List<ProductUnitEntity.ValueEntity> ProductUnitList=new ArrayList<>();
        public String SalesStockProductInfo;
        public String ProductUnitName = "";
        public double MemberPrice;
        public double BaseMemberPrice;
        public String DiscountValue = "";

        @Override
        public ValueEntity clone() {
            ValueEntity stu = null;
            try {
                stu = (ValueEntity) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return stu;
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
            dest.writeInt(this.BillId);
            dest.writeInt(this.SalesType);
            dest.writeInt(this.WarehouseId);
            dest.writeInt(this.LocationId);
            dest.writeInt(this.ProductId);
            dest.writeString(this.BatchId);
            dest.writeInt(this.Quantity);
            dest.writeDouble(this.BasicQuantity);
            dest.writeString(this.PackString);
            dest.writeInt(this.UnitId);
            dest.writeInt(this.BasicUnitId);
            dest.writeInt(this.PriceSystemId);
            dest.writeDouble(this.BasicUnitPrice);
            dest.writeDouble(this.ActualUnitPrice);
            dest.writeDouble(this.UnitPrice);
            dest.writeDouble(this.ActualPrice);
            dest.writeFloat(this.Discount);
            dest.writeDouble(this.TaxRate);
            dest.writeString(this.Remark);
            dest.writeDouble(this.TotalPrice);
            dest.writeInt(this.ReferenceBillId);
            dest.writeInt(this.ReferenceBillTypeId);
            dest.writeString(this.PrimaryBarcode);
            dest.writeInt(this.ReferenceBillItemId);
            dest.writeDouble(this.ReferencedQuantity);
            dest.writeString(this.WarehouseRemark);
            dest.writeDouble(this.OweQuantity);
            dest.writeDouble(this.OweReferencedQuantity);
            dest.writeInt(this.OweStatus);
            dest.writeString(this.OweRemark);
            dest.writeString(this.AluminumColor);
            dest.writeString(this.GlassColor);
            dest.writeString(this.Pedestal);
            dest.writeString(this.Direction);
            dest.writeString(this.Lengthc);
            dest.writeString(this.width);
            dest.writeString(this.high);
            dest.writeString(this.CAddressAndPhone);
            dest.writeString(this.Specification);
            dest.writeDouble(this.RebateAmount);
            dest.writeDouble(this.VipPoint);
            dest.writeByte(this.IsActivity ? (byte) 1 : (byte) 0);
            dest.writeByte(this.IsVip ? (byte) 1 : (byte) 0);
            dest.writeLong(this.BeginTime);
            dest.writeLong(this.EndTime);
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
            dest.writeDouble(this.MinSalesQuantity);
            dest.writeDouble(this.MaxSalesQuantity);
            dest.writeDouble(this.MinSalesPrice);
            dest.writeDouble(this.MaxPurchasePrice);
            dest.writeDouble(this.MemoryPrice);
            dest.writeString(this.ProductMenmonic);
            dest.writeString(this.ProductName);
            dest.writeString(this.ProductCode);
            dest.writeString(this.BasicUnitName);
            dest.writeString(this.WarehouseName);
            dest.writeString(this.DefaultLocationName);
            dest.writeString(this.CategoryName);
            dest.writeString(this.BrandName);
            dest.writeString(this.OrderCode);
            dest.writeString(this.PackSpec);
            dest.writeTypedList(this.ProductUnitList);
            dest.writeString(this.SalesStockProductInfo);
            dest.writeString(this.ProductUnitName);
            dest.writeDouble(this.MemberPrice);
            dest.writeDouble(this.BaseMemberPrice);
            dest.writeString(this.DiscountValue);
        }

        protected ValueEntity(Parcel in) {
            this.Id = in.readInt();
            this.Code = in.readString();
            this.BillId = in.readInt();
            this.SalesType = in.readInt();
            this.WarehouseId = in.readInt();
            this.LocationId = in.readInt();
            this.ProductId = in.readInt();
            this.BatchId = in.readString();
            this.Quantity = in.readInt();
            this.BasicQuantity = in.readDouble();
            this.PackString = in.readString();
            this.UnitId = in.readInt();
            this.BasicUnitId = in.readInt();
            this.PriceSystemId = in.readInt();
            this.BasicUnitPrice = in.readDouble();
            this.ActualUnitPrice = in.readDouble();
            this.UnitPrice = in.readDouble();
            this.ActualPrice = in.readDouble();
            this.Discount = in.readFloat();
            this.TaxRate = in.readDouble();
            this.Remark = in.readString();
            this.TotalPrice = in.readDouble();
            this.ReferenceBillId = in.readInt();
            this.ReferenceBillTypeId = in.readInt();
            this.PrimaryBarcode = in.readString();
            this.ReferenceBillItemId = in.readInt();
            this.ReferencedQuantity = in.readDouble();
            this.WarehouseRemark = in.readString();
            this.OweQuantity = in.readDouble();
            this.OweReferencedQuantity = in.readDouble();
            this.OweStatus = in.readInt();
            this.OweRemark = in.readString();
            this.AluminumColor = in.readString();
            this.GlassColor = in.readString();
            this.Pedestal = in.readString();
            this.Direction = in.readString();
            this.Lengthc = in.readString();
            this.width = in.readString();
            this.high = in.readString();
            this.CAddressAndPhone = in.readString();
            this.Specification = in.readString();
            this.RebateAmount = in.readDouble();
            this.VipPoint = in.readDouble();
            this.IsActivity = in.readByte() != 0;
            this.IsVip = in.readByte() != 0;
            this.BeginTime = in.readLong();
            this.EndTime = in.readLong();
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
            this.MinSalesQuantity = in.readDouble();
            this.MaxSalesQuantity = in.readDouble();
            this.MinSalesPrice = in.readDouble();
            this.MaxPurchasePrice = in.readDouble();
            this.MemoryPrice = in.readDouble();
            this.ProductMenmonic = in.readString();
            this.ProductName = in.readString();
            this.ProductCode = in.readString();
            this.BasicUnitName = in.readString();
            this.WarehouseName = in.readString();
            this.DefaultLocationName = in.readString();
            this.CategoryName = in.readString();
            this.BrandName = in.readString();
            this.OrderCode = in.readString();
            this.PackSpec = in.readString();
            this.ProductUnitList = in.createTypedArrayList(ProductUnitEntity.ValueEntity.CREATOR);
            this.SalesStockProductInfo = in.readString();
            this.ProductUnitName = in.readString();
            this.MemberPrice = in.readDouble();
            this.BaseMemberPrice = in.readDouble();
            this.DiscountValue = in.readString();
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
