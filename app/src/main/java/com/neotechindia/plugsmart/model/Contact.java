package com.neotechindia.plugsmart.model;

public class Contact {

    private int id;
    private String Buyername;
    private String Buyerphone;
    private String Buyeremail;
    private String Adminpass;
    private String Buyerlong;
    private String Buyerlat;
    private String UUID;
    private String Type;

    public Contact() {
    }

    public Contact(int id, String buyername, String buyerphone, String buyeremail, String adminpass, String buyerlong, String buyerlat, String UUID, String type) {
        this.id = id;
        Buyername = buyername;
        Buyerphone = buyerphone;
        Buyeremail = buyeremail;
        Adminpass = adminpass;
        Buyerlong = buyerlong;
        Buyerlat = buyerlat;
        this.UUID = UUID;
        Type = type;
    }

    public Contact(String buyername, String buyerphone, String buyeremail, String adminpass, String buyerlong, String buyerlat, String UUID, String type) {
        Buyername = buyername;
        Buyerphone = buyerphone;
        Buyeremail = buyeremail;
        Adminpass = adminpass;
        Buyerlong = buyerlong;
        Buyerlat = buyerlat;
        this.UUID = UUID;
        Type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBuyername() {
        return Buyername;
    }

    public void setBuyername(String buyername) {
        Buyername = buyername;
    }

    public String getBuyerphone() {
        return Buyerphone;
    }

    public void setBuyerphone(String buyerphone) {
        Buyerphone = buyerphone;
    }

    public String getBuyeremail() {
        return Buyeremail;
    }

    public void setBuyeremail(String buyeremail) {
        Buyeremail = buyeremail;
    }

    public String getAdminpass() {
        return Adminpass;
    }

    public void setAdminpass(String adminpass) {
        Adminpass = adminpass;
    }

    public String getBuyerlong() {
        return Buyerlong;
    }

    public void setBuyerlong(String buyerlong) {
        Buyerlong = buyerlong;
    }

    public String getBuyerlat() {
        return Buyerlat;
    }

    public void setBuyerlat(String buyerlat) {
        Buyerlat = buyerlat;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
