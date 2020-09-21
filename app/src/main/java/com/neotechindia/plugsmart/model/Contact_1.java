package com.neotechindia.plugsmart.model;

public class Contact_1 {
    private int id;
    private String device_name;
    private String mac_id;
    private String serial_number;
    private String Licence_number;
    private String status;

    public Contact_1() {
    }

    public Contact_1(int id, String device_name, String mac_id, String serial_number, String licence_number, String status) {
        this.id = id;
        this.device_name = device_name;
        this.mac_id = mac_id;
        this.serial_number = serial_number;
        Licence_number = licence_number;
        this.status = status;
    }

    public Contact_1(String device_name, String mac_id, String serial_number, String licence_number, String status) {
        this.device_name = device_name;
        this.mac_id = mac_id;
        this.serial_number = serial_number;
        Licence_number = licence_number;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getMac_id() {
        return mac_id;
    }

    public void setMac_id(String mac_id) {
        this.mac_id = mac_id;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public String getLicence_number() {
        return Licence_number;
    }

    public void setLicence_number(String licence_number) {
        Licence_number = licence_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
