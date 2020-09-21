package com.neotechindia.plugsmart.model;

import android.os.Parcel;
import android.os.Parcelable;

public class JsonDataFieldsModel implements Parcelable {

    protected JsonDataFieldsModel(Parcel in) {
        one = in.readString();
        two = in.readString();
        three = in.readString();
        four = in.readString();
        five = in.readString();
        isSelected = in.readInt();
    }

    public static final Creator<JsonDataFieldsModel> CREATOR = new Creator<JsonDataFieldsModel>() {
        @Override
        public JsonDataFieldsModel createFromParcel(Parcel in) {
            return new JsonDataFieldsModel(in);
        }

        @Override
        public JsonDataFieldsModel[] newArray(int size) {
            return new JsonDataFieldsModel[size];
        }
    };

    public JsonDataFieldsModel() {
    }

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    public String getTwo() {
        return two;
    }

    public void setTwo(String two) {
        this.two = two;
    }

    public String getThree() {
        return three;
    }

    public void setThree(String three) {
        this.three = three;
    }

    public String getFour() {
        return four;
    }

    public void setFour(String four) {
        this.four = four;
    }

    public String getFive() {
        return five;
    }

    public void setFive(String five) {
        this.five = five;
    }

    private String one;
    private String two;
    private String three;
    private String four;
    private String five;
    private int isSelected = 0;

    public int isSelected() {
        return isSelected;
    }

    public void setSelected(int selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(one);
        parcel.writeString(two);
        parcel.writeString(three);
        parcel.writeString(four);
        parcel.writeString(five);
        parcel.writeInt(isSelected);
    }
}
