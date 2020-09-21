package com.neotechindia.plugsmart.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RecyclerSelectorScheduleModel implements Parcelable {

    protected RecyclerSelectorScheduleModel(Parcel in) {
        one = in.readString();
        two = in.readString();
        three = in.readString();
        four = in.readString();
        five = in.readString();
    }

    public static final Creator<RecyclerSelectorScheduleModel> CREATOR = new Creator<RecyclerSelectorScheduleModel>() {
        @Override
        public RecyclerSelectorScheduleModel createFromParcel(Parcel in) {
            return new RecyclerSelectorScheduleModel(in);
        }

        @Override
        public RecyclerSelectorScheduleModel[] newArray(int size) {
            return new RecyclerSelectorScheduleModel[size];
        }
    };

    public RecyclerSelectorScheduleModel() {
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

    public String one;
    public String two;
    public String three;
    public String four;
    public String five;

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
    }
}