package com.neotechindia.plugsmart.listeners;

/**
 * Created by Sheshnath.Yadav on 12/21/2016.
 */
public interface IUpdateUiListener {
    void onPacketReceived(String packets,String mac);
    void onPacketReceived(String packets);

}
