package com.neotechindia.plugsmart.activity;

import java.util.List;
import java.util.Map;

class SubmitDetailsRequest {

    private String name;
    private String email;
    private String phone;
    private String uuid;


    public String getBuyername() {
        return buyername;
    }

    public void setBuyername(String buyername) {
        this.buyername = buyername;
    }

    public String getBuyeremail() {
        return buyeremail;
    }

    public void setBuyeremail(String buyeremail) {
        this.buyeremail = buyeremail;
    }

    public String getBuyerphone() {
        return buyerphone;
    }

    public void setBuyerphone(String buyerphone) {
        this.buyerphone = buyerphone;
    }

    public String getBuyerlong() {
        return buyerlong;
    }

    public void setBuyerlong(String buyerlong) {
        this.buyerlong = buyerlong;
    }

    public String getBuyerlat() {
        return buyerlat;
    }

    public void setBuyerlat(String buyerlat) {
        this.buyerlat = buyerlat;
    }

    private String buyername;
    private String buyeremail;
    private String buyerphone;
    private String buyerlong;
    private String buyerlat;
    private String clientname;

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    public String getClientphone() {
        return clientphone;
    }

    public void setClientphone(String clientphone) {
        this.clientphone = clientphone;
    }

    public String getClientlong() {
        return clientlong;
    }

    public void setClientlong(String clientlong) {
        this.clientlong = clientlong;
    }

    public String getClientlat() {
        return clientlat;
    }

    public void setClientlat(String clientlat) {
        this.clientlat = clientlat;
    }

    private String clientphone;
    private String clientlong;
    private String clientlat;
    private int devices;
    private String mac_id;
    private String serial_no;
    private String lic_no;
    private String source;
    private String name_device;
    private String ip;
    private List<Map<String, Object>> data;

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getDevices() {
        return devices;
    }

    public void setDevices(int devices) {
        this.devices = devices;
    }

    public String getMac_id() {
        return mac_id;
    }

    public void setMac_id(String mac_id) {
        this.mac_id = mac_id;
    }

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public String getLic_no() {
        return lic_no;
    }

    public void setLic_no(String lic_no) {
        this.lic_no = lic_no;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName_device() {
        return name_device;
    }

    public void setName_device(String name_device) {
        this.name_device = name_device;
    }

//    @Override
//    public String toString() {
//        return "SubmitDetailsRequest{" +
//                "name='" + name + '\'' +
//                ", email='" + email + '\'' +
//                ", phone='" + phone + '\'' +
//                ", uuid='" + uuid + '\'' +
//                ", buyername='" + buyername + '\'' +
//                ", buyeremail='" + buyeremail + '\'' +
//                ", buyerphone='" + buyerphone + '\'' +
//                ", buyerlong='" + buyerlong + '\'' +
//                ", buyerlat='" + buyerlat + '\'' +
//                ", devices=" + devices +
//                ", mac_id='" + mac_id + '\'' +
//                ", serial_no='" + serial_no + '\'' +
//                ", lic_no='" + lic_no + '\'' +
//                ", source='" + source + '\'' +
//                ", name_device='" + name_device + '\'' +
//                ", ip='" + ip + '\'' +
//                ", data=" + data +
//                '}';
//    }

    @Override
    public String toString() {
        return "SubmitDetailsRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", uuid='" + uuid + '\'' +
                ", buyername='" + buyername + '\'' +
                ", buyeremail='" + buyeremail + '\'' +
                ", buyerphone='" + buyerphone + '\'' +
                ", buyerlong='" + buyerlong + '\'' +
                ", buyerlat='" + buyerlat + '\'' +
                ", clientname='" + clientname + '\'' +
                ", clientphone='" + clientphone + '\'' +
                ", clientlong='" + clientlong + '\'' +
                ", clientlat='" + clientlat + '\'' +
                ", devices=" + devices +
                ", mac_id='" + mac_id + '\'' +
                ", serial_no='" + serial_no + '\'' +
                ", lic_no='" + lic_no + '\'' +
                ", source='" + source + '\'' +
                ", name_device='" + name_device + '\'' +
                ", ip='" + ip + '\'' +
                ", data=" + data +
                '}';
    }
}
