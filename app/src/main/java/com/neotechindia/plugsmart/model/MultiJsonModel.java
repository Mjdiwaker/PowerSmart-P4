package com.neotechindia.plugsmart.model;

import android.os.Parcel;
import android.os.Parcelable;


import com.neotechindia.plugsmart.Utilility.JsonUtil;

import java.util.Map;

/**
 * Created by Administrator on 12/21/2015.
 */
public class MultiJsonModel implements Parcelable {
    private String sid;
    private String bad;
    private String sad;
    private String cmd;
    private Map<String, String> data;

    protected MultiJsonModel(Parcel in) {
        sid = in.readString();
        bad = in.readString();
        sad = in.readString();
        cmd = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sid);
        dest.writeString(bad);
        dest.writeString(sad);
        dest.writeString(cmd);
    }


    @SuppressWarnings("unused")
    public static final Creator<MultiJsonModel> CREATOR = new Creator<MultiJsonModel>() {
        @Override
        public MultiJsonModel createFromParcel(Parcel in) {
            return new MultiJsonModel(in);
        }

        @Override
        public MultiJsonModel[] newArray(int size) {
            return new MultiJsonModel[size];
        }
    };

    public MultiJsonModel() {
    }

    public MultiJsonModel(String sid, String bad, String sad, String cmd, Map<String, String> data) {
        this.sid = sid;
        this.bad = bad;
        this.sad = sad;
        this.cmd = cmd;
        this.data = data;
    }


    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getBad() {
        return bad;
    }

    public void setBad(String bad) {
        this.bad = bad;
    }

    public String getSad() {
        return sad;
    }

    public void setSad(String sad) {
        this.sad = sad;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String toMapString() {
        return "\"data\":" + JsonUtil.getJsonOfMap(data);
    }

    @Override
    public String toString() {
        return "{" +
                "\"sid\":\"" + sid + "\"" +
                ",\"bad\":\"" + bad + "\"" +
                ",\"sad\":\"" + sad + "\"" +
                ",\"cmd\":\"" + cmd + "\"" +
                ",\"data\":" + JsonUtil.getJsonOfMap(data) +
                "}";
    }

    public String toStringWithoutBAD() {
        return "{" +
                "\"sid\":\"" + sid + "\"" +
                ",\"sad\":\"" + sad + "\"" +
                ",\"cmd\":\"" + cmd + "\"" +
                ",\"data\":" + JsonUtil.getJsonOfMap(data) +
                "}";
    }
}
