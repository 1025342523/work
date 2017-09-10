package com.yifarj.yifadinghuobao.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 商品多属性
 *
 * @auther Czech.Yuan
 * @date 2017/9/7 10:46
 */
public class ProductPropertyListEntity {


    public boolean HasError;
    public Object Information;
    public Object PageInfo;
    public int AffectedRowCount;
    public Object Tag;
    public Object MValue;
    public List<ValueEntity> Value;

    public static class ValueEntity implements Parcelable {
        /**
         * Id : 102
         * ParentId : 100
         * Level : 2
         * Name : 红
         * Path : 100.102.
         * Ordinal : 0
         * ProductCount : 0
         */

        public int Id;
        public int ParentId;
        public int Level;
        public String Name;
        public String Path;
        public int Ordinal;
        public int ProductCount;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.Id);
            dest.writeInt(this.ParentId);
            dest.writeInt(this.Level);
            dest.writeString(this.Name);
            dest.writeString(this.Path);
            dest.writeInt(this.Ordinal);
            dest.writeInt(this.ProductCount);
        }

        public ValueEntity() {
        }

        protected ValueEntity(Parcel in) {
            this.Id = in.readInt();
            this.ParentId = in.readInt();
            this.Level = in.readInt();
            this.Name = in.readString();
            this.Path = in.readString();
            this.Ordinal = in.readInt();
            this.ProductCount = in.readInt();
        }

        public static final Parcelable.Creator<ValueEntity> CREATOR = new Parcelable.Creator<ValueEntity>() {
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
