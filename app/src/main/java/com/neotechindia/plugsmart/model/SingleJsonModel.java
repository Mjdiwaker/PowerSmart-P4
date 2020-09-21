package com.neotechindia.plugsmart.model;

/**
 * Created by Administrator on 12/21/2015.
 */
public class SingleJsonModel {
    private String sid;
    private String bad;
    private String sad;
    private String cmd;
    private String data;
    private String idx;
    private String imt;

    public SingleJsonModel(String sid, String bad, String sad, String cmd, String data) {
        this.sid = sid;
        this.bad = bad;
        this.sad = sad;
        this.cmd = cmd;
        this.data = data;
    }
    public SingleJsonModel(String sid, String bad, String sad, String cmd) {
        this.sid = sid;
        this.bad = bad;
        this.sad = sad;
        this.cmd = cmd;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
                "\"sid\":\"" + sid + "\""+
                ",\"bad\":\"" + bad + "\""+
                ",\"sad\":\"" + sad +"\""+
                ",\"cmd\":\"" + cmd +"\""+
                ",\"data\":\"" + data +"\""+
                "}";
    }
    public String toStringWithoutBAD() {
        return "{" +
                "\"sid\":\"" + sid + "\""+
                ",\"sad\":\"" + sad +"\""+
                ",\"cmd\":\"" + cmd +"\""+
                ",\"data\":\"" + data +"\""+
                "}";
    }
    public String toStringWithoutData() {
        return "{" +
                "\"sid\":\"" + sid + "\""+
                ",\"sad\":\"" + sad +"\""+
                ",\"cmd\":\"" + cmd +"\""+
                "}";
    }
}
