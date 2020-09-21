package com.neotechindia.plugsmart.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ImagePickBean implements Parcelable {
    int picId;
    int picPosition;

    public ImagePickBean(Parcel in) {
        picId = in.readInt();
        picPosition = in.readInt();
    }
    public ImagePickBean() {

    }
    public static final Creator<ImagePickBean> CREATOR = new Creator<ImagePickBean>() {
        @Override
        public ImagePickBean createFromParcel(Parcel in) {
            return new ImagePickBean(in);
        }

        @Override
        public ImagePickBean[] newArray(int size) {
            return new ImagePickBean[size];
        }
    };

    public int getPicId() {
        return picId;
    }

    public void setPicId(int picId) {
        this.picId = picId;
    }

    public int getPicPosition() {
        return picPosition;
    }

    public void setPicPosition(int picPosition) {
        this.picPosition = picPosition;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(picId);
        parcel.writeInt(picPosition);
    }
}
