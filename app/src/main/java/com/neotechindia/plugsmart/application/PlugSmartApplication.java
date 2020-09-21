package com.neotechindia.plugsmart.application;


import java.net.Socket;
import java.util.HashMap;

public class PlugSmartApplication extends android.app.Application {
    private static PlugSmartApplication _instance;
    public static HashMap<String, Socket> hashMap=new HashMap<>();
    public PlugSmartApplication() {
        _instance = this;
    }

    public static PlugSmartApplication getInstance() {
        if (_instance == null) {
            _instance = new PlugSmartApplication();
        }
        return _instance;

    }

    public static  HashMap<String, Socket> getHashMap()
    {
        return hashMap;
    }
}
