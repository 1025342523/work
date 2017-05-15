package com.yifarj.yifadinghuobao.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
* ProductPicture
* @auther  Czech.Yuan
* @date 2017/5/15 15:55
*/

public class ProductPicture implements Parcelable {

    public int Id;
    public int ProductId;
    public String Name;
    public String Path;
    public String Description;
    public int Ordinal;
    public String ProductPictureFile;

    public ProductPicture() {
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
        dest.writeString(this.Path);
        dest.writeString(this.Description);
        dest.writeInt(this.Ordinal);
        dest.writeString(this.ProductPictureFile);
    }

    protected ProductPicture(Parcel in) {
        this.Id = in.readInt();
        this.ProductId = in.readInt();
        this.Name = in.readString();
        this.Path = in.readString();
        this.Description = in.readString();
        this.Ordinal = in.readInt();
        this.ProductPictureFile = in.readString();
    }

    public static final Creator<ProductPicture> CREATOR = new Creator<ProductPicture>() {
        @Override
        public ProductPicture createFromParcel(Parcel source) {
            return new ProductPicture(source);
        }

        @Override
        public ProductPicture[] newArray(int size) {
            return new ProductPicture[size];
        }
    };
}
