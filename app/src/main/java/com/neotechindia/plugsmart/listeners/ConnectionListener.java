package com.neotechindia.plugsmart.listeners;

import com.neotechindia.plugsmart.model.DeviceBean;

import java.util.ArrayList;

/**
 * Created by Sheshnath.Yadav on 12/22/2016.
 */
public interface ConnectionListener  {

    void onWifiConnectStatus(boolean isConnected);
    void onWifiConnectStatus(boolean isConnected, ArrayList<DeviceBean> arrayList);



}
