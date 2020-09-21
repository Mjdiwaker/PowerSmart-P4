package com.neotechindia.plugsmart.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceBean implements Parcelable {

    public String name;
    public String macId;
    public String ip;
    public String status;
    public String source;
    public Boolean switchs;
    public Boolean switchEvent;
    public String iconType;
    public String serial_no;
    public String license_no;
    public String LicenceType; //1,2,3,4

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public String getLicense_no() {
        return license_no;
    }

    public void setLicense_no(String license_no) {
        this.license_no = license_no;
    }

    public void setLicenceType(String licenceType){
        LicenceType=licenceType;
    }

    public String getLicenceType(){
        return LicenceType;
    }



    public DeviceBean() {
    }

    public DeviceBean(Parcel in) {
        iconType = in.readString();
        name = in.readString();
        macId = in.readString();
        ip = in.readString();
        status = in.readString();
        source = in.readString();
        byte tmpSwitchs = in.readByte();
        switchs = tmpSwitchs == 0 ? null : tmpSwitchs == 1;
        byte tmpSwitchEvent = in.readByte();
        switchEvent = tmpSwitchEvent == 0 ? null : tmpSwitchEvent == 1;
        serial_no=in.readString();
        LicenceType=in.readString();


    }

    public static final Creator<DeviceBean> CREATOR = new Creator<DeviceBean>() {
        @Override
        public DeviceBean createFromParcel(Parcel in) {
            return new DeviceBean(in);
        }

        @Override
        public DeviceBean[] newArray(int size) {
            return new DeviceBean[size];
        }
    };

    public String getIconType() {
        return iconType;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getSwitchs() {
        return switchs;
    }

    public void setSwitchs(Boolean switchs) {
        this.switchs = switchs;
    }

    public Boolean getSwitchEvent() {
        return switchEvent;
    }

    public void setSwitchEvent(Boolean switchEvent) {
        this.switchEvent = switchEvent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(iconType);
        parcel.writeString(name);
        parcel.writeString(macId);
        parcel.writeString(ip);
        parcel.writeString(status);
        parcel.writeString(source);
        parcel.writeByte((byte) (switchs == null ? 0 : switchs ? 1 : 2));
        parcel.writeByte((byte) (switchEvent == null ? 0 : switchEvent ? 1 : 2));
        parcel.writeString(serial_no);
        parcel.writeString(LicenceType);
    }
}
