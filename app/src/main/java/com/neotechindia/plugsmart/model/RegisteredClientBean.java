package com.neotechindia.plugsmart.model;

public class RegisteredClientBean {
    public RegisteredClientBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;
}
