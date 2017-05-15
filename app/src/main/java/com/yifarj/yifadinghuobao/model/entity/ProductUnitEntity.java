package com.yifarj.yifadinghuobao.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 货品单位
 */
public class ProductUnitEntity  {

    public boolean HasError;
    public Object Information;

    public Object PageInfo;
    public int AffectedRowCount;
    public Object Tag;
    public Object MValue;

    public ValueEntity Value;


    public static class ValueEntity implements Parcelable {
        public int Id;
        public int ProductId;
        public String Name;
        public int Factor;
        public int BasicFactor = 1;
        public boolean IsBasic;//判断是否为最小单位 1为true
        public boolean IsDefault;
        public boolean BreakupNotify;
        public int Ordinal;

        public ValueEntity() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.Id);
            dest.writeInt(this.ProductId);
            dest.writeString(this.Name);
            dest.writeInt(this.Factor);
            dest.writeInt(this.BasicFactor);
            dest.writeByte(this.IsBasic ? (byte) 1 : (byte) 0);
            dest.writeByte(this.IsDefault ? (byte) 1 : (byte) 0);
            dest.writeByte(this.BreakupNotify ? (byte) 1 : (byte) 0);
            dest.writeInt(this.Ordinal);
        }

        protected ValueEntity(Parcel in) {
            this.Id = in.readInt();
            this.ProductId = in.readInt();
            this.Name = in.readString();
            this.Factor = in.readInt();
            this.BasicFactor = in.readInt();
            this.IsBasic = in.readByte() != 0;
            this.IsDefault = in.readByte() != 0;
            this.BreakupNotify = in.readByte() != 0;
            this.Ordinal = in.readInt();
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
